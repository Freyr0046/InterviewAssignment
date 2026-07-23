package com.example.interview.data.util

import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

private const val HTTP_SERVER_ERROR_MIN = 500
private const val HTTP_SERVER_ERROR_MAX = 599
private val RETRYABLE_HTTP_STATUS_CODES = HTTP_SERVER_ERROR_MIN..HTTP_SERVER_ERROR_MAX

suspend fun <T> retryWithExponentialBackoff(
    times: Int = 3,
    initialDelayMs: Long = 300L,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelayMs
    repeat(times) {
        try {
            return block()
        } catch (ignore: IOException) {
            // retryable network failure — fall through to delay & retry
        } catch (e: HttpException) {
            if (e.code() !in RETRYABLE_HTTP_STATUS_CODES) throw e
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong()
    }
    return block()
}
