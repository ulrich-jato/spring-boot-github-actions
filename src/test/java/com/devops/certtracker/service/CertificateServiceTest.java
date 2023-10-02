/**
 * This package contains the service classes for managing certificates.
 */
package com.devops.certtracker.service;

import com.devops.certtracker.entity.Certificate;
import com.devops.certtracker.exception.CertificateNoContentException;
import com.devops.certtracker.exception.CertificateServiceException;
import com.devops.certtracker.exception.EntityNotFoundException;
import com.devops.certtracker.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the CertificateService. It contains test cases for various
 * operations related to certificates.
 */
@ExtendWith(MockitoExtension.class)
public class CertificateServiceTest {

    // Mocked repository for simulating interactions with the Certificate database.
    @Mock
    private CertificateRepository certificateRepository;

    // The service under test, which will be automatically injected with mocked dependencies.
    @InjectMocks
    private CertificateService certificateService;

    // Sample Certificate instances used for testing purposes.
    private Certificate certificate1;
    private Certificate certificate2;


    /**
     * Initialize test data before each test case.
     */
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        certificate1 = new Certificate();
        certificate1.setUrl("https://www.google.com");
        certificate1.setSubject("CN=google.com");
        certificate1.setIssuer("CN=issuer.com");
        certificate1.setValidFrom(new Date());
        certificate1.setValidTo(new Date());

        certificate2 = new Certificate();
        certificate2.setUrl("https://www.github.com");
        certificate2.setSubject("CN=github.com");
        certificate2.setIssuer("CN=issuer.com");
        certificate2.setValidFrom(new Date());
        certificate2.setValidTo(new Date());
    }

    /**
     * Test the retrieval of all certificates from the database.
     */
    @Test
    @DisplayName("Get all certificates")
    public void testGetAllCertificates() {
        // Mock data
        List<Certificate> certificates = new ArrayList<>();
        certificates.add(certificate1);
        certificates.add(certificate2);
        when(certificateRepository.findAll()).thenReturn(certificates);

        // Test
        List<Certificate> result = certificateService.getAllCertificates();

        // Assertions
        assertEquals(2, certificates.size());
        assertEquals(certificates, result);
        assertNotNull(certificates);
    }

    /**
     * Test the scenario where no certificates are found in the database, resulting in a
     * CertificateNoContentException.
     */
    @Test
    @DisplayName("Get all certificates with an empty list - No Content")
    public void testGetAllCertificates_EmptyList() {
        // Create an empty list of certificates
        List<Certificate> emptyCertificates = new ArrayList<>();

        // Mock data
        when(certificateRepository.findAll()).thenReturn(emptyCertificates);

        // Test and assert exception
        CertificateNoContentException exception = assertThrows(CertificateNoContentException.class, () -> {
            certificateService.getAllCertificates();
        });

        assertEquals("No certificates found in the database", exception.getMessage());
    }

    /**
     * Test the deletion of a certificate by ID.
     */
    @Test
    @DisplayName("Delete a certificate by ID")
    public void testDeleteCertificateById() {
        // Mock data
        Long certificateId = 1L;
        when(certificateRepository.existsById(certificateId)).thenReturn(true);

        // Test
        certificateService.deleteCertificateById(certificateId);

        // Verify that deleteById is called
        verify(certificateRepository, times(1)).deleteById(certificateId);
    }

    /**
     * Test the scenario where a certificate with a non-existing ID is attempted to be deleted,
     * resulting in an EntityNotFoundException.
     */
    @Test
    @DisplayName("Delete a certificate by non-existing ID - Entity Not Found")
    public void testDeleteCertificateById_NonExistingId() {
        // Mock data
        Long certificateId = 1L;
        when(certificateRepository.existsById(certificateId)).thenReturn(false);

        // Test and assert exception
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            certificateService.deleteCertificateById(certificateId);
        });

        assertEquals("Certificate with ID " + certificateId + " not found", exception.getMessage());
    }

    /**
     * Test the retrieval and saving of a certificate with a valid URL.
     *
     * @throws Exception If an exception occurs during the test.
     */
    @Test
    @DisplayName("Retrieve and save a certificate with a valid URL")
    public void testRetrieveAndSaveCertificate_ValidUrl() throws Exception {
        // Test case for retrieving and saving a certificate
        String validHttpsUrl = "https://www.google.com";

        Certificate savedCertificate = new Certificate();
        savedCertificate.setUrl(validHttpsUrl);
        savedCertificate.setSubject("CN=Test Subject");
        savedCertificate.setIssuer("CN=Test Issuer");
        savedCertificate.setValidFrom(new Date());
        savedCertificate.setValidTo(new Date());

        when(certificateRepository.save(any(Certificate.class))).thenReturn(savedCertificate);

        Certificate result = certificateService.retrieveAndSaveCertificate(validHttpsUrl);

        assertNotNull(result);
        assertEquals(validHttpsUrl, result.getUrl());
        assertEquals("CN=Test Subject", result.getSubject());
        assertEquals("CN=Test Issuer", result.getIssuer());
        assertNotNull(result.getValidFrom());
        assertNotNull(result.getValidTo());
    }

    /**
     * Test the retrieval and saving of a certificate with an invalid URL, which should result in
     * a CertificateServiceException.
     *
     * @throws Exception If an exception occurs during the test.
     */
    @Test
    @DisplayName("Retrieve and save a certificate with an invalid URL - Malformed URL")
    public void testRetrieveAndSaveCertificate_InvalidURL() throws Exception {
        // Test case for retrieving and saving a certificate with an invalid URL
        String invalidUrl = "invalid-url";  // Provide an invalid URL

        // Ensure that a MalformedURLException is thrown when calling the method with an invalid URL
        CertificateServiceException exception = assertThrows(CertificateServiceException.class, () -> {
            certificateService.retrieveAndSaveCertificate(invalidUrl);
        });

        assertEquals("Invalid URL format - no protocol: invalid-url", exception.getMessage());

        // Verify that the certificateRepository.save method was not called for the invalid URL
        verify(certificateRepository, never()).save(any(Certificate.class));
    }

    /**
     * Test the retrieval and saving of a certificate with a non-secure URL, which should result in
     * a CertificateServiceException.
     *
     * @throws Exception If an exception occurs during the test.
     */
    @Test
    @DisplayName("Retrieve and save a certificate with a non-secure URL - Non-Secure URL")
    public void testRetrieveAndSaveCertificate_NotSecureURL() throws Exception {
        // Test case for retrieving and saving a certificate with a non-secured URL
        String unsecuredLink = "http://www.google.com";

        // Ensure that CertificateServiceException is thrown when calling the method with a non-secure URL
        CertificateServiceException exception = assertThrows(CertificateServiceException.class, () -> {
            certificateService.retrieveAndSaveCertificate(unsecuredLink);
        });

        assertEquals("Only HTTPS URLs are supported.", exception.getMessage());

        // Verify that the certificateRepository.save method was not called
        verify(certificateRepository, never()).save(any(Certificate.class));
    }

    /**
     * Test the retrieval and saving of a certificate with an HTTP error, which should result in
     * a CertificateServiceException.
     *
     * @throws Exception If an exception occurs during the test.
     */
    @Test
    @DisplayName("Retrieve and save a certificate with an HTTP error - HTTPS Connection Error")
    public void testRetrieveAndSaveCertificate_HttpError() throws Exception {
        String httpErrorUrl = "https://chat.openai.com";

        // Define the expected exception
        assertThrows(CertificateServiceException.class, () -> {
            certificateService.retrieveAndSaveCertificate(httpErrorUrl);
        });

        // Verify that the certificateRepository.save method is never called
        verify(certificateRepository, never()).save(any(Certificate.class));
    }
}
