package com.example.interview.ui.mapper

import com.example.interview.domain.model.Stock
import com.example.interview.ui.model.PriceIndicatorColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StockUiModelMapperTest {
    private val mapper = StockUiModelMapper()

    private fun baseStock() =
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
        )

    @Test
    fun `formats prices with 2 decimals and counts with thousand separators`() {
        val uiModel = mapper.toUiModel(baseStock())

        assertEquals("13.87", uiModel.openingPrice)
        assertEquals("13.88", uiModel.closingPrice)
        assertEquals("9,467", uiModel.transaction)
        assertEquals("39,821,926", uiModel.tradeVolume)
        assertEquals("554,902,048", uiModel.tradeValue)
    }

    @Test
    fun `negative change formats with minus sign and DOWN_GREEN color`() {
        val uiModel = mapper.toUiModel(baseStock().copy(change = -0.45))

        assertEquals("-0.45", uiModel.change)
        assertEquals(PriceIndicatorColor.DOWN_GREEN, uiModel.changeColor)
    }

    @Test
    fun `positive change formats with plus sign and UP_RED color`() {
        val uiModel = mapper.toUiModel(baseStock().copy(change = 0.45))

        assertEquals("+0.45", uiModel.change)
        assertEquals(PriceIndicatorColor.UP_RED, uiModel.changeColor)
    }

    @Test
    fun `zero change has no sign and NEUTRAL color`() {
        val uiModel = mapper.toUiModel(baseStock().copy(change = 0.0))

        assertEquals("0.00", uiModel.change)
        assertEquals(PriceIndicatorColor.NEUTRAL, uiModel.changeColor)
    }

    @Test
    fun `closingPrice above monthlyAveragePrice is UP_RED`() {
        val uiModel = mapper.toUiModel(baseStock().copy(closingPrice = 15.0, monthlyAveragePrice = 14.0))

        assertEquals(PriceIndicatorColor.UP_RED, uiModel.closingPriceColor)
    }

    @Test
    fun `closingPrice below monthlyAveragePrice is DOWN_GREEN`() {
        val uiModel = mapper.toUiModel(baseStock().copy(closingPrice = 13.0, monthlyAveragePrice = 14.0))

        assertEquals(PriceIndicatorColor.DOWN_GREEN, uiModel.closingPriceColor)
    }

    @Test
    fun `closingPrice equal to monthlyAveragePrice is NEUTRAL`() {
        val uiModel = mapper.toUiModel(baseStock().copy(closingPrice = 14.0, monthlyAveragePrice = 14.0))

        assertEquals(PriceIndicatorColor.NEUTRAL, uiModel.closingPriceColor)
    }

    @Test
    fun `null fields map to placeholder dash and NEUTRAL colors`() {
        val uiModel =
            mapper.toUiModel(
                baseStock().copy(
                    monthlyAveragePrice = null,
                    peRatio = null,
                    dividendYield = null,
                    pbRatio = null,
                ),
            )

        assertEquals("-", uiModel.monthlyAveragePrice)
        assertEquals("-", uiModel.peRatio)
        assertEquals("-", uiModel.dividendYield)
        assertEquals("-", uiModel.pbRatio)
        assertEquals(PriceIndicatorColor.NEUTRAL, uiModel.closingPriceColor)
    }
}
