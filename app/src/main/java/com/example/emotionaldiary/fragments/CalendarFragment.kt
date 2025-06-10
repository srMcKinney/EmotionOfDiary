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
    private lateinit var data_text: TextView
    private lateinit var emotionRepository: EmotionRepository
    private lateinit var authManager: AuthManager

    private val calendar = Calendar.getInstance()
    private var currentMonth = calendar.get(Calendar.MONTH)
    private var currentYear = calendar.get(Calendar.YEAR)

    private var selectedDate: Date = calendar.time

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

        calendarGrid = view.findViewById(R.id.calendar_grid)
        monthYearText = view.findViewById(R.id.month_year)
        entriesList = view.findViewById(R.id.entries_list)
        data_text = view.findViewById(R.id.date_text)

        // Кнопки переключения месяца
        view.findViewById<View>(R.id.prev_month).setOnClickListener {
            navigateToPreviousMonth()
        }
        view.findViewById<View>(R.id.next_month).setOnClickListener {
            navigateToNextMonth()
        }

        // Кнопка добавления записи
        view.findViewById<View>(R.id.add_entry_button).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddEntryFragment())
                .addToBackStack(null)
                .commit()
        }

        updateCalendar()
        updateSelectedDateDisplay()
    }

    private fun updateSelectedDateDisplay() {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
        data_text.text = dateFormat.format(selectedDate)
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.YEAR, currentYear)
        monthYearText.text = dateFormat.format(calendar.time)

        // Обновляем отображение выбранной даты
        updateSelectedDateDisplay()

        val dates = getDatesForCalendarGrid()

        val adapter = CalendarAdapter(requireContext(), dates, currentMonth)
        calendarGrid.adapter = adapter

        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            selectedDate = dates[position]
            updateSelectedDateDisplay()
            showEntriesForDate(selectedDate)
        }

        showEntriesForMonth()
    }

    private fun getDatesForCalendarGrid(): List<Date> {
        val dates = mutableListOf<Date>()

        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1

        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        for (i in 0 until 42) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Восстановить месяц и год
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
            val dateFormat = SimpleDateFormat("d MMMM", Locale("ru"))
            val entriesText = entries.joinToString("\n") { entry ->
                val emotionName = when (entry.emotionType) {
                    EmotionType.CALM -> "Спокойствие"
                    EmotionType.HAPPY -> "Счастье"
                    EmotionType.EXCITED -> "Возбуждение"
                    EmotionType.ANXIOUS -> "Тревога"
                    EmotionType.ANGRY -> "Гнев"
                    EmotionType.SAD -> "Грусть"
                    EmotionType.NEUTRAL -> "Нейтральное"
                    EmotionType.INTEREST -> "Интерес"
                    EmotionType.SURPRISE -> "Удивление"
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