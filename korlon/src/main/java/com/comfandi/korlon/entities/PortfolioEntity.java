package com.comfandi.korlon.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "portfolio")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PortfolioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "modality_id")
    private ModalityEntity modality;

    @Column(name = "name")
    private String name;

    @Column(name = "value_fee")
    private Double valueFee;

    @Column(name = "account")
    private String account;

    @Column(name = "down_payment_1")
    private Double downPayment1;

    @Column(name = "down_payment_2")
    private Double downPayment2;

    @Column(name = "down_payment_3")
    private Double downPayment3;
}
