package com.devops.certtracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;

/**
 * Represents a Certificate entity stored in the database.
 */
@Entity
@Table(name = "certificates")
public class Certificate {
    /**
     * The unique identifier (ID) of the certificate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The URL associated with the certificate.
     */
    @Column(name = "url")
    private String url;

    /**
     * The subject of the certificate.
     */
    @Column(name = "subject")
    private String subject;

    /**
     * The issuer of the certificate.
     */
    @Column(name = "issuer")
    private String issuer;

    /**
     * The date when the certificate becomes valid.
     */
    @Column(name = "valid_from")
    private Date validFrom;

    /**
     * The date when the certificate expires.
     */
    @Column(name = "valid_to")
    private Date validTo;


    /**
     * Default constructor.
     */
    public Certificate() {
        // Default constructor
    }

    /**
     * Constructs a Certificate object with the specified attributes.
     *
     * @param url       The URL associated with the certificate.
     * @param subject   The subject of the certificate.
     * @param issuer    The issuer of the certificate.
     * @param validFrom The date when the certificate becomes valid.
     * @param validTo   The date when the certificate expires.
     */
    public Certificate(String url, String subject, String issuer, Date validFrom, Date validTo) {
        this.url = url;
        this.subject = subject;
        this.issuer = issuer;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    // Getters and setters

    /**
     * Gets the ID of the certificate.
     *
     * @return The ID of the certificate.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the certificate.
     *
     * @param id The ID of the certificate.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the URL associated with the certificate.
     *
     * @return The URL of the certificate.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL associated with the certificate.
     *
     * @param url The URL of the certificate.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the subject of the certificate.
     *
     * @return The subject of the certificate.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject of the certificate.
     *
     * @param subject The subject of the certificate.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the issuer of the certificate.
     *
     * @return The issuer of the certificate.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the issuer of the certificate.
     *
     * @param issuer The issuer of the certificate.
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Gets the date when the certificate becomes valid.
     *
     * @return The valid from date of the certificate.
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the date when the certificate becomes valid.
     *
     * @param validFrom The valid from date of the certificate.
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Gets the date when the certificate expires.
     *
     * @return The valid to date of the certificate.
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Sets the date when the certificate expires.
     *
     * @param validTo The valid to date of the certificate.
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
}

