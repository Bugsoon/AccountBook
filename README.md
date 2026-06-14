# 记账本 (AccountBook)

一款简洁的 Android 个人记账应用，帮助你轻松管理日常收支。

A simple Android personal finance app that helps you track daily income and expenses with ease.

## 功能特性 / Features

- 📝 记录收入和支出 / Record income and expenses
- 📊 统计图表 / Statistical charts (饼图、折线图 / Pie chart, line chart)
- 💰 预算管理 / Budget management
- 🏷️ 自定义分类 / Custom categories with icons
- 🔄 自动记账模板 / Auto record templates
- 📅 日期筛选 / Date filtering
- 🎨 Material Design 界面 / Material Design UI

## 截图 / Screenshots

<!-- 在此处添加应用截图 / Add app screenshots here -->

## 技术栈 / Tech Stack

- **语言**: Kotlin
- **架构**: MVVM
- **数据库**: Room
- **UI**: Material Design + 自定义图表
- **最低版本**: Android 8.0 (API 26)

- **Language**: Kotlin
- **Architecture**: MVVM
- **Database**: Room
- **UI**: Material Design + Custom Charts
- **Min SDK**: Android 8.0 (API 26)

## 构建 / Build

```bash
# 克隆项目 / Clone the project
git clone https://github.com/your-username/AccountBook.git

# 构建调试版本 / Build debug APK
./gradlew assembleDebug
```

## 项目结构 / Project Structure

```
app/src/main/java/com/example/accountbook/
├── data/           # 数据层 / Data layer (Entity, DAO, Repository)
├── ui/             # 界面层 / UI layer (Activity, Fragment, Adapter, View)
├── utils/          # 工具类 / Utilities
└── MainActivity.kt # 主入口 / Main entry point
```

## 许可证 / License

MIT
