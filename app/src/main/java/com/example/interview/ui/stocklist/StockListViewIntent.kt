package com.example.interview.ui.stocklist

import com.example.interview.ui.model.SortOption

sealed interface StockListViewIntent {
    data object OnRefresh : StockListViewIntent

    data object OnRetryClicked : StockListViewIntent

    data class OnStockClicked(
        val code: String,
    ) : StockListViewIntent

    data object OnStockDetailDismissed : StockListViewIntent

    data object OnFilterIconClicked : StockListViewIntent

    data object OnSortSheetDismissed : StockListViewIntent

    data class OnSortOptionSelected(
        val sortOption: SortOption,
    ) : StockListViewIntent
}
