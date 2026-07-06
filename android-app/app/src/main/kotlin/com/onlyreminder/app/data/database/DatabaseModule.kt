package com.onlyreminder.app.data.database

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.onlyreminder.app.core.security.SecurePrefs
import com.onlyreminder.app.data.database.dao.ContactDao
import com.onlyreminder.app.data.database.dao.MainDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.security.SecureRandom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val KEY_DB_PASSPHRASE = "db_passphrase"

    @Provides
    @Singleton
    fun providePassphrase(@SecurePrefs sharedPreferences: SharedPreferences): ByteArray {
        val storedPassphrase = sharedPreferences.getString(KEY_DB_PASSPHRASE, null)
        return if (storedPassphrase != null) {
            android.util.Base64.decode(storedPassphrase, android.util.Base64.DEFAULT)
        } else {
            val newPassphrase = ByteArray(32).apply { SecureRandom().nextBytes(this) }
            sharedPreferences.edit()
                .putString(
                    KEY_DB_PASSPHRASE,
                    android.util.Base64.encodeToString(newPassphrase, android.util.Base64.DEFAULT)
                )
                .apply()
            newPassphrase
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        passphrase: ByteArray
    ): AppDatabase {
        val factory = SupportOpenHelperFactory(passphrase)
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration() // Only for early development
            .addCallback(object : Room.Database.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // We can't use DAO here directly because the DB is being created.
                    // But we can use raw SQL or just let it be empty and handle it in a Repository/ViewModel.
                    // Better to do it in a Repository or a specialized DataInitializer.
                }
            })
            .build()
    }

    @Provides
    fun provideContactDao(db: AppDatabase): ContactDao = db.contactDao()

    @Provides
    fun provideMainDao(db: AppDatabase): MainDao = db.mainDao()
}
