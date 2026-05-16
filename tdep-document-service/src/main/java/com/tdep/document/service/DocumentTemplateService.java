package com.tdep.document.service;

import com.tdep.document.dto.DocumentTemplateListRequest;
import com.tdep.document.dto.DocumentTemplateUploadRequest;
import com.tdep.document.vo.DocumentTemplateVO;

import java.util.List;

public interface DocumentTemplateService {

    DocumentTemplateVO upload(DocumentTemplateUploadRequest request);

    List<DocumentTemplateVO> list(DocumentTemplateListRequest request);

    DocumentTemplateVO enable(Long id);

    DocumentTemplateVO disable(Long id);

    DocumentTemplateVO preview(Long id);
}
