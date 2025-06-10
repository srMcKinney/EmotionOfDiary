package com.example.emotionaldiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        dateText = view.findViewById(R.id.date_text)
        emotionIcon = view.findViewById(R.id.emotion_icon)
        feelingsInput = view.findViewById(R.id.feelings_input)
        commentsInput = view.findViewById(R.id.comments_input)

        updateDateDisplay()

        emotionIcon.setOnClickListener {
            showEmotionSelector()
        }

        loadTodayEntry()
        setupTrendsChart(view)
        setupAutoSave()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
        dateText.text = dateFormat.format(calendar.time)
    }

    private fun loadTodayEntry() {
        val userId = authManager.getUserId() ?: return

        val entry = emotionRepository.getEntryForDate(userId, calendar.time)

        if (entry != null) {
            selectedEmotionType = entry.emotionType
            feelingsInput.text = entry.overallFeeling
            commentsInput.text = entry.comments

            updateEmotionIconFromFeeling(entry.overallFeeling)
        }
    }

    private fun updateEmotionIconFromFeeling(feeling: String) {
        val iconRes = when (feeling) {
            "Неприятные" -> R.drawable.tree4
            "Отчасти неприятные" -> R.drawable.tree1
            "Нейтральные" -> R.drawable.tree2
            "Отчасти приятные" -> R.drawable.tree3
            "Приятные" -> R.drawable.tree5
            else -> R.drawable.tree
        }
        emotionIcon.setImageResource(iconRes)
    }

    private fun setupTrendsChart(view: View) {
        val userId = authManager.getUserId() ?: return

        val endDate = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val startDate = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 30)

        val entries = emotionRepository.getAllEntriesForUser(userId)
            .filter { it.date in startDate..endDate }

        val chartImage = view.findViewById<ImageView>(R.id.chart_image)
        chartImage.visibility = View.GONE

        val chartView = EmotionChartView(requireContext())
        val parent = chartImage.parent as ViewGroup
        parent.addView(chartView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        chartView.setData(entries)
    }

    private fun setupAutoSave() {
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
        selectedEmotionType = when (selectedEmotionType) {
            EmotionType.NEUTRAL -> EmotionType.CALM
            EmotionType.CALM -> EmotionType.HAPPY
            EmotionType.HAPPY -> EmotionType.EXCITED
            EmotionType.EXCITED -> EmotionType.ANXIOUS
            EmotionType.ANXIOUS -> EmotionType.ANGRY
            EmotionType.ANGRY -> EmotionType.SAD
            EmotionType.SAD -> EmotionType.NEUTRAL
            EmotionType.INTEREST -> EmotionType.INTEREST
            EmotionType.SURPRISE -> EmotionType.SURPRISE
        }

        saveEntry()
    }

    override fun onPause() {
        super.onPause()
        saveEntry()
    }
}