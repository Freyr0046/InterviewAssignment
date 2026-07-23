package com.example.interview.domain.repository

import com.example.interview.domain.model.DailyAveragePrice
import com.example.interview.domain.model.DailyTrading
import com.example.interview.domain.model.ValuationRatio

interface StockRepository {
    suspend fun getDailyTrading(): Result<List<DailyTrading>>

    suspend fun getDailyAveragePrice(): Result<List<DailyAveragePrice>>

    suspend fun getValuationRatios(): Result<List<ValuationRatio>>
}
