package ru.rsppv.criminalintent.model

import java.text.SimpleDateFormat
import java.util.*


class Crime(val id: UUID) {

    constructor() : this(UUID.randomUUID())

    var title: String? = null
    var date: Date = Date()
    var isSolved: Boolean = false

    val dateString: String
        get() {
            val format = SimpleDateFormat(DATE_PATTERN)
            return format.format(date)
        }

    companion object {
        private const val DATE_PATTERN = "EEE, d MMM yyyy"
    }
}
