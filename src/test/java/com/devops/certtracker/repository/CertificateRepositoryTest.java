/**
 * This package contains the repository classes for managing certificates in the database.
 */
package com.devops.certtracker.repository;

import com.devops.certtracker.entity.Certificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the CertificateRepository, which is responsible for
 * interacting with the database to perform CRUD operations on certificates.
 */
@DataJpaTest
public class CertificateRepositoryTest {

    @Autowired
    private CertificateRepository certificateRepository;

    private Certificate certificate1;
    private Certificate certificate2;

    /**
     * Initialize test data before each test case.
     */
    @BeforeEach
    public void init() {
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
     * Test saving a certificate to the database.
     */
    @Test
    @DisplayName("Should save certificate to the database")
    public void testSave() {
        // Save certificate1 to the database and get the saved certificate (newCertificate)
        Certificate newCertificate = certificateRepository.save(certificate1);

        // Verify that the saved certificate is not null (indicating a successful save operation)
        assertNotNull(newCertificate);

        // Verify that the ID of the saved certificate is generated and not null
        assertNotNull(newCertificate.getId());
    }

    /**
     * Test retrieving a list of certificates from the database.
     */
    @Test
    @DisplayName("Should return the certificate list with a size of 2")
    public void testFindAll() {
        // Save two certificates to the database
        certificateRepository.save(certificate1);
        certificateRepository.save(certificate2);

        // Retrieve the list of certificates from the database
        List<Certificate> list = certificateRepository.findAll();

        // Ensure that the list is not null
        assertNotNull(list);

        // Assert that the list has a size of 2
        assertEquals(2, list.size());
    }

    /**
     * Test retrieving a certificate by its ID from the database.
     */
    @Test
    @DisplayName("Should return certificate by its id")
    public void testFindById() {
        // Save certificate in the database
        Certificate savedCertificate = certificateRepository.save(certificate1);

        // Retrieve the certificate by its ID
        Certificate retrievedCertificate = certificateRepository.findById(certificate1.getId()).orElse(null);

        // Assert that the retrieved certificate is not null and its properties match the saved certificate
        assertNotNull(retrievedCertificate);
        assertEquals(savedCertificate.getUrl(), retrievedCertificate.getUrl());
        assertEquals(savedCertificate.getSubject(), retrievedCertificate.getSubject());
        assertEquals(savedCertificate.getIssuer(), retrievedCertificate.getIssuer());
        assertEquals(savedCertificate.getValidFrom(), retrievedCertificate.getValidFrom());
        assertEquals(savedCertificate.getValidTo(), retrievedCertificate.getValidTo());
    }

    /**
     * Test attempting to retrieve a certificate by a non-existent ID, expecting null as the result.
     */
    @Test
    @DisplayName("Should return null for a non-existent certificate ID")
    public void testFindByIdNonExistent() {
        // Declare a non-existing ID
        Long nonExistingId = -1L;

        // Attempt to retrieve a certificate using a non-existent ID
        Certificate retrievedCertificate = certificateRepository.findById(nonExistingId).orElse(null);

        // Ensure that the retrieved certificate is null
        assertNull(retrievedCertificate);
    }

    /**
     * Test deleting an existing certificate from the database.
     */
    @Test
    @DisplayName("Should delete an existing certificate from the database")
    public void testDelete() {
        // Save certificate1 to the database
        certificateRepository.save(certificate1);

        // Save certificate2 to the database
        certificateRepository.save(certificate2);

        // Get the ID of certificate1
        Long id = certificate1.getId();

        // Delete certificate1 from the database
        certificateRepository.delete(certificate1);

        // Verify that certificate1 is no longer in the database
        List<Certificate> list = certificateRepository.findAll();
        Optional<Certificate> existingCertificate = certificateRepository.findById(id);

        assertEquals(1, list.size());

        // Check if certificate1 is not present in the database
        assertFalse(existingCertificate.isPresent());
    }
}
