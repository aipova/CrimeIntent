package ru.rsppv.criminalintent.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import ru.rsppv.criminalintent.database.CrimeDbSchema.CrimeTable

class CrimeBaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CRIME_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        const val VERSION = 1
        const val DATABASE_NAME = "crimeBase.db"
        const val CREATE_CRIME_TABLE = "create table ${CrimeTable.TABLE_NAME} (" +
                "${BaseColumns._ID} integer primary key autoincrement, " +
                "${CrimeTable.UUID}, " +
                "${CrimeTable.TITLE}, " +
                "${CrimeTable.DATE}, " +
                "${CrimeTable.SOLVED}, " +
                "${CrimeTable.SUSPECT})"
    }
}