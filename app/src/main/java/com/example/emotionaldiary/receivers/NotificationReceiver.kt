package com.example.emotionaldiary.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.emotionaldiary.utils.NotificationScheduler

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationScheduler = NotificationScheduler(context)
        notificationScheduler.showNotification()
    }
}
