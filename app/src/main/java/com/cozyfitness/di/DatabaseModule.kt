package com.cozyfitness.di

import android.content.Context
import androidx.room.Room
import com.cozyfitness.data.local.CozyFitnessDatabase
import com.cozyfitness.data.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CozyFitnessDatabase {
        return Room.databaseBuilder(
            context,
            CozyFitnessDatabase::class.java,
            "cozy_fitness_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppRepository(database: CozyFitnessDatabase): AppRepository {
        return AppRepository(database)
    }
}