package org.airella.airella.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.R
import org.airella.airella.data.LoginRepository
import org.airella.airella.data.Result
import kotlin.concurrent.thread

class RegisterViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _loginForm

    private val _loginResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _loginResult

    fun login(username: String, password: String) {
        thread(start = true) {
            val result = loginRepository.login(username, password)

            if (result is Result.Success) {
                _loginResult.postValue(RegisterResult(success = result.data))
            } else {
                _loginResult.postValue(RegisterResult(error = R.string.login_failed))
            }
        }
    }

    fun usernameChanged(username: String, password: String) {
        if (isUserNameValid(username)) {
            if (isPasswordValid(password)) {
                _loginForm.value = RegisterFormState(isDataValid = true)
            }
        } else {
            _loginForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        }
    }

    fun passwordChanged(username: String, password: String) {
        if (isPasswordValid(password)) {
            if (isUserNameValid(username)) {
                _loginForm.value = RegisterFormState(isDataValid = true)
            }
        } else {
            _loginForm.value = RegisterFormState(passwordError = R.string.invalid_password)
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
