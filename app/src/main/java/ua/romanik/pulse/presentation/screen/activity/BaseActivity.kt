package ua.romanik.pulse.presentation.screen.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.navOptions

abstract class BaseActivity : AppCompatActivity() {

    protected fun navigateTo(intent: Intent) {
        ActivityNavigator(this).apply {
            navigate(
                this.createDestination().setIntent(intent),
                null,
                navOptions { launchSingleTop = true },
                null
            )
        }
    }

}