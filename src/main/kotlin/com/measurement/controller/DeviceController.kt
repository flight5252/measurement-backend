package com.measurement.controller

import com.measurement.configuration.MicroMeter
import com.measurement.entity.DeviceEntity
import com.measurement.service.DeviceService
import io.micrometer.core.annotation.Timed
import io.swagger.annotations.Api
import io.swagger.annotations.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/devices", description = "Handling of creation and listing of devices")
@RestController
class DeviceController(
        val deviceService: DeviceService,
        val microMeter: MicroMeter
) {

    val logger = LoggerFactory.getLogger(DeviceController::class.java)

    @PostMapping(
            path = ["/devices"],
            produces = [(MediaType.APPLICATION_JSON_VALUE)])
    @ApiResponse(code = 201, message = "The newly created device")
    @Timed(percentiles = [0.5, 0.95, 0.999], histogram = true)
    private fun createNewDevice(): ResponseEntity<WrappedResponse<DeviceEntity>> {

        microMeter.counterPOSTDevice.increment()

        val device = deviceService.createNewDevice()

        logger.info("POST /devices - Created new device with id: ${device.deviceId}")

        return ResponseEntity
                .status(201)
                .body(WrappedResponse(
                        code = 201,
                        data = device
                ).validated())
    }

    @GetMapping(
            path = ["/devices"],
            produces = [(MediaType.APPLICATION_JSON_VALUE)])
    @ApiResponse(code = 200, message = "List of all available devices")
    private fun listAllDevices(): ResponseEntity<WrappedResponse<List<DeviceEntity>>> {

        microMeter.counterGETDevice.increment()
        logger.info("GET /devices - list all device")

        return microMeter.timerGETDevice.recordCallable {
            ResponseEntity
                    .status(200)
                    .body(WrappedResponse(
                            code = 200,
                            data = deviceService.getAllDevices()
                    ).validated())
        }
    }
}