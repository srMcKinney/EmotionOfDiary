package com.example.emotionaldiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.emotionaldiary.R
import com.example.emotionaldiary.auth.AuthManager
import com.example.emotionaldiary.data.EmotionRepository
import com.example.emotionaldiary.models.EmotionEntry
import com.example.emotionaldiary.models.EmotionType
import com.example.emotionaldiary.views.EmotionChartView
import java.text.SimpleDateFormat
import java.util.*

class DiaryFragment : Fragment() {

    private lateinit var dateText: TextView
    private lateinit var emotionIcon: ImageView
    private lateinit var feelingsInput: TextView
    private lateinit var commentsInput: TextView

    private lateinit var emotionRepository: EmotionRepository
    private lateinit var authManager: AuthManager

    private var selectedEmotionType = EmotionType.NEUTRAL
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emotionRepository = EmotionRepository(requireContext())
        authManager = AuthManager(requireContext())

        // Initialize views
        dateText = view.findViewById(R.id.date_text)
        emotionIcon = view.findViewById(R.id.emotion_icon)
        feelingsInput = view.findViewById(R.id.feelings_input)
        commentsInput = view.findViewById(R.id.comments_input)

        // Set up date display
        updateDateDisplay()

        // Set up emotion icon click
        emotionIcon.setOnClickListener {
            showEmotionSelector()
        }

        // Load existing entry for today
        loadTodayEntry()

        // Set up chart
        setupTrendsChart(view)

        // Set up auto-save
        setupAutoSave()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
        dateText.text = dateFormat.format(calendar.time)

        // Update day number
        val dayNumber = view?.findViewById<TextView>(R.id.day_number)
        dayNumber?.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    private fun loadTodayEntry() {
        val userId = authManager.getUserId() ?: return

        val entry = emotionRepository.getEntryForDate(userId, calendar.time)

        if (entry != null) {
            selectedEmotionType = entry.emotionType
            updateEmotionIcon()

            feelingsInput.setText(entry.overallFeeling)
            commentsInput.setText(entry.comments)
        }
    }

    private fun setupTrendsChart(view: View) {
        val userId = authManager.getUserId() ?: return

        // Get entries for the last 30 days
        val endDate = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val startDate = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 30) // Reset calendar

        val entries = emotionRepository.getAllEntriesForUser(userId)
            .filter { it.date in startDate..endDate }

        // Find chart view in the trends card
        val chartImage = view.findViewById<ImageView>(R.id.chart_image)
        chartImage.visibility = View.GONE

        // Create and add EmotionChartView
        val chartView = EmotionChartView(requireContext())
        val parent = chartImage.parent as ViewGroup
        parent.addView(chartView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        // Set data
        chartView.setData(entries)
    }

    private fun setupAutoSave() {
        // Auto-save when focus changes
        feelingsInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveEntry()
        }

        commentsInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveEntry()
        }
    }

    private fun saveEntry() {
        val userId = authManager.getUserId() ?: return

        val feelings = feelingsInput.text.toString().trim()
        val comments = commentsInput.text.toString().trim()

        if (feelings.isEmpty() && comments.isEmpty()) return

        val entry = EmotionEntry(
            userId = userId,
            date = calendar.time,
            emotionType = selectedEmotionType,
            overallFeeling = feelings,
            comments = comments
        )

        emotionRepository.saveEntry(entry)
        Toast.makeText(context, "Запись сохранена", Toast.LENGTH_SHORT).show()
    }

    private fun showEmotionSelector() {
        // In a real app, this would show a dialog or bottom sheet with emotion options
        // For simplicity, we'll just cycle through emotions
        selectedEmotionType = when (selectedEmotionType) {
            EmotionType.NEUTRAL -> EmotionType.CALM
            EmotionType.CALM -> EmotionType.HAPPY
            EmotionType.HAPPY -> EmotionType.EXCITED
            EmotionType.EXCITED -> EmotionType.ANXIOUS
            EmotionType.ANXIOUS -> EmotionType.ANGRY
            EmotionType.ANGRY -> EmotionType.SAD
            EmotionType.SAD -> EmotionType.NEUTRAL
        }

        updateEmotionIcon()
        saveEntry()
    }

    private fun updateEmotionIcon() {
        // Update icon based on selected emotion
        val iconResource = when (selectedEmotionType) {
            EmotionType.CALM -> R.drawable.tree // Use appropriate icons
            EmotionType.HAPPY -> R.drawable.tree
            EmotionType.EXCITED -> R.drawable.tree
            EmotionType.ANXIOUS -> R.drawable.tree
            EmotionType.ANGRY -> R.drawable.tree
            EmotionType.SAD -> R.drawable.tree
            EmotionType.NEUTRAL -> R.drawable.tree
        }

        emotionIcon.setImageResource(iconResource)
    }

    override fun onPause() {
        super.onPause()
        saveEntry()
    }
}
