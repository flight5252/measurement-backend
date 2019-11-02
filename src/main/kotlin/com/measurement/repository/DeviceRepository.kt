package com.measurement.repository

import com.measurement.entity.DeviceEntity
import com.measurement.entity.MeasurementEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : CrudRepository<DeviceEntity, Long>{
    fun findByDeviceId(id: Long): DeviceEntity?
}