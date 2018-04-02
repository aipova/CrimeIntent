package ru.rsppv.criminalintent.model


import android.content.Context
import java.util.*

class CrimeLab private constructor(context: Context) {
    private val mCrimes: MutableList<Crime> = mutableListOf()

    init {
        for (i in 0..99) {
            val crime = Crime().apply {
                title = "Crime #$i"
                isSolved = i % 2 == 0
            }
            mCrimes.add(crime)
        }

    }

    fun allCrimes() = mCrimes

    fun getCrime(id: UUID): Crime? {
        return mCrimes.find { it.id == id }
    }

    companion object {
        private var INSTANCE: CrimeLab? = null

        fun getInstance(context: Context): CrimeLab =
            INSTANCE ?: CrimeLab(context).also { INSTANCE = it }

    }
}
