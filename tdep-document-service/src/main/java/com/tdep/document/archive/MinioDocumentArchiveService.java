package com.tdep.document.archive;

import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.document.entity.DocumentArchive;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.storage.DocumentObjectStorageService;
import com.tdep.document.storage.DocumentStorageObject;
import com.tdep.document.storage.DocumentStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Primary
@Service
@RequiredArgsConstructor
public class MinioDocumentArchiveService implements DocumentArchiveService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final DocumentObjectStorageService objectStorageService;

    private final DocumentStorageProperties storageProperties;

    @Override
    public DocumentArchive archive(DocumentFile documentFile, Long archivedBy) {
        FileLocation source = resolveArchiveSource(documentFile);
        String archiveNo = "ARCH-" + LocalDate.now().format(DATE_FORMATTER) + "-" + documentFile.getCaseId() + "-" + documentFile.getId();
        String objectKey = "archive/" + LocalDate.now().getYear() + "/" + documentFile.getCaseId()
                + "/" + documentFile.getDocumentNo() + ".pdf";
        DocumentStorageObject storageObject = objectStorageService.copyObject(source.bucketName(), source.objectKey(),
                storageProperties.getArchiveBucket(), objectKey, Map.of(
                        "case-id", String.valueOf(documentFile.getCaseId()),
                        "document-id", String.valueOf(documentFile.getId()),
                        "document-no", documentFile.getDocumentNo(),
                        "archive-no", archiveNo,
                        "sha256", documentFile.getFileHash() == null ? "" : documentFile.getFileHash()
                ));

        DocumentArchive archive = new DocumentArchive();
        archive.setDocumentId(documentFile.getId());
        archive.setCaseId(documentFile.getCaseId());
        archive.setArchiveNo(archiveNo);
        archive.setBucketName(storageObject.getBucketName());
        archive.setObjectKey(storageObject.getObjectKey());
        archive.setFileHash(documentFile.getFileHash());
        archive.setFileSize(storageObject.getSize() == null ? 0L : storageObject.getSize());
        archive.setArchivedBy(archivedBy);
        archive.setArchivedAt(LocalDateTime.now());
        archive.setRetentionYears(30);
        return archive;
    }

    private FileLocation resolveArchiveSource(DocumentFile documentFile) {
        if (StringUtils.hasText(documentFile.getSignedObjectKey())) {
            return new FileLocation(documentFile.getSignedBucket(), documentFile.getSignedObjectKey());
        }
        if (StringUtils.hasText(documentFile.getPdfObjectKey())) {
            return new FileLocation(documentFile.getPdfBucket(), documentFile.getPdfObjectKey());
        }
        throw new BusinessException(ResultCode.CONFLICT, "文书缺少可归档 PDF 文件");
    }

    private record FileLocation(String bucketName, String objectKey) {
    }
}
