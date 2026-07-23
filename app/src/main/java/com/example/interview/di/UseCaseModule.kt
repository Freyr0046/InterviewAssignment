package com.example.interview.di

import com.example.interview.domain.usecase.GetStockListUseCase
import com.example.interview.domain.usecase.GetStockListUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {
    @Binds
    fun bindGetStockListUseCase(impl: GetStockListUseCaseImpl): GetStockListUseCase
}
