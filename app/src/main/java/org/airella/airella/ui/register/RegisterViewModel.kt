package org.airella.airella.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.R
import org.airella.airella.data.api.ApiException
import org.airella.airella.data.model.Result
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.AuthUtils
import retrofit2.HttpException

class RegisterViewModel() : ViewModel() {

    private val authService = AuthService

    private var username: String = ""
    private var email: String = ""
    private var password: String = ""
    private var passwordConfirm: String = ""

    val usernameError = MutableLiveData<Int>(null)
    val emailError = MutableLiveData<Int>(null)
    val passwordError = MutableLiveData<Int>(null)
    val passwordConfirmError = MutableLiveData<Int>(null)

    val isDataValid = MutableLiveData(false)

    private val _registerResult = MutableLiveData<Result<Boolean, Int>>()
    val registerResult: LiveData<Result<Boolean, Int>> = _registerResult

    fun register() {
        if (!isFormValid())
            return

        authService.register(username, email, password)
            .subscribe(
                {
                    _registerResult.value = Result.Success(true)
                },
                { error ->
                    when (error) {
                        is ApiException -> _registerResult.value =
                            Result.Error(R.string.login_failed_error)
                        is HttpException -> {
                            when (error.code()) {
                                401, 403 -> _registerResult.value =
                                    Result.Error(R.string.login_failed_error)
                                else -> _registerResult.value =
                                    Result.Error(R.string.login_failed_error)
                            }
                        }
                        else -> _registerResult.value = Result.Error(R.string.internet_error)
                    }
                })
    }

    fun usernameChanged(username: String) {
        this.username = username
        if (!AuthUtils.isUserNameValid(username)) {
            usernameError.value = R.string.invalid_username
        }
        validateForm()
    }

    fun emailChanged(email: String) {
        this.email = email
        if (!AuthUtils.isEmailValid(email)) {
            emailError.value = R.string.invalid_email
        }
        validateForm()
    }

    fun passwordChanged(password: String) {
        this.password = password
        if (!AuthUtils.isPasswordValid(password)) {
            passwordError.value = R.string.invalid_password
        }
        validateForm()
    }

    fun passwordConfirmChanged(passwordConfirm: String) {
        this.passwordConfirm = passwordConfirm
        if (!AuthUtils.isPasswordConfirmValid(password, passwordConfirm)) {
            passwordConfirmError.value = R.string.invalid_password_confirm
        }
        validateForm()
    }

    private fun validateForm() {
        if (isFormValid()) {
            isDataValid.value = true
        }
    }

    private fun isFormValid() = AuthUtils.isUserNameValid(username) &&
            AuthUtils.isEmailValid(email) &&
            AuthUtils.isPasswordValid(password) &&
            AuthUtils.isPasswordConfirmValid(password, passwordConfirm)

}
