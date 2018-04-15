package ru.rsppv.criminalintent.model

import android.content.Context
import android.text.format.DateFormat
import java.util.*


class Crime(val id: UUID) {

    constructor() : this(UUID.randomUUID())

    var title: String? = null
    var date: Date = Date()
    var isSolved: Boolean = false
    var suspect: String? = null

    fun getDateString(context: Context?): String? {
        return DateFormat.getDateFormat(context).format(date)
    }

    fun getPhotoFilename() = "IMG_$id.jpg"
}
