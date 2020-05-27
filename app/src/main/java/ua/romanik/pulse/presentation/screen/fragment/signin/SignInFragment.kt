package ua.romanik.pulse.presentation.screen.fragment.signin

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import ru.ldralighieri.corbind.widget.textChanges
import ua.romanik.pulse.R
import ua.romanik.pulse.data.local.UserRepository
import ua.romanik.pulse.data.network.api.UserApi
import ua.romanik.pulse.data.network.model.UserDataModel
import ua.romanik.pulse.presentation.screen.activity.MainActivity
import ua.romanik.pulse.presentation.screen.fragment.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {

    private val api by inject<UserApi>()
    private val userRepository by inject<UserRepository>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        buttonSignIn.setOnClickListener { signIn() }
        buttonSignUp.setOnClickListener {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            )
        }
    }

    private fun observeState() {
        email.textChanges()
            .map { isValidEmail(it.toString()) }
            .onEach { buttonSignIn.isEnabled = it }
            .launchIn(lifecycleScope)
    }

    private fun signIn() {
        lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    api.signIn(
                        email.text?.toString().orEmpty()
                    ).also { userRepository.saveAuthUserData(it.email.orEmpty()) }
                }
            }.onSuccess {
                navigateToActivity(MainActivity.newIntent(requireContext()))
            }.onFailure {
                handleError(it)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

}
