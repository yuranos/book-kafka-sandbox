package com.yuranos.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@EnableKafka
@Configuration
class KafkaConfiguration {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstapBrokerConfig: String

    @Value("\${spring.kafka.consumer.group-id}")
    lateinit var groupId: String

    @Value("\${spring.kafka.consumer.concurrency}")
    lateinit var concurrency: String

    @Value("\${spring.kafka.consumer.auto-offset-reset}")
    lateinit var autoOffsetReset: String

    @Value("\${spring.kafka.consumer.pollTimeout}")
    lateinit var pollTimeout: String

    @Value("\${pixel.kafka.retries}")
    lateinit var numOfRetries: String

    @Value("\${pixel.kafka.startFrom:}")
    lateinit var startFrom: String

    @Bean
    fun producerFactory(): ProducerFactory<String, String> = DefaultKafkaProducerFactory(producerConfigs())

    @Bean
    fun producerConfigs(): Map<String, Any> {
        val props = mutableMapOf<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstapBrokerConfig
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        return props
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(producerFactory())

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(concurrency.toInt())
        factory.containerProperties.pollTimeout = pollTimeout.toLong()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

        setRetryPolicy(factory)

        return factory
    }

    fun setRetryPolicy(factory: ConcurrentKafkaListenerContainerFactory<String, String>) {
        val seekToCurrentErrorHandler = SeekToCurrentErrorHandler(3)
        seekToCurrentErrorHandler.setCommitRecovered(true)
        factory.setErrorHandler(seekToCurrentErrorHandler)
        factory.setStatefulRetry(true)
        val retryTemplate = RetryTemplate()
        val backOffPolicy = ExponentialRandomBackOffPolicy()
        retryTemplate.setBackOffPolicy(backOffPolicy)
        retryTemplate.setThrowLastExceptionOnExhausted(true)
        retryTemplate.setRetryPolicy(SimpleRetryPolicy(3))
        factory.setRetryTemplate(retryTemplate)
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    fun consumerConfigs(): Map<String, Any> {
        val props = mutableMapOf<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstapBrokerConfig
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = autoOffsetReset
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        return props
    }
}

const val INPUT_TOPIC_LISTENER_ID = "inputTopicListener"
