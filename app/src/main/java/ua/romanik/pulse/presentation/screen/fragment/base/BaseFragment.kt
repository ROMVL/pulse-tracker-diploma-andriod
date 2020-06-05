package ua.romanik.pulse.presentation.screen.fragment.base

import android.content.Intent
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.ActivityNavigator
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    protected fun navigateToActivity(intent: Intent) {
        ActivityNavigator(requireContext()).apply {
            navigate(
                this.createDestination().setIntent(intent),
                null,
                navOptions { launchSingleTop = true },
                null
            )
        }
    }

    protected fun handleError(throwable: Throwable) {
        throwable.localizedMessage?.let { error ->
            view?.let { currentView ->
                Snackbar.make(currentView, error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

}