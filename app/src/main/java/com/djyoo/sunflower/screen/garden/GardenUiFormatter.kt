package com.djyoo.sunflower.screen.garden

import android.content.Context
import com.djyoo.sunflower.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GardenUiFormatter {

    private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

    fun formatTodayDate(): String = dateFormatter.format(Date())

    fun formatWateringMessage(context: Context, wateringInterval: Int): String {
        return context.getString(R.string.garden_watering_message, wateringInterval)
    }
}

