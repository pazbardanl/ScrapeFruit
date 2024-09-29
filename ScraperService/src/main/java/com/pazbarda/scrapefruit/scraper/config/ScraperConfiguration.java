package com.pazbarda.scrapefruit.scraper.config;

import com.pazbarda.scrapefruit.scraper.helpers.KafkaFactory;
import com.pazbarda.scrapefruit.scraper.helpers.UrlScraper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScraperConfiguration {

    @Bean
    public UrlScraper scraper(){
        return new UrlScraper();
    }

    @Bean
    public KafkaFactory kafkaFactory(@Value("${KAFKA_BROKER}") String broker) {
        return new KafkaFactory(broker);
    }
}
