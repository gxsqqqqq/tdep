package com.tdep.document.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentExportPdfRequest {

    @NotNull
    private Long documentId;

    private Boolean preview;
}
