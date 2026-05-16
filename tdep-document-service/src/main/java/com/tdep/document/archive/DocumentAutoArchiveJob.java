package com.tdep.document.archive;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tdep.document.entity.DocumentArchive;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.enums.DocumentStatus;
import com.tdep.document.mapper.DocumentArchiveMapper;
import com.tdep.document.mapper.DocumentFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "tdep.document.archive", name = "auto-enabled", havingValue = "true")
public class DocumentAutoArchiveJob {

    private final DocumentFileMapper documentFileMapper;

    private final DocumentArchiveMapper documentArchiveMapper;

    private final DocumentArchiveService documentArchiveService;

    private final DocumentArchiveProperties archiveProperties;

    @Scheduled(cron = "${tdep.document.archive.cron:0 0 2 * * ?}")
    @Transactional(rollbackFor = Exception.class)
    public void archiveEffectiveDocuments() {
        List<DocumentFile> documents = documentFileMapper.selectList(new LambdaQueryWrapper<DocumentFile>()
                .eq(DocumentFile::getStatus, DocumentStatus.EFFECTIVE.name())
                .last("limit " + archiveProperties.getBatchSize()));
        for (DocumentFile documentFile : documents) {
            if (!StringUtils.hasText(documentFile.getPdfObjectKey()) && !StringUtils.hasText(documentFile.getSignedObjectKey())) {
                continue;
            }
            Long count = documentArchiveMapper.selectCount(new LambdaQueryWrapper<DocumentArchive>()
                    .eq(DocumentArchive::getDocumentId, documentFile.getId()));
            if (count != null && count > 0) {
                continue;
            }
            DocumentArchive archive = documentArchiveService.archive(documentFile, null);
            documentArchiveMapper.insert(archive);
            documentFileMapper.update(null, new LambdaUpdateWrapper<DocumentFile>()
                    .eq(DocumentFile::getId, documentFile.getId())
                    .set(DocumentFile::getStatus, DocumentStatus.ARCHIVED.name()));
        }
    }
}
