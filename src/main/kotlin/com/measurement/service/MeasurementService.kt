package com.measurement.service

import com.measurement.dto.MeasurementDto
import com.measurement.entity.DeviceEntity
import com.measurement.entity.MeasurementEntity
import com.measurement.repository.MeasurementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

@Service
class MeasurementService {

    @Autowired
    private lateinit var measurementRepository: MeasurementRepository

    fun getAllMeasurementsFromDeviceId(deviceId: Long): List<MeasurementEntity> {
        return measurementRepository.findAllByDevice_DeviceId(deviceId)
    }

    fun insertNewMeasurement(measurementDto: MeasurementDto, device: DeviceEntity): MeasurementEntity {
        //Here to simulate some delay in creating a new measurement

        Thread.sleep(Random.nextLong(1000))
        val measurementEntity = MeasurementEntity(
                device = device,
                lat = measurementDto.lat,
                lng = measurementDto.lng,
                sievert = measurementDto.sievert,
                created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )

        return measurementRepository.save(measurementEntity)
    }

}