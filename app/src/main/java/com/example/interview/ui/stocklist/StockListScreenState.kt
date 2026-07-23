package com.example.interview.ui.stocklist

import com.example.interview.ui.model.SortOption
import com.example.interview.ui.model.StockUiModel

data class StockListScreenState(
    val uiState: StockListUiState,
    val sortOption: SortOption,
    val isSortSheetVisible: Boolean,
    val selectedStock: StockUiModel?,
)
