package ua.romanik.pulse.data.local

import android.content.SharedPreferences

class UserRepository(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val AUTH_USER_DATA_KEY: String = "auth_user_data_key"
        private const val DEFAULT_VALUE: String = ""
    }

    fun fetchAuthUserData(): String {
        return sharedPreferences.getString(AUTH_USER_DATA_KEY, DEFAULT_VALUE).orEmpty()
    }

    fun saveAuthUserData(email: String) {
        sharedPreferences.edit().putString(AUTH_USER_DATA_KEY, email).apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}