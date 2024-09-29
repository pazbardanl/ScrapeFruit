package com.pazbarda.scrapefruit.scraper.helpers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;

// TODO -- extract to a utils class for the entire project
@Component
public class KafkaFactory {

    private final String broker;

    public KafkaFactory(String broker) {
        this.broker = broker;
    }

    public KafkaConsumer<String, String> createKafkaConsumer(String topic, String consumerGroup) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConsumerProperties(consumerGroup));
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    public KafkaProducer<String, String> createKafkaProducer(){
        return new KafkaProducer<>(getProducerProperties(broker));
    }

    private Properties getConsumerProperties(String consumerGroup){
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerProps;
    }

    private Properties getProducerProperties(String broker) {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return producerProps;
    }
}
