package com.bignerdranch.android.susanin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.util.*
import com.bignerdranch.android.susanin.ui.history.SusaninBaseHelper
import com.bignerdranch.android.susanin.ui.history.SusaninCursorWrapper
import com.bignerdranch.android.susanin.database.SusaninDbSchema
import kotlin.collections.ArrayList

class PointDBHelper private constructor(context: Context) {
    private val context: Context = context.applicationContext
    private val database: SQLiteDatabase = SusaninBaseHelper(this.context).writableDatabase
    private val photoFile = null

    companion object {
        var pointDBHelper: PointDBHelper? = null
        fun get(context: Context): PointDBHelper {
            if (pointDBHelper == null) {
                pointDBHelper = PointDBHelper(context)
            }
            return pointDBHelper!!
        }

        private fun getContentValues(susaninPoint: SusaninPoint): ContentValues {
            val values = ContentValues()
            values.put(SusaninDbSchema.SusaninTable.Cols.UUID, susaninPoint.id.toString())
            values.put(SusaninDbSchema.SusaninTable.Cols.NAME, susaninPoint.name)
            values.put(SusaninDbSchema.SusaninTable.Cols.LATITUDE, susaninPoint.latitude)
            values.put(SusaninDbSchema.SusaninTable.Cols.LONGITUDE, susaninPoint.longitude)

            return values
        }
    }

    fun addSusaninPoint(susaninPoint: SusaninPoint) {
        val values = getContentValues(susaninPoint)
        database.insert(SusaninDbSchema.SusaninTable.NAME, null, values)
    }

    fun deleteSusaninPoint(susaninPoint: SusaninPoint) {
        database.delete(
            SusaninDbSchema.SusaninTable.NAME,
            SusaninDbSchema.SusaninTable.Cols.UUID + " = ?",
            Array(1) { susaninPoint.id.toString() })
    }

    fun getSusaninPoint(id: UUID): SusaninPoint? {
        val cursor = querySusaninPoints(
            SusaninDbSchema.SusaninTable.Cols.UUID + " = ?",
            Array(1) { id.toString() }
        )
        cursor.use { cursor ->
            if (cursor.count == 0) {
                return null
            }

            cursor.moveToFirst()
            return cursor.getSusaninPoint()
        }
    }

    fun getSusaninPoints(): List<SusaninPoint> {
        val susaninPoints = ArrayList<SusaninPoint>()
        val cursor = querySusaninPoints(null, null)

        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                susaninPoints.add(cursor.getSusaninPoint())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return susaninPoints
    }

    fun updateSusaninPoint(susaninPoint: SusaninPoint) {
        val uuidString = susaninPoint.id.toString()
        val values = getContentValues(susaninPoint)

        database.update(
            SusaninDbSchema.SusaninTable.NAME,
            values,
            SusaninDbSchema.SusaninTable.Cols.UUID + " = ?",
            Array(1) { uuidString })
    }

    private fun querySusaninPoints(whereClause: String?, whereArgs: Array<String>?): SusaninCursorWrapper {
        val cursor = database.query(
            SusaninDbSchema.SusaninTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null,
        )
        return SusaninCursorWrapper(cursor)
    }
}