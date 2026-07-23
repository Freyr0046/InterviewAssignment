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
        // Created fresh per toUiModel() call rather than shared instance fields —
        // java.text.DecimalFormat is not thread-safe.
        fun toUiModel(stock: Stock): StockUiModel {
            val priceFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
            val countFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
            return with(stock) {
                StockUiModel(
                    code = code,
                    name = name,
                    openingPrice = openingPrice.formatPrice(priceFormat),
                    closingPrice = closingPrice.formatPrice(priceFormat),
                    closingPriceColor = closingPriceColor(closingPrice, monthlyAveragePrice),
                    highestPrice = highestPrice.formatPrice(priceFormat),
                    lowestPrice = lowestPrice.formatPrice(priceFormat),
                    change = change.formatSignedPrice(priceFormat),
                    changeColor = changeColor(change),
                    monthlyAveragePrice = monthlyAveragePrice.formatPrice(priceFormat),
                    transaction = transaction.formatCount(countFormat),
                    tradeVolume = tradeVolume.formatCount(countFormat),
                    tradeValue = tradeValue.formatCount(countFormat),
                    peRatio = peRatio.formatPrice(priceFormat),
                    dividendYield = dividendYield.formatPrice(priceFormat),
                    pbRatio = pbRatio.formatPrice(priceFormat),
                )
            }
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

        private fun Double?.formatPrice(format: DecimalFormat): String = this?.let { format.format(it) } ?: PLACEHOLDER

        private fun Double?.formatSignedPrice(format: DecimalFormat): String {
            if (this == null) return PLACEHOLDER
            val magnitude = format.format(abs(this))
            return when {
                this > 0 -> "+$magnitude"
                this < 0 -> "-$magnitude"
                else -> magnitude
            }
        }

        private fun Long?.formatCount(format: DecimalFormat): String = this?.let { format.format(it) } ?: PLACEHOLDER
    }
