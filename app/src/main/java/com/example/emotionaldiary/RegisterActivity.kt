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

class RegisterActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register) // Используем тот же layout

        authManager = AuthManager(this)

        nameInput = findViewById(R.id.register_name)
        surnameInput = findViewById(R.id.register_surname)
        emailInput = findViewById(R.id.register_email)
        passwordInput = findViewById(R.id.register_password)

        findViewById<View>(R.id.save_button).setOnClickListener {
            registerWithEmail()
        }

        findViewById<View>(R.id.login_link).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerWithEmail() {
        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (name.isEmpty()) {
            nameInput.error = "Введите имя"
            return
        }

        if (surname.isEmpty()) {
            surnameInput.error = "Введите фамилию"
            return
        }

        if (email.isEmpty()) {
            emailInput.error = "Введите email"
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Введите пароль"
            return
        }

        lifecycleScope.launch {
            if (authManager.isEmailRegistered(email)) {
                emailInput.error = "Этот email уже зарегистрирован"
                return@launch
            }

            val success = authManager.registerWithEmail(email, password, name, surname)

            if (success) {
                Toast.makeText(this@RegisterActivity, "Регистрация выполнена успешно", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@RegisterActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
            }
        }
    }
}