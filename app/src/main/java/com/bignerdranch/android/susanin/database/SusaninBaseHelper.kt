package com.bignerdranch.android.susanin.ui.history

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.bignerdranch.android.susanin.database.SusaninDbSchema

class SusaninBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    companion object {
        const val VERSION = 1
        const val DATABASE_NAME = "susaninBase.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table " + SusaninDbSchema.SusaninTable.NAME + "(" +
                    " _id integer primary key autoincrement, " +
                    SusaninDbSchema.SusaninTable.Cols.UUID + ", " +
                    SusaninDbSchema.SusaninTable.Cols.NAME + ", " +
                    SusaninDbSchema.SusaninTable.Cols.LATITUDE + ", " +
                    SusaninDbSchema.SusaninTable.Cols.LONGITUDE +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}