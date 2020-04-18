package org.airella.airella.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.R
import org.airella.airella.data.LoginRepository
import org.airella.airella.data.Result
import kotlin.concurrent.thread

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val registerFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val registerResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        thread(start = true) {
            val result = loginRepository.login(username, password)

            if (result is Result.Success) {
                _loginResult.postValue(LoginResult(success = result.data))
            } else {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }
    }

    fun usernameChanged(username: String, password: String) {
        if (isUserNameValid(username)) {
            if (isPasswordValid(password)) {
                _loginForm.value = LoginFormState(isDataValid = true)
            }
        } else {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        }
    }

    fun passwordChanged(username: String, password: String) {
        if (isPasswordValid(password)) {
            if (isUserNameValid(username)) {
                _loginForm.value = LoginFormState(isDataValid = true)
            }
        } else {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
