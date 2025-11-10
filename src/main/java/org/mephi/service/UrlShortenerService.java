package org.mephi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UrlShortenerService {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final Random random = new Random();

    @Value("${link.length:8}")
    private int shortLinkLength;

    // алгоритм взял из сервиса который показывали на семинаре:
    // https://github.com/SF-MEPHI-DEV/Java-Basics/blob/main/src/main/java/ru/mephi/url_shortener/UrlShortenerService.java#L157
    public String generateShortUrl() {
        StringBuilder sb = new StringBuilder(shortLinkLength);
        for (int i = 0; i < shortLinkLength; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
