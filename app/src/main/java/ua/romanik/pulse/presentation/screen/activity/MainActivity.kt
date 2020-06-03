package ua.romanik.pulse.presentation.screen.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import ua.romanik.pulse.R
import ua.romanik.pulse.data.local.UserRepository
import ua.romanik.pulse.data.network.api.PulseApi
import ua.romanik.pulse.data.network.model.PulseRequestModel
import kotlin.random.Random

class MainActivity : BaseActivity() {

    private val pulseApi by inject<PulseApi>()
    private val userRepository by inject<UserRepository>()

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
        startGenerationPulse {
            Random.nextInt(75, 90).toString()
        }

        startGenerationPulse(180000) {
            Random.nextInt(110, 170).toString()
        }

        startGenerationPulse(120000) {
            Random.nextInt(30, 50).toString()
        }
    }

    override fun onNavigateUp(): Boolean = findNavController(R.id.navigation_main).navigateUp()

    private fun startGenerationPulse(
        delay: Long = 500,
        pulse: () -> String
    ) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val email = userRepository.fetchAuthUserData()
                while (true) {
                    pulseApi.setPulse(
                        PulseRequestModel(
                            email,
                            pulse.invoke()
                        )
                    )
                    delay(delay)
                }
            }
        }
    }

}
