package com.example.interview.data.mapper

import com.example.interview.data.remote.dto.BwibbuDto
import com.example.interview.data.remote.dto.StockDayAvgDto
import com.example.interview.data.remote.dto.StockDayDto
import com.example.interview.domain.model.DailyAveragePrice
import com.example.interview.domain.model.DailyTrading
import com.example.interview.domain.model.ValuationRatio

fun StockDayDto.toDomain(): DailyTrading =
    DailyTrading(
        code = code,
        name = name,
        openingPrice = openingPrice.toDoubleOrNull(),
        highestPrice = highestPrice.toDoubleOrNull(),
        lowestPrice = lowestPrice.toDoubleOrNull(),
        closingPrice = closingPrice.toDoubleOrNull(),
        change = change.toDoubleOrNull(),
        transaction = transaction.toLongOrNull(),
        tradeVolume = tradeVolume.toLongOrNull(),
        tradeValue = tradeValue.toLongOrNull(),
    )

// StockDayAvgDto.closingPrice is intentionally dropped — DailyTrading's closingPrice
// (the primary source) is the single source of truth, see docs/specs Phase 3.
fun StockDayAvgDto.toDomain(): DailyAveragePrice =
    DailyAveragePrice(
        code = code,
        monthlyAveragePrice = monthlyAveragePrice.toDoubleOrNull(),
    )

fun BwibbuDto.toDomain(): ValuationRatio =
    ValuationRatio(
        code = code,
        peRatio = peRatio.toDoubleOrNull(),
        dividendYield = dividendYield.toDoubleOrNull(),
        pbRatio = pbRatio.toDoubleOrNull(),
    )
