package com.devops.certtracker.exception;

public class CertificateServiceException extends RuntimeException {
    public CertificateServiceException(String message) {
        super(message);
    }

    public CertificateServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateServiceException(Throwable cause) {
        super(cause);
    }
}

