package com.measurement.repository

import com.measurement.entity.MeasurementEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MeasurementRepository : CrudRepository<MeasurementEntity, Long>{
    fun findAllByDevice_DeviceId(deviceId: Long): List<MeasurementEntity>
}