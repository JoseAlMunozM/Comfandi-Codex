package com.comfandi.phobos.entity;

import jakarta.persistence.*;
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
