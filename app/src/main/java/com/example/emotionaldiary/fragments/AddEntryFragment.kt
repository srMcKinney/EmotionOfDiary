package com.example.emotionaldiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.emotionaldiary.R
import com.example.emotionaldiary.auth.AuthManager
import com.example.emotionaldiary.data.EmotionRepository
import com.example.emotionaldiary.models.EmotionEntry
import com.example.emotionaldiary.models.EmotionType
import com.example.emotionaldiary.models.EmotionValues
import java.util.*

class AddEntryFragment : Fragment() {

    private lateinit var mainFeelingsEditText: EditText
    private lateinit var additionalInfoEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var backButton: ImageButton

    // Ползунки эмоций
    private lateinit var happinessSeekBar: SeekBar
    private lateinit var calmSeekBar: SeekBar
    private lateinit var excitementSeekBar: SeekBar
    private lateinit var anxietySeekBar: SeekBar
    private lateinit var angerSeekBar: SeekBar
    private lateinit var sadnessSeekBar: SeekBar

    // Текстовые поля для отображения значений ползунков
    private lateinit var happinessValueText: TextView
    private lateinit var calmValueText: TextView
    private lateinit var excitementValueText: TextView
    private lateinit var anxietyValueText: TextView
    private lateinit var angerValueText: TextView
    private lateinit var sadnessValueText: TextView

    private lateinit var emotionRepository: EmotionRepository
    private lateinit var authManager: AuthManager

    private var entryDate: Date = Date() // По умолчанию текущая дата

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emotionRepository = EmotionRepository(requireContext())
        authManager = AuthManager(requireContext())

        // Инициализация UI элементов
        initializeViews(view)

        // Установка даты
        setupDateDisplay()

        // Настройка ползунков
        setupEmotionSliders()

        // Настройка кнопок
        setupButtons()

        // Проверка наличия существующей записи на эту дату
        checkExistingEntry()

        view.findViewById<View>(R.id.save_button).setOnClickListener {
            // Navigate to diary fragment to add a new entry
            parentFragmentManager.beginTransaction()
                saveEntry()
        }
    }

    private fun initializeViews(view: View) {
        mainFeelingsEditText = view.findViewById(R.id.main_feelings_edit_text)
        additionalInfoEditText = view.findViewById(R.id.additional_info_edit_text)
        dateTextView = view.findViewById(R.id.date_text_view)
        backButton = view.findViewById(R.id.back_button)

        // Инициализация ползунков
        happinessSeekBar = view.findViewById(R.id.happiness_seekbar)
        calmSeekBar = view.findViewById(R.id.calm_seekbar)
        excitementSeekBar = view.findViewById(R.id.excitement_seekbar)
        anxietySeekBar = view.findViewById(R.id.anxiety_seekbar)
        angerSeekBar = view.findViewById(R.id.anger_seekbar)
        sadnessSeekBar = view.findViewById(R.id.sadness_seekbar)

        // Инициализация текстовых полей для значений
        happinessValueText = view.findViewById(R.id.happiness_value)
        calmValueText = view.findViewById(R.id.calm_value)
        excitementValueText = view.findViewById(R.id.excitement_value)
        anxietyValueText = view.findViewById(R.id.anxiety_value)
        angerValueText = view.findViewById(R.id.anger_value)
        sadnessValueText = view.findViewById(R.id.sadness_value)
    }

    private fun setupDateDisplay() {
        // Получаем дату из аргументов, если она была передана
        arguments?.getLong("date")?.let {
            entryDate = Date(it)
        }

        // Форматируем и отображаем дату
        val dateFormat = java.text.SimpleDateFormat("d MMMM yyyy", Locale("ru"))
        dateTextView.text = dateFormat.format(entryDate)
    }

    private fun setupEmotionSliders() {
        // Настройка ползунка счастья
        setupSlider(happinessSeekBar, happinessValueText)

        // Настройка ползунка спокойствия
        setupSlider(calmSeekBar, calmValueText)

        // Настройка ползунка возбуждения
        setupSlider(excitementSeekBar, excitementValueText)

        // Настройка ползунка тревоги
        setupSlider(anxietySeekBar, anxietyValueText)

        // Настройка ползунка гнева
        setupSlider(angerSeekBar, angerValueText)

        // Настройка ползунка грусти
        setupSlider(sadnessSeekBar, sadnessValueText)
    }

    private fun setupSlider(seekBar: SeekBar, valueText: TextView) {
        // Устанавливаем начальное значение
        seekBar.progress = 5
        updateValueText(valueText, 5)

        // Добавляем слушатель изменений
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateValueText(valueText, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateValueText(textView: TextView, value: Int) {
        textView.text = value.toString()
    }

    private fun setupButtons() {
        // Кнопка назад
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Кнопка сохранения
    }

    private fun checkExistingEntry() {
        val userId = authManager.getUserId() ?: return

        // Получаем запись на эту дату, если она существует
        val existingEntry = emotionRepository.getEntryForDate(userId, entryDate)

        if (existingEntry != null) {
            // Заполняем поля существующими данными
            mainFeelingsEditText.setText(existingEntry.overallFeeling)
            additionalInfoEditText.setText(existingEntry.comments)

            // Устанавливаем значения ползунков
            happinessSeekBar.progress = existingEntry.happinessValue
            calmSeekBar.progress = existingEntry.calmValue
            excitementSeekBar.progress = existingEntry.excitementValue
            anxietySeekBar.progress = existingEntry.anxietyValue
            angerSeekBar.progress = existingEntry.angerValue
            sadnessSeekBar.progress = existingEntry.sadnessValue

            // Обновляем текстовые поля значений
            updateValueText(happinessValueText, existingEntry.happinessValue)
            updateValueText(calmValueText, existingEntry.calmValue)
            updateValueText(excitementValueText, existingEntry.excitementValue)
            updateValueText(anxietyValueText, existingEntry.anxietyValue)
            updateValueText(angerValueText, existingEntry.angerValue)
            updateValueText(sadnessValueText, existingEntry.sadnessValue)
        }
    }

    private fun saveEntry() {
        val userId = authManager.getUserId() ?: return

        val mainFeelings = mainFeelingsEditText.text.toString().trim()
        val additionalInfo = additionalInfoEditText.text.toString().trim()

        // Создаем объект значений эмоций
        val emotionValues = EmotionValues(
            happinessValue = happinessSeekBar.progress,
            calmValue = calmSeekBar.progress,
            excitementValue = excitementSeekBar.progress,
            anxietyValue = anxietySeekBar.progress,
            angerValue = angerSeekBar.progress,
            sadnessValue = sadnessSeekBar.progress
        )

        // Определяем доминирующую эмоцию
        val dominantEmotion = emotionValues.getDominantEmotion()

        // Создаем объект записи
        val entry = EmotionEntry(
            userId = userId,
            date = entryDate,
            emotionType = dominantEmotion,
            overallFeeling = mainFeelings,
            comments = additionalInfo,
            // Добавляем значения всех эмоций
            happinessValue = happinessSeekBar.progress,
            calmValue = calmSeekBar.progress,
            excitementValue = excitementSeekBar.progress,
            anxietyValue = anxietySeekBar.progress,
            angerValue = angerSeekBar.progress,
            sadnessValue = sadnessSeekBar.progress
        )

        // Сохраняем запись
        emotionRepository.saveEntry(entry)

        // Показываем сообщение об успешном сохранении
        //Toast.makeText(context, "Запись сохранена", Toast.LENGTH_SHORT).show()

        // Возвращаемся на предыдущий экран
        requireActivity().supportFragmentManager.popBackStack()
    }
}
