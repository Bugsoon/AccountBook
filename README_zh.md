# 记账本 (AccountBook)

一款轻量级 Android 个人财务管理应用，基于 Kotlin 和 Room 数据库开发。

## 项目简介

记账本旨在帮助用户以简洁直观的界面记录日常收支。支持自定义分类、预算管理、统计分析和自动记账模板。

## 功能特性

- **记录管理**：快速添加收入和支出记录，支持金额、分类、日期和备注
- **分类系统**：内置 14 个默认分类（9 个支出、5 个收入），支持自定义分类和图标
- **统计图表**：饼图展示分类分布，折线图展示月度趋势
- **预算控制**：按分类设置月度预算，实时追踪预算使用情况
- **自动记账模板**：创建可复用模板，用于周期性交易
- **日期筛选**：按日期范围筛选记录
- **Material Design 界面**：底部导航、悬浮按钮快速记账、自定义日期选择器

## 技术栈

| 组件 | 技术 |
|------|------|
| 开发语言 | Kotlin 1.9.22 |
| 最低版本 | Android 8.0 (API 26) |
| 目标版本 | Android 14 (API 34) |
| 架构模式 | MVVM |
| 数据库 | Room 2.6.1 |
| UI 组件 | Material Design 1.11.0, MPAndroidChart 3.1.0 |
| 异步处理 | Coroutines, LiveData |
| 构建工具 | Gradle 8.13, AGP 8.13.2 |

## 项目结构

```
app/src/main/java/com/example/accountbook/
├── data/
│   ├── entity/          # Room 实体：Record, Category, Budget, AutoRecordTemplate
│   ├── dao/             # 数据访问对象
│   ├── repository/      # 仓库层
│   └── AppDatabase.kt   # Room 数据库单例
├── ui/
│   ├── activity/        # AddRecord, ManageCategories, AutoRecord, AmountInput
│   ├── fragment/        # Home, Statistics, Budget, Settings
│   ├── adapter/         # RecyclerView 适配器
│   ├── view/            # PieChartView, LineChartView, WheelDatePickerDialog, DraggableView
│   └── viewmodel/       # 各功能模块 ViewModel
└── utils/
    └── CategoryIconHelper.kt
```

## 构建方式

```bash
# 调试版本
./gradlew assembleDebug

# 发布版本
./gradlew assembleRelease

# 清理并重新构建
./gradlew clean assembleDebug
```

## 数据库说明

- 数据库名称：`account_book_database`
- 版本：1
- 首次启动自动创建 14 个默认分类
- Schema 导出已禁用

## 备注

- UI 中文字符串硬编码在实体类中，未使用 `strings.xml`
- 使用阿里云 Maven 镜像，备用 Google/Maven Central
- Room 使用 kapt 注解处理器

## 许可证

MIT
