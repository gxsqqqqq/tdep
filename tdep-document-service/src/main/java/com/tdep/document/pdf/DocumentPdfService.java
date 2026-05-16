package com.tdep.document.pdf;

import com.tdep.document.entity.DocumentFile;

public interface DocumentPdfService {

    PdfExportResult exportPdf(DocumentFile documentFile);
}
