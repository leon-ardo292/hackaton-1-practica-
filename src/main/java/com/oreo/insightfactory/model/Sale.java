package com.oreo.insightfactory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Integer units;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Instant soldAt;

    @Column(nullable = false)
    private String createdBy;

    protected Sale() {
    }

    public Sale(String sku, Integer units, BigDecimal price, String branch, Instant soldAt, String createdBy) {
        this.branch = branch;
        this.sku = sku;
        this.units = units;
        this.price = price;
        this.soldAt = soldAt;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getBranch() {
        return branch;
    }

    public String getSku() {
        return sku;
    }

    public String getPublicId() {
        return "s_" + id;
    }

    public Integer getUnits() {
        return units;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getSoldAt() {
        return soldAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public BigDecimal getTotalAmount() {
        return price.multiply(BigDecimal.valueOf(units));
    }

    public void update(String sku, Integer units, BigDecimal price, String branch, Instant soldAt) {
        this.sku = sku;
        this.units = units;
        this.price = price;
        this.branch = branch;
        this.soldAt = soldAt;
    }
}
