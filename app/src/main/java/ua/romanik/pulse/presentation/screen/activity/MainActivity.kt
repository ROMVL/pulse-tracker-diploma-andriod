package ua.romanik.pulse.presentation.screen.activity

import android.content.Context
import android.content.Intent
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
        startGenerationData()
    }

    override fun onNavigateUp(): Boolean = findNavController(R.id.navigation_main).navigateUp()

    private fun startGenerationData() {
        lifecycleScope.launch {
            val email = userRepository.fetchAuthUserData()
            while (true) {
                generateData().forEach {
                    pulseApi.setPulse(PulseRequestModel(email, it))
                    delay(500)
                }
            }
        }
    }

    private suspend fun generateData(): List<String> = withContext(Dispatchers.Default) {
        return@withContext mutableListOf<String>().apply {
            //addAll(List(40) { Random.nextInt(75, 90).toString() })
            addAll(List(20) { Random.nextInt(130, 170).toString() })
            addAll(List(50) { Random.nextInt(75, 90).toString() })
            addAll(List(20) { Random.nextInt(10, 50).toString() })
        }
    }

}
