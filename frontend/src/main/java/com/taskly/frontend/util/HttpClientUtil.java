package com.taskly.frontend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientUtil {
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();
    private static final ObjectMapper mapper = new ObjectMapper();
    
    static {
        // KONFIGURASI PENTING: Register JavaTimeModule untuk LocalDateTime support
        mapper.registerModule(new JavaTimeModule());
    }

    public static ObjectMapper getMapper() { return mapper; }

    public static <T> T sendPost(String url, Object requestBody, Class<T> responseType, String token) throws Exception {
        String json = mapper.writeValueAsString(requestBody);
        System.out.println("[DEBUG] Request JSON: " + json); // Debug: lihat JSON yang dikirim
        
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[DEBUG] Response Status: " + response.statusCode()); // Debug: lihat status code
        System.out.println("[DEBUG] Response Body: " + response.body()); // Debug: lihat response body
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readValue(response.body(), responseType);
        } else {
            // PERBAIKAN: Error message lebih informatif dengan status code
            String errorMsg = "HTTP " + response.statusCode() + ": " + response.body();
            System.err.println("[ERROR] " + errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    public static String sendGet(String url, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10));
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        HttpRequest request = builder.GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("GET failed: " + response.body());
        }
    }

    public static <T> T sendPut(String url, Object requestBody, Class<T> responseType, String token) throws Exception {
        String json = mapper.writeValueAsString(requestBody);
        System.out.println("[DEBUG] PUT Request JSON: " + json);
        
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        HttpRequest request = builder.PUT(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[DEBUG] PUT Response Status: " + response.statusCode());
        System.out.println("[DEBUG] PUT Response Body: " + response.body());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readValue(response.body(), responseType);
        } else {
            String errorMsg = "HTTP " + response.statusCode() + ": " + response.body();
            System.err.println("[ERROR] " + errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    public static void sendDelete(String url, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10));
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        HttpRequest request = builder.DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[DEBUG] DELETE Response Status: " + response.statusCode());
        
        if (!(response.statusCode() >= 200 && response.statusCode() < 300)) {
            String errorMsg = "HTTP " + response.statusCode() + ": " + response.body();
            System.err.println("[ERROR] " + errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}