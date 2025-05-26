package com.example.emotionaldiary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.emotionaldiary.auth.AuthManager
import com.example.emotionaldiary.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    private val customTabsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle VK auth callback
        val data = result.data
        if (data != null && data.data != null) {
            handleAuthResponse(data.data!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthManager(this)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Check if user is logged in
        if (!authManager.isLoggedIn()) {
            loadFragment(LoginFragment())
        } else {
            // Set default fragment
            if (savedInstanceState == null) {
                loadFragment(DiaryFragment())
            }

            // Set up bottom navigation
            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.diary -> {
                        loadFragment(DiaryFragment())
                        true
                    }
                    R.id.calendar -> {
                        loadFragment(CalendarFragment())
                        true
                    }
                    R.id.about -> {
                        loadFragment(AboutFragment())
                        true
                    }
                    R.id.settings -> {
                        loadFragment(SettingsFragment())
                        true
                    }
                    else -> false
                }
            }
        }

        // Handle deep links
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val data = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            handleAuthResponse(data)
        }
    }

    private fun handleAuthResponse(uri: Uri) {
        lifecycleScope.launch {
            val success = authManager.handleAuthResponse(uri)
            if (success) {
                Toast.makeText(this@MainActivity, "Авторизация успешна", Toast.LENGTH_SHORT).show()
                loadFragment(DiaryFragment())
            } else {
                Toast.makeText(this@MainActivity, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                loadFragment(LoginFragment())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}