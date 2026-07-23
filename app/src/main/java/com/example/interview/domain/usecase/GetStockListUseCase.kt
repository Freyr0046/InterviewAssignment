package com.example.interview.domain.usecase

import com.example.interview.domain.model.Stock

interface GetStockListUseCase {
    suspend operator fun invoke(): Result<List<Stock>>
}
