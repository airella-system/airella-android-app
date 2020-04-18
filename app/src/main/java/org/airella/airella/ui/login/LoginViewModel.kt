package org.airella.airella.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.R
import org.airella.airella.data.LoginRepository
import org.airella.airella.data.Result
import org.airella.airella.utils.LoginUtils
import kotlin.concurrent.thread

class LoginViewModel : ViewModel() {

    private val loginRepository = LoginRepository

    private val _loginForm = MutableLiveData<LoginFormState>()
    val registerFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val registerResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        thread(start = true) {
            val result = loginRepository.login(username, password)

            if (result is Result.Success) {
                _loginResult.postValue(LoginResult.Success(result.data))
            } else {
                _loginResult.postValue(LoginResult.Error(R.string.login_failed))
            }
        }
    }

    fun usernameChanged(username: String, password: String) {
        if (LoginUtils.isUserNameValid(username)) {
            if (LoginUtils.isPasswordValid(password)) {
                _loginForm.value = LoginFormState(isDataValid = true)
            }
        } else {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        }
    }

    fun passwordChanged(username: String, password: String) {
        if (LoginUtils.isPasswordValid(password)) {
            if (LoginUtils.isUserNameValid(username)) {
                _loginForm.value = LoginFormState(isDataValid = true)
            }
        } else {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        }
    }

}
