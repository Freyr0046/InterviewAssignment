package com.example.interview.domain.model

data class Stock(
    val code: String,
    val name: String,
    val openingPrice: Double?,
    val closingPrice: Double?,
    val highestPrice: Double?,
    val lowestPrice: Double?,
    val change: Double?,
    val monthlyAveragePrice: Double?,
    val transaction: Long?,
    val tradeVolume: Long?,
    val tradeValue: Long?,
    val peRatio: Double?,
    val dividendYield: Double?,
    val pbRatio: Double?,
)
