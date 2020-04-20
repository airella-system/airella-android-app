package org.airella.airella.ui.register

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.airella.airella.R
import org.airella.airella.data.Result
import org.airella.airella.utils.afterTextChanged

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val passwordConfirm = findViewById<EditText>(R.id.password_confirm)

        val loginButton = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        registerViewModel.usernameError.observe(this, Observer {
            username.error = it?.let { getString(it) }
        })

        registerViewModel.emailError.observe(this, Observer {
            email.error = it?.let { getString(it) }
        })

        registerViewModel.passwordError.observe(this, Observer {
            password.error = it?.let { getString(it) }
        })

        registerViewModel.passwordConfirmError.observe(this, Observer {
            passwordConfirm.error = it?.let { getString(it) }
        })

        registerViewModel.isDataValid.observe(this, Observer {
            loginButton.isEnabled = it
        })

        registerViewModel.registerResult.observe(this, Observer {
            val registerResult = it ?: return@Observer

            loading.visibility = View.GONE
            when (registerResult) {
                is Result.Success -> {
                    showRegisterSuccess()

                    setResult(Activity.RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    showRegisterFailed(registerResult.data)
                }
            }
        })


        username.afterTextChanged {
            registerViewModel.usernameChanged(username.text.toString())
        }

        email.afterTextChanged {
            registerViewModel.emailChanged(email.text.toString())
        }

        password.afterTextChanged {
            registerViewModel.passwordChanged(password.text.toString())
        }

        passwordConfirm.afterTextChanged {
            registerViewModel.passwordConfirmChanged(passwordConfirm.text.toString())
        }

        passwordConfirm.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    registerViewModel.register()
            }
            false
        }

        loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            registerViewModel.register()
        }

    }

    private fun showRegisterSuccess() {
        Toast.makeText(
            applicationContext,
            getString(R.string.register_success),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showRegisterFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
