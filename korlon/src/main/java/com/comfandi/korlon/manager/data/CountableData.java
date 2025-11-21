package com.comfandi.korlon.manager.data;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountableData {
    private String portfolioId;
    private Double sum;
    private Long cant;

}
