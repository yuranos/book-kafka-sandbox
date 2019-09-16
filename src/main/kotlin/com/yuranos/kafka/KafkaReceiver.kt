package com.yuranos.kafka

interface KafkaReceiver<K, V> {
    fun startReceiving()
    fun stopReceiving()
    fun isRunning(): Boolean
}
