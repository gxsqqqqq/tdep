package com.tdep.document.dto;

import com.tdep.document.enums.DocumentFileFormat;
import lombok.Data;

@Data
public class DocumentDownloadRequest {

    private DocumentFileFormat format = DocumentFileFormat.SIGNED_PDF;
}
