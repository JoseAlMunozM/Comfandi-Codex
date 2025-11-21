package com.comfandi.korlon.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "modality")
@Getter
@Setter
@Builder
public class ModalityEntity extends BaseEntity{

}
