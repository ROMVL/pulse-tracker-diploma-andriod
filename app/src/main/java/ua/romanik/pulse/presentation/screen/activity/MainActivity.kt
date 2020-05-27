package ua.romanik.pulse.presentation.screen.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import ua.romanik.pulse.R

class MainActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(
            context,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavView.setupWithNavController(findNavController(R.id.navigation_main))
    }

    override fun onNavigateUp(): Boolean = findNavController(R.id.navigation_main).navigateUp()
}
