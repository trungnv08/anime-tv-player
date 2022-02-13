package com.example.anime.player.service.impl;

import com.example.anime.player.service.WebCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class BaseWebCrawler implements WebCrawler {

    protected String baseUrl;
    protected int timeout;
    protected final String resourceName;

    public BaseWebCrawler(String baseUrl, String resourceName) {
        this.baseUrl = baseUrl;
        this.resourceName = resourceName;
        this.timeout = 30_000;
    }

    public BaseWebCrawler(String baseUrl, int timeout, String resourceName) {
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.resourceName = resourceName;
    }

    protected Document getPageSource(String path) throws IOException {
        return Jsoup.connect(baseUrl + path)
                .userAgent("Mozilla")
                .timeout(timeout)
                .get();
    }

    protected Document getPageSourceFullUrl(String fullUrl) throws IOException {
        return Jsoup.connect(fullUrl)
                .userAgent("Mozilla")
                .timeout(timeout)
                .get();
    }

    protected Document getPageSource() throws IOException {
        return Jsoup.connect(baseUrl)
                .userAgent("Mozilla")
                .timeout(timeout)
                .get();
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }
}
