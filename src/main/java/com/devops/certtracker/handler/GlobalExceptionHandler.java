package com.devops.certtracker.handler;

import com.devops.certtracker.entity.ErrorResponse;
import com.devops.certtracker.exception.CertificateDeleteException;
import com.devops.certtracker.exception.CertificateNoContentException;
import com.devops.certtracker.exception.CertificateServiceException;
import com.devops.certtracker.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(CertificateServiceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error processing certificate", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Certificate not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(CertificateDeleteException.class)
    public ResponseEntity<ErrorResponse> handleCertificateDeleteException(CertificateDeleteException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting the certificate", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(CertificateNoContentException.class)
    public ResponseEntity<ErrorResponse> handleCertificateNoContentException(CertificateNoContentException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "No Content Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
