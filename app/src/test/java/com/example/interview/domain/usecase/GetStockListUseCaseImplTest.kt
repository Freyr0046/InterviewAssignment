package com.example.interview.domain.usecase

import com.example.interview.domain.model.DailyAveragePrice
import com.example.interview.domain.model.DailyTrading
import com.example.interview.domain.model.ValuationRatio
import com.example.interview.domain.repository.StockRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetStockListUseCaseImplTest {
    private val repository: StockRepository = mockk()
    private val useCase = GetStockListUseCaseImpl(repository)

    private val dailyTrading =
        DailyTrading(
            code = "1101",
            name = "台泥",
            openingPrice = 13.87,
            highestPrice = 14.01,
            lowestPrice = 13.76,
            closingPrice = 13.88,
            change = -0.45,
            transaction = 9467L,
            tradeVolume = 39821926L,
            tradeValue = 554902048L,
        )

    @Test
    fun `merges all three sources by code when all succeed`() =
        runTest {
            coEvery { repository.getDailyTrading() } returns Result.success(listOf(dailyTrading))
            coEvery { repository.getDailyAveragePrice() } returns
                Result.success(listOf(DailyAveragePrice(code = "1101", monthlyAveragePrice = 14.16)))
            coEvery { repository.getValuationRatios() } returns
                Result.success(
                    listOf(ValuationRatio(code = "1101", peRatio = 10.89, dividendYield = 7.09, pbRatio = 0.65)),
                )

            val result = useCase()

            assertTrue(result.isSuccess)
            val stock = result.getOrNull()?.single()
            assertEquals("1101", stock?.code)
            assertEquals(14.16, stock?.monthlyAveragePrice)
            assertEquals(10.89, stock?.peRatio)
            assertEquals(7.09, stock?.dividendYield)
            assertEquals(0.65, stock?.pbRatio)
        }

    @Test
    fun `overall result fails when primary source fails`() =
        runTest {
            val failure = IllegalStateException("primary source down")
            coEvery { repository.getDailyTrading() } returns Result.failure(failure)
            coEvery { repository.getDailyAveragePrice() } returns Result.success(emptyList())
            coEvery { repository.getValuationRatios() } returns Result.success(emptyList())

            val result = useCase()

            assertTrue(result.isFailure)
            assertEquals(failure, result.exceptionOrNull())
        }

    @Test
    fun `succeeds with null valuation fields when only the valuation source fails`() =
        runTest {
            coEvery { repository.getDailyTrading() } returns Result.success(listOf(dailyTrading))
            coEvery { repository.getDailyAveragePrice() } returns
                Result.success(listOf(DailyAveragePrice(code = "1101", monthlyAveragePrice = 14.16)))
            coEvery { repository.getValuationRatios() } returns
                Result.failure(IllegalStateException("valuation source down"))

            val result = useCase()

            assertTrue(result.isSuccess)
            val stock = result.getOrNull()?.single()
            assertEquals(14.16, stock?.monthlyAveragePrice)
            assertNull(stock?.peRatio)
            assertNull(stock?.dividendYield)
            assertNull(stock?.pbRatio)
        }

    @Test
    fun `succeeds with all secondary fields null when both secondary sources fail`() =
        runTest {
            coEvery { repository.getDailyTrading() } returns Result.success(listOf(dailyTrading))
            coEvery { repository.getDailyAveragePrice() } returns
                Result.failure(IllegalStateException("average price source down"))
            coEvery { repository.getValuationRatios() } returns
                Result.failure(IllegalStateException("valuation source down"))

            val result = useCase()

            assertTrue(result.isSuccess)
            val stock = result.getOrNull()?.single()
            assertEquals("1101", stock?.code)
            assertNull(stock?.monthlyAveragePrice)
            assertNull(stock?.peRatio)
            assertNull(stock?.dividendYield)
            assertNull(stock?.pbRatio)
        }
}
