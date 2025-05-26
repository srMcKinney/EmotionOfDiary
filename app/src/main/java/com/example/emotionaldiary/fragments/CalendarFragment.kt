package com.example.emotionaldiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.emotionaldiary.R
import com.example.emotionaldiary.adapters.CalendarAdapter
import com.example.emotionaldiary.data.EmotionRepository
import com.example.emotionaldiary.auth.AuthManager
import com.example.emotionaldiary.models.EmotionType
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarGrid: GridView
    private lateinit var monthYearText: TextView
    private lateinit var entriesList: TextView
    private lateinit var emotionRepository: EmotionRepository
    private lateinit var authManager: AuthManager
    private lateinit var data_text: TextView

    private val calendar = Calendar.getInstance()
    private var currentMonth = calendar.get(Calendar.MONTH)
    private var currentYear = calendar.get(Calendar.YEAR)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emotionRepository = EmotionRepository(requireContext())
        authManager = AuthManager(requireContext())

        // Initialize views
        calendarGrid = view.findViewById(R.id.calendar_grid)
        monthYearText = view.findViewById(R.id.month_year)
        entriesList = view.findViewById(R.id.entries_list)
        data_text = view.findViewById(R.id.date_text)

        fun updateDateDisplay() {
            val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
            data_text.text = dateFormat.format(calendar.time)

            // Update day number
            val dayNumber = view?.findViewById<TextView>(R.id.day_number)
            dayNumber?.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        }

        // Set up month navigation
        view.findViewById<View>(R.id.prev_month).setOnClickListener {
            navigateToPreviousMonth()
        }

        view.findViewById<View>(R.id.next_month).setOnClickListener {
            navigateToNextMonth()
        }

        // Set up add entry button
        view.findViewById<View>(R.id.add_entry_button).setOnClickListener {
            // Navigate to diary fragment to add a new entry
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddEntryFragment())
                .addToBackStack(null)
                .commit()
        }

        // Initialize calendar
        updateCalendar()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
        data_text.text = dateFormat.format(calendar.time)

        // Update day number
        val dayNumber = view?.findViewById<TextView>(R.id.day_number)
        dayNumber?.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
    }


    private fun updateCalendar() {
        // Update month/year display
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.YEAR, currentYear)
        monthYearText.text = dateFormat.format(calendar.time)

        // Generate dates for the grid
        val dates = getDatesForCalendarGrid()

        // Set up adapter
        val adapter = CalendarAdapter(requireContext(), dates, currentMonth)
        calendarGrid.adapter = adapter

        // Set up click listener for dates
        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            val selectedDate = dates[position]
            showEntriesForDate(selectedDate)
        }

        // Show entries for the current month
        showEntriesForMonth()
    }

    private fun getDatesForCalendarGrid(): List<Date> {
        val dates = mutableListOf<Date>()

        // Set to first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Get day of week for first day of month (0 = Sunday, 1 = Monday, etc.)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // Move calendar to include days from previous month
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        // Add 42 days (6 weeks) to the grid
        for (i in 0 until 42) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Reset calendar to current month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.YEAR, currentYear)

        return dates
    }

    private fun navigateToPreviousMonth() {
        currentMonth--
        if (currentMonth < 0) {
            currentMonth = 11
            currentYear--
        }
        updateCalendar()
    }

    private fun navigateToNextMonth() {
        currentMonth++
        if (currentMonth > 11) {
            currentMonth = 0
            currentYear++
        }
        updateCalendar()
    }

    private fun showEntriesForDate(date: Date) {
        val userId = authManager.getUserId() ?: return

        val entry = emotionRepository.getEntryForDate(userId, date)

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

        if (entry != null) {
            entriesList.text = "${dateFormat.format(date)}\n${entry.emotionType}: ${entry.overallFeeling}"
        } else {
            entriesList.text = "${dateFormat.format(date)}\nНет записей"
        }
    }

    private fun showEntriesForMonth() {
        val userId = authManager.getUserId() ?: return

        val entries = emotionRepository.getEntriesForMonth(userId, currentYear, currentMonth)

        if (entries.isNotEmpty()) {
            // Используем русскую локаль для форматирования даты
            val dateFormat = SimpleDateFormat("d MMMM", Locale("ru"))
            val entriesText = entries.joinToString("\n") { entry ->
                // Добавляем больше информации о записи
                val emotionName = when (entry.emotionType) {
                    EmotionType.CALM -> "Спокойствие"
                    EmotionType.HAPPY -> "Счастье"
                    EmotionType.EXCITED -> "Возбуждение"
                    EmotionType.ANXIOUS -> "Тревога"
                    EmotionType.ANGRY -> "Гнев"
                    EmotionType.SAD -> "Грусть"
                    EmotionType.NEUTRAL -> "Нейтральное"
                }

                val shortFeeling = if (entry.overallFeeling.length > 30) {
                    entry.overallFeeling.substring(0, 27) + "..."
                } else {
                    entry.overallFeeling
                }

                "${dateFormat.format(entry.date)}: $emotionName - $shortFeeling"
            }
            entriesList.text = entriesText
        } else {
            entriesList.text = "Нет записей за этот месяц"
        }
    }
}