package com.pazbarda.scrapefruit.scraper.services;

import com.pazbarda.scrapefruit.scraper.helpers.KafkaFactory;
import com.pazbarda.scrapefruit.scraper.helpers.UrlScraper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ScraperService {

    private final UrlScraper urlScraper;

    private final  KafkaConsumer<String, String> kafkaConsumer;

    private final KafkaProducer<String, String> kafkaProducer;

    private final String outputTopic;

    @Autowired
    public ScraperService(UrlScraper urlScraper, KafkaFactory kafkaFactory) {
        this.urlScraper = urlScraper;
        this.kafkaConsumer = kafkaFactory.createKafkaConsumer(
                System.getenv("INPUT_KAFKA_TOPIC"),
                System.getenv("KAFKA_CONSUMER_GROUP")
        );
        this.kafkaProducer = kafkaFactory.createKafkaProducer();
        this.outputTopic = System.getenv("OUTPUT_KAFKA_TOPIC");
    }

    public void run() {
        System.out.println(">> ScraperService started");
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    String urlToScrape = record.value();
                    try {
                        String scrapedText = urlScraper.scrapeUrl(urlToScrape);
                        kafkaProducer.send(new ProducerRecord<>(outputTopic, urlToScrape, scrapedText));
                        System.out.println(">> Scraped and sent content for URL: " + urlToScrape);
                    } catch (Exception e) {
                        System.err.println(">ERROR> Failed to scrape URL: " + urlToScrape);
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