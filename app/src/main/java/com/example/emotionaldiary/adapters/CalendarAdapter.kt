package com.example.emotionaldiary.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.emotionaldiary.R
import java.util.*

class CalendarAdapter(private val context: Context, private val dates: List<Date>, private val currentMonth: Int) : BaseAdapter() {

    private val calendar: Calendar = Calendar.getInstance()
    private val today: Date = Calendar.getInstance().time
    private val highlightedDates: MutableSet<Int> = mutableSetOf(10, 26) // Example dates with entries

    override fun getCount(): Int = dates.size

    override fun getItem(position: Int): Any = dates[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val date = dates[position]
        calendar.time = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.calendar_day_item, parent, false)
        val dayText = view.findViewById<TextView>(R.id.day_text)

        // Set the day number
        dayText.text = day.toString()

        // Style based on whether the date is in the current month
        if (month != currentMonth) {
            dayText.setTextColor(context.getColor(R.color.text_secondary))
        } else {
            dayText.setTextColor(context.getColor(R.color.text_primary))
        }

        // Highlight today
        if (isSameDay(date, today)) {
            dayText.setTextColor(Color.WHITE)
            dayText.setBackgroundResource(R.drawable.today_background)
        } else {
            dayText.background = null
        }

        // Highlight dates with entries
        if (highlightedDates.contains(day) && month == currentMonth) {
            dayText.setTextColor(context.getColor(R.color.blue_accent))
        }

        return view
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}