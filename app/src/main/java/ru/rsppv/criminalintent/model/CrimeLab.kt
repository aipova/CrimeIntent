package ru.rsppv.criminalintent.model


import android.content.ContentValues
import android.content.Context
import ru.rsppv.criminalintent.database.CrimeBaseHelper
import ru.rsppv.criminalintent.database.CrimeCursorWrapper
import ru.rsppv.criminalintent.database.CrimeDbSchema.CrimeTable
import java.io.File
import java.util.*

class CrimeLab private constructor(val context: Context?) {
    private val mDatabase = CrimeBaseHelper(context).writableDatabase

    fun getAllCrimes(): MutableList<Crime> {
        val crimes = mutableListOf<Crime>()
        queryCrimes(null, null).use { cursor ->
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                crimes.add(cursor.getCrime())
                cursor.moveToNext()
            }
        }
        return crimes
    }

    fun getCrime(id: UUID): Crime? {
        queryCrimes("${CrimeTable.UUID} = ?", arrayOf(id.toString())).use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                return cursor.getCrime()
            }
            return null
        }
    }

    fun getPhotoFile(crime: Crime) = File(context?.filesDir, crime.getPhotoFilename())

    fun addCrime(crime: Crime) {
        mDatabase.insert(CrimeTable.TABLE_NAME, null, getContentValues(crime))
    }

    fun updateCrime(crime: Crime) {
        mDatabase.update(
            CrimeTable.TABLE_NAME,
            getContentValues(crime),
            "${CrimeTable.UUID} = ?",
            arrayOf(crime.id.toString())
        )
    }

    fun removeCrime(crime: Crime) {
        mDatabase.delete(
            CrimeTable.TABLE_NAME,
            "${CrimeTable.UUID} = ?",
            arrayOf(crime.id.toString())
        )
    }

    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper {
        val cursor = mDatabase.query(
            CrimeTable.TABLE_NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null
        )
        return CrimeCursorWrapper(cursor)
    }

    private fun getContentValues(crime: Crime): ContentValues {
        return ContentValues().apply {
            put(CrimeTable.UUID, crime.id.toString())
            put(CrimeTable.TITLE, crime.title)
            put(CrimeTable.DATE, crime.date.time)
            put(CrimeTable.SOLVED, if (crime.isSolved) 1 else 0)
            put(CrimeTable.SUSPECT, crime.suspect)
        }
    }

    companion object {
        private var INSTANCE: CrimeLab? = null

        fun getInstance(context: Context?): CrimeLab =
            INSTANCE ?: CrimeLab(context?.applicationContext).also { INSTANCE = it }

    }
}
