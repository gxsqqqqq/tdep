package com.tdep.document.template;

import com.tdep.document.entity.DocumentFile;
import com.tdep.document.entity.DocumentTemplate;

import java.util.Map;

public interface DocumentTemplateRenderer {

    RenderedDocument render(DocumentTemplate template, DocumentFile documentFile, Map<String, Object> context);
}
