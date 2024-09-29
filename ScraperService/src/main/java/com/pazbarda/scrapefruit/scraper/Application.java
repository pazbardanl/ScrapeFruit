package com.pazbarda.scrapefruit.scraper;

import com.pazbarda.scrapefruit.scraper.services.ScraperService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        System.out.println(">> Scraper Application Started");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.pazbarda.scrapefruit.scraper");
        ScraperService scraperService = context.getBean(ScraperService.class);
        scraperService.run();
    }
}
