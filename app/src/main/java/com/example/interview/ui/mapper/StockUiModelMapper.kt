package com.example.interview.ui.mapper

import com.example.interview.domain.model.Stock
import com.example.interview.ui.model.PriceIndicatorColor
import com.example.interview.ui.model.StockUiModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

private const val PLACEHOLDER = "-"

class StockUiModelMapper
    @Inject
    constructor() {
        private val priceFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
        private val countFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))

        fun toUiModel(stock: Stock): StockUiModel =
            with(stock) {
                StockUiModel(
                    code = code,
                    name = name,
                    openingPrice = openingPrice.formatPrice(),
                    closingPrice = closingPrice.formatPrice(),
                    closingPriceColor = closingPriceColor(closingPrice, monthlyAveragePrice),
                    highestPrice = highestPrice.formatPrice(),
                    lowestPrice = lowestPrice.formatPrice(),
                    change = change.formatSignedPrice(),
                    changeColor = changeColor(change),
                    monthlyAveragePrice = monthlyAveragePrice.formatPrice(),
                    transaction = transaction.formatCount(),
                    tradeVolume = tradeVolume.formatCount(),
                    tradeValue = tradeValue.formatCount(),
                    peRatio = peRatio.formatPrice(),
                    dividendYield = dividendYield.formatPrice(),
                    pbRatio = pbRatio.formatPrice(),
                )
            }

        private fun closingPriceColor(
            closingPrice: Double?,
            monthlyAveragePrice: Double?,
        ): PriceIndicatorColor {
            if (closingPrice == null || monthlyAveragePrice == null) return PriceIndicatorColor.NEUTRAL
            return when {
                closingPrice > monthlyAveragePrice -> PriceIndicatorColor.UP_RED
                closingPrice < monthlyAveragePrice -> PriceIndicatorColor.DOWN_GREEN
                else -> PriceIndicatorColor.NEUTRAL
            }
        }

        private fun changeColor(change: Double?): PriceIndicatorColor =
            when {
                change == null || change == 0.0 -> PriceIndicatorColor.NEUTRAL
                change > 0 -> PriceIndicatorColor.UP_RED
                else -> PriceIndicatorColor.DOWN_GREEN
            }

        private fun Double?.formatPrice(): String = this?.let { priceFormat.format(it) } ?: PLACEHOLDER

        private fun Double?.formatSignedPrice(): String {
            if (this == null) return PLACEHOLDER
            val magnitude = priceFormat.format(abs(this))
            return when {
                this > 0 -> "+$magnitude"
                this < 0 -> "-$magnitude"
                else -> magnitude
            }
        }

        private fun Long?.formatCount(): String = this?.let { countFormat.format(it) } ?: PLACEHOLDER
    }
