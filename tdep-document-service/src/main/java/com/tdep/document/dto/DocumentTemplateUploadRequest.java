package com.tdep.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentTemplateUploadRequest {

    @NotBlank
    private String documentType;

    @NotBlank
    private String templateName;

    private String templateCode;

    private String engineType;

    private Boolean currentVersion;

    @NotNull
    private MultipartFile file;
}
