package com.bignerdranch.android.susanin

import java.util.*

class SusaninPoint(val latitude: Double, val longitude: Double, val name: String, val id: UUID) {

    constructor(latitude: Double, longitude: Double, name: String) : this(
        latitude,
        longitude,
        name,
        UUID.randomUUID()
    )

    fun copy(
        latitude: Double = this.latitude,
        longitude: Double = this.longitude,
        name: String = this.name,
        id: UUID = this.id
    ) = SusaninPoint(latitude, longitude, name, id)

}