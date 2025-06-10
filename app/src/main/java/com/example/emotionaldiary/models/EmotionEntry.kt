package com.example.emotionaldiary.models

import java.util.*

data class EmotionEntry(
    val userId: String,
    val date: Date,
    val emotionType: EmotionType,
    val overallFeeling: String = "",
    val comments: String = "",

    // Основные 8 эмоций
    val happinessValue: Int = 5,
    val calmValue: Int = 5,
    val excitementValue: Int = 5,
    val anxietyValue: Int = 5,
    val angerValue: Int = 5,
    val sadnessValue: Int = 5,
    val interestValue: Int = 5,
    val surpriseValue: Int = 5,

    // Дополнительные 19 эмоций
    val additionalEmotions: Map<String, Int> = emptyMap()
)

enum class EmotionType {
    NEUTRAL,
    CALM,
    HAPPY,
    EXCITED,
    ANXIOUS,
    ANGRY,
    SAD,
    INTEREST,
    SURPRISE
}