package com.yuranos.kafka

import org.springframework.stereotype.Service

@Service
class IngestorService(
    private val receiver: KafkaReceiver<String, String>
) : Ingestor {


    override fun startIngesting() {
        receiver.startReceiving()
    }

}
