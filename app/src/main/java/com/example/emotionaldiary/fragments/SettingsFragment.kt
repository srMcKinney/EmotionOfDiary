package com.example.emotionaldiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.emotionaldiary.R
import com.example.emotionaldiary.auth.AuthManager

class SettingsFragment : Fragment() {

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        // Обновляем имя пользователя
        val userNameText = view.findViewById<TextView>(R.id.user_name)
        userNameText.text = authManager.getUserFullName()

        view.findViewById<View>(R.id.logout_button).setOnClickListener {
            // Navigate to diary fragment to add a new entry
            parentFragmentManager.beginTransaction()
            authManager.logout()
            Toast.makeText(context, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()

            // Переход на экран входа
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }

        // Настраиваем обработчики нажатий для других элементов
        view.findViewById<View>(R.id.login_settings_card).setOnClickListener {
            Toast.makeText(context, "Настройки входа", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.notifications_card).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationSettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}