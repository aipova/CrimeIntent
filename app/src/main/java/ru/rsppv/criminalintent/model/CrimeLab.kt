package ru.rsppv.criminalintent.model


import android.content.Context
import java.util.*

class CrimeLab private constructor(context: Context) {
    private val mCrimes: MutableList<Crime> = mutableListOf()

    fun allCrimes() = mCrimes

    fun getCrime(id: UUID): Crime? {
        return mCrimes.find { it.id == id }
    }

    fun addCrime(crime: Crime) = mCrimes.add(crime)

    companion object {
        private var INSTANCE: CrimeLab? = null

        fun getInstance(context: Context): CrimeLab =
            INSTANCE ?: CrimeLab(context).also { INSTANCE = it }

    }
}
