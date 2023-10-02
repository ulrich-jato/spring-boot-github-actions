package com.devops.certtracker.controller;

import com.devops.certtracker.entity.Certificate;
import com.devops.certtracker.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @PostMapping("/info")
    public ResponseEntity<Object> getCertificateInfo(@RequestBody Map<String, String> requestBody) {
        String url = requestBody.get("url");
        Certificate certificate = certificateService.getCertificateInfo(url);
        return ResponseEntity.ok(certificate);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addCertificate(@RequestBody Map<String, String> requestBody) {
        String url = requestBody.get("url");
        Certificate certificate = certificateService.retrieveAndSaveCertificate(url);
        return ResponseEntity.ok(certificate);
    }

    @DeleteMapping("delete/{certificateId}")
    public ResponseEntity<Void> deleteCertificateById(@PathVariable Long certificateId){
        certificateService.deleteCertificateById(certificateId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    ResponseEntity<List<Certificate>> getALLCertificates(){
        List<Certificate> certificates = certificateService.getAllCertificates();
        return ResponseEntity.ok(certificates);
    }

}
