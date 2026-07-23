package com.example.interview.data.remote

import com.example.interview.data.remote.dto.BwibbuDto
import com.example.interview.data.remote.dto.StockDayAvgDto
import com.example.interview.data.remote.dto.StockDayDto
import retrofit2.http.GET

interface TwseApiService {
    @GET("exchangeReport/BWIBBU_ALL")
    suspend fun getBwibbuAll(): List<BwibbuDto>

    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockDayAvgAll(): List<StockDayAvgDto>

    @GET("exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayAll(): List<StockDayDto>
}
