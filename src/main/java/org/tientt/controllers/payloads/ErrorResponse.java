package org.tientt.controllers.payloads;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
    public static ResponseEntity<?> build(HttpStatus status, String message, Object detail) {
        long timestamp = Instant.now().getEpochSecond();
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("message", message);
        body.put("detail", detail);
        body.put("timestamp", timestamp);
        return new ResponseEntity<>(body, status);
    }
}
