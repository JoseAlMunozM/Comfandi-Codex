package com.comfandi.korlon.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "billing_account_amortization")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccountAmortizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "billing_account_id")
    private BillingAccountEntity billingAccount;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortfolioEntity portfolio;

    @Column(name = "amortization_number")
    private Integer amortizationNumber;

    @Column(name = "amortization_date")
    private LocalDate amortizationDate;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private Integer month;

    @Column(name = "percent")
    private Double percent;

    @Column(name = "value")
    private Double value;

    @Column(name = "amortization_url", columnDefinition = "TEXT")
    private String amortizatioUrl1;
}
