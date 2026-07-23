package com.example.interview.di

import com.example.interview.data.repository.StockRepositoryImpl
import com.example.interview.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindStockRepository(impl: StockRepositoryImpl): StockRepository
}
