package com.comfandi.korlon.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regional")
@Getter
@Setter
@Builder
public class RegionalEntity extends BaseEntity{

}
