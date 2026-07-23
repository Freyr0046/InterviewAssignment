package com.example.interview.ui.stocklist.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.interview.R
import com.example.interview.ui.model.StockUiModel

@Composable
fun StockDetailDialog(
    stock: StockUiModel,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.stock_detail_close))
            }
        },
        title = { Text(text = "${stock.name}（${stock.code}）") },
        text = {
            Column {
                DetailRow(stringResource(R.string.stock_detail_pe_ratio), stock.peRatio)
                DetailRow(stringResource(R.string.stock_detail_dividend_yield), stock.dividendYield)
                DetailRow(stringResource(R.string.stock_detail_pb_ratio), stock.pbRatio)
            }
        },
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, modifier = Modifier.weight(1f))
        Text(text = value)
    }
}
