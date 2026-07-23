package com.example.interview.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.example.interview.ui.model.PriceIndicatorColor

val StockUpRed = Color(0xFFD32F2F)
val StockDownGreen = Color(0xFF2E7D32)

@Composable
@ReadOnlyComposable
fun PriceIndicatorColor.asColor(): Color =
    when (this) {
        PriceIndicatorColor.UP_RED -> StockUpRed
        PriceIndicatorColor.DOWN_GREEN -> StockDownGreen
        PriceIndicatorColor.NEUTRAL -> LocalContentColor.current
    }
