package com.tdep.document.pdf;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PdfExportResult {

    private String bucketName;

    private String objectKey;

    private String fileHash;
}
