package com.example.emotionaldiary.data

import android.content.Context
import android.content.SharedPreferences
import com.example.emotionaldiary.models.EmotionEntry
import com.example.emotionaldiary.models.EmotionType
import com.example.emotionaldiary.models.EmotionValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class EmotionRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("emotion_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * Получает все записи для указанного пользователя
     */
    fun getAllEntriesForUser(userId: String): List<EmotionEntry> {
        val entriesJson = sharedPreferences.getString("entries_$userId", null) ?: return emptyList()

        val type = object : TypeToken<List<EmotionEntry>>() {}.type
        return gson.fromJson(entriesJson, type) ?: emptyList()
    }

    /**
     * Получает запись для указанной даты
     */
    fun getEntryForDate(userId: String, date: Date): EmotionEntry? {
        val entries = getAllEntriesForUser(userId)

        // Создаем календарь для сравнения дат без учета времени
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val targetDate = calendar.time

        // Ищем запись с совпадающей датой (без учета времени)
        return entries.find { entry ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entry.date
            entryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            entryCalendar.set(Calendar.MINUTE, 0)
            entryCalendar.set(Calendar.SECOND, 0)
            entryCalendar.set(Calendar.MILLISECOND, 0)

            entryCalendar.time == targetDate
        }
    }

    /**
     * Сохраняет запись
     */
    fun saveEntry(entry: EmotionEntry) {
        val entries = getAllEntriesForUser(entry.userId).toMutableList()

        // Проверяем, существует ли уже запись на эту дату
        val existingEntryIndex = entries.indexOfFirst { existingEntry ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = existingEntry.date
            entryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            entryCalendar.set(Calendar.MINUTE, 0)
            entryCalendar.set(Calendar.SECOND, 0)
            entryCalendar.set(Calendar.MILLISECOND, 0)

            val newEntryCalendar = Calendar.getInstance()
            newEntryCalendar.time = entry.date
            newEntryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            newEntryCalendar.set(Calendar.MINUTE, 0)
            newEntryCalendar.set(Calendar.SECOND, 0)
            newEntryCalendar.set(Calendar.MILLISECOND, 0)

            entryCalendar.time == newEntryCalendar.time
        }

        if (existingEntryIndex != -1) {
            // Обновляем существующую запись
            entries[existingEntryIndex] = entry
        } else {
            // Добавляем новую запись
            entries.add(entry)
        }

        // Сохраняем обновленный список
        val entriesJson = gson.toJson(entries)
        sharedPreferences.edit().putString("entries_${entry.userId}", entriesJson).apply()
    }

    /**
     * Удаляет запись
     */
    fun deleteEntry(userId: String, date: Date) {
        val entries = getAllEntriesForUser(userId).toMutableList()

        // Создаем календарь для сравнения дат без учета времени
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val targetDate = calendar.time

        // Удаляем запись с совпадающей датой
        val updatedEntries = entries.filterNot { entry ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entry.date
            entryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            entryCalendar.set(Calendar.MINUTE, 0)
            entryCalendar.set(Calendar.SECOND, 0)
            entryCalendar.set(Calendar.MILLISECOND, 0)

            entryCalendar.time == targetDate
        }

        // Сохраняем обн��вленный список
        val entriesJson = gson.toJson(updatedEntries)
        sharedPreferences.edit().putString("entries_$userId", entriesJson).apply()
    }

    /**
     * Получает записи между указанными датами
     */
    fun getEntriesBetweenDates(userId: String, startDate: Date, endDate: Date): List<EmotionEntry> {
        val allEntries = getAllEntriesForUser(userId)

        return allEntries.filter { entry ->
            entry.date >= startDate && entry.date <= endDate
        }
    }

    /**
     * Получает записи за указанный месяц и год
     */
    fun getEntriesForMonth(userId: String, year: Int, month: Int): List<EmotionEntry> {
        val allEntries = getAllEntriesForUser(userId)

        // Создаем календарь для начала месяца
        val startCalendar = Calendar.getInstance()
        startCalendar.set(year, month, 1, 0, 0, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)

        // Создаем календарь для конца месяца
        val endCalendar = Calendar.getInstance()
        endCalendar.set(year, month, 1, 0, 0, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)
        endCalendar.add(Calendar.MONTH, 1)
        endCalendar.add(Calendar.MILLISECOND, -1)

        // Фильтруем записи, которые попадают в указанный месяц
        return allEntries.filter { entry ->
            val entryDate = entry.date
            entryDate >= startCalendar.time && entryDate <= endCalendar.time
        }
    }

    /**
     * Получает статистику эмоций за указанный период
     */
    fun getEmotionStatistics(userId: String, startDate: Date, endDate: Date): Map<EmotionType, Int> {
        val entries = getEntriesBetweenDates(userId, startDate, endDate)

        // Подсчитываем количество каждого типа эмоций
        val statistics = mutableMapOf<EmotionType, Int>()

        for (entry in entries) {
            val count = statistics.getOrDefault(entry.emotionType, 0)
            statistics[entry.emotionType] = count + 1
        }

        return statistics
    }

    /**
     * Получает средние значения эмоций за указанный период
     */
    fun getAverageEmotionValues(userId: String, startDate: Date, endDate: Date): EmotionValues {
        val entries = getEntriesBetweenDates(userId, startDate, endDate)

        if (entries.isEmpty()) {
            return EmotionValues()
        }

        var totalHappiness = 0
        var totalCalm = 0
        var totalExcitement = 0
        var totalAnxiety = 0
        var totalAnger = 0
        var totalSadness = 0

        for (entry in entries) {
            totalHappiness += entry.happinessValue
            totalCalm += entry.calmValue
            totalExcitement += entry.excitementValue
            totalAnxiety += entry.anxietyValue
            totalAnger += entry.angerValue
            totalSadness += entry.sadnessValue
        }

        val count = entries.size

        return EmotionValues(
            happinessValue = totalHappiness / count,
            calmValue = totalCalm / count,
            excitementValue = totalExcitement / count,
            anxietyValue = totalAnxiety / count,
            angerValue = totalAnger / count,
            sadnessValue = totalSadness / count
        )
    }
}
