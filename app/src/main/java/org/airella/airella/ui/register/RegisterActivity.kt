package org.airella.airella.ui.register

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.airella.airella.R
import org.airella.airella.data.model.Result
import org.airella.airella.utils.afterTextChanged

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val passwordConfirm = findViewById<EditText>(R.id.password_confirm)

        val registerButton = findViewById<Button>(R.id.register)
        val loading = findViewById<ProgressBar>(R.id.loading)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        registerViewModel.emailError.observe(this, {
            email.error = it?.let { getString(it) }
        })

        registerViewModel.passwordError.observe(this, {
            password.error = it?.let { getString(it) }
        })

        registerViewModel.passwordConfirmError.observe(this, {
            passwordConfirm.error = it?.let { getString(it) }
        })

        registerViewModel.isDataValid.observe(this, {
            registerButton.isEnabled = it
        })

        registerViewModel.registerResult.observe(this, {
            val registerResult = it ?: return@observe

            loading.visibility = View.GONE
            when (registerResult) {
                is Result.Success -> {
                    showRegisterSuccess()
                }
                is Result.Error -> {
                    showRegisterFailed(registerResult.data)
                }
            }
        })

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
                    registerViewModel.register(this)
            }
            false
        }

        registerButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            registerViewModel.register(this)
        }

    }

    private fun showRegisterSuccess() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Registration successful")
            .setMessage("To complete registration process please check your email for a activation request.")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
            .show()
    }

    private fun showRegisterFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
