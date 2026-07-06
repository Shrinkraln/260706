# 代码生成工作区 (.codegen)

> **独立分区** — 与 `uasservice/` 项目代码完全分离，不提交到项目仓库。

## 目录结构

```
.codegen/
├── README.md                # 本文件
├── analysis/                # 框架分析文档（只读参考）
│   └── 项目框架与后端生成模式.md
├── templates/               # 代码模板（Handlebars 风格变量）
│   ├── Entity.java.tmpl
│   ├── Mapper.java.tmpl
│   ├── Service.java.tmpl
│   ├── ServiceImpl.java.tmpl
│   └── Controller.java.tmpl
├── scripts/                 # 生成脚本
│   └── gen-module.ps1       # 主生成器
└── generated/               # 生成产物（在此审查，再复制到项目）
    └── .gitkeep
```

## 快速使用

### 1. 生成一个新模块

```powershell
# 在 .codegen/scripts/ 下执行
.\gen-module.ps1 -Name Task -Table task -IdType Long -UrlPath tasks
```

参数说明:

| 参数 | 必填 | 说明 | 示例 |
|------|------|------|------|
| `-Name` | ✅ | 模块名 (PascalCase) | `Task`, `FlightLog` |
| `-Table` | ✅ | 数据库表名 (snake_case) | `task`, `flight_log` |
| `-IdType` | ❌ | 主键类型 String/Long，默认 String | `Long` |
| `-UrlPath` | ❌ | REST URL 路径，默认 Name+s | `tasks` |

### 2. 检查生成产物

生成文件在 `generated/<Name>/` 下，逐个审查：
- 业务字段是否完整
- 方法是否需要增减
- 包名是否正确

### 3. 复制到项目

确认无误后，按提示将文件复制到 `uasservice/` 对应目录。

## 模板变量说明

模板使用 `{{变量名}}` 语法，由 `gen-module.ps1` 替换：

| 变量 | 说明 | 示例 |
|------|------|------|
| `{{ENTITY_NAME}}` | 实体类名 | `Task` |
| `{{ENTITY_VAR}}` | 实体变量名 (首字母小写) | `task` |
| `{{TABLE_NAME}}` | 数据库表名 | `task` |
| `{{PK_TYPE}}` | 主键 Java 类型 | `String` / `Long` |
| `{{ID_STRATEGY}}` | MyBatis-Plus ID 策略 | `IdType.AUTO` / (空) |
| `{{URL_PATH}}` | REST URL 路径 | `tasks` |

## 生成模式（标准 CRUD）

一次性生成 5 个文件：

| # | 层 | 模板 | 输出 |
|---|-----|------|------|
| 1 | Entity | `Entity.java.tmpl` | `Task.java` |
| 2 | Mapper | `Mapper.java.tmpl` | `TaskMapper.java` |
| 3 | Service 接口 | `Service.java.tmpl` | `TaskService.java` |
| 4 | Service 实现 | `ServiceImpl.java.tmpl` | `TaskServiceImpl.java` |
| 5 | Controller | `Controller.java.tmpl` | `TaskController.java` |

## 注意事项

- `.codegen/` 整体加入 `.gitignore`，不提交到 `uasservice` 仓库
- `generated/` 是临时产物，确认后手动复制到项目
- 生成后务必检查：实体字段、业务方法签名、Redis 缓存需求、事件发布需求
- 如需自定义模板，直接编辑 `templates/` 下的 `.tmpl` 文件
