package com.bignerdranch.android.susanin.ui.history

import android.database.Cursor
import android.database.CursorWrapper
import com.bignerdranch.android.susanin.SusaninPoint
import com.bignerdranch.android.susanin.database.SusaninDbSchema
import java.util.*

class SusaninCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {

    fun getSusaninPoint(): SusaninPoint {
        val uuidString = getString(getColumnIndex(SusaninDbSchema.SusaninTable.Cols.UUID))
        val nameString = getString(getColumnIndex(SusaninDbSchema.SusaninTable.Cols.NAME))
        val latitudeDouble = getDouble(getColumnIndex(SusaninDbSchema.SusaninTable.Cols.LATITUDE))
        val longitudeDouble = getDouble(getColumnIndex(SusaninDbSchema.SusaninTable.Cols.LONGITUDE))

        return SusaninPoint(latitudeDouble, longitudeDouble, nameString, UUID.fromString(uuidString))
    }
}