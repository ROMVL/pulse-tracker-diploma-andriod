package ua.romanik.pulse.presentation.screen.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import ua.romanik.pulse.R

class AuthActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(
            context,
            AuthActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    override fun onNavigateUp(): Boolean = findNavController(R.id.navigation_auth).navigateUp()

}
