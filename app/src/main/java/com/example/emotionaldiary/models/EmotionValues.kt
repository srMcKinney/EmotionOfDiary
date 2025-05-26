package com.example.emotionaldiary.models

/**
 * Класс для хранения значений эмоций
 */
data class EmotionValues(
    val happinessValue: Int = 5,
    val calmValue: Int = 5,
    val excitementValue: Int = 5,
    val anxietyValue: Int = 5,
    val angerValue: Int = 5,
    val sadnessValue: Int = 5
) {
    /**
     * Получает доминирующую эмоцию на основе значений
     */
    fun getDominantEmotion(): EmotionType {
        val emotions = mapOf(
            EmotionType.HAPPY to happinessValue,
            EmotionType.CALM to calmValue,
            EmotionType.EXCITED to excitementValue,
            EmotionType.ANXIOUS to anxietyValue,
            EmotionType.ANGRY to angerValue,
            EmotionType.SAD to sadnessValue
        )

        // Находим эмоцию с максимальным значением
        val maxEmotion = emotions.maxByOrNull { it.value }

        // Если все эмоции имеют одинаковое значение или близкое к среднему,
        // возвращаем NEUTRAL, иначе возвращаем доминирующую эмоцию
        return if (maxEmotion != null && maxEmotion.value > 6) {
            maxEmotion.key
        } else {
            EmotionType.NEUTRAL
        }
    }

    /**
     * Получает среднее значение всех эмоций
     */
    fun getAverageValue(): Float {
        return (happinessValue + calmValue + excitementValue +
                anxietyValue + angerValue + sadnessValue) / 6.0f
    }

    /**
     * Получает общую интенсивность эмоций
     */
    fun getTotalIntensity(): Int {
        return happinessValue + calmValue + excitementValue +
                anxietyValue + angerValue + sadnessValue
    }
}
