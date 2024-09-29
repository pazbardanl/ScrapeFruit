package com.pazbarda.scrapefruit.scraper.services;

import com.pazbarda.scrapefruit.scraper.helpers.KafkaFactory;
import com.pazbarda.scrapefruit.scraper.helpers.Scraper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

public class ScraperService {

    private static Scraper scraper;

    private static KafkaConsumer<String, String> kafkaConsumer;

    private static KafkaProducer<String, String> kafkaProducer;

    private static String outputTopic;

    @Autowired
    public ScraperService(Scraper scraper, KafkaFactory kafkaFactory) {
        ScraperService.scraper = scraper;
        ScraperService.kafkaConsumer = kafkaFactory.createKafkaConsumer(
                System.getenv("INPUT_KAFKA_TOPIC"),
                System.getenv("KAFKA_CONSUMER_GROUP")
        );
        ScraperService.kafkaProducer = kafkaFactory.createKafkaProducer();
        ScraperService.outputTopic = System.getenv("OUTPUT_KAFKA_TOPIC");
    }

    public static void main(String[] args) {
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    String urlToScrape = record.value();
                    try {
                        String scrapedText = scraper.scrapeUrl(urlToScrape);
                        kafkaProducer.send(new ProducerRecord<>(outputTopic, urlToScrape, scrapedText));
                        System.out.println("Scraped and sent content for URL: " + urlToScrape);
                    } catch (Exception e) {
                        System.err.println("Failed to scrape URL: " + urlToScrape);
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            kafkaConsumer.close();
            kafkaProducer.close();
        }
    }
}