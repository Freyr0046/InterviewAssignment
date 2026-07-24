package com.example.interview.ui.stocklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview.domain.model.Stock
import com.example.interview.domain.usecase.GetStockListUseCase
import com.example.interview.ui.mapper.StockUiModelMapper
import com.example.interview.ui.model.SortOption
import com.example.interview.ui.model.StockUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockListViewModel
    @Inject
    constructor(
        private val getStockListUseCase: GetStockListUseCase,
        private val uiModelMapper: StockUiModelMapper,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<StockListUiState>(StockListUiState.Loading)
        val uiState: StateFlow<StockListUiState> = _uiState.asStateFlow()

        private val _sortOption = MutableStateFlow(SortOption.CODE_DESC)
        val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

        private val _isSortSheetVisible = MutableStateFlow(false)
        val isSortSheetVisible: StateFlow<Boolean> = _isSortSheetVisible.asStateFlow()

        private val _selectedStock = MutableStateFlow<StockUiModel?>(null)
        val selectedStock: StateFlow<StockUiModel?> = _selectedStock.asStateFlow()

        init {
            loadStocks()
        }

        fun onIntent(intent: StockListViewIntent) {
            when (intent) {
                StockListViewIntent.OnRefresh -> refresh()
                StockListViewIntent.OnRetryClicked -> loadStocks()
                is StockListViewIntent.OnStockClicked -> onStockClicked(intent.code)
                StockListViewIntent.OnStockDetailDismissed -> _selectedStock.value = null
                StockListViewIntent.OnFilterIconClicked -> _isSortSheetVisible.value = true
                StockListViewIntent.OnSortSheetDismissed -> _isSortSheetVisible.value = false
                is StockListViewIntent.OnSortOptionSelected -> onSortOptionSelected(intent.sortOption)
            }
        }

        private fun loadStocks() {
            _uiState.value = StockListUiState.Loading
            viewModelScope.launch {
                _uiState.value = fetchState(isRefreshing = false)
            }
        }

        private fun refresh() {
            _uiState.value =
                when (val current = _uiState.value) {
                    is StockListUiState.Success -> current.copy(isRefreshing = true)
                    is StockListUiState.Error -> current.copy(isRefreshing = true)
                    StockListUiState.Empty, StockListUiState.Loading -> StockListUiState.Loading
                }
            viewModelScope.launch {
                _uiState.value = fetchState(isRefreshing = false)
            }
        }

        private suspend fun fetchState(isRefreshing: Boolean): StockListUiState =
            getStockListUseCase().fold(
                onSuccess = { stocks -> buildSuccessOrEmptyState(stocks, isRefreshing) },
                onFailure = { error ->
                    StockListUiState.Error(
                        message = error.message.orEmpty(),
                        isRefreshing = isRefreshing,
                    )
                },
            )

        private fun buildSuccessOrEmptyState(
            stocks: List<Stock>,
            isRefreshing: Boolean,
        ): StockListUiState {
            if (stocks.isEmpty()) return StockListUiState.Empty
            val sorted = sortStocks(stocks.map(uiModelMapper::toUiModel), _sortOption.value)
            return StockListUiState.Success(
                stocks = sorted,
                isRefreshing = isRefreshing,
                tradingDate = stocks.first().date?.replace('-', '/'),
            )
        }

        private fun onSortOptionSelected(option: SortOption) {
            _sortOption.value = option
            _isSortSheetVisible.value = false
            val current = _uiState.value
            if (current is StockListUiState.Success) {
                _uiState.value = current.copy(stocks = sortStocks(current.stocks, option))
            }
        }

        private fun sortStocks(
            stocks: List<StockUiModel>,
            option: SortOption,
        ): List<StockUiModel> =
            when (option) {
                SortOption.CODE_DESC -> stocks.sortedByDescending { it.code }
                SortOption.CODE_ASC -> stocks.sortedBy { it.code }
            }

        private fun onStockClicked(code: String) {
            val current = _uiState.value
            if (current is StockListUiState.Success) {
                _selectedStock.value = current.stocks.find { it.code == code }
            }
        }
    }
