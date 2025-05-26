package com.example.emotionaldiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.emotionaldiary.R
import com.example.emotionaldiary.auth.AuthManager
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var authManager: AuthManager
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())
        emailInput = view.findViewById(R.id.login_email)
        passwordInput = view.findViewById(R.id.login_password)

        // Настройка обработчиков нажатий
        view.findViewById<View>(R.id.continue_button).setOnClickListener {
            loginWithEmail()
        }

        view.findViewById<View>(R.id.register_link).setOnClickListener {
            // Переход на экран регистрации
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
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
                Toast.makeText(context, "Вход выполнен успешно", Toast.LENGTH_SHORT).show()

                // Переход на экран дневника
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DiaryFragment())
                    .commit()
            } else {
                Toast.makeText(context, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}