package com.pazbarda.scrapefruit.services.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ScraperService {
    public static void main(String[] args) {
        System.out.println("ScraperService - Hello world!");
        try {
            scrapeUrl("https://www.bbc.com/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String scrapeUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String htmlContent = doc.html();
        String plainTextContent = doc.text();

        System.out.println("HTML Content: ");
        System.out.println(htmlContent);

        System.out.println("\nPlain Text Content: ");
        System.out.println(plainTextContent);
        return null;
    }
}