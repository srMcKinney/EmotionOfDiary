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

    fun getAllEntriesForUser(userId: String): List<EmotionEntry> {
        val entriesJson = sharedPreferences.getString("entries_$userId", null) ?: return emptyList()

        val type = object : TypeToken<List<EmotionEntry>>() {}.type
        return gson.fromJson(entriesJson, type) ?: emptyList()
    }

    fun getEntryForDate(userId: String, date: Date): EmotionEntry? {
        val entries = getAllEntriesForUser(userId)

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val targetDate = calendar.time

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

    fun saveEntry(entry: EmotionEntry) {
        val entries = getAllEntriesForUser(entry.userId).toMutableList()

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
            entries[existingEntryIndex] = entry
        } else {
            entries.add(entry)
        }

        val entriesJson = gson.toJson(entries)
        sharedPreferences.edit().putString("entries_${entry.userId}", entriesJson).apply()
    }

    fun deleteEntry(userId: String, date: Date) {
        val entries = getAllEntriesForUser(userId).toMutableList()

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val targetDate = calendar.time

        val updatedEntries = entries.filterNot { entry ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entry.date
            entryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            entryCalendar.set(Calendar.MINUTE, 0)
            entryCalendar.set(Calendar.SECOND, 0)
            entryCalendar.set(Calendar.MILLISECOND, 0)

            entryCalendar.time == targetDate
        }

        val entriesJson = gson.toJson(updatedEntries)
        sharedPreferences.edit().putString("entries_$userId", entriesJson).apply()
    }

    fun getEntriesBetweenDates(userId: String, startDate: Date, endDate: Date): List<EmotionEntry> {
        val allEntries = getAllEntriesForUser(userId)

        return allEntries.filter { entry ->
            entry.date >= startDate && entry.date <= endDate
        }
    }

    fun getEntriesForMonth(userId: String, year: Int, month: Int): List<EmotionEntry> {
        val allEntries = getAllEntriesForUser(userId)

        val startCalendar = Calendar.getInstance()
        startCalendar.set(year, month, 1, 0, 0, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)

        val endCalendar = Calendar.getInstance()
        endCalendar.set(year, month, 1, 0, 0, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)
        endCalendar.add(Calendar.MONTH, 1)
        endCalendar.add(Calendar.MILLISECOND, -1)

        return allEntries.filter { entry ->
            val entryDate = entry.date
            entryDate >= startCalendar.time && entryDate <= endCalendar.time
        }
    }

    fun getEmotionStatistics(userId: String, startDate: Date, endDate: Date): Map<EmotionType, Int> {
        val entries = getEntriesBetweenDates(userId, startDate, endDate)

        val statistics = mutableMapOf<EmotionType, Int>()

        for (entry in entries) {
            val count = statistics.getOrDefault(entry.emotionType, 0)
            statistics[entry.emotionType] = count + 1
        }

        return statistics
    }

    fun getAverageEmotionValues(userId: String, startDate: Date, endDate: Date): EmotionValues {
        val entries = getEntriesBetweenDates(userId, startDate, endDate)

        if (entries.isEmpty()) return EmotionValues()

        var totalHappiness = 0
        var totalCalm = 0
        var totalExcitement = 0
        var totalAnxiety = 0
        var totalAnger = 0
        var totalSadness = 0
        var totalInterest = 0
        var totalSurprise = 0

        val additionalEmotionTotals = mutableMapOf<String, Int>()

        for (entry in entries) {
            totalHappiness += entry.happinessValue
            totalCalm += entry.calmValue
            totalExcitement += entry.excitementValue
            totalAnxiety += entry.anxietyValue
            totalAnger += entry.angerValue
            totalSadness += entry.sadnessValue
            totalInterest += entry.interestValue
            totalSurprise += entry.surpriseValue

            for ((key, value) in entry.additionalEmotions) {
                additionalEmotionTotals[key] = additionalEmotionTotals.getOrDefault(key, 0) + value
            }
        }

        val count = entries.size
        val averageAdditional = additionalEmotionTotals.mapValues { it.value / count }

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
