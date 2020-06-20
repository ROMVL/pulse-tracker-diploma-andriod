package ua.romanik.pulse.presentation.screen.fragment.hints

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.fragment_hints.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import ua.romanik.pulse.R
import ua.romanik.pulse.data.local.UserRepository
import ua.romanik.pulse.data.network.api.PulseApi
import ua.romanik.pulse.data.network.model.PulseDataModel
import ua.romanik.pulse.presentation.screen.fragment.base.BaseFragment

class HintsFragment : BaseFragment(R.layout.fragment_hints) {

    private val pulseApi by inject<PulseApi>()
    private val userRepository by inject<UserRepository>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchPulse()
    }

    private fun fetchPulse() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    runCatching {
                        pulseApi.getPulse(userRepository.fetchAuthUserData()).last()
                    }.onSuccess {
                        showHint(it)
                    }.onFailure {
                        handleError(it)
                    }
                    delay(2000)
                }
            }
        }
    }

    private suspend fun showHint(pulse: PulseDataModel) {
        pulse.pulseValue?.toInt()?.let { pulseValue ->
            withContext(Dispatchers.Main) {
                when {
                    pulseValue in 0..50 -> {
                        tvHint.setText(R.string.sport)
                        ivHint.setImageResource(R.drawable.ic_gym)
                    }
                    pulseValue >= 130 -> {
                        tvHint.setText(R.string.rest)
                        ivHint.setImageResource(R.drawable.ic_sleep)
                    }
                    else -> {
                        tvHint.setText(R.string.things_are_good)
                        ivHint.setImageResource(R.drawable.ic_nice)
                    }
                }
            }
        }
    }

}