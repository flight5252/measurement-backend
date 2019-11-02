package com.measurement.service

import com.measurement.configuration.MicroMeter
import com.measurement.entity.DeviceEntity
import com.measurement.entity.MeasurementEntity
import com.measurement.repository.DeviceRepository
import com.measurement.repository.MeasurementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class DeviceService(
        val deviceRepository: DeviceRepository,
        microMeter: MicroMeter
) {

    fun getAllDevices(): List<DeviceEntity> {
        return deviceRepository.findAll().toList()
    }

    fun getDevice(deviceId: Long): DeviceEntity? {
        return deviceRepository.findByDeviceId(deviceId)
    }

    fun createNewDevice(): DeviceEntity {
        val deviceEntity = DeviceEntity(
                measurements = emptyList(),
                created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )
        return deviceRepository.save(deviceEntity)
    }
}