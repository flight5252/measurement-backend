package com.measurement.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class MeasurementEntity(

        @field:Id
        @field:GeneratedValue
        var id: Long = -1,

        @field:ManyToOne(fetch = FetchType.LAZY)
        @field:JsonIgnore
        var device: DeviceEntity,

        @field:NotNull
        var lat: Double,

        @field:NotNull
        var lng: Double,

        @field:NotNull
        var sievert: Double,

        @field:NotNull
        var created: Long
)