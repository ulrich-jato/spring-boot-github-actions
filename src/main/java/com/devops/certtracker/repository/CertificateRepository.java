package com.devops.certtracker.repository;

import com.devops.certtracker.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

}
