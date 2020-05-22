package org.airella.airella.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.R
import org.airella.airella.data.api.ApiException
import org.airella.airella.data.model.Result
import org.airella.airella.data.model.auth.LoginResponse
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.AuthUtils
import retrofit2.HttpException

class LoginViewModel : ViewModel() {

    private val loginService = AuthService

    private var username: String = ""
    private var password: String = ""

    val usernameError = MutableLiveData<Int>(null)
    val passwordError = MutableLiveData<Int>(null)

    val isDataValid = MutableLiveData<Boolean>(false)

    private val _loginResult = MutableLiveData<Result<LoginResponse, Int>>()
    val loginResult: LiveData<Result<LoginResponse, Int>> = _loginResult

    fun login() {
        if (!isFormValid())
            return

        loginService.login(username, password)
            .subscribe(
                { user ->
                    _loginResult.value = Result.Success(user)
                },
                { error ->
                    when (error) {
                        is ApiException -> _loginResult.value =
                            Result.Error(R.string.internal_error)
                        is HttpException -> {
                            when (error.code()) {
                                401, 403 -> _loginResult.value =
                                    Result.Error(R.string.login_failed_error)
                                else -> _loginResult.value = Result.Error(R.string.internet_error)
                            }
                        }
                        else -> _loginResult.value = Result.Error(R.string.internet_error)
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

    fun passwordChanged(password: String) {
        this.password = password
        if (!AuthUtils.isPasswordValid(password)) {
            passwordError.value = R.string.invalid_password
        }
        validateForm()
    }

    private fun validateForm() {
        if (isFormValid()) {
            isDataValid.value = true
        }
    }

    private fun isFormValid() = AuthUtils.isUserNameValid(username) &&
            AuthUtils.isPasswordValid(password)

}
