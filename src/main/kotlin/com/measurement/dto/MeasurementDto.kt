package com.measurement.dto

import javax.persistence.*
import javax.validation.constraints.NotNull


data class MeasurementDto (

        val deviceId: Long,

        val lat: Double,

        val lng: Double,

        val sievert: Double
)