package com.devops.certtracker.controller;

import com.devops.certtracker.entity.Certificate;
import com.devops.certtracker.exception.CertificateNoContentException;
import com.devops.certtracker.exception.CertificateServiceException;
import com.devops.certtracker.exception.EntityNotFoundException;
import com.devops.certtracker.service.CertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CertificateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateService certificateService;

    @Autowired
    private ObjectMapper objectMapper;

    private Certificate certificate1;
    private Certificate certificate2;

    @BeforeEach
    public void init() {
        certificate1 = new Certificate();
        certificate1.setId(1L);
        certificate1.setUrl("https://www.google.com");
        certificate1.setSubject("CN=google.com");
        certificate1.setIssuer("CN=issuer.com");
        certificate1.setValidFrom(new Date());
        certificate1.setValidTo(new Date());

        certificate2 = new Certificate();
        certificate2.setId(2L);
        certificate2.setUrl("https://www.github.com");
        certificate2.setSubject("CN=github.com");
        certificate2.setIssuer("CN=issuer.com");
        certificate2.setValidFrom(new Date());
        certificate2.setValidTo(new Date());
    }

    @Test
    public void testAddCertificateEndpoint_ValidUrl() throws Exception {
        String url = "https://www.google.com";
        String request = "{\"url\": \"" + url + "\"}";

        when(certificateService.retrieveAndSaveCertificate(url)).thenReturn(certificate1);

        this.mockMvc.perform(post("/api/certificates/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.url").value(url))
                .andExpect(jsonPath("$.subject").exists())
                .andExpect(jsonPath("$.issuer").exists())
                .andExpect(jsonPath("$.validFrom").exists())
                .andExpect(jsonPath("$.validTo").exists());
    }

    @Test
    public void testAddCertificateEndpoint_InvalidUrl() throws Exception{
        String invalidUrl = "invalid url";
        String request = "{\"url\": \""+ invalidUrl + "\"}";
        when(certificateService.retrieveAndSaveCertificate(invalidUrl))
                .thenThrow(new CertificateServiceException("Invalid URL format"));
        this.mockMvc.perform(post("/api/certificates/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error processing certificate"))
                .andExpect(jsonPath("$.message").value("Invalid URL format"));
    }

    @Test
    public void testAddCertificateEndpoint_NotSecureURL() throws Exception{
        String unsecureUrl = "http://www.google.com";
        String request = "{\"url\": \""+ unsecureUrl + "\"}";
        when(certificateService.retrieveAndSaveCertificate(unsecureUrl))
                .thenThrow(new CertificateServiceException("Only HTTPS URLs are supported."));
        this.mockMvc.perform(post("/api/certificates/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error processing certificate"))
                .andExpect(jsonPath("$.message").value("Only HTTPS URLs are supported."));
    }

    @Test
    public void testAddGetCertificateEndpoint_HTTPError() throws Exception{
        String fakeUrl = "http://www.my-fake-web-site.com";
        String request = "{\"url\": \""+ fakeUrl + "\"}";
        when(certificateService.retrieveAndSaveCertificate(fakeUrl))
                .thenThrow(new CertificateServiceException("Error while establishing the HTTPS connection"));
        this.mockMvc.perform(post("/api/certificates/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error processing certificate"))
                .andExpect(jsonPath("$.message").value("Error while establishing the HTTPS connection"));
    }
    @Test
    public void testGetCertificateInfoEndpoint_ValidUrl() throws Exception {
        String url = "https://www.google.com";
        String request = "{\"url\": \"" + url + "\"}";

        when(certificateService.getCertificateInfo(url)).thenReturn(certificate1);

        this.mockMvc.perform(post("/api/certificates/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(url))
                .andExpect(jsonPath("$.subject").exists())
                .andExpect(jsonPath("$.issuer").exists())
                .andExpect(jsonPath("$.validFrom").exists())
                .andExpect(jsonPath("$.validTo").exists());
    }

    @Test
    public void testGetCertificateInfoEndpoint_InvalidUrl() throws Exception{
        String invalidUrl = "invalid url";
        String request = "{\"url\": \""+ invalidUrl + "\"}";
        when(certificateService.getCertificateInfo(invalidUrl))
                .thenThrow(new CertificateServiceException("Invalid URL format"));
        this.mockMvc.perform(post("/api/certificates/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error processing certificate"))
                .andExpect(jsonPath("$.message").value("Invalid URL format"));
    }

    @Test
    public void testGetCertificateInfoEndpoint_NotSecureURL() throws Exception{
        String unsecureUrl = "http://www.google.com";
        String request = "{\"url\": \""+ unsecureUrl + "\"}";
        when(certificateService.getCertificateInfo(unsecureUrl))
                .thenThrow(new CertificateServiceException("Only HTTPS URLs are supported."));
        this.mockMvc.perform(post("/api/certificates/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error processing certificate"))
                .andExpect(jsonPath("$.message").value("Only HTTPS URLs are supported."));
    }

    @Test
    public void testGetCertificateInfoEndpoint_HTTPError() throws Exception{
        String fakeUrl = "http://www.my-fake-web-site.com";
        String request = "{\"url\": \""+ fakeUrl + "\"}";
        when(certificateService.getCertificateInfo(fakeUrl))
                .thenThrow(new CertificateServiceException("Error while establishing the HTTPS connection"));
        this.mockMvc.perform(post("/api/certificates/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error processing certificate"))
                .andExpect(jsonPath("$.message").value("Error while establishing the HTTPS connection"));
    }

    @Test
    public void testGetAllCertificates() throws Exception {
        List<Certificate> list = new ArrayList<>();
        list.add(certificate1);
        list.add(certificate2);

        when(certificateService.getAllCertificates()).thenReturn(list);

        this.mockMvc.perform(get("/api/certificates/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(list.size())));
    }

    @Test
    public void testGetAllCertificates_EmptyList() throws Exception {
        List<Certificate> list = new ArrayList<>();

        when(certificateService.getAllCertificates())
                .thenThrow(new CertificateNoContentException("No certificates found in the database"));

        this.mockMvc.perform(get("/api/certificates/all"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No Content Found"))
                .andExpect(jsonPath("$.message").value("No certificates found in the database"));
    }

    @Test
    void testDeleteCertificateById() throws Exception {
        // Define the ID of the certificate to be deleted
        Long certificateId = 1L;

        // Perform an HTTP DELETE request to the /api/certificates/{id} endpoint
        mockMvc.perform(delete("/api/certificates/delete/{id}", certificateId))
                // Expect a successful response with HTTP status 204 No Content
                .andExpect(status().isNoContent());

        // Verify that the deleteCertificateById method in the service was called with the specified ID
        verify(certificateService, times(1)).deleteCertificateById(certificateId);
    }

    @Test
    void testDeleteCertificateById_NonExistingId() throws Exception {
        Long nonExistingId = 999L;

        doThrow(new EntityNotFoundException("Certificate with ID " + nonExistingId + " not found"))
                .when(certificateService)
                .deleteCertificateById(nonExistingId);

        this.mockMvc.perform(delete("/api/certificates/delete/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Certificate not Found"))
                .andExpect(jsonPath("$.message").value("Certificate with ID " + nonExistingId + " not found"));
    }
}
