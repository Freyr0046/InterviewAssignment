package com.example.interview.domain.model

data class DailyTrading(
    val code: String,
    val name: String,
    val openingPrice: Double?,
    val highestPrice: Double?,
    val lowestPrice: Double?,
    val closingPrice: Double?,
    val change: Double?,
    val transaction: Long?,
    val tradeVolume: Long?,
    val tradeValue: Long?,
)
