package com.tdep.document.sign;

import com.tdep.document.dto.DocumentSignRequest;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.entity.DocumentSign;

public interface DocumentSignService {

    DocumentSign sign(DocumentFile documentFile, DocumentSignRequest request);
}
