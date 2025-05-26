package com.example.emotionaldiary.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class AuthManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val userPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val TAG = "AuthManager"

    // VK OAuth constants
    private val VK_CLIENT_ID = "your_vk_client_id" // Replace with your VK client ID
    private val VK_REDIRECT_URI = "https://oauth.vk.com/blank.html"
    private val VK_AUTH_URL = "https://oauth.vk.com/authorize"
    private val VK_TOKEN_URL = "https://oauth.vk.com/access_token"
    private val VK_SCOPE = "email"

    // User data
    private var userId: String? = null
    private var userEmail: String? = null
    private var userName: String? = null
    private var userSurname: String? = null
    private var accessToken: String? = null

    init {
        // Load saved auth data
        userId = sharedPreferences.getString("user_id", null)
        userEmail = sharedPreferences.getString("user_email", null)
        userName = sharedPreferences.getString("user_name", null)
        userSurname = sharedPreferences.getString("user_surname", null)
        accessToken = sharedPreferences.getString("access_token", null)
    }

    fun isLoggedIn(): Boolean {
        return !accessToken.isNullOrEmpty()
    }

    fun getUserId(): String? = userId

    fun getUserEmail(): String? = userEmail

    fun getUserName(): String? = userName

    fun getUserSurname(): String? = userSurname

    fun getUserFullName(): String {
        return if (userName != null && userSurname != null) {
            "$userName $userSurname"
        } else {
            "Пользователь"
        }
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        // В реальном приложении здесь был бы запрос к серверу
        // Для демо-версии проверяем в локальном хранилище
        return userPreferences.contains("email_$email")
    }

    fun startVkAuth(customTabsLauncher: ActivityResultLauncher<Intent>) {
        val state = UUID.randomUUID().toString()

        // Save state for verification
        sharedPreferences.edit().putString("auth_state", state).apply()

        // Build auth URL
        val authUri = Uri.parse(VK_AUTH_URL).buildUpon()
            .appendQueryParameter("client_id", VK_CLIENT_ID)
            .appendQueryParameter("redirect_uri", VK_REDIRECT_URI)
            .appendQueryParameter("display", "mobile")
            .appendQueryParameter("scope", VK_SCOPE)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("state", state)
            .build()

        // Launch Custom Tabs
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsLauncher.launch(customTabsIntent.intent.apply {
            data = authUri
        })
    }

    suspend fun handleAuthResponse(uri: Uri): Boolean {
        // Verify state to prevent CSRF
        val state = uri.getQueryParameter("state")
        val savedState = sharedPreferences.getString("auth_state", null)

        if (state != savedState) {
            Log.e(TAG, "State mismatch, possible CSRF attack")
            return false
        }

        // Get authorization code
        val code = uri.getQueryParameter("code")
        if (code.isNullOrEmpty()) {
            Log.e(TAG, "No authorization code received")
            return false
        }

        // Exchange code for token
        return withContext(Dispatchers.IO) {
            try {
                val tokenUrl = URL("$VK_TOKEN_URL?client_id=$VK_CLIENT_ID&redirect_uri=$VK_REDIRECT_URI&code=$code")
                val connection = tokenUrl.openConnection() as HttpsURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)

                    accessToken = jsonObject.optString("access_token")
                    userId = jsonObject.optString("user_id")
                    userEmail = jsonObject.optString("email")

                    // Save to SharedPreferences
                    sharedPreferences.edit()
                        .putString("access_token", accessToken)
                        .putString("user_id", userId)
                        .putString("user_email", userEmail)
                        .apply()

                    true
                } else {
                    Log.e(TAG, "Token request failed with code: $responseCode")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error exchanging code for token", e)
                false
            }
        }
    }

    fun logout() {
        // Clear auth data
        accessToken = null
        userId = null
        userEmail = null
        userName = null
        userSurname = null

        // Clear from SharedPreferences
        sharedPreferences.edit()
            .remove("access_token")
            .remove("user_id")
            .remove("user_email")
            .remove("user_name")
            .remove("user_surname")
            .apply()
    }

    // Для входа по email/паролю
    suspend fun loginWithEmail(email: String, password: String): Boolean {
        // В реальном приложении здесь была бы проверка на сервере
        // Для демо-версии проверяем в локальном хранилище

        if (!userPreferences.contains("email_$email")) {
            return false // Пользователь не найден
        }

        val savedPassword = userPreferences.getString("password_$email", null)
        if (savedPassword != password) {
            return false // Неверный пароль
        }

        userId = userPreferences.getString("id_$email", UUID.randomUUID().toString())
        userEmail = email
        userName = userPreferences.getString("name_$email", null)
        userSurname = userPreferences.getString("surname_$email", null)
        accessToken = "simulated_token_${System.currentTimeMillis()}"

        // Сохраняем в SharedPreferences
        sharedPreferences.edit()
            .putString("access_token", accessToken)
            .putString("user_id", userId)
            .putString("user_email", userEmail)
            .putString("user_name", userName)
            .putString("user_surname", userSurname)
            .apply()

        return true
    }

    suspend fun registerWithEmail(email: String, password: String, name: String, surname: String): Boolean {
        // В реальном приложении здесь была бы регистрация на сервере
        // Для демо-версии сохраняем в локальном хранилище

        if (userPreferences.contains("email_$email")) {
            return false // Email уже зарегистрирован
        }

        val newUserId = UUID.randomUUID().toString()

        // Сохраняем данные пользователя
        userPreferences.edit()
            .putString("email_$email", email)
            .putString("password_$email", password)
            .putString("name_$email", name)
            .putString("surname_$email", surname)
            .putString("id_$email", newUserId)
            .apply()

        // Устанавливаем текущего пользователя
        userId = newUserId
        userEmail = email
        userName = name
        userSurname = surname
        accessToken = "simulated_token_${System.currentTimeMillis()}"

        // Сохраняем в SharedPreferences
        sharedPreferences.edit()
            .putString("access_token", accessToken)
            .putString("user_id", userId)
            .putString("user_email", userEmail)
            .putString("user_name", userName)
            .putString("user_surname", userSurname)
            .apply()

        return true
    }
}