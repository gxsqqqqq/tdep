package com.tdep.document.archive;

import com.tdep.document.entity.DocumentArchive;
import com.tdep.document.entity.DocumentFile;

public interface DocumentArchiveService {

    DocumentArchive archive(DocumentFile documentFile, Long archivedBy);
}
