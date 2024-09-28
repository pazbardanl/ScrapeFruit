package com.pazbarda.scrapefruit.services.scraper;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ScraperService {
    public static void main(String[] args) {
        System.out.println("ScraperService - Hello world!");
        String kafkaBroker = System.getenv("KAFKA_BROKER");
        String inputTopic = System.getenv("INPUT_KAFKA_TOPIC");
        String outputTopic = System.getenv("OUTPUT_KAFKA_TOPIC");

        // Kafka Consumer Config
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "scraper-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Create Kafka Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(inputTopic));

        // Kafka Producer Config
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create Kafka Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        try {
            while (true) {
                // Poll the Kafka topic for new messages (URLs to scrape)
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    String urlToScrape = record.value();

                    // Scrape the URL content using JSoup
                    try {
                        String scrapedText = scrapeUrl(urlToScrape);

                        // Produce the scraped text to the output Kafka topic
                        producer.send(new ProducerRecord<>(outputTopic, urlToScrape, scrapedText));
                        System.out.println("Scraped and sent content for URL: " + urlToScrape);
                    } catch (Exception e) {
                        System.err.println("Failed to scrape URL: " + urlToScrape);
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            consumer.close();
            producer.close();
        }
    }

    private static String scrapeUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.text();
    }
}