package com.measurement

import com.google.gson.Gson
import com.measurement.dto.MeasurementDto
import com.measurement.entity.DeviceEntity
import com.measurement.repository.DeviceRepository
import com.measurement.repository.MeasurementRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(MeasurementBackendApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MeasurementControllerTest {


    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var measurementRepository: MeasurementRepository

    @LocalServerPort
    protected var port = 0


    @BeforeEach
    fun initTest() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/devices"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        measurementRepository.deleteAll()
        deviceRepository.deleteAll()

    }

    private fun createDevice(): Long {

        val deviceId =
                given().contentType(ContentType.JSON)
                        .post("/")
                        .then()
                        .extract()
                        .path<Int>("data.deviceId").toLong()

        return deviceId

    }

    private fun createMeasurement(deviceId: Long, lat: Double, lng: Double, sievert: Double): Long {
        val measurementDto = MeasurementDto(deviceId, lat, lng, sievert)
        val measurementId =
                given().contentType(ContentType.JSON)
                        .body(Gson().toJson(measurementDto))
                        .post("/${measurementDto.deviceId}/measurements")
                        .then()
                        .extract()
                        .path<Int>("data.id").toLong()
        return measurementId

    }


    @Test
    fun testCreateDeviceAndMeasurementForDevice() {
        val deviceId = createDevice()

        val measurementId = createMeasurement(deviceId, 59.12, 10.62, 112.22)

        assertNotNull(measurementId)

    }

    @Test
    fun testConflictingIdsObjectAndUrl() {
        val deviceId = createDevice()
        val deviceId2 = createDevice()

        val measurementDto = MeasurementDto(deviceId, 59.12, 10.62, 98.22)

        given().contentType(ContentType.JSON)
                .body(Gson().toJson(measurementDto))
                .post("/$deviceId2/measurements")
                .then()
                .statusCode(409)
    }

    @Test
    fun testCreateDeviceAndListAll() {
        val deviceId = createDevice()

        val measurementId = createMeasurement(deviceId, 59.12, 10.62, 112.22)
        val measurementId2 = createMeasurement(deviceId, 59.12, 10.62, 112.22)
        val measurementId3 = createMeasurement(deviceId, 59.12, 10.62, 112.22)
        val measurementId4 = createMeasurement(deviceId, 59.12, 10.62, 112.22)

        val listOfMeasurments = listOf(measurementId, measurementId2, measurementId3, measurementId4).distinct()

        assertEquals(4, listOfMeasurments.size)

    }

    @Test
    fun testManyDevicesAndCheckAllHaveUniqueId() {
        createDevice()
        createDevice()
        createDevice()
        createDevice()

        val response =
                given().accept(ContentType.JSON)
                        .get()
                        .then()
                        .statusCode(200)
                        //note here that "status" represent the HTTP code
                        .body("status", equalTo("SUCCESS"))
                        .body("data.size()", equalTo(4))
                        .extract()
                        .response()
        val listOfDevices = response.path<List<DeviceEntity>>("data")

        assertEquals(4, listOfDevices.distinct().size)
    }
}