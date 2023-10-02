package com.devops.certtracker.service;

import com.devops.certtracker.entity.Certificate;
import com.devops.certtracker.exception.CertificateDeleteException;
import com.devops.certtracker.exception.CertificateNoContentException;
import com.devops.certtracker.exception.CertificateServiceException;
import com.devops.certtracker.exception.EntityNotFoundException;
import com.devops.certtracker.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    public List<Certificate> getAllCertificates(){
        List<Certificate> certificates = certificateRepository.findAll();
        if (certificates.isEmpty()){
            throw new CertificateNoContentException("No certificates found in the database");
        }
        return  certificates;
    }

    public void deleteCertificateById(Long certificateId){
        // Check if the certificate exists before attempting to delete
        if(!certificateRepository.existsById(certificateId)){
            throw new EntityNotFoundException("Certificate with ID "+ certificateId + " not found");
        }
        try{
            certificateRepository.deleteById(certificateId);
        }catch(Exception e){
            // Handle other exceptions
            throw new CertificateDeleteException("Error deleting the certificate");
        }
    }

    public Certificate retrieveAndSaveCertificate(String url) {
        validateUrl(url);

        try {
            URL urlObject = new URL(url);

            if (!"https".equalsIgnoreCase(urlObject.getProtocol())) {
                throw new CertificateServiceException("Only HTTPS URLs are supported.");
            }

            HttpsURLConnection httpsConnection = (HttpsURLConnection) urlObject.openConnection();
            int responseCode = httpsConnection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new CertificateServiceException("Failed to establish HTTPS connection. Response code: " + responseCode);
            }

            Optional<SSLSession> optionalSslSession = httpsConnection.getSSLSession();

            if (optionalSslSession.isPresent()) {
                SSLSession sslSession = optionalSslSession.get();
                try {
                    X509Certificate x509Certificate = extractCertificate(sslSession);
                    return saveCertificate(url, x509Certificate);
                } catch (CertificateException e) {
                    throw new CertificateServiceException("Error while processing the SSL certificate: " + e.getMessage());
                }
            } else {
                throw new CertificateServiceException("No SSL session established.");
            }
        } catch (MalformedURLException e) {
            throw new CertificateServiceException("Invalid URL format - " + e.getMessage());
        } catch (IOException e) {
            throw new CertificateServiceException("Error while establishing the HTTPS connection: " + e.getMessage());
        }
    }

    public Certificate getCertificateInfo(String url) {
        validateUrl(url);

        try {
            URL urlObject = new URL(url);

            if (!"https".equalsIgnoreCase(urlObject.getProtocol())) {
                throw new CertificateServiceException("Only HTTPS URLs are supported.");
            }

            HttpsURLConnection httpsConnection = (HttpsURLConnection) urlObject.openConnection();
            int responseCode = httpsConnection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new CertificateServiceException("Failed to establish HTTPS connection. Response code: " + responseCode);
            }

            Optional<SSLSession> optionalSslSession = httpsConnection.getSSLSession();

            if (optionalSslSession.isPresent()) {
                SSLSession sslSession = optionalSslSession.get();
                try {
                    X509Certificate x509Certificate = extractCertificate(sslSession);
                    return createCertificateInfo(url, x509Certificate);
                } catch (CertificateException e) {
                    throw new CertificateServiceException("Error while processing the SSL certificate: " + e.getMessage());
                }
            } else {
                throw new CertificateServiceException("No SSL session established.");
            }
        } catch (MalformedURLException e) {
            throw new CertificateServiceException("Invalid URL format - " + e.getMessage());
        } catch (IOException e) {
            throw new CertificateServiceException("Error while establishing the HTTPS connection: " + e.getMessage());
        }
    }


    private void validateUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new CertificateServiceException("URL cannot be null or empty.");
        }
    }

    private X509Certificate extractCertificate(SSLSession sslSession) throws CertificateException, IOException {
        java.security.cert.Certificate[] serverCertificates = sslSession.getPeerCertificates();

        if (serverCertificates == null || serverCertificates.length == 0) {
            throw new CertificateServiceException("No server certificates found.");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(serverCertificates[0].getEncoded())) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(inputStream);
        }
    }

    private Certificate saveCertificate(String url, X509Certificate x509Certificate) {
        Certificate certificate = new Certificate();
        certificate.setUrl(url);
        certificate.setSubject(x509Certificate.getSubjectX500Principal().getName());
        certificate.setIssuer(x509Certificate.getIssuerX500Principal().getName());
        certificate.setValidFrom(x509Certificate.getNotBefore());
        certificate.setValidTo(x509Certificate.getNotAfter());
        return certificateRepository.save(certificate);
    }

    private Certificate createCertificateInfo(String url, X509Certificate x509Certificate){
        Certificate certificate = new Certificate();
        certificate.setUrl(url);
        certificate.setSubject(x509Certificate.getSubjectX500Principal().getName());
        certificate.setIssuer(x509Certificate.getIssuerX500Principal().getName());
        certificate.setValidFrom(x509Certificate.getNotBefore());
        certificate.setValidTo(x509Certificate.getNotAfter());
        return certificate;
    }
}
