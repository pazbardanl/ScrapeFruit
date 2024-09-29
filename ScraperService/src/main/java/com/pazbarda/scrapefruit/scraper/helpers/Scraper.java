package com.pazbarda.scrapefruit.scraper.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Scraper {
    public String scrapeUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.text();
    }
}
