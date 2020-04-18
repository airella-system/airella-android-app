package org.airella.airella.ui.register

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.airella.airella.R
import org.airella.airella.data.model.LoggedInUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        registerViewModel = ViewModelProviders.of(this, RegisterViewModelFactory())
            .get(RegisterViewModel::class.java)

        registerViewModel.registerFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            loginButton.isEnabled = loginState.isDataValid

            username.error = loginState.usernameError?.let { err -> getString(err) }
            password.error = loginState.passwordError?.let { err -> getString(err) }
        })


        registerViewModel.registerResult.observe(this, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            } else if (loginResult.success != null) {
                showLoginSuccess(loginResult.success)

                setResult(Activity.RESULT_OK)
                finish()
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
            registerViewModel.login(username.text.toString(), password.text.toString())
        }

    }

    private fun showLoginSuccess(model: LoggedInUser) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
