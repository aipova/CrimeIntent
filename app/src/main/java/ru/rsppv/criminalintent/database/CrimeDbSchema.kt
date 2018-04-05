package ru.rsppv.criminalintent.database

object CrimeDbSchema {

    object CrimeTable {
        const val TABLE_NAME = "crimes"

        const val UUID = "uuid"
        const val TITLE = "title"
        const val DATE = "date"
        const val SOLVED = "solved"
        const val SUSPECT = "suspect"
    }
}