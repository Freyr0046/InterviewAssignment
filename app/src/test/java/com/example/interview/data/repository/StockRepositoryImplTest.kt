package com.example.interview.data.repository

import com.example.interview.data.remote.TwseApiService
import com.example.interview.data.remote.dto.BwibbuDto
import com.example.interview.data.remote.dto.StockDayAvgDto
import com.example.interview.data.remote.dto.StockDayDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class StockRepositoryImplTest {
    private val apiService: TwseApiService = mockk()
    private val repository = StockRepositoryImpl(apiService)

    private fun notFoundException(): HttpException = HttpException(Response.error<Any>(404, "".toResponseBody()))

    @Test
    fun `getDailyTrading maps successful response to domain models`() =
        runTest {
            coEvery { apiService.getStockDayAll() } returns
                listOf(
                    StockDayDto(
                        code = "1101",
                        name = "台泥",
                        tradeVolume = "39821926",
                        tradeValue = "554902048",
                        openingPrice = "13.87",
                        highestPrice = "14.01",
                        lowestPrice = "13.76",
                        closingPrice = "13.88",
                        change = "-0.45",
                        transaction = "9467",
                    ),
                )

            val result = repository.getDailyTrading()

            assertTrue(result.isSuccess)
            assertEquals("1101", result.getOrNull()?.single()?.code)
            assertEquals(-0.45, result.getOrNull()?.single()?.change)
        }

    @Test
    fun `getDailyTrading wraps non-retryable failure as Result failure`() =
        runTest {
            coEvery { apiService.getStockDayAll() } throws notFoundException()

            val result = repository.getDailyTrading()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is HttpException)
        }

    @Test
    fun `getDailyTrading retries on IOException then succeeds`() =
        runTest {
            var attempt = 0
            coEvery { apiService.getStockDayAll() } answers {
                attempt++
                if (attempt < 2) throw IOException("network blip")
                listOf(
                    StockDayDto(
                        code = "1101",
                        name = "台泥",
                        tradeVolume = "1",
                        tradeValue = "1",
                        openingPrice = "1",
                        highestPrice = "1",
                        lowestPrice = "1",
                        closingPrice = "1",
                        change = "0",
                        transaction = "1",
                    ),
                )
            }

            val result = repository.getDailyTrading()

            assertTrue(result.isSuccess)
            assertEquals(2, attempt)
        }

    @Test
    fun `getDailyTrading rethrows CancellationException instead of wrapping it`() =
        runTest {
            coEvery { apiService.getStockDayAll() } throws CancellationException("scope cancelled")

            var thrown: Throwable? = null
            try {
                repository.getDailyTrading()
            } catch (e: CancellationException) {
                thrown = e
            }

            assertTrue(thrown is CancellationException)
        }

    @Test
    fun `getDailyAveragePrice maps successful response to domain models`() =
        runTest {
            coEvery { apiService.getStockDayAvgAll() } returns
                listOf(
                    StockDayAvgDto(
                        code = "1101",
                        name = "台泥",
                        closingPrice = "13.88",
                        monthlyAveragePrice = "14.16",
                    ),
                )

            val result = repository.getDailyAveragePrice()

            assertTrue(result.isSuccess)
            assertEquals(14.16, result.getOrNull()?.single()?.monthlyAveragePrice)
        }

    @Test
    fun `getDailyAveragePrice wraps failure as Result failure without affecting other sources`() =
        runTest {
            coEvery { apiService.getStockDayAvgAll() } throws notFoundException()

            val result = repository.getDailyAveragePrice()

            assertTrue(result.isFailure)
        }

    @Test
    fun `getValuationRatios maps successful response to domain models`() =
        runTest {
            coEvery { apiService.getBwibbuAll() } returns
                listOf(
                    BwibbuDto(
                        code = "1102",
                        name = "亞泥",
                        peRatio = "10.89",
                        dividendYield = "7.09",
                        pbRatio = "0.65",
                    ),
                )

            val result = repository.getValuationRatios()

            assertTrue(result.isSuccess)
            assertEquals(10.89, result.getOrNull()?.single()?.peRatio)
        }

    @Test
    fun `getValuationRatios wraps failure as Result failure`() =
        runTest {
            coEvery { apiService.getBwibbuAll() } throws notFoundException()

            val result = repository.getValuationRatios()

            assertTrue(result.isFailure)
        }
}
