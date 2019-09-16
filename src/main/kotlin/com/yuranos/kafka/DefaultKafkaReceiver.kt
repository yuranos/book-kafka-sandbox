package com.yuranos.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class DefaultKafkaReceiver<K, V> : KafkaReceiver<K, V> {
    val logger: Logger = LoggerFactory.getLogger(DefaultKafkaReceiver::class.java)

    @Value("\${pixel.kafka.input-topic}")
    lateinit var inputTopic: String

    @Autowired
    @Qualifier(KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
    lateinit var endpointRegistry: KafkaListenerEndpointRegistry

    override fun startReceiving() {
        endpointRegistry.getListenerContainer(INPUT_TOPIC_LISTENER_ID).start()
        logger.info("Kafka listener container for topic=$inputTopic with id=$INPUT_TOPIC_LISTENER_ID has been started.")
    }

    override fun stopReceiving() {
        endpointRegistry.getListenerContainer(INPUT_TOPIC_LISTENER_ID).stop()
        logger.info("Kafka listener container for topic=$inputTopic with id=$INPUT_TOPIC_LISTENER_ID has been stopped.")
    }

    override fun isRunning(): Boolean = endpointRegistry.getListenerContainer(INPUT_TOPIC_LISTENER_ID).isRunning

    @KafkaListener(
        topics = ["\${pixel.kafka.input-topic}"],
        autoStartup = "false",
        id = INPUT_TOPIC_LISTENER_ID,
        idIsGroup = false
    )
    fun receive(record: ConsumerRecord<K, V>, acknowledgment: Acknowledgment) {
        throw RuntimeException("Something really bad happened")
//        acknowledgment.acknowledge()
    }
}

