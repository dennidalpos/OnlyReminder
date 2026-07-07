package com.onlyreminder.app.features.whatsapp.di

import com.onlyreminder.app.features.whatsapp.data.WhatsAppApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WhatsAppModule {

    private const val BASE_URL = "https://graph.facebook.com/v21.0/"

    @Provides
    @Singleton
    fun provideWhatsAppApiService(): WhatsAppApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(WhatsAppApiService::class.java)
    }
}
