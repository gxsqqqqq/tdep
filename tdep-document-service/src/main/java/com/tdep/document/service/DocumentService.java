package com.tdep.document.service;

import com.tdep.document.dto.DocumentExportPdfRequest;
import com.tdep.document.dto.DocumentGenerateRequest;
import com.tdep.document.dto.DocumentSignRequest;
import com.tdep.document.enums.DocumentFileFormat;
import com.tdep.document.vo.DocumentArchiveVO;
import com.tdep.document.vo.DocumentDownloadVO;
import com.tdep.document.vo.DocumentFileVO;

public interface DocumentService {

    DocumentFileVO generate(DocumentGenerateRequest request);

    DocumentFileVO getById(Long id);

    DocumentFileVO exportPdf(DocumentExportPdfRequest request);

    DocumentFileVO sign(DocumentSignRequest request);

    DocumentDownloadVO download(Long id, DocumentFileFormat format);

    DocumentDownloadVO previewPdf(Long id);

    DocumentArchiveVO archive(Long id);

    Boolean verifySign(Long id);
}
