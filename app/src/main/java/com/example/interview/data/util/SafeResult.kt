package com.example.interview.data.util

import kotlinx.coroutines.CancellationException

/**
 * Like [runCatching], but rethrows [CancellationException] instead of wrapping it into
 * [Result.failure] — swallowing cancellation breaks structured concurrency.
 */
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
