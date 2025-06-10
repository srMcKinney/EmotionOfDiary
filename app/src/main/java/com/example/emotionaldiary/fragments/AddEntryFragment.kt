package com.example.emotionaldiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.emotionaldiary.R
import com.example.emotionaldiary.auth.AuthManager
import com.example.emotionaldiary.data.EmotionRepository
import com.example.emotionaldiary.models.EmotionEntry
import com.example.emotionaldiary.models.EmotionValues
import java.util.*

class AddEntryFragment : Fragment() {

    private lateinit var additionalInfoEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var backButton: ImageButton

    private lateinit var feelingsSeekBar: SeekBar
    private lateinit var feelingsDescription: TextView

    private lateinit var emotionRepository: EmotionRepository
    private lateinit var authManager: AuthManager

    private var entryDate: Date = Date()

    private val feelingsLevels = listOf(
        "Неприятные",
        "Отчасти неприятные",
        "Нейтральные",
        "Отчасти приятные",
        "Приятные"
    )

    // Основные эмоции
    private lateinit var happinessSeekBar: SeekBar
    private lateinit var calmSeekBar: SeekBar
    private lateinit var excitementSeekBar: SeekBar
    private lateinit var anxietySeekBar: SeekBar
    private lateinit var angerSeekBar: SeekBar
    private lateinit var sadnessSeekBar: SeekBar
    private lateinit var interestSeekBar: SeekBar
    private lateinit var surpriseSeekBar: SeekBar

    private lateinit var happinessValueText: TextView
    private lateinit var calmValueText: TextView
    private lateinit var excitementValueText: TextView
    private lateinit var anxietyValueText: TextView
    private lateinit var angerValueText: TextView
    private lateinit var sadnessValueText: TextView
    private lateinit var interestValueText: TextView
    private lateinit var surpriseValueText: TextView

    // Дополнительные эмоции (19)
    private val additionalEmotionsIds = mapOf(
        "anticipation" to R.id.seekBarAnticipation,
        "alertness" to R.id.seekBarAlertness,
        "serenity" to R.id.seekBarSerenity,
        "ecstasy" to R.id.seekBarEcstasy,
        "acceptance" to R.id.seekBarAcceptance,
        "admiration" to R.id.seekBarAdmiration,
        "anxiety2" to R.id.seekBarAnxiety,
        "terror" to R.id.seekBarTerror,
        "confusion" to R.id.seekBarConfusion,
        "amazement" to R.id.seekBarAmazement,
        "sadness2" to R.id.seekBarSadness,
        "grief" to R.id.seekBarGrief,
        "boredom" to R.id.seekBarBoredom,
        "displeasure" to R.id.seekBarDispleasure,
        "annoyance" to R.id.seekBarAnnoyance,
        "anger2" to R.id.seekBarAnger,
        "embarrassment" to R.id.seekBarEmbarrassment,
        "shame" to R.id.seekBarShame,
        "guilt" to R.id.seekBarGuilt
    )
    private val additionalSeekBars = mutableMapOf<String, SeekBar>()
    private val additionalValueTexts = mutableMapOf<String, TextView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_entry, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emotionRepository = EmotionRepository(requireContext())
        authManager = AuthManager(requireContext())

        initializeViews(view)
        setupDateDisplay()
        setupEmotionSliders()
        setupOverallFeelingSlider()
        setupAdditionalEmotionSliders()
        setupButtons()
        checkExistingEntry()

        view.findViewById<View>(R.id.save_button).setOnClickListener {
            saveEntry()
        }
    }

    private fun initializeViews(view: View) {
        additionalInfoEditText = view.findViewById(R.id.additional_info_edit_text)
        dateTextView = view.findViewById(R.id.date_text_view)
        backButton = view.findViewById(R.id.back_button)

        feelingsSeekBar = view.findViewById(R.id.feelings_seekbar)
        feelingsDescription = view.findViewById(R.id.feelings_description)

        happinessSeekBar = view.findViewById(R.id.happiness_seekbar)
        calmSeekBar = view.findViewById(R.id.calm_seekbar)
        excitementSeekBar = view.findViewById(R.id.excitement_seekbar)
        anxietySeekBar = view.findViewById(R.id.anxiety_seekbar)
        angerSeekBar = view.findViewById(R.id.anger_seekbar)
        sadnessSeekBar = view.findViewById(R.id.sadness_seekbar)
        interestSeekBar = view.findViewById(R.id.interest_seekbar)
        surpriseSeekBar = view.findViewById(R.id.surprise_seekbar)

        happinessValueText = view.findViewById(R.id.happiness_value)
        calmValueText = view.findViewById(R.id.calm_value)
        excitementValueText = view.findViewById(R.id.excitement_value)
        anxietyValueText = view.findViewById(R.id.anxiety_value)
        angerValueText = view.findViewById(R.id.anger_value)
        sadnessValueText = view.findViewById(R.id.sadness_value)
        interestValueText = view.findViewById(R.id.interest_value)
        surpriseValueText = view.findViewById(R.id.surprise_value)

        additionalEmotionsIds.forEach { (key, id) ->
            additionalSeekBars[key] = view.findViewById(id)
        }

        additionalValueTexts["anticipation"] = view.findViewById(R.id.AnticipationValue)
        additionalValueTexts["alertness"] = view.findViewById(R.id.AlertnessValue)
        additionalValueTexts["serenity"] = view.findViewById(R.id.SerenityValue)
        additionalValueTexts["ecstasy"] = view.findViewById(R.id.EcstasyValue)
        additionalValueTexts["acceptance"] = view.findViewById(R.id.AcceptanceValue)
        additionalValueTexts["admiration"] = view.findViewById(R.id.AdmirationValue)
        additionalValueTexts["anxiety2"] = view.findViewById(R.id.AnxietyValue)
        additionalValueTexts["terror"] = view.findViewById(R.id.TerrorValue)
        additionalValueTexts["confusion"] = view.findViewById(R.id.ConfusionValue)
        additionalValueTexts["amazement"] = view.findViewById(R.id.AmazementValue)
        additionalValueTexts["sadness2"] = view.findViewById(R.id.SadnessValue)
        additionalValueTexts["grief"] = view.findViewById(R.id.GriefValue)
        additionalValueTexts["boredom"] = view.findViewById(R.id.BoredomValue)
        additionalValueTexts["displeasure"] = view.findViewById(R.id.DispleasureValue)
        additionalValueTexts["annoyance"] = view.findViewById(R.id.AnnoyanceValue)
        additionalValueTexts["anger2"] = view.findViewById(R.id.AngerValue)
        additionalValueTexts["embarrassment"] = view.findViewById(R.id.EmbarrassmentValue)
        additionalValueTexts["shame"] = view.findViewById(R.id.ShameValue)
        additionalValueTexts["guilt"] = view.findViewById(R.id.GuiltValue)
    }

    private fun setupDateDisplay() {
        arguments?.getLong("date")?.let {
            entryDate = Date(it)
        }
        val dateFormat = java.text.SimpleDateFormat("d MMMM yyyy", Locale("ru"))
        dateTextView.text = dateFormat.format(entryDate)
    }

    private fun setupEmotionSliders() {
        setupSlider(happinessSeekBar, happinessValueText)
        setupSlider(calmSeekBar, calmValueText)
        setupSlider(excitementSeekBar, excitementValueText)
        setupSlider(anxietySeekBar, anxietyValueText)
        setupSlider(angerSeekBar, angerValueText)
        setupSlider(sadnessSeekBar, sadnessValueText)
        setupSlider(interestSeekBar, interestValueText)
        setupSlider(surpriseSeekBar, surpriseValueText)

        additionalSeekBars.values.forEach {
            it.progress = 0
        }
    }

    private fun setupSlider(seekBar: SeekBar, valueText: TextView) {
        seekBar.progress = 5
        updateValueText(valueText, 5)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateValueText(valueText, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupAdditionalEmotionSliders() {
        additionalSeekBars.forEach { (key, seekBar) ->
            val valueText = additionalValueTexts[key]
            if (valueText != null) {
                valueText.text = seekBar.progress.toString()
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                        valueText.text = progress.toString()
                    }
                    override fun onStartTrackingTouch(sb: SeekBar?) {}
                    override fun onStopTrackingTouch(sb: SeekBar?) {}
                })
            }
        }
    }

    private fun updateValueText(textView: TextView, value: Int) {
        textView.text = value.toString()
    }

    private fun setupOverallFeelingSlider() {
        feelingsSeekBar.max = 4
        feelingsSeekBar.progress = 2
        feelingsDescription.text = feelingsLevels[2]
        feelingsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                feelingsDescription.text = feelingsLevels[progress]
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun checkExistingEntry() {
        val userId = authManager.getUserId() ?: return
        val existingEntry = emotionRepository.getEntryForDate(userId, entryDate) ?: return

        val index = feelingsLevels.indexOf(existingEntry.overallFeeling)
        if (index != -1) {
            feelingsSeekBar.progress = index
            feelingsDescription.text = feelingsLevels[index]
        }

        additionalInfoEditText.setText(existingEntry.comments)

        happinessSeekBar.progress = existingEntry.happinessValue
        calmSeekBar.progress = existingEntry.calmValue
        excitementSeekBar.progress = existingEntry.excitementValue
        anxietySeekBar.progress = existingEntry.anxietyValue
        angerSeekBar.progress = existingEntry.angerValue
        sadnessSeekBar.progress = existingEntry.sadnessValue
        interestSeekBar.progress = existingEntry.interestValue
        surpriseSeekBar.progress = existingEntry.surpriseValue

        updateValueText(happinessValueText, existingEntry.happinessValue)
        updateValueText(calmValueText, existingEntry.calmValue)
        updateValueText(excitementValueText, existingEntry.excitementValue)
        updateValueText(anxietyValueText, existingEntry.anxietyValue)
        updateValueText(angerValueText, existingEntry.angerValue)
        updateValueText(sadnessValueText, existingEntry.sadnessValue)
        updateValueText(interestValueText, existingEntry.interestValue)
        updateValueText(surpriseValueText, existingEntry.surpriseValue)

        additionalEmotionsIds.forEach { (key, _) ->
            val saved = existingEntry.additionalEmotions[key]
            if (saved != null) {
                additionalSeekBars[key]?.progress = saved
                additionalValueTexts[key]?.text = saved.toString()
            }
        }
    }

    private fun saveEntry() {
        val userId = authManager.getUserId() ?: return

        val mainFeelings = feelingsLevels[feelingsSeekBar.progress]
        val additionalInfo = additionalInfoEditText.text.toString().trim()

        val emotionValues = EmotionValues(
            happinessValue = happinessSeekBar.progress,
            calmValue = calmSeekBar.progress,
            excitementValue = excitementSeekBar.progress,
            anxietyValue = anxietySeekBar.progress,
            angerValue = angerSeekBar.progress,
            sadnessValue = sadnessSeekBar.progress,
            interestValue = interestSeekBar.progress,
            surpriseValue = surpriseSeekBar.progress
        )

        val dominantEmotion = emotionValues.getDominantEmotion()

        val additionalEmotions = additionalSeekBars.mapValues { it.value.progress }

        val entry = EmotionEntry(
            userId = userId,
            date = entryDate,
            emotionType = dominantEmotion,
            overallFeeling = mainFeelings,
            comments = additionalInfo,
            happinessValue = happinessSeekBar.progress,
            calmValue = calmSeekBar.progress,
            excitementValue = excitementSeekBar.progress,
            anxietyValue = anxietySeekBar.progress,
            angerValue = angerSeekBar.progress,
            sadnessValue = sadnessSeekBar.progress,
            interestValue = interestSeekBar.progress,
            surpriseValue = surpriseSeekBar.progress,
            additionalEmotions = additionalEmotions
        )

        emotionRepository.saveEntry(entry)
        requireActivity().supportFragmentManager.popBackStack()
    }
}