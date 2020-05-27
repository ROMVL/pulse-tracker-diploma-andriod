package ua.romanik.pulse.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.romanik.pulse.BuildConfig
import ua.romanik.pulse.data.network.api.UserApi
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { providesHttpClient() }
    single { provideRetrofit(get()) }
    single { get<Retrofit>().create(UserApi::class.java) }
}

fun providesHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    } else {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
    }

    val client = OkHttpClient.Builder()
    client.readTimeout(60, TimeUnit.SECONDS)
    client.writeTimeout(60, TimeUnit.SECONDS)
    client.connectTimeout(60, TimeUnit.SECONDS)
    client.addInterceptor(loggingInterceptor)

    return client.build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.API_ENDPOINT)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}