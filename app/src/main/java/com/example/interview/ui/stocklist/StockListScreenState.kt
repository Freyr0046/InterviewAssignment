package com.example.interview.ui.stocklist

import androidx.compose.runtime.Immutable
import com.example.interview.ui.model.SortOption
import com.example.interview.ui.model.StockUiModel

@Immutable
data class StockListScreenState(
    val uiState: StockListUiState,
    val sortOption: SortOption,
    val isSortSheetVisible: Boolean,
    val selectedStock: StockUiModel?,
)
