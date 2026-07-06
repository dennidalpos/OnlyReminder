package com.onlyreminder.app.data.repository

import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.MainDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideContactRepository(contactDao: ContactDao): ContactRepositoryImpl {
        return ContactRepositoryImpl(contactDao)
    }

    @Provides
    @Singleton
    fun provideMainRepository(mainDao: MainDao): MainRepositoryImpl {
        return MainRepositoryImpl(mainDao)
    }
}
