package com.comfandi.korlon.manager.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PortfolioGroup {
    private Long id;
    private String name;
}
