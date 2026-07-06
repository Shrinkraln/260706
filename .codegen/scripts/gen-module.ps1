# ============================================================
# 代码生成器: gen-module.ps1
# 用法: .\gen-module.ps1 -Name Task -Table task -IdType Long -UrlPath tasks
# ============================================================
param(
    [Parameter(Mandatory=$true)]
    [string]$Name,              # 模块名 (PascalCase), 如 Task

    [Parameter(Mandatory=$true)]
    [string]$Table,             # 数据库表名 (snake_case), 如 task

    [ValidateSet("String", "Long")]
    [string]$IdType = "String", # 主键类型

    [string]$UrlPath,           # URL 路径, 默认 = Name 小写+s

    [switch]$Force              # 覆盖已存在的生成文件
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir = Split-Path -Parent $ScriptDir

# 推导变量
$EntityName = $Name
$EntityVar = $Name.Substring(0,1).ToLower() + $Name.Substring(1)
if (-not $UrlPath) {
    $UrlPath = "$($EntityVar)s"
}

$PK_TYPE = $IdType
$STRING_ID = ($IdType -eq "String")
$LONG_ID   = ($IdType -eq "Long")

# 输出目录
$OutDir = Join-Path $RootDir "generated\$Name"
if (Test-Path $OutDir) {
    if ($Force) {
        Remove-Item -Recurse -Force $OutDir
    } else {
        Write-Warning "目录已存在: $OutDir，使用 -Force 覆盖"
        exit 1
    }
}
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  代码生成: $Name 模块" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  表名:      $Table"
Write-Host "  主键:      $IdType"
Write-Host "  URL:       /api/v1/$UrlPath"
Write-Host "  输出:      .codegen\generated\$Name\"
Write-Host "========================================"

# ---- 替换函数 ----
function Render-Template($tmplPath, $outPath) {
    $content = Get-Content $tmplPath -Raw -Encoding UTF8

    # 基础替换
    $content = $content -replace '\{\{ENTITY_NAME\}\}', $EntityName
    $content = $content -replace '\{\{ENTITY_VAR\}\}', $EntityVar
    $content = $content -replace '\{\{TABLE_NAME\}\}', $Table
    $content = $content -replace '\{\{PK_TYPE\}\}', $PK_TYPE
    $content = $content -replace '\{\{URL_PATH\}\}', $UrlPath

    # 条件块: {{#if STRING_ID}}...{{else}}...{{/if}}
    if ($STRING_ID) {
        $content = $content -replace '(?s)\{\{#if STRING_ID\}\}(.*?)\{\{else\}\}.*?\{\{/if\}\}', '$1'
        $content = $content -replace '(?s)\{\{#if LONG_ID\}\}.*?\{\{/if\}\}', ''
    } else {
        $content = $content -replace '(?s)\{\{#if STRING_ID\}\}.*?\{\{else\}\}(.*?)\{\{/if\}\}', '$1'
        $content = $content -replace '(?s)\{\{#if LONG_ID\}\}(.*?)\{\{/if\}\}', '$1'
    }

    # 清理残留标记
    $content = $content -replace '\{\{#if .*?\}\}', ''
    $content = $content -replace '\{\{else\}\}', ''
    $content = $content -replace '\{\{/if\}\}', ''
    $content = $content -replace '(?m)^\s*\n', "`n"

    Set-Content -Path $outPath -Value $content -Encoding UTF8
    Write-Host "  -> $outPath" -ForegroundColor Green
}

# ---- 生成 ----
$TmplDir = Join-Path $RootDir "templates"

Render-Template "$TmplDir\Entity.java.tmpl"        "$OutDir\$EntityName.java"
Render-Template "$TmplDir\Mapper.java.tmpl"         "$OutDir\$($EntityName)Mapper.java"
Render-Template "$TmplDir\Service.java.tmpl"        "$OutDir\$($EntityName)Service.java"
Render-Template "$TmplDir\ServiceImpl.java.tmpl"    "$OutDir\$($EntityName)ServiceImpl.java"
Render-Template "$TmplDir\Controller.java.tmpl"     "$OutDir\$($EntityName)Controller.java"

# ---- 打印目标路径 ----
Write-Host ""
Write-Host "========================================" -ForegroundColor Yellow
Write-Host "  生成完成！复制到项目:" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow
Write-Host "  Entity:      uas-core\...\entity\$EntityName.java"
Write-Host "  Mapper:       uas-core\...\mapper\$($EntityName)Mapper.java"
Write-Host "  Service:      uas-core\...\service\$($EntityName)Service.java"
Write-Host "  ServiceImpl:  uas-core\...\service\impl\$($EntityName)ServiceImpl.java"
Write-Host "  Controller:   uas-api\...\controller\$($EntityName)Controller.java"
Write-Host ""
Write-Host "  SQL:  CREATE TABLE $Table ( id $($(if ($STRING_ID) {'VARCHAR(64)'} else {'BIGINT AUTO_INCREMENT'})) PRIMARY KEY, ... );" -ForegroundColor Yellow
