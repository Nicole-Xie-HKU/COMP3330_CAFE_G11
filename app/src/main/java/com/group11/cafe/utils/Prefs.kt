package com.group11.cafe.utils

import android.content.Context

object Prefs {

    private const val FILE_NAME = "cafe"

    fun saveLastEvent(context: Context, eventId: String, eventName: String) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("lastEventId", eventId)
            .putString("lastEventName", eventName)
            .apply()
    }

    fun lastEventId(context: Context): String {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .getString("lastEventId", "")
            .orEmpty()
    }

    fun lastEventName(context: Context): String {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .getString("lastEventName", "")
            .orEmpty()
    }
}
