package com.comfandi.phobos.service.dto;

import java.util.Map;

import com.comfandi.phobos.entity.ValidationDocument;

import lombok.*;

@Data
@AllArgsConstructor
public class SubsanacionResponse {
    private ValidationDocument excel;
    private Map<String, ValidationDocument> rapFiles;
}