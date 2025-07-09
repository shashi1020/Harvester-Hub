package com.example.harvesterhub.utils


import android.content.Context

fun saveLastFetchDate(context: Context, dateString: String) {
    val prefs = context.getSharedPreferences("market_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("last_fetch_date", dateString).apply()
}

fun getLastFetchDate(context: Context): String? {
    val prefs = context.getSharedPreferences("market_prefs", Context.MODE_PRIVATE)
    return prefs.getString("last_fetch_date", null)
}
