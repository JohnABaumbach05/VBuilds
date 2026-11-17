package com.vbuilds.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class UploadErrorHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleOversizedUpload(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("Upload too large. Each image must be 10 MB or smaller.");
    }
}
