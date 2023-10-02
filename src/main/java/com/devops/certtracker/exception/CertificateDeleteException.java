package com.devops.certtracker.exception;

public class CertificateDeleteException extends RuntimeException {
    public CertificateDeleteException(String message) {
        super(message);
    }

    public CertificateDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateDeleteException(Throwable cause) {
        super(cause);
    }
}
