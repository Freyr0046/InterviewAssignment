package com.example.interview.data.repository

import com.example.interview.data.mapper.toDomain
import com.example.interview.data.remote.TwseApiService
import com.example.interview.data.util.retryWithExponentialBackoff
import com.example.interview.domain.model.DailyAveragePrice
import com.example.interview.domain.model.DailyTrading
import com.example.interview.domain.model.ValuationRatio
import com.example.interview.domain.repository.StockRepository
import javax.inject.Inject

class StockRepositoryImpl
    @Inject
    constructor(
        private val apiService: TwseApiService,
    ) : StockRepository {
        override suspend fun getDailyTrading(): Result<List<DailyTrading>> =
            runCatching {
                retryWithExponentialBackoff { apiService.getStockDayAll() }.map { it.toDomain() }
            }

        override suspend fun getDailyAveragePrice(): Result<List<DailyAveragePrice>> =
            runCatching {
                retryWithExponentialBackoff { apiService.getStockDayAvgAll() }.map { it.toDomain() }
            }

        override suspend fun getValuationRatios(): Result<List<ValuationRatio>> =
            runCatching {
                retryWithExponentialBackoff { apiService.getBwibbuAll() }.map { it.toDomain() }
            }
    }
