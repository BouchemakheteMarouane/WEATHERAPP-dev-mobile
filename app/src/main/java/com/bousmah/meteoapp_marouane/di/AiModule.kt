package com.bousmah.meteoapp_marouane.di

import com.bousmah.meteoapp_marouane.data.ai.SkyAnalyzerImpl
import com.bousmah.meteoapp_marouane.domain.ai.SkyAnalyzer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindSkyAnalyzer(
        skyAnalyzerImpl: SkyAnalyzerImpl
    ): SkyAnalyzer
}
