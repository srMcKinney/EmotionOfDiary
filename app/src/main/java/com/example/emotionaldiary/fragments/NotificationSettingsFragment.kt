package com.example.emotionaldiary.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.emotionaldiary.R
import com.example.emotionaldiary.utils.NotificationScheduler
import java.util.*

class NotificationSettingsFragment : Fragment() {

    private lateinit var notificationSwitch: Switch
    private lateinit var timeSelectionCard: CardView
    private lateinit var selectedTimeText: TextView
    private lateinit var changeTimeButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationScheduler: NotificationScheduler

    private var selectedHour = 20
    private var selectedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        notificationScheduler = NotificationScheduler(requireContext())

        initViews(view)
        loadSavedSettings()
        setupListeners()
    }

    private fun initViews(view: View) {
        notificationSwitch = view.findViewById(R.id.notification_switch)
        timeSelectionCard = view.findViewById(R.id.time_selection_card)
        selectedTimeText = view.findViewById(R.id.selected_time)
        changeTimeButton = view.findViewById(R.id.change_time_button)

        view.findViewById<View>(R.id.back_button).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadSavedSettings() {
        val isEnabled = sharedPreferences.getBoolean("notifications_enabled", false)
        selectedHour = sharedPreferences.getInt("notification_hour", 20)
        selectedMinute = sharedPreferences.getInt("notification_minute", 0)

        notificationSwitch.isChecked = isEnabled
        updateTimeDisplay()
        updateTimeCardState(isEnabled)
    }

    private fun setupListeners() {
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateTimeCardState(isChecked)
            saveNotificationSettings(isChecked)
            
            if (isChecked) {
                notificationScheduler.scheduleNotification(selectedHour, selectedMinute)
            } else {
                notificationScheduler.cancelNotification()
            }
        }

        changeTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

        timeSelectionCard.setOnClickListener {
            if (notificationSwitch.isChecked) {
                showTimePickerDialog()
            }
        }
    }

    private fun updateTimeCardState(enabled: Boolean) {
        timeSelectionCard.alpha = if (enabled) 1.0f else 0.5f
        changeTimeButton.isEnabled = enabled
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                updateTimeDisplay()
                saveTimeSettings()
                
                if (notificationSwitch.isChecked) {
                    notificationScheduler.scheduleNotification(selectedHour, selectedMinute)
                }
            },
            selectedHour,
            selectedMinute,
            true
        )
        timePickerDialog.show()
    }

    private fun updateTimeDisplay() {
        val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
        selectedTimeText.text = timeString
    }

    private fun saveNotificationSettings(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean("notifications_enabled", enabled)
            .apply()
    }

    private fun saveTimeSettings() {
        sharedPreferences.edit()
            .putInt("notification_hour", selectedHour)
            .putInt("notification_minute", selectedMinute)
            .apply()
    }
}
