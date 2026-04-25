package com.virtualszafa.di

import android.content.Context
import androidx.room.Room
import com.virtualszafa.data.local.AppDatabase
import com.virtualszafa.data.local.ProductDao
import com.virtualszafa.data.repository.WardrobeRepository
import com.virtualszafa.labelrecognition.ProductLabelRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "virtualszafa_database"
        )
        .fallbackToDestructiveMigration() // For dev, in prod use migrations
        .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase) = database.productDao()

    @Provides
    @Singleton
    fun provideWardrobeRepository(dao: ProductDao): WardrobeRepository {
        return WardrobeRepository(dao)
    }

    @Provides
    @Singleton
    fun provideProductLabelRecognizer(): ProductLabelRecognizer {
        return ProductLabelRecognizer()
    }
}