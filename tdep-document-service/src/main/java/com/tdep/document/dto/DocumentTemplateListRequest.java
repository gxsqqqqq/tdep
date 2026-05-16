package com.tdep.document.dto;

import lombok.Data;

@Data
public class DocumentTemplateListRequest {

    private String documentType;

    private Integer enabled;
}
