package com.measurement.configuration

import com.google.common.util.concurrent.AtomicDouble
import io.micrometer.core.instrument.*
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger


@Service
class MicroMeter(
        meterRegistry: MeterRegistry
) {

    init {
        val ENVIRONMENT = System.getProperty("environment", "prod")
            meterRegistry.config().commonTags("stack", ENVIRONMENT)
    }

    var counterGETDevice: Counter = Counter.builder("http.requests")
            .description("How many times has get-devices-endpoint been triggered")
            .tag("uri", "/devices")
            .tag("method", "GET")
            .register(meterRegistry)

    var counterGETMeasurement: Counter = Counter.builder("http.requests")
            .description("How many times has get-measurements-endpoint been triggered")
            .tag("uri", "/devices/{id}/measurements")
            .tag("method", "GET")
            .register(meterRegistry)

    var counterPOSTDevice: Counter = Counter.builder("http.requests")
            .description("How many times has post-devices-endpoint been triggered")
            .tag("uri", "/devices")
            .tag("method", "POST")
            .register(meterRegistry)

    var counterPOSTMeasurement: Counter = Counter.builder("http.requests")
            .description("How many times has post-measurement-endpoint been triggered")
            .tag("uri", "/devices/{id}/measurements")
            .tag("method", "POST")
            .register(meterRegistry)

    var timerGETDevice = Timer.builder("http.requests")
            .description("Measure time for GET /devices endpoint takes")
            .tag("uri", "/devices")
            .tag("method", "GET")
            .tag("type", "timer")
            .register(meterRegistry)

    var longTaskTimerPOSTMeasurement = LongTaskTimer.builder("http.requests")
            .description("Makes a new measurement")
            .tags("uri", "/devices/{id}/measurement")
            .register(meterRegistry)

    var distributionSummarySiervertMeasurements  = DistributionSummary
            .builder("sievert.value")
            .description("Distribution summary of reported sievert values")
            .register(meterRegistry)

    var gaugeMinSievertLast24h = meterRegistry.gauge("sievert.min.value", AtomicDouble(0.0))

    var gaugeMaxSievertLast24h = meterRegistry.gauge("sievert.max.value", AtomicDouble(0.0))





}