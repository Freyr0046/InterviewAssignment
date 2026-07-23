package com.example.interview.data.util

import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RetryPolicyTest {
    private fun httpException(code: Int): HttpException =
        HttpException(Response.error<Any>(code, "".toResponseBody("text/plain".toMediaType())))

    private suspend fun <T> runAndCountAttempts(block: suspend () -> T): Pair<Result<T>, Int> {
        var callCount = 0
        val result =
            runCatching {
                retryWithExponentialBackoff {
                    callCount++
                    block()
                }
            }
        return result to callCount
    }

    @Test
    fun `succeeds without retry when block does not throw`() =
        runTest {
            val (result, callCount) = runAndCountAttempts { "ok" }

            assertEquals("ok", result.getOrNull())
            assertEquals(1, callCount)
        }

    @Test
    fun `retries on IOException and succeeds once it stops throwing`() =
        runTest {
            var attempt = 0
            val (result, callCount) =
                runAndCountAttempts {
                    attempt++
                    if (attempt < 3) throw IOException("network blip")
                    "ok"
                }

            assertEquals("ok", result.getOrNull())
            assertEquals(3, callCount)
        }

    @Test
    fun `retries on 5xx HttpException and throws after exhausting all attempts`() =
        runTest {
            val (result, callCount) =
                runAndCountAttempts {
                    throw httpException(503)
                }

            assert(result.exceptionOrNull() is HttpException)
            assertEquals(4, callCount)
        }

    @Test
    fun `does not retry on 4xx HttpException`() =
        runTest {
            val (result, callCount) =
                runAndCountAttempts {
                    throw httpException(404)
                }

            assert(result.exceptionOrNull() is HttpException)
            assertEquals(1, callCount)
        }
}
