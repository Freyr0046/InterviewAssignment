# InterviewAssignment

單畫面的上市股票資訊瀏覽 App，串接台灣證券交易所（TWSE）OpenAPI，讓使用者一次瀏覽全部
上市個股的當日成交資訊，並可依股票代號排序、下拉重新整理，點擊個股卡片快速查看本益比、
殖利率、股價淨值比等估值指標。

操作畫面錄影：https://youtu.be/2DpmTgW_tTo?si=0fklMszJEPjgNu5Q

## 功能特色

- **股票清單**：顯示所有上市個股的開盤價、收盤價、最高價、最低價、漲跌價差、月平均價、
  成交股數/金額/筆數，數值皆有千分位逗號與統一小數位數格式化
- **顏色語意**：收盤價高於月平均價顯示紅字、低於顯示綠字；漲跌價差正值紅字、負值綠字
- **排序**：點擊右上角 icon 開啟排序 BottomSheet，可依股票代號升序/降序切換，僅在記憶體中
  重新排序、不重打 API
- **個股詳情**：點擊卡片彈出 Dialog，顯示本益比、殖利率、股價淨值比（無資料以 `-` 呈現）
- **下拉重新整理**：重新整理期間顯示 indicator，不清空既有清單
- **標題列交易日期**：顯示資料對應的實際交易日（TWSE 回應中的民國日期已轉換為西元格式）
- **狀態處理**：Loading（skeleton）、Empty、Error（含重試按鈕）皆有對應畫面
- **深色模式與動態色彩**、**螢幕旋轉**（ViewModel 存活於 configuration change，不重新打 API）

## 技術架構

採 Clean Architecture 分層，搭配 MVI 風格的單向資料流（UDF）：

```
UI 層（Jetpack Compose）
  StockListScreen / StockCard / SortBottomSheet / StockDetailDialog
  StockListViewModel（@HiltViewModel，僅持有 UI State / 處理 Intent，無商業邏輯）
  StockUiModelMapper（Domain → 呈現用格式化模型）
        │
        ▼
Domain 層(純 Kotlin，不依賴 Android/Context)
  GetStockListUseCase（平行呼叫三支 API、依主要/輔助資料源規則合併結果）
  StockRepository（interface）
        │
        ▼
Data 層
  TwseApiService（Retrofit）／DTO／StockDtoMapper（安全轉型，不使用 `!!`）
  StockRepositoryImpl（呼叫 API、套用重試策略、Result 包裝）
```

- **依賴注入**：Hilt，全面採建構子注入
- **非同步**：Kotlin Coroutines + Flow，三支 API 平行呼叫（`async` + `awaitAll`）
- **狀態管理**：`StateFlow` 對外唯讀曝露，UI 以 `collectAsStateWithLifecycle()` 收集
- **錯誤處理**：跨層一律使用 `Result<T>`，不在 Repository/UseCase 直接 `throw`

## API

資料來源為 TWSE OpenAPI（`https://openapi.twse.com.tw/v1/`，公開資料、無需金鑰），平行呼叫
以下三支端點並以股票代號（`Code`）為 key 合併：

| Endpoint | 說明 | 角色 |
|---|---|---|
| `GET /exchangeReport/STOCK_DAY_ALL` | 上市個股日成交資訊 | **主要資料源**：失敗則整體顯示 Error |
| `GET /exchangeReport/STOCK_DAY_AVG_ALL` | 上市個股日收盤價及月平均價 | 輔助資料源：失敗僅該欄位顯示 `-` |
| `GET /exchangeReport/BWIBBU_ALL` | 上市個股日本益比、殖利率及股價淨值比 | 輔助資料源：失敗僅該欄位顯示 `-` |

每支 API 各自套用指數型退避重試（300ms → 600ms → 1200ms，最多 3 次），僅對可重試錯誤
（`IOException`/timeout/5xx）生效。Base URL 定義於 `gradle.properties`，透過
`BuildConfig.TWSE_BASE_URL` 提供給 Hilt 的 `NetworkModule`，全專案僅此一處讀取。

## 建置與測試

```bash
# 建置 Debug APK
./gradlew assembleDebug

# 執行單元測試
./gradlew :app:testDebugUnitTest

# 靜態分析(detekt + ktlint)
./gradlew detekt ktlintCheck

# 完整檢查(CI 等效)
./gradlew detekt ktlintCheck :app:testDebugUnitTest
```

### 測試涵蓋範圍

| 層級 | 框架 | 測試重點 |
|---|---|---|
| Repository | JUnit 5 + MockK | DTO → Domain 轉換、安全轉型、Result 包裝 |
| UseCase | JUnit 5 + MockK | 三來源合併邏輯、主要/輔助資料源失敗情境 |
| ViewModel | JUnit 5 + MockK + Turbine | UiState 轉換、排序、下拉更新、Dialog/BottomSheet 狀態 |
| UI | Compose UI Test | Happy path：清單渲染、排序切換、點擊顯示 Dialog、排序後捲回頂部 |

## 技術棧

Kotlin ‧ Jetpack Compose (Material 3) ‧ Hilt ‧ Retrofit + kotlinx.serialization ‧ Coroutines/Flow
‧ LeakCanary（debug）‧ detekt / ktlint

## 環境需求

- Android Studio 最新版本
- `minSdk` 24，`targetSdk` / `compileSdk` 36
- 需要網路連線以呼叫 TWSE OpenAPI
