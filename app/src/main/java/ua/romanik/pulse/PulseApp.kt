package ua.romanik.pulse

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ua.romanik.pulse.di.networkModule
import ua.romanik.pulse.di.repositoryModule

class PulseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PulseApp)
            modules(
                listOf(
                    networkModule,
                    repositoryModule
                )
            )
        }
    }

}