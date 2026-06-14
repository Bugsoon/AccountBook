# AGENTS.md

## Project overview

Kotlin Android personal finance/accounting app ("记账本"). Single-module Gradle project.

## Build & run

```bash
./gradlew assembleDebug          # build debug APK
./gradlew assembleRelease        # build release APK
./gradlew clean assembleDebug    # clean rebuild
./gradlew testDebugUnitTest      # unit tests (none currently)
./gradlew connectedDebugAndroidTest  # instrumented tests (none currently)
```

**Gradle wrapper**: 8.13 · **AGP**: 8.13.2 · **Kotlin**: 1.9.22 · **Java target**: 17

No test files exist yet. Tests directory is configured but empty.

## Architecture

MVVM with Room database. Pattern: **Entity → DAO → Repository → ViewModel → Fragment/Activity**.

Key packages under `app/src/main/java/com/example/accountbook/`:
- `data/entity/` — Room entities: Record, Category, Budget, AutoRecordTemplate
- `data/dao/` — Room DAOs for each entity
- `data/repository/` — Repository layer per entity
- `data/AppDatabase.kt` — Room singleton, DB version 1, `account_book_database`
- `ui/fragment/` — Home, Statistics, Budget, Settings
- `ui/activity/` — AddRecord, ManageCategories, AutoRecord, AmountInput
- `ui/adapter/` — RecyclerView adapters
- `ui/view/` — Custom views (PieChartView, LineChartView, WheelDatePickerDialog, DraggableView)
- `ui/viewmodel/` — ViewModels for Record, Category, Budget, AutoRecordTemplate
- `utils/CategoryIconHelper.kt` — icon mapping utility

Entry point: `MainActivity.kt` (bottom navigation: Home → Statistics → Budget → Settings, FAB for add record).

## Database

Room DB version 1, no schema export. On first create, seeds 14 default categories (Chinese labels, icons via `ic_cat_*` drawables). If you add entities or change schema, bump version and add a migration — no migration support exists yet.

## Dependencies worth noting

- **Room** with kapt (not KSP) — annotation processor runs at build time
- **MPAndroidChart** v3.1.0 — but project uses custom chart views (`PieChartView`, `LineChartView`) not the library's views directly
- **Aliyun Maven mirrors** configured in `settings.gradle` and `build.gradle` — network access may depend on these being reachable or google()/mavenCentral() fallbacks working

## Code conventions

- No comments or KDoc in codebase
- Chinese UI strings hardcoded in entities, not in `strings.xml` — keep consistency when adding user-facing text
- `android.nonTransitiveRClass=true` — use fully qualified R references when crossing module boundaries (currently single module, but worth knowing)
- `exportSchema = false` on Room DB — no schema JSON exported
