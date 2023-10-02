package com.devops.certtracker.exception;

public class CertificateNoContentException extends  RuntimeException{

    public CertificateNoContentException(String message) {
        super(message);
    }

    public CertificateNoContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateNoContentException(Throwable cause) {
        super(cause);
    }
}
