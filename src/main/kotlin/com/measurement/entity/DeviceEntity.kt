package com.measurement.entity

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class DeviceEntity(

        @field:Id
        @field:GeneratedValue
        var deviceId: Long = -1,

        @field:OneToMany(fetch = FetchType.LAZY)
        var measurements: List<MeasurementEntity>,

        @field:NotNull
        var created: Long

)