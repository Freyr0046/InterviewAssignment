package com.example.interview.ui.model

data class StockUiModel(
    val code: String,
    val name: String,
    val openingPrice: String,
    val closingPrice: String,
    val closingPriceColor: PriceIndicatorColor,
    val highestPrice: String,
    val lowestPrice: String,
    val change: String,
    val changeColor: PriceIndicatorColor,
    val monthlyAveragePrice: String,
    val transaction: String,
    val tradeVolume: String,
    val tradeValue: String,
    val peRatio: String,
    val dividendYield: String,
    val pbRatio: String,
)
