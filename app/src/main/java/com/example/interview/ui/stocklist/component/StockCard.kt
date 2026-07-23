package com.example.interview.ui.stocklist.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.interview.domain.model.Stock
import com.example.interview.ui.mapper.StockUiModelMapper
import com.example.interview.ui.model.StockUiModel
import com.example.interview.ui.theme.InterviewTheme
import com.example.interview.ui.theme.asColor

private data class ValueEntry(
    val label: String,
    val value: String,
    val color: Color = Color.Unspecified,
)

@Composable
fun StockCard(
    stock: StockUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stock.code, style = MaterialTheme.typography.labelMedium)
            Text(text = stock.name, style = MaterialTheme.typography.titleMedium)

            LabeledValueRow(
                ValueEntry("開盤價", stock.openingPrice),
                ValueEntry("收盤價", stock.closingPrice, stock.closingPriceColor.asColor()),
            )
            LabeledValueRow(
                ValueEntry("最高價", stock.highestPrice),
                ValueEntry("最低價", stock.lowestPrice),
            )
            LabeledValueRow(
                ValueEntry("漲跌價差", stock.change, stock.changeColor.asColor()),
                ValueEntry("月平均價", stock.monthlyAveragePrice),
            )
            LabeledValueRow(
                ValueEntry("成交筆數", stock.transaction),
                ValueEntry("成交股數", stock.tradeVolume),
                ValueEntry("成交金額", stock.tradeValue),
            )
        }
    }
}

@Composable
private fun LabeledValueRow(vararg entries: ValueEntry) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        entries.forEach { entry ->
            LabeledValue(
                label = entry.label,
                value = entry.value,
                valueColor = entry.color,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LabeledValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = valueColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun StockCardPreview() {
    val stock =
        StockUiModelMapper().toUiModel(
            Stock(
                code = "1101",
                name = "台泥",
                openingPrice = 13.87,
                closingPrice = 13.88,
                highestPrice = 14.01,
                lowestPrice = 13.76,
                change = -0.45,
                monthlyAveragePrice = 14.16,
                transaction = 9467L,
                tradeVolume = 39821926L,
                tradeValue = 554902048L,
                peRatio = 10.89,
                dividendYield = 7.09,
                pbRatio = 0.65,
            ),
        )
    InterviewTheme {
        StockCard(stock = stock, onClick = {})
    }
}
