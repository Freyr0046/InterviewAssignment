package com.example.interview.ui.stocklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.interview.R
import com.example.interview.ui.model.SortOption
import com.example.interview.ui.model.StockUiModel
import com.example.interview.ui.stocklist.component.SortBottomSheet
import com.example.interview.ui.stocklist.component.StockCard
import com.example.interview.ui.stocklist.component.StockListEmptyView
import com.example.interview.ui.stocklist.component.StockListErrorView
import com.example.interview.ui.stocklist.component.StockListLoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(
    uiState: StockListUiState,
    sortOption: SortOption,
    isSortSheetVisible: Boolean,
    onIntent: (StockListViewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.stock_list_title)) },
                actions = {
                    IconButton(onClick = { onIntent(StockListViewIntent.OnFilterIconClicked) }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.stock_list_sort_icon_content_description),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                StockListUiState.Loading -> StockListLoadingSkeleton(modifier = Modifier.fillMaxSize())
                StockListUiState.Empty -> StockListEmptyView(modifier = Modifier.fillMaxSize())
                is StockListUiState.Error ->
                    StockListErrorView(
                        message = uiState.message,
                        onRetryClicked = { onIntent(StockListViewIntent.OnRetryClicked) },
                        modifier = Modifier.fillMaxSize(),
                    )
                is StockListUiState.Success ->
                    StockList(
                        stocks = uiState.stocks,
                        onStockClicked = { code -> onIntent(StockListViewIntent.OnStockClicked(code)) },
                    )
            }
        }
    }

    if (isSortSheetVisible) {
        SortBottomSheet(
            selectedOption = sortOption,
            onOptionSelected = { onIntent(StockListViewIntent.OnSortOptionSelected(it)) },
            onDismiss = { onIntent(StockListViewIntent.OnSortSheetDismissed) },
        )
    }
}

@Composable
private fun StockList(
    stocks: List<StockUiModel>,
    onStockClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = stocks, key = { it.code }) { stock ->
            StockCard(stock = stock, onClick = { onStockClicked(stock.code) })
        }
    }
}
