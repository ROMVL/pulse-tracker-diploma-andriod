package ua.romanik.pulse.presentation.screen.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import ua.romanik.pulse.R
import ua.romanik.pulse.data.local.UserRepository
import ua.romanik.pulse.data.network.api.UserApi

class SplashScreenActivity : BaseActivity() {

    private val userDataRepo by inject<UserRepository>()
    private val userApi by inject<UserApi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        checkUser()
    }

    private fun checkUser() {
        lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    delay(1500)
                    userDataRepo.fetchAuthUserData().takeIf {
                        it.isNotEmpty()
                    }?.let { userApi.signIn(it) } ?: throw Throwable("not authorized")
                }
            }.onSuccess {
                navigateTo(MainActivity.newIntent(this@SplashScreenActivity))
            }.onFailure {
                navigateTo(AuthActivity.newIntent(this@SplashScreenActivity))
            }
        }
    }
}
