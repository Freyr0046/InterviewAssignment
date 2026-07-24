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
        date = date.rocDateToIso(),
        openingPrice = openingPrice.toDoubleOrNull(),
        highestPrice = highestPrice.toDoubleOrNull(),
        lowestPrice = lowestPrice.toDoubleOrNull(),
        closingPrice = closingPrice.toDoubleOrNull(),
        change = change.toDoubleOrNull(),
        transaction = transaction.toLongOrNull(),
        tradeVolume = tradeVolume.toLongOrNull(),
        tradeValue = tradeValue.toLongOrNull(),
    )

private const val ROC_DATE_LENGTH = 7
private const val ROC_YEAR_LENGTH = 3
private const val ROC_MONTH_LENGTH = 2
private const val ROC_ERA_OFFSET = 1911

// "Date" is undocumented in the TWSE spec but confirmed present on every response (see
// docs/specs §Task 1): ROC calendar, e.g. "1150723" = ROC year 115 + month 07 + day 23.
private fun String.rocDateToIso(): String? {
    if (length != ROC_DATE_LENGTH) return null
    val rocYear = substring(0, ROC_YEAR_LENGTH).toIntOrNull()
    val month = substring(ROC_YEAR_LENGTH, ROC_YEAR_LENGTH + ROC_MONTH_LENGTH).toIntOrNull()
    val day = substring(ROC_YEAR_LENGTH + ROC_MONTH_LENGTH).toIntOrNull()
    return if (rocYear != null && month != null && day != null) {
        "%04d-%02d-%02d".format(rocYear + ROC_ERA_OFFSET, month, day)
    } else {
        null
    }
}

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
