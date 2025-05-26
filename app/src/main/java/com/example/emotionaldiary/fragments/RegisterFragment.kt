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

class RegisterFragment : Fragment() {

    private lateinit var authManager: AuthManager
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        // Инициализация полей ввода
        nameInput = view.findViewById(R.id.register_name)
        surnameInput = view.findViewById(R.id.register_surname)
        emailInput = view.findViewById(R.id.register_email)
        passwordInput = view.findViewById(R.id.register_password)

        // Настройка обработчиков нажатий
        view.findViewById<View>(R.id.save_button).setOnClickListener {
            registerWithEmail()
        }

        view.findViewById<View>(R.id.login_link).setOnClickListener {
            // Переход на экран входа
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    private fun registerWithEmail() {
        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        // Проверка заполнения всех полей
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
            // Проверка уникальности email
            if (authManager.isEmailRegistered(email)) {
                emailInput.error = "Этот email уже зарегистрирован"
                return@launch
            }

            val success = authManager.registerWithEmail(email, password, name, surname)

            if (success) {
                Toast.makeText(context, "Регистрация выполнена успешно", Toast.LENGTH_SHORT).show()

                // Переход на экран дневника
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DiaryFragment())
                    .commit()
            } else {
                Toast.makeText(context, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
            }
        }
    }
}