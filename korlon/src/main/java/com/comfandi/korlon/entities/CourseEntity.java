package com.comfandi.korlon.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "id_modality", nullable = false)
    private ModalityEntity modality;

    @ManyToOne
    @JoinColumn(name = "id_provider",nullable = false)
    private ProviderEntity provider;

    @Column(name = "start_date_training")
    private LocalDateTime trainingStartDate;

    @Column(name = "end_date_training")
    private LocalDateTime trainingEndDate;

    @Column(name = "value")
    private Double value;

    @Column(name = "year")
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "id_portfolio", nullable = true)
    private PortfolioEntity portfolio;
}
