package ua.romanik.pulse.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ua.romanik.pulse.data.local.UserRepository

private const val AUTH_USER_DATA_SHARED_PREFERENCES = "user_data_repo"

val repositoryModule = module {
    single { UserRepository(provideSharedPreferences(androidApplication())) }
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(AUTH_USER_DATA_SHARED_PREFERENCES, Context.MODE_PRIVATE)
}