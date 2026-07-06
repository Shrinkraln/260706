# 研究发现

> 项目：uasservice 后端补全
> 创建日期：2026-07-06

---

## 1. 项目架构

- Java 17 + Spring Boot 3.2.5 + Maven 多模块
- MyBatis-Plus 3.5.5 + PostgreSQL 15+ (PostGIS) + Redis 7+
- Spring Security + JWT (jjwt 0.12.5) + SpringDoc OpenAPI
- 2 个可部署服务 + 1 个共享模块

## 2. 现有代码模式（Demo 约定）

### 2.1 统一响应格式

所有接口使用 `ApiResult<T>` 包装：
```java
ApiResult.ok(data)  // 成功 code=200
ApiResult.fail(400, "msg")  // 失败
```

### 2.2 分页格式

使用 `PageResult<T>(total, records)` 包装列表。

### 2.3 Controller 模式

- 类级别 `@RestController` + `@RequestMapping("/api/v1/...")`
- 构造器注入（`@RequiredArgsConstructor`）
- 返回 `ApiResult<...>`

### 2.4 Service 模式

- 接口在 `com.uas.core.service`
- 实现在 `com.uas.core.service.impl`
- `@Service` + `@RequiredArgsConstructor`
- 使用 MyBatis-Plus LambdaQueryWrapper 查询

### 2.5 Entity 模式

- `@TableName` 指定表名
- `@TableId` 指定主键
- 字段名与数据库列名一致（启用 map-underscore-to-camel-case）

### 2.6 数据库命名

- 用户表参考 exam_web：`sys_user.zhdlxxbs`（账户标识）、`sys_user.dlzh`（登录账户）
- 角色表：`sys_role.groupid`、`sys_role.groupname`
- 权限表：`sys_uac`、`sys_uac_url`、`sys_role_uac`

## 3. 配置环境

| 环境变量 | 用途 | 远程值 |
|----------|------|--------|
| DB_URL | PostgreSQL | `jdbc:postgresql://pgm-f8z4w4jt4tg153d11o.pg.rds.aliyuncs.com:5432/...` |
| REDIS_HOST | Redis | `47.120.72.179` |
| JWT_SECRET | JWT 签名 | ≥32 字符 |
| MOCK_ENABLED | 模拟数据 | true（无硬件时） |

## 4. 关键发现

1. **AdminStubControllers 是最大的问题** — 8 个端点全部返回空数据，需要拆分为独立 Controller 并实现真实逻辑
2. **AuthController 是第二个关键问题** — 登录返回 "TODO" token，没有 JWT 实现
3. **SecurityConfig 全开放** — `anyRequest().permitAll()`，没有 JWT 过滤器
4. **CounterService/DronService/AlertService 核心逻辑完整** — 只需要补全查询端点和持久化
5. **DroneTrackPoint 未持久化** — DroneServiceImpl 只写 Redis 缓存，不写数据库
6. **zone_stats 表未使用** — AlertService 发事件但不更新 zone_stats 表
7. **审计日志未集成** — counter 操作等关键行为未写 audit_log

## 5. 数据库表补齐

已有实体但缺失对应数据库表的补充表：`sys_role`、`sys_role_uac`、`sys_uac`、`sys_uac_url`（已在 schema.sql 定义但无 Entity）

缺失 Entity + Mapper 的表：`map_resource`、`audit_log`、`zone_stats`、`system_config`、`device_comm_log`、`drone_track_point`
