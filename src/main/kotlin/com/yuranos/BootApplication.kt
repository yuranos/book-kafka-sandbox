package com.yuranos

import com.yuranos.kafka.IngestorService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.*

@SpringBootApplication
class BootApplication(val ingestor: IngestorService) {
    val logger: Logger = LoggerFactory.getLogger(BootApplication::class.java)

    @Bean
    fun runner(): ApplicationRunner = ApplicationRunner {
        logger.info("Pixel Ingestor service started with following arguments: ${Arrays.toString(it.sourceArgs)}")
        ingestor.startIngesting()
    }

}

fun main(args: Array<String>) {
    runApplication<BootApplication>(*args)
}
