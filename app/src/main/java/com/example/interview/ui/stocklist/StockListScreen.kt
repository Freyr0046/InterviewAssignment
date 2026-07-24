package com.example.interview.ui.stocklist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.interview.R
import com.example.interview.ui.model.SortOption
import com.example.interview.ui.model.StockUiModel
import com.example.interview.ui.stocklist.component.SortBottomSheet
import com.example.interview.ui.stocklist.component.StockCard
import com.example.interview.ui.stocklist.component.StockDetailDialog
import com.example.interview.ui.stocklist.component.StockListEmptyView
import com.example.interview.ui.stocklist.component.StockListErrorView
import com.example.interview.ui.stocklist.component.StockListLoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(
    screenState: StockListScreenState,
    onIntent: (StockListViewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    StockListTitle(tradingDate = (screenState.uiState as? StockListUiState.Success)?.tradingDate)
                },
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
        PullToRefreshBox(
            isRefreshing = screenState.uiState.isRefreshing,
            onRefresh = { onIntent(StockListViewIntent.OnRefresh) },
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        ) {
            when (val uiState = screenState.uiState) {
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
                        sortOption = screenState.sortOption,
                        onStockClicked = { code -> onIntent(StockListViewIntent.OnStockClicked(code)) },
                    )
            }
        }
    }

    if (screenState.isSortSheetVisible) {
        SortBottomSheet(
            selectedOption = screenState.sortOption,
            onOptionSelected = { onIntent(StockListViewIntent.OnSortOptionSelected(it)) },
            onDismiss = { onIntent(StockListViewIntent.OnSortSheetDismissed) },
        )
    }

    screenState.selectedStock?.let { stock ->
        StockDetailDialog(
            stock = stock,
            onDismiss = { onIntent(StockListViewIntent.OnStockDetailDismissed) },
        )
    }
}

@Composable
private fun StockListTitle(tradingDate: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(R.string.stock_list_title))
        tradingDate?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = it, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun StockList(
    stocks: List<StockUiModel>,
    sortOption: SortOption,
    onStockClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(sortOption) {
        listState.scrollToItem(0)
    }
    LazyColumn(state = listState, modifier = modifier.fillMaxSize()) {
        items(items = stocks, key = { it.code }) { stock ->
            StockCard(stock = stock, onClick = { onStockClicked(stock.code) })
        }
    }
}
