package com.example.interview.ui.stocklist

import com.example.interview.domain.model.Stock
import com.example.interview.domain.usecase.GetStockListUseCase
import com.example.interview.ui.mapper.StockUiModelMapper
import com.example.interview.ui.model.SortOption
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StockListViewModelTest {
    private val getStockListUseCase: GetStockListUseCase = mockk()
    private val uiModelMapper = StockUiModelMapper()
    private val dispatcher = StandardTestDispatcher()

    private fun createViewModel() = StockListViewModel(getStockListUseCase, uiModelMapper)

    private fun stock(
        code: String,
        change: Double = 0.0,
        date: String? = "2026-07-23",
    ) = Stock(
        code = code,
        name = "Stock $code",
        date = date,
        openingPrice = 10.0,
        closingPrice = 10.0,
        highestPrice = 10.0,
        lowestPrice = 10.0,
        change = change,
        monthlyAveragePrice = 10.0,
        transaction = 1L,
        tradeVolume = 1L,
        tradeValue = 1L,
        peRatio = 1.0,
        dividendYield = 1.0,
        pbRatio = 1.0,
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading state emitted on init`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))

            val viewModel = createViewModel()

            assertEquals(StockListUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `success state emitted when use case returns non-empty list`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))

            val viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is StockListUiState.Success)
            assertEquals("1101", (state as StockListUiState.Success).stocks.single().code)
            assertEquals("2026/07/23", state.tradingDate)
        }

    @Test
    fun `empty state emitted when use case returns empty list`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(emptyList())

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(StockListUiState.Empty, viewModel.uiState.value)
        }

    @Test
    fun `error state emitted when use case returns failure`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.failure(IllegalStateException("boom"))

            val viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is StockListUiState.Error)
            assertEquals("boom", (state as StockListUiState.Error).message)
        }

    @Test
    fun `retry intent emits loading then re-invokes use case`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.failure(IllegalStateException("boom"))
            val viewModel = createViewModel()
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value is StockListUiState.Error)

            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))
            viewModel.onIntent(StockListViewIntent.OnRetryClicked)

            assertEquals(StockListUiState.Loading, viewModel.uiState.value)

            advanceUntilIdle()

            assertTrue(viewModel.uiState.value is StockListUiState.Success)
        }

    @Test
    fun `refresh intent sets isRefreshing true then false on success`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onIntent(StockListViewIntent.OnRefresh)

            val refreshingState = viewModel.uiState.value
            assertTrue(refreshingState is StockListUiState.Success)
            assertTrue((refreshingState as StockListUiState.Success).isRefreshing)

            advanceUntilIdle()

            val finalState = viewModel.uiState.value
            assertTrue(finalState is StockListUiState.Success)
            assertFalse((finalState as StockListUiState.Success).isRefreshing)
        }

    @Test
    fun `refresh intent emits error state when use case returns failure`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))
            val viewModel = createViewModel()
            advanceUntilIdle()

            coEvery { getStockListUseCase() } returns Result.failure(IllegalStateException("refresh failed"))
            viewModel.onIntent(StockListViewIntent.OnRefresh)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is StockListUiState.Error)
            assertEquals("refresh failed", (state as StockListUiState.Error).message)
        }

    @Test
    fun `default sort option is CODE_DESC`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))
            val viewModel = createViewModel()

            assertEquals(SortOption.CODE_DESC, viewModel.sortOption.value)
        }

    @Test
    fun `sort option selected re-sorts existing list without re-invoking use case`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101"), stock("2330")))
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onIntent(StockListViewIntent.OnSortOptionSelected(SortOption.CODE_ASC))

            val state = viewModel.uiState.value
            assertTrue(state is StockListUiState.Success)
            assertEquals(listOf("1101", "2330"), (state as StockListUiState.Success).stocks.map { it.code })
            assertEquals(SortOption.CODE_ASC, viewModel.sortOption.value)
            coVerify(exactly = 1) { getStockListUseCase() }
        }

    @Test
    fun `filter icon clicked shows sort sheet`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(emptyList())
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onIntent(StockListViewIntent.OnFilterIconClicked)

            assertTrue(viewModel.isSortSheetVisible.value)
        }

    @Test
    fun `sort sheet dismissed hides sort sheet`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(emptyList())
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onIntent(StockListViewIntent.OnFilterIconClicked)
            viewModel.onIntent(StockListViewIntent.OnSortSheetDismissed)

            assertFalse(viewModel.isSortSheetVisible.value)
        }

    @Test
    fun `selecting sort option hides sort sheet and updates sort option`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onIntent(StockListViewIntent.OnFilterIconClicked)
            viewModel.onIntent(StockListViewIntent.OnSortOptionSelected(SortOption.CODE_ASC))

            assertFalse(viewModel.isSortSheetVisible.value)
            assertEquals(SortOption.CODE_ASC, viewModel.sortOption.value)
        }

    @Test
    fun `stock clicked emits matching stock as selected stock`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101"), stock("2330")))
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onIntent(StockListViewIntent.OnStockClicked("2330"))

            assertEquals("2330", viewModel.selectedStock.value?.code)
        }

    @Test
    fun `stock detail dismissed clears selected stock`() =
        runTest(dispatcher) {
            coEvery { getStockListUseCase() } returns Result.success(listOf(stock("1101")))
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onIntent(StockListViewIntent.OnStockClicked("1101"))
            assertEquals("1101", viewModel.selectedStock.value?.code)

            viewModel.onIntent(StockListViewIntent.OnStockDetailDismissed)

            assertNull(viewModel.selectedStock.value)
        }
}
