package ua.romanik.pulse.presentation.screen.fragment.signup

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import ru.ldralighieri.corbind.material.checkedChanges
import ru.ldralighieri.corbind.view.clicks
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
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {

    private val api by inject<UserApi>()
    private val userRepository by inject<UserRepository>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        buttonSignUp.clicks()
            .onEach {
                signUp(
                    UserDataModel(
                        email?.text?.toString(),
                        name?.text?.toString(),
                        secondName?.text?.toString(),
                        age?.text?.toString()?.toInt(),
                        relativeName?.text?.toString(),
                        relativePhone?.text?.toString()
                    )
                )
            }.launchIn(lifecycleScope)

        observeValidState()
    }

    private fun observeValidState() {
        combine(
            email.textChanges()
                .map { isValidEmail(it.toString()) },
            name.textChanges()
                .map { isValidName(it.toString()) },
            secondName.textChanges()
                .map { isValidName(it.toString()) },
            age.textChanges()
                .map {
                    if (it.toString().isNotEmpty()) {
                        isValidAge(it.toString().toInt())
                    } else {
                        false
                    }
                },
            relativeName.textChanges()
                .map { isValidName(it.toString()) },
            relativePhone.textChanges()
                .map { isValidPhone(it.toString()) }
        ) {
            !it.contains(false)
        }.onEach {
            buttonSignUp.isEnabled = it
        }.catch {
            handleError(it)
            this.emit(false)
        }.launchIn(lifecycleScope)
    }

    private suspend fun signUp(userDataModel: UserDataModel) {
        runCatching {
            api.signUp(userDataModel).also { userRepository.saveAuthUserData(it.email.orEmpty()) }
        }.onSuccess {
            withContext(Dispatchers.Main) {
                navigateToActivity(MainActivity.newIntent(requireContext()))
            }
        }.onFailure {
            handleError(it)
        }
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidName(name: String): Boolean = name.length >= 3

    private fun isValidAge(age: Int): Boolean = age in 10..110

    private fun isValidPhone(phone: String): Boolean =
        Patterns.PHONE.matcher(phone).matches()

}
