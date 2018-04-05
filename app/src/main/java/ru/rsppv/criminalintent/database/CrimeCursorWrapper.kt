package ru.rsppv.criminalintent.database

import android.database.Cursor
import android.database.CursorWrapper
import ru.rsppv.criminalintent.database.CrimeDbSchema.CrimeTable
import ru.rsppv.criminalintent.model.Crime
import java.util.*

class CrimeCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {

    fun getCrime(): Crime {
        val uuid = UUID.fromString(getString(getColumnIndex(CrimeTable.UUID)))
        return Crime(uuid).apply {
            title = getString(getColumnIndex(CrimeTable.TITLE))
            date = Date(getLong(getColumnIndex(CrimeTable.DATE)))
            isSolved = getInt(getColumnIndex(CrimeTable.SOLVED)) != 0
            suspect = getString(getColumnIndex(CrimeTable.SUSPECT))
        }
    }
}