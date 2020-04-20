package org.airella.airella.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.airella.airella.R
import org.airella.airella.data.Result
import org.airella.airella.data.api.ApiException
import org.airella.airella.data.service.LoginService
import org.airella.airella.utils.LoginUtils
import retrofit2.HttpException

class RegisterViewModel() : ViewModel() {

    private val loginService = LoginService

    private var username: String = ""
    private var email: String = ""
    private var password: String = ""
    private var passwordConfirm: String = ""

    val usernameError = MutableLiveData<Int>(null)
    val emailError = MutableLiveData<Int>(null)
    val passwordError = MutableLiveData<Int>(null)
    val passwordConfirmError = MutableLiveData<Int>(null)

    val isDataValid = MutableLiveData<Boolean>(false)

    private val _registerResult = MutableLiveData<Result<Boolean, Int>>()
    val registerResult: LiveData<Result<Boolean, Int>> = _registerResult

    fun register() {
        if (!isFormValid())
            return

        loginService.register(username, email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user ->
                    _registerResult.value = Result.Success(true)
                },
                { error ->
                    when (error) {
                        is ApiException -> _registerResult.value =
                            Result.Error(R.string.login_failed)
                        is HttpException -> {
                            when (error.code()) {
                                401, 403 -> _registerResult.value =
                                    Result.Error(R.string.login_failed)
                                else -> _registerResult.value = Result.Error(R.string.login_failed)
                            }
                        }
                        else -> _registerResult.value = Result.Error(R.string.unexpected_error)
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

    fun emailChanged(email: String) {
        this.email = email
        if (!LoginUtils.isEmailValid(email)) {
            emailError.value = R.string.invalid_username
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

    fun passwordConfirmChanged(passwordConfirm: String) {
        this.passwordConfirm = passwordConfirm
        if (!LoginUtils.isPasswordConfirmValid(password, passwordConfirm)) {
            passwordConfirmError.value = R.string.invalid_password
        }
        validateForm()
    }

    private fun validateForm() {
        if (isFormValid()) {
            isDataValid.value = true
        }
    }

    private fun isFormValid() = LoginUtils.isUserNameValid(username) &&
            LoginUtils.isEmailValid(email) &&
            LoginUtils.isPasswordValid(password) &&
            LoginUtils.isPasswordConfirmValid(password, passwordConfirm)

}
