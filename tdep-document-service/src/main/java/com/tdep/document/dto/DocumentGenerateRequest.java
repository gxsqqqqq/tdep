package com.tdep.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class DocumentGenerateRequest {

    @NotNull
    private Long caseId;

    @NotBlank
    private String documentType;

    private String caseStatus;

    private String title;

    private Long templateId;

    private Boolean exportPdf;

    private Map<String, Object> context;
}
