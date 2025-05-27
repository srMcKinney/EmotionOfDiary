package com.example.emotionaldiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionaldiary.R
import com.example.emotionaldiary.auth.AuthManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login) // используем тот же layout

        authManager = AuthManager(this)
        emailInput = findViewById(R.id.login_email)
        passwordInput = findViewById(R.id.login_password)

        findViewById<View>(R.id.continue_button).setOnClickListener {
            loginWithEmail()
        }

        findViewById<View>(R.id.register_link).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginWithEmail() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty()) {
            emailInput.error = "Введите email"
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Введите пароль"
            return
        }

        lifecycleScope.launch {
            val success = authManager.loginWithEmail(email, password)

            if (success) {
                Toast.makeText(this@LoginActivity, "Вход выполнен успешно", Toast.LENGTH_SHORT).show()

                // Переход на главный экран/дневник
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}