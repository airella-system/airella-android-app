package org.airella.airella.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.R
import org.airella.airella.data.LoginRepository
import org.airella.airella.data.Result
import org.airella.airella.utils.LoginUtils
import kotlin.concurrent.thread

class RegisterViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(username: String, email: String, password: String) {
        thread(start = true) {
            val result = loginRepository.register(username, email, password)

            if (result is Result.Success) {
                _registerResult.postValue(RegisterResult.Success(result.data))
            } else {
                _registerResult.postValue(RegisterResult.Error(R.string.login_failed))
            }
        }
    }

    fun usernameChanged(username: String, password: String) {
        if (LoginUtils.isUserNameValid(username)) {
            if (LoginUtils.isPasswordValid(password)) {
                _registerForm.value = RegisterFormState(isDataValid = true)
            }
        } else {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        }
    }

    fun passwordChanged(username: String, password: String) {
        if (LoginUtils.isPasswordValid(password)) {
            if (LoginUtils.isUserNameValid(username)) {
                _registerForm.value = RegisterFormState(isDataValid = true)
            }
        } else {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        }
    }

}
