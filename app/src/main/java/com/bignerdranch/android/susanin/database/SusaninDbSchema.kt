package com.bignerdranch.android.susanin.database

class SusaninDbSchema {
    object SusaninTable {
        const val NAME = "susaninPoints"
        object Cols {
            const val UUID = "id"
            const val NAME = "name"
            const val LATITUDE = "latitude"
            const val LONGITUDE = "longitude"
        }
    }
}