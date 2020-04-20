package org.airella.airella.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.airella.airella.R
import org.airella.airella.data.Result
import org.airella.airella.data.api.ApiException
import org.airella.airella.data.model.LoginResponse
import org.airella.airella.data.service.LoginService
import org.airella.airella.utils.LoginUtils
import retrofit2.HttpException

class LoginViewModel : ViewModel() {

    private val loginService = LoginService

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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    _loginResult.value = Result.Success(user)
                },
                { error ->
                    when (error) {
                        is ApiException -> _loginResult.value = Result.Error(R.string.login_failed)
                        is HttpException -> {
                            when (error.code()) {
                                401, 403 -> _loginResult.value = Result.Error(R.string.login_failed)
                                else -> _loginResult.value = Result.Error(R.string.login_failed)
                            }
                        }
                        else -> _loginResult.value = Result.Error(R.string.unexpected_error)
                    }
                    Log.e("airella", "error", error)
                })
    }

    fun usernameChanged(username: String) {
        this.username = username
        if (!LoginUtils.isUserNameValid(username)) {
            usernameError.value = R.string.invalid_username
        }
        validateForm()
    }

    fun passwordChanged(password: String) {
        this.password = password
        if (!LoginUtils.isPasswordValid(password)) {
            passwordError.value = R.string.invalid_password
        }
        validateForm()
    }

    private fun validateForm() {
        if (isFormValid()) {
            isDataValid.value = true
        }
    }

    private fun isFormValid() = LoginUtils.isUserNameValid(username) &&
            LoginUtils.isPasswordValid(password)

}
