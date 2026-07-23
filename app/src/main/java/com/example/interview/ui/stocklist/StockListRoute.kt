package com.example.interview.ui.stocklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StockListRoute(
    modifier: Modifier = Modifier,
    viewModel: StockListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val isSortSheetVisible by viewModel.isSortSheetVisible.collectAsStateWithLifecycle()

    StockListScreen(
        uiState = uiState,
        sortOption = sortOption,
        isSortSheetVisible = isSortSheetVisible,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
