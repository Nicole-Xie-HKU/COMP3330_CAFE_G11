package com.group11.cafe.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {

    fun isOnline(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetwork != null
    }
}
