package application.createUrlShortner.services.impl;

import application.createUrlShortner.models.UrlData;
import application.createUrlShortner.services.S3StorageService;
import application.createUrlShortner.services.UrlShortenerService;
import application.createUrlShortner.validators.RequestValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final static int SUBSTRING_START_INDEX = 0;
    private final static int SUBSTRING_END_INDEX = 8;

    private final S3StorageService s3StorageService;
    private final ObjectMapper objectMapper;

    public UrlShortenerServiceImpl(S3StorageService s3StorageService, ObjectMapper objectMapper) {
        this.s3StorageService = s3StorageService;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> handleShorteningRequest(Map<String, Object> input) {
        String body = input.get("body").toString();
        Map<String, String> bodyMap = RequestValidator.parseRequestBody(body, objectMapper);
        String originalUrl = bodyMap.get("originalUrl");
        long expirationTimeInSeconds = RequestValidator.parseExpirationTime(bodyMap.get("expirationTime"));

        String shortUrlCode = generateUrlCode();
        UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

        s3StorageService.saveUrlData(shortUrlCode, urlData);

        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCode);

        return response;
    }

    private static String generateUrlCode() {
        return UUID.randomUUID().toString().substring(SUBSTRING_START_INDEX, SUBSTRING_END_INDEX);
    }
}