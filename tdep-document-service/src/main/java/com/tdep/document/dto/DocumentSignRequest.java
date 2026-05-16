package com.tdep.document.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentSignRequest {

    @NotNull
    private Long documentId;

    private String signerName;

    private String signType;

    private String certSerialNo;

    private String signatureField;
}
