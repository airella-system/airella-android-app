package org.airella.airella.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.R
import org.airella.airella.data.api.ApiException
import org.airella.airella.data.model.Result
import org.airella.airella.data.model.auth.User
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.AuthUtils

class LoginViewModel : ViewModel() {

    private val loginService = AuthService

    private var email: String = ""
    private var password: String = ""

    val emailError = MutableLiveData<Int>(null)
    val passwordError = MutableLiveData<Int>(null)

    val isDataValid = MutableLiveData(false)

    private val _loginResult = MutableLiveData<Result<User, String>>()
    val loginResult: LiveData<Result<User, String>> = _loginResult

    fun login(context: Context) {
        if (!isFormValid())
            return

        loginService.login(email, password)
            .subscribe(
                { user ->
                    _loginResult.value = Result.Success(user)
                },
                { error ->
                    when (error) {
                        is ApiException ->
                            _loginResult.value = Result.Error(error.message)
                        else ->
                            _loginResult.value =
                                Result.Error(context.getString(R.string.internet_error))
                    }
                })
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

    private fun validateForm() {
        if (isFormValid()) {
            isDataValid.value = true
        }
    }

    private fun isFormValid() = AuthUtils.isEmailValid(email) &&
            AuthUtils.isPasswordValid(password)

}
