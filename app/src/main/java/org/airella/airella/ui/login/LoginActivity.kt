package org.airella.airella.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*
import org.airella.airella.MainActivity
import org.airella.airella.R
import org.airella.airella.data.api.ApiManager
import org.airella.airella.data.model.Result
import org.airella.airella.data.model.auth.User
import org.airella.airella.ui.register.RegisterActivity
import org.airella.airella.utils.afterTextChanged

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)
        val registerButton = findViewById<Button>(R.id.register)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.emailError.observe(this, Observer {
            email.error = it?.let { getString(it) }
        })

        loginViewModel.passwordError.observe(this, Observer {
            password.error = it?.let { getString(it) }
        })

        loginViewModel.isDataValid.observe(this, Observer {
            loginButton.isEnabled = it
        })

        loginViewModel.loginResult.observe(this, Observer {
            val loginResult: Result<User, String> = it ?: return@Observer

            loading.visibility = View.GONE
            when (loginResult) {
                is Result.Success -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is Result.Error -> {
                    showLoginFailed(loginResult.data)
                }
            }
        })

        email.afterTextChanged {
            loginViewModel.emailChanged(email.text.toString())
        }

        password.afterTextChanged {
            loginViewModel.passwordChanged(password.text.toString())
        }

        password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    loginViewModel.login(this)
            }
            false
        }

        loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login(this)
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        apiUrlButton.setOnClickListener {
            val form = layoutInflater.inflate(
                R.layout.view_api_url_config,
                null
            )
            val apiUrlEditText: EditText = form.findViewById(R.id.apiUrl)
            apiUrlEditText.setText(ApiManager.baseApiUrl)

            val alert = MaterialAlertDialogBuilder(this)
                .setView(form)
                .setPositiveButton(R.string.action_save) { _, _ ->
                    var apiUrl = apiUrlEditText.text.trim().toString()
                    if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
                        apiUrl = "http://$apiUrl"
                    }
                    ApiManager.baseApiUrl = apiUrl
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            alert.show()
        }

    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}