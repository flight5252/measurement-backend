package com.measurement

import com.measurement.entity.DeviceEntity
import com.measurement.repository.DeviceRepository
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


@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(MeasurementBackendApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceControllerTest {


    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @LocalServerPort
    protected var port = 0

    @BeforeEach
    fun clean() {
        deviceRepository.deleteAll()
    }

    @BeforeEach
    fun initTest() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/devices"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    private fun createDevice() {
        given().contentType(ContentType.JSON)
                .post("/")
    }


    @Test
    fun testCreateDeviceAndList() {
        createDevice()

        given().accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                //note here that "status" represent the HTTP code
                .body("status", equalTo("SUCCESS"))
                .body("data.size()", equalTo(1))

    }

    @Test
    fun testCreateDeviceAndListAll() {
        createDevice()
        createDevice()
        createDevice()
        createDevice()

        given().accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                //note here that "status" represent the HTTP code
                .body("status", equalTo("SUCCESS"))
                .body("data.size()", equalTo(4))
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