package com.onlyreminder.app.data.repository

import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.GroupDao
import com.onlyreminder.app.data.database.dao.MainDao
import com.onlyreminder.app.data.database.dao.TagDao
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
    fun provideContactRepository(
        contactDao: ContactDao,
        groupDao: GroupDao,
        tagDao: TagDao
    ): ContactRepositoryImpl {
        return ContactRepositoryImpl(contactDao, groupDao, tagDao)
    }

    @Provides
    @Singleton
    fun provideMainRepository(mainDao: MainDao): MainRepositoryImpl {
        return MainRepositoryImpl(mainDao)
    }
}
