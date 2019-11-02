package com.measurement.controller

import com.measurement.configuration.MicroMeter
import com.measurement.dto.MeasurementDto
import com.measurement.entity.MeasurementEntity
import com.measurement.service.DeviceService
import com.measurement.service.MeasurementService
import io.micrometer.core.annotation.Timed
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Api(value = "/devices", description = "Handling of creation and retrieving measurements ")
@RestController
class MeasurementController(
        val deviceService: DeviceService,
        val measurementService: MeasurementService,
        val microMeter: MicroMeter
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    @ApiOperation("List all measurements done by a given device")
    @GetMapping(path = ["devices/{deviceId}/measurements"],
            produces = [(MediaType.APPLICATION_JSON_VALUE)])
    @Timed(percentiles = [0.5, 0.95, 0.999], histogram = true)
    private fun listMeasurementsByDevice(@PathVariable deviceId: String): ResponseEntity<WrappedResponse<List<MeasurementEntity>>> {

        microMeter.counterGETMeasurement.increment()

        val id = try {
            deviceId.toLong()
        } catch (e: NumberFormatException) {
            logger.error("GET /devices/${deviceId}/measurements - proviced deviceId is not a number. Provided deviceId = ${deviceId}")
            return ResponseEntity
                    .status(400)
                    .body(WrappedResponse(
                            code = 400,
                            message = "Invalid deviceId format"
                    ))
        }
        val device = deviceService.getDevice(id)

        return if (device == null) {

            logger.info("GET /devices/${deviceId}/measurements - provided deviceId was not recognized by the db. Provided deviceId = ${deviceId}")

            ResponseEntity
                    .status(404)
                    .body(WrappedResponse(
                            code = 404,
                            message = "DeviceId is not valid"
                    ))
        } else {
            logger.info("GET /devices/${deviceId}/measurements - Provided deviceId = ${deviceId}")
            ResponseEntity
                    .status(200)
                    .body(WrappedResponse(
                            code = 200,
                            data = measurementService.getAllMeasurementsFromDeviceId(id)
                    ))
        }
    }

    @ApiOperation("Create a new measurement for a device")
    @PostMapping(path = ["devices/{deviceId}/measurements"],
            consumes = [(MediaType.APPLICATION_JSON_VALUE)],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    private fun insertNewMeasurement(
            @PathVariable deviceId: String,
            @RequestBody measurementDto: MeasurementDto): ResponseEntity<WrappedResponse<MeasurementEntity>> {

        microMeter.counterPOSTMeasurement.increment()

        val id = try {
            deviceId.toLong()
        } catch (e: NumberFormatException) {

            logger.error("POST /devices/${deviceId}/measurements - provided deviceId is not a number. Provided deviceId = ${deviceId}")

            return ResponseEntity
                    .status(400)
                    .body(WrappedResponse(
                            code = 400,
                            message = "Invalid deviceId format"
                    ))
        }

        if(id != measurementDto.deviceId){

            logger.info("POST /devices/${deviceId}/measurements - deviceId in url doesnt match deviceId in body object. Provided deviceId in url = ${deviceId}, in object = ${measurementDto.deviceId} ")

            return ResponseEntity
                    .status(409)
                    .body(WrappedResponse(
                            code = 409,
                            message = "Conflicting id's in object and url"
                    ))
        }

        val device = deviceService.getDevice(id)

        return microMeter.longTaskTimerPOSTMeasurement.recordCallable {

            if (device == null) {

                logger.info("POST /devices/${deviceId}/measurements - deviceId is not recognized by Db. Provided deviceId= ${deviceId}")

                ResponseEntity
                        .status(404)
                        .body(WrappedResponse(
                                code = 404,
                                message = "DeviceId is not valid"
                        ))
            } else {
                microMeter.distributionSummarySiervertMeasurements.record(measurementDto.sievert)

                if(measurementDto.sievert > microMeter.gaugeMaxSievertLast24h.get()){
                    microMeter.gaugeMaxSievertLast24h.set(measurementDto.sievert)

                }else if(measurementDto.sievert < microMeter.gaugeMinSievertLast24h.get()) {
                    microMeter.gaugeMinSievertLast24h.set(measurementDto.sievert)
                }

                MDC.put("location", "[${measurementDto.lat}, ${measurementDto.lng}]")
                MDC.put("latitude", "${measurementDto.lat}")
                MDC.put("longitude","${measurementDto.lng}")
                MDC.put("sievert","${measurementDto.sievert}")

                logger.info("POST /devices/${deviceId}/measurements - Provided deviceId= $deviceId")


                ResponseEntity
                        .status(200)
                        .body(WrappedResponse(
                                code = 200,
                                data = measurementService.insertNewMeasurement(measurementDto, device)
                        ))
            }
        }
    }
}