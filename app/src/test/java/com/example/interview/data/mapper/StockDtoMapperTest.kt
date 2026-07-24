package com.example.interview.data.mapper

import com.example.interview.data.remote.dto.BwibbuDto
import com.example.interview.data.remote.dto.StockDayAvgDto
import com.example.interview.data.remote.dto.StockDayDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StockDtoMapperTest {
    @Test
    fun `StockDayDto with valid numeric fields maps to DailyTrading`() {
        val dto =
            StockDayDto(
                code = "1101",
                name = "台泥",
                date = "1150723",
                tradeVolume = "39821926",
                tradeValue = "554902048",
                openingPrice = "13.87",
                highestPrice = "14.01",
                lowestPrice = "13.76",
                closingPrice = "13.88",
                change = "-0.4500",
                transaction = "9467",
            )

        val domain = dto.toDomain()

        assertEquals("1101", domain.code)
        assertEquals("台泥", domain.name)
        assertEquals("2026-07-23", domain.date)
        assertEquals(39821926L, domain.tradeVolume)
        assertEquals(554902048L, domain.tradeValue)
        assertEquals(13.87, domain.openingPrice)
        assertEquals(14.01, domain.highestPrice)
        assertEquals(13.76, domain.lowestPrice)
        assertEquals(13.88, domain.closingPrice)
        assertEquals(-0.45, domain.change)
        assertEquals(9467L, domain.transaction)
    }

    @Test
    fun `StockDayDto with malformed date maps to null`() {
        val dto =
            StockDayDto(
                code = "1101",
                name = "台泥",
                date = "",
                tradeVolume = "39821926",
                tradeValue = "554902048",
                openingPrice = "13.87",
                highestPrice = "14.01",
                lowestPrice = "13.76",
                closingPrice = "13.88",
                change = "-0.4500",
                transaction = "9467",
            )

        assertNull(dto.toDomain().date)
    }

    @Test
    fun `StockDayDto with empty string numeric fields maps to null`() {
        val dto =
            StockDayDto(
                code = "0000",
                name = "停牌股",
                tradeVolume = "",
                tradeValue = "",
                openingPrice = "",
                highestPrice = "",
                lowestPrice = "",
                closingPrice = "",
                change = "",
                transaction = "",
            )

        val domain = dto.toDomain()

        assertNull(domain.tradeVolume)
        assertNull(domain.tradeValue)
        assertNull(domain.openingPrice)
        assertNull(domain.highestPrice)
        assertNull(domain.lowestPrice)
        assertNull(domain.closingPrice)
        assertNull(domain.change)
        assertNull(domain.transaction)
    }

    @Test
    fun `StockDayAvgDto maps monthlyAveragePrice and drops its own closingPrice`() {
        val dto =
            StockDayAvgDto(
                code = "1101",
                name = "台泥",
                closingPrice = "13.88",
                monthlyAveragePrice = "14.16",
            )

        val domain = dto.toDomain()

        assertEquals("1101", domain.code)
        assertEquals(14.16, domain.monthlyAveragePrice)
    }

    @Test
    fun `StockDayAvgDto with empty monthlyAveragePrice maps to null`() {
        val dto =
            StockDayAvgDto(
                code = "1101",
                name = "台泥",
                closingPrice = "13.88",
                monthlyAveragePrice = "",
            )

        assertNull(dto.toDomain().monthlyAveragePrice)
    }

    @Test
    fun `BwibbuDto with valid fields maps to ValuationRatio`() {
        val dto =
            BwibbuDto(
                code = "1102",
                name = "亞泥",
                peRatio = "10.89",
                dividendYield = "7.09",
                pbRatio = "0.65",
            )

        val domain = dto.toDomain()

        assertEquals("1102", domain.code)
        assertEquals(10.89, domain.peRatio)
        assertEquals(7.09, domain.dividendYield)
        assertEquals(0.65, domain.pbRatio)
    }

    @Test
    fun `BwibbuDto with empty peRatio maps to null`() {
        val dto =
            BwibbuDto(
                code = "1101",
                name = "台泥",
                peRatio = "",
                dividendYield = "3.35",
                pbRatio = "0.76",
            )

        assertNull(dto.toDomain().peRatio)
    }
}
