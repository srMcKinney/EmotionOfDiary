package com.example.emotionaldiary.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.emotionaldiary.R
import com.example.emotionaldiary.models.EmotionEntry
import com.example.emotionaldiary.models.EmotionType
import java.util.*

class EmotionChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = context.getColor(R.color.text_primary)
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private val labelPaint = Paint().apply {
        color = context.getColor(R.color.text_secondary)
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private var entries: List<EmotionEntry> = emptyList()
    private val emotionColors = mapOf(
        EmotionType.CALM to Color.parseColor("#4CAF50"),
        EmotionType.HAPPY to Color.parseColor("#8BC34A"),
        EmotionType.EXCITED to Color.parseColor("#CDDC39"),
        EmotionType.NEUTRAL to Color.parseColor("#9E9E9E"),
        EmotionType.ANXIOUS to Color.parseColor("#FFC107"),
        EmotionType.ANGRY to Color.parseColor("#FF5722"),
        EmotionType.SAD to Color.parseColor("#2196F3")
    )

    fun setData(entries: List<EmotionEntry>) {
        this.entries = entries
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (entries.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val padding = 50f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding

        // Draw grid lines
        for (i in 0..4) {
            val y = padding + chartHeight * (1 - i / 4f)
            canvas.drawLine(padding, y, width - padding, y, gridPaint)
            canvas.drawText("${i * 25}%", padding / 2, y + 10, labelPaint)
        }

        // Group entries by emotion type
        val emotionCounts = mutableMapOf<EmotionType, Int>()
        for (entry in entries) {
            emotionCounts[entry.emotionType] = (emotionCounts[entry.emotionType] ?: 0) + 1
        }

        // Calculate percentages
        val totalEntries = entries.size
        val emotionPercentages = emotionCounts.mapValues { (it.value.toFloat() / totalEntries) * 100 }

        // Draw bars
        val barWidth = chartWidth / (emotionPercentages.size * 2)
        var x = padding + barWidth / 2

        for ((emotion, percentage) in emotionPercentages) {
            val barHeight = (percentage / 100) * chartHeight
            val rect = RectF(x - barWidth / 2, height - padding - barHeight, x + barWidth / 2, height - padding)

            barPaint.color = emotionColors[emotion] ?: Color.GRAY
            canvas.drawRect(rect, barPaint)

            // Draw emotion label
            canvas.drawText(emotion.name.lowercase().capitalize(), x, height - padding + 30, labelPaint)

            // Draw percentage
            canvas.drawText("${percentage.toInt()}%", x, height - padding - barHeight - 10, textPaint)

            x += barWidth * 2
        }
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}