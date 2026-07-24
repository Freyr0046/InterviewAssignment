package com.example.interview.ui.stocklist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.example.interview.ui.model.PriceIndicatorColor
import com.example.interview.ui.model.SortOption
import com.example.interview.ui.model.StockUiModel
import org.junit.Rule
import org.junit.Test

class StockListScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun stockUiModel(
        code: String,
        name: String,
    ) = StockUiModel(
        code = code,
        name = name,
        openingPrice = "10.00",
        closingPrice = "10.50",
        closingPriceColor = PriceIndicatorColor.UP_RED,
        highestPrice = "10.80",
        lowestPrice = "9.90",
        change = "+0.50",
        changeColor = PriceIndicatorColor.UP_RED,
        monthlyAveragePrice = "10.20",
        transaction = "100",
        tradeVolume = "1,000",
        tradeValue = "10,000",
        peRatio = "12.34",
        dividendYield = "3.45",
        pbRatio = "1.23",
    )

    private fun defaultScreenState(
        uiState: StockListUiState,
        isSortSheetVisible: Boolean = false,
        selectedStock: StockUiModel? = null,
    ) = StockListScreenState(
        uiState = uiState,
        sortOption = SortOption.CODE_DESC,
        isSortSheetVisible = isSortSheetVisible,
        selectedStock = selectedStock,
    )

    @Test
    fun successState_rendersStockList() {
        val stocks = listOf(stockUiModel("1101", "台泥"), stockUiModel("2330", "台積電"))

        composeRule.setContent {
            StockListScreen(
                screenState = defaultScreenState(StockListUiState.Success(stocks)),
                onIntent = {},
            )
        }

        composeRule.onNodeWithText("台泥").assertExists()
        composeRule.onNodeWithText("台積電").assertExists()
    }

    @Test
    fun clickingCard_sendsOnStockClickedIntent() {
        val stocks = listOf(stockUiModel("1101", "台泥"))
        val receivedIntents = mutableListOf<StockListViewIntent>()

        composeRule.setContent {
            StockListScreen(
                screenState = defaultScreenState(StockListUiState.Success(stocks)),
                onIntent = { receivedIntents.add(it) },
            )
        }

        composeRule.onNodeWithText("台泥").performClick()

        assert(receivedIntents.contains(StockListViewIntent.OnStockClicked("1101"))) {
            "Expected OnStockClicked(1101), got $receivedIntents"
        }
    }

    @Test
    fun stockDetailDialog_showsSelectedStockValuationFields() {
        val stock = stockUiModel("1101", "台泥")

        composeRule.setContent {
            StockListScreen(
                screenState =
                    defaultScreenState(
                        uiState = StockListUiState.Success(listOf(stock)),
                        selectedStock = stock,
                    ),
                onIntent = {},
            )
        }

        composeRule.onNodeWithText("12.34").assertExists()
    }

    @Test
    fun sortBottomSheet_selectingAscendingSendsIntent() {
        val stocks = listOf(stockUiModel("1101", "台泥"))
        val receivedIntents = mutableListOf<StockListViewIntent>()

        composeRule.setContent {
            StockListScreen(
                screenState =
                    defaultScreenState(
                        uiState = StockListUiState.Success(stocks),
                        isSortSheetVisible = true,
                    ),
                onIntent = { receivedIntents.add(it) },
            )
        }

        composeRule.onNodeWithText("依股票代號升序").performClick()

        assert(receivedIntents.contains(StockListViewIntent.OnSortOptionSelected(SortOption.CODE_ASC))) {
            "Expected OnSortOptionSelected(CODE_ASC), got $receivedIntents"
        }
    }

    @Test
    fun changingSortOption_scrollsListBackToTop() {
        val stocks = (1..30).map { stockUiModel(code = "%04d".format(it), name = "Stock$it") }
        var sortOption by mutableStateOf(SortOption.CODE_DESC)

        composeRule.setContent {
            StockListScreen(
                screenState =
                    StockListScreenState(
                        uiState = StockListUiState.Success(stocks),
                        sortOption = sortOption,
                        isSortSheetVisible = false,
                        selectedStock = null,
                    ),
                onIntent = {},
            )
        }

        composeRule.onNode(hasScrollAction()).performScrollToIndex(stocks.lastIndex)
        composeRule.onNodeWithText(stocks.first().name).assertDoesNotExist()

        composeRule.runOnIdle { sortOption = SortOption.CODE_ASC }

        composeRule.onNodeWithText(stocks.first().name).assertIsDisplayed()
    }

    @Test
    fun refreshingSuccessState_keepsExistingListVisible() {
        val stocks = listOf(stockUiModel("1101", "台泥"))

        composeRule.setContent {
            StockListScreen(
                screenState = defaultScreenState(StockListUiState.Success(stocks, isRefreshing = true)),
                onIntent = {},
            )
        }

        composeRule.onNodeWithText("台泥").assertExists()
    }
}
