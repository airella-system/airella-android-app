package org.airella.airella.ui.register

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.airella.airella.R
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

        registerViewModel.registerFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            loginButton.isEnabled = loginState.isDataValid

            username.error = loginState.usernameError?.let { err -> getString(err) }
            password.error = loginState.passwordError?.let { err -> getString(err) }
        })


        registerViewModel.registerResult.observe(this, Observer {
            val registerResult = it ?: return@Observer

            loading.visibility = View.GONE
            when (registerResult) {
                is RegisterResult.Success -> {
                    showRegisterSuccess()

                    setResult(Activity.RESULT_OK)
                    finish()
                }
                is RegisterResult.Error -> {
                    showRegisterFailed(registerResult.error)
                }
            }
        })

        username.afterTextChanged {
            registerViewModel.usernameChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.afterTextChanged {
            registerViewModel.passwordChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            registerViewModel.register(
                username.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
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
