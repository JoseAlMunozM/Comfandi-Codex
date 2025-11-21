package com.comfandi.korlon.manager.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRevoked {

    private Long accountId;
    private String document;
    private String documentType;
    private String status;
    private String reason;
}
