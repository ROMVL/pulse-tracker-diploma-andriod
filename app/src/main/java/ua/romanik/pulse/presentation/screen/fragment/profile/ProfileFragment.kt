package ua.romanik.pulse.presentation.screen.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import ua.romanik.pulse.R
import ua.romanik.pulse.data.local.UserRepository
import ua.romanik.pulse.data.network.api.UserApi
import ua.romanik.pulse.data.network.model.UserDataModel
import ua.romanik.pulse.presentation.screen.fragment.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val api by inject<UserApi>()
    private val userRepository by inject<UserRepository>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    userRepository.fetchAuthUserData()
                        .takeIf { it.isNotEmpty() }
                        ?.let { api.getUserProfile(it) } ?: throw Throwable("user not exist")
                }
            }.onSuccess {
                setUpProfile(it)
            }.onFailure {
                handleError(it)
            }
        }
    }

    private fun setUpProfile(data: UserDataModel) {
        email.setText(data.email.orEmpty())
        name.setText(data.firstName.orEmpty())
        secondName.setText(data.lastName.orEmpty())
        relativeName.setText(data.relativeName.orEmpty())
        relativePhone.setText(data.relativePhone.orEmpty())
        age.setText(data.age?.toString().orEmpty())
    }

}
