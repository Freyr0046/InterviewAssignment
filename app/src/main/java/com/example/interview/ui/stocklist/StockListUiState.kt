package com.example.interview.ui.stocklist

import com.example.interview.ui.model.StockUiModel

sealed interface StockListUiState {
    data object Loading : StockListUiState

    data class Success(
        val stocks: List<StockUiModel>,
        val isRefreshing: Boolean = false,
    ) : StockListUiState

    data class Error(
        val message: String,
        val isRefreshing: Boolean = false,
    ) : StockListUiState

    data object Empty : StockListUiState
}

val StockListUiState.isRefreshing: Boolean
    get() =
        when (this) {
            is StockListUiState.Success -> isRefreshing
            is StockListUiState.Error -> isRefreshing
            StockListUiState.Loading, StockListUiState.Empty -> false
        }
