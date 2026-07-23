package com.example.interview.domain.usecase

import com.example.interview.domain.model.DailyAveragePrice
import com.example.interview.domain.model.DailyTrading
import com.example.interview.domain.model.Stock
import com.example.interview.domain.model.ValuationRatio
import com.example.interview.domain.repository.StockRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetStockListUseCaseImpl
    @Inject
    constructor(
        private val repository: StockRepository,
    ) : GetStockListUseCase {
        override suspend fun invoke(): Result<List<Stock>> =
            coroutineScope {
                val dailyTradingDeferred = async { repository.getDailyTrading() }
                val averagePriceDeferred = async { repository.getDailyAveragePrice() }
                val valuationRatioDeferred = async { repository.getValuationRatios() }

                val dailyTradingResult = dailyTradingDeferred.await()
                val averagePriceResult = averagePriceDeferred.await()
                val valuationRatioResult = valuationRatioDeferred.await()

                dailyTradingResult.fold(
                    onSuccess = { dailyTradingList ->
                        val averagePriceByCode =
                            averagePriceResult.getOrDefault(emptyList()).associateBy { it.code }
                        val valuationRatioByCode =
                            valuationRatioResult.getOrDefault(emptyList()).associateBy { it.code }
                        Result.success(
                            dailyTradingList.map { trading ->
                                trading.toStock(
                                    averagePriceByCode[trading.code],
                                    valuationRatioByCode[trading.code],
                                )
                            },
                        )
                    },
                    onFailure = { Result.failure(it) },
                )
            }
    }

private fun DailyTrading.toStock(
    averagePrice: DailyAveragePrice?,
    valuationRatio: ValuationRatio?,
): Stock =
    Stock(
        code = code,
        name = name,
        openingPrice = openingPrice,
        closingPrice = closingPrice,
        highestPrice = highestPrice,
        lowestPrice = lowestPrice,
        change = change,
        monthlyAveragePrice = averagePrice?.monthlyAveragePrice,
        transaction = transaction,
        tradeVolume = tradeVolume,
        tradeValue = tradeValue,
        peRatio = valuationRatio?.peRatio,
        dividendYield = valuationRatio?.dividendYield,
        pbRatio = valuationRatio?.pbRatio,
    )
