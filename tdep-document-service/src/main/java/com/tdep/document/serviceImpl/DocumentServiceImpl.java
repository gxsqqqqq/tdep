package com.tdep.document.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.document.audit.DocumentAuditRecorder;
import com.tdep.document.archive.DocumentArchiveService;
import com.tdep.document.dto.DocumentExportPdfRequest;
import com.tdep.document.dto.DocumentGenerateRequest;
import com.tdep.document.dto.DocumentSignRequest;
import com.tdep.document.entity.DocumentArchive;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.entity.DocumentSign;
import com.tdep.document.entity.DocumentTemplate;
import com.tdep.document.enums.DocumentAction;
import com.tdep.document.enums.DocumentFileFormat;
import com.tdep.document.enums.DocumentOperationType;
import com.tdep.document.enums.DocumentRole;
import com.tdep.document.enums.DocumentStatus;
import com.tdep.document.enums.DocumentType;
import com.tdep.document.enums.OperationResult;
import com.tdep.document.mapper.DocumentFileMapper;
import com.tdep.document.mapper.DocumentArchiveMapper;
import com.tdep.document.mapper.DocumentSignMapper;
import com.tdep.document.mapper.DocumentTemplateMapper;
import com.tdep.document.pdf.DocumentPdfService;
import com.tdep.document.pdf.PdfExportResult;
import com.tdep.document.security.DocumentPermissionChecker;
import com.tdep.document.sign.DocumentSignService;
import com.tdep.document.storage.DocumentObjectStorageService;
import com.tdep.document.template.DocumentTemplateRenderer;
import com.tdep.document.template.RenderedDocument;
import com.tdep.document.vo.DocumentArchiveVO;
import com.tdep.document.vo.DocumentDownloadVO;
import com.tdep.document.vo.DocumentFileVO;
import com.tdep.document.workflow.DocumentStateMachine;
import com.tdep.document.workflow.DocumentTransition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements com.tdep.document.service.DocumentService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final DocumentFileMapper documentFileMapper;

    private final DocumentTemplateMapper documentTemplateMapper;

    private final DocumentSignMapper documentSignMapper;

    private final DocumentArchiveMapper documentArchiveMapper;

    private final DocumentTemplateRenderer documentTemplateRenderer;

    private final DocumentPdfService documentPdfService;

    private final DocumentSignService documentSignService;

    private final DocumentArchiveService documentArchiveService;

    private final DocumentObjectStorageService objectStorageService;

    private final DocumentPermissionChecker permissionChecker;

    private final DocumentStateMachine stateMachine;

    private final DocumentAuditRecorder auditRecorder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentFileVO generate(DocumentGenerateRequest request) {
        DocumentType documentType = parseDocumentType(request.getDocumentType());
        validateCaseStatus(documentType, request.getCaseStatus());
        ensureActionAllowed(DocumentStatus.DRAFT, DocumentAction.GENERATE);

        DocumentTemplate template = resolveTemplate(documentType, request.getTemplateId());
        DocumentFile documentFile = new DocumentFile();
        documentFile.setCaseId(request.getCaseId());
        documentFile.setDocumentType(documentType.name());
        documentFile.setDocumentNo(generateDocumentNo(request.getCaseId(), documentType));
        documentFile.setTitle(resolveTitle(request, documentType));
        documentFile.setStatus(DocumentStatus.GENERATED.name());
        documentFile.setTemplateId(template == null ? null : template.getId());
        documentFile.setCreatedBy(permissionChecker.currentUserId());
        documentFile.setGeneratedAt(LocalDateTime.now());

        RenderedDocument renderedDocument = documentTemplateRenderer.render(template, documentFile, safeContext(request.getContext()));
        documentFile.setWordBucket(renderedDocument.getBucketName());
        documentFile.setWordObjectKey(renderedDocument.getObjectKey());
        documentFile.setFileHash(renderedDocument.getFileHash());

        if (Boolean.TRUE.equals(request.getExportPdf())) {
            PdfExportResult pdfResult = documentPdfService.exportPdf(documentFile);
            documentFile.setPdfBucket(pdfResult.getBucketName());
            documentFile.setPdfObjectKey(pdfResult.getObjectKey());
            documentFile.setFileHash(pdfResult.getFileHash());
        }

        documentFileMapper.insert(documentFile);
        auditRecorder.record(documentFile, DocumentOperationType.GENERATE, DocumentStatus.DRAFT.name(),
                documentFile.getStatus(), OperationResult.SUCCESS, "generate document");
        return toVO(documentFile);
    }

    @Override
    public DocumentFileVO getById(Long id) {
        return toVO(requireDocument(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentFileVO exportPdf(DocumentExportPdfRequest request) {
        DocumentFile documentFile = requireDocument(request.getDocumentId());
        ensureReadable(documentFile);
        PdfExportResult pdfResult = documentPdfService.exportPdf(documentFile);
        documentFile.setPdfBucket(pdfResult.getBucketName());
        documentFile.setPdfObjectKey(pdfResult.getObjectKey());
        documentFile.setFileHash(pdfResult.getFileHash());
        documentFileMapper.updateById(documentFile);
        auditRecorder.record(documentFile, DocumentOperationType.EXPORT_PDF, documentFile.getStatus(),
                documentFile.getStatus(), OperationResult.SUCCESS, "export pdf");
        return toVO(documentFile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentFileVO sign(DocumentSignRequest request) {
        DocumentFile documentFile = requireDocument(request.getDocumentId());
        DocumentStatus beforeStatus = parseDocumentStatus(documentFile.getStatus());
        DocumentTransition transition = requireTransition(beforeStatus, DocumentAction.SIGN);

        DocumentSign sign = documentSignService.sign(documentFile, request);
        documentFile.setStatus(transition.getToStatus().name());
        documentFile.setSignedBy(sign.getSignerId());
        documentFile.setSignedAt(sign.getSignedAt());

        documentSignMapper.insert(sign);
        documentFileMapper.updateById(documentFile);
        auditRecorder.record(documentFile, DocumentOperationType.SIGN, beforeStatus.name(),
                documentFile.getStatus(), OperationResult.SUCCESS, "sign document");
        return toVO(documentFile);
    }

    @Override
    public DocumentDownloadVO download(Long id, DocumentFileFormat format) {
        DocumentFile documentFile = requireDocument(id);
        ensureReadable(documentFile);
        DocumentFileFormat targetFormat = format == null ? DocumentFileFormat.SIGNED_PDF : format;
        FileLocation fileLocation = resolveFileLocation(documentFile, targetFormat);
        String downloadUrl = objectStorageService.presignedGetUrl(fileLocation.bucketName(), fileLocation.objectKey());
        auditRecorder.record(documentFile, DocumentOperationType.DOWNLOAD, documentFile.getStatus(),
                documentFile.getStatus(), OperationResult.SUCCESS, "download " + targetFormat.name());
        return DocumentDownloadVO.builder()
                .documentId(documentFile.getId())
                .fileName(documentFile.getDocumentNo() + fileLocation.extension())
                .bucketName(fileLocation.bucketName())
                .objectKey(fileLocation.objectKey())
                .downloadUrl(downloadUrl)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentDownloadVO previewPdf(Long id) {
        DocumentFile documentFile = requireDocument(id);
        ensureReadable(documentFile);
        if (!StringUtils.hasText(documentFile.getPdfObjectKey())) {
            PdfExportResult pdfResult = documentPdfService.exportPdf(documentFile);
            documentFile.setPdfBucket(pdfResult.getBucketName());
            documentFile.setPdfObjectKey(pdfResult.getObjectKey());
            documentFile.setFileHash(pdfResult.getFileHash());
            documentFileMapper.updateById(documentFile);
        }
        DocumentDownloadVO preview = download(documentFile.getId(), DocumentFileFormat.PDF);
        auditRecorder.record(documentFile, DocumentOperationType.PREVIEW, documentFile.getStatus(),
                documentFile.getStatus(), OperationResult.SUCCESS, "preview pdf");
        return preview;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentArchiveVO archive(Long id) {
        DocumentFile documentFile = requireDocument(id);
        DocumentStatus beforeStatus = parseDocumentStatus(documentFile.getStatus());
        if (beforeStatus != DocumentStatus.EFFECTIVE && beforeStatus != DocumentStatus.SIGNED) {
            throw new BusinessException(ResultCode.CONFLICT, "只有已签章或已生效文书可以归档");
        }
        ensureArchiveAllowed(beforeStatus);
        DocumentArchive archive = documentArchiveService.archive(documentFile, permissionChecker.currentUserId());
        documentArchiveMapper.insert(archive);
        documentFile.setStatus(DocumentStatus.ARCHIVED.name());
        documentFileMapper.updateById(documentFile);
        auditRecorder.record(documentFile, DocumentOperationType.ARCHIVE, beforeStatus.name(),
                documentFile.getStatus(), OperationResult.SUCCESS, "archive document");
        return toArchiveVO(archive);
    }

    @Override
    public Boolean verifySign(Long id) {
        DocumentFile documentFile = requireDocument(id);
        if (!StringUtils.hasText(documentFile.getSignedBucket()) || !StringUtils.hasText(documentFile.getSignedObjectKey())) {
            throw new BusinessException(ResultCode.CONFLICT, "文书尚未签章");
        }
        DocumentSign latestSign = documentSignMapper.selectOne(new LambdaQueryWrapper<DocumentSign>()
                .eq(DocumentSign::getDocumentId, documentFile.getId())
                .orderByDesc(DocumentSign::getSignedAt)
                .last("limit 1"));
        if (latestSign == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "签章记录不存在");
        }
        try (java.io.InputStream inputStream = objectStorageService.getObject(documentFile.getSignedBucket(), documentFile.getSignedObjectKey())) {
            String currentHash = com.tdep.document.util.DocumentHashUtil.sha256(inputStream.readAllBytes());
            return currentHash.equalsIgnoreCase(latestSign.getAfterHash());
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "签章校验失败: " + ex.getMessage());
        }
    }

    private DocumentFile requireDocument(Long id) {
        DocumentFile documentFile = documentFileMapper.selectById(id);
        if (documentFile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文书不存在");
        }
        return documentFile;
    }

    private DocumentType parseDocumentType(String documentType) {
        try {
            return DocumentType.valueOf(documentType);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的文书类型");
        }
    }

    private DocumentStatus parseDocumentStatus(String status) {
        try {
            return DocumentStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.CONFLICT, "文书状态无效");
        }
    }

    private void validateCaseStatus(DocumentType documentType, String caseStatus) {
        if (!StringUtils.hasText(caseStatus)) {
            return;
        }
        if (!documentType.getAllowedCaseStatuses().contains(caseStatus)) {
            throw new BusinessException(ResultCode.CONFLICT, "当前案件阶段不允许生成该类型文书");
        }
    }

    private DocumentTemplate resolveTemplate(DocumentType documentType, Long templateId) {
        if (templateId != null) {
            DocumentTemplate template = documentTemplateMapper.selectById(templateId);
            if (template == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "文书模板不存在");
            }
            return template;
        }
        return documentTemplateMapper.selectOne(new LambdaQueryWrapper<DocumentTemplate>()
                .eq(DocumentTemplate::getDocumentType, documentType.name())
                .eq(DocumentTemplate::getEnabled, 1)
                .eq(DocumentTemplate::getCurrentVersion, 1)
                .last("limit 1"));
    }

    private void ensureActionAllowed(DocumentStatus status, DocumentAction action) {
        requireTransition(status, action);
    }

    private void ensureArchiveAllowed(DocumentStatus status) {
        if (status == DocumentStatus.SIGNED) {
            Set<DocumentRole> roles = permissionChecker.currentDocumentRoles();
            boolean allowed = roles.contains(DocumentRole.CLERK) || roles.contains(DocumentRole.ADMIN);
            if (!allowed) {
                throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权归档文书");
            }
            return;
        }
        requireTransition(status, DocumentAction.ARCHIVE);
    }

    private DocumentTransition requireTransition(DocumentStatus status, DocumentAction action) {
        Set<DocumentRole> roles = permissionChecker.currentDocumentRoles();
        DocumentTransition transition = stateMachine.findTransition(status, action)
                .orElseThrow(() -> new BusinessException(ResultCode.CONFLICT, "文书状态不允许执行该操作"));
        boolean allowed = transition.getAllowedRoles().stream().anyMatch(roles::contains);
        if (!allowed) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权执行该文书操作");
        }
        return transition;
    }

    private void ensureReadable(DocumentFile documentFile) {
        Set<DocumentRole> roles = permissionChecker.currentDocumentRoles();
        if (roles.stream().noneMatch(role -> role == DocumentRole.ADMIN || role == DocumentRole.JUDGE
                || role == DocumentRole.CLERK || role == DocumentRole.PARTY)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权查看该文书");
        }
    }

    private FileLocation resolveFileLocation(DocumentFile documentFile, DocumentFileFormat format) {
        if (format == DocumentFileFormat.WORD && StringUtils.hasText(documentFile.getWordObjectKey())) {
            return new FileLocation(documentFile.getWordBucket(), documentFile.getWordObjectKey(), ".docx");
        }
        if (format == DocumentFileFormat.PDF && StringUtils.hasText(documentFile.getPdfObjectKey())) {
            return new FileLocation(documentFile.getPdfBucket(), documentFile.getPdfObjectKey(), ".pdf");
        }
        if (StringUtils.hasText(documentFile.getSignedObjectKey())) {
            return new FileLocation(documentFile.getSignedBucket(), documentFile.getSignedObjectKey(), ".pdf");
        }
        if (StringUtils.hasText(documentFile.getPdfObjectKey())) {
            return new FileLocation(documentFile.getPdfBucket(), documentFile.getPdfObjectKey(), ".pdf");
        }
        if (StringUtils.hasText(documentFile.getWordObjectKey())) {
            return new FileLocation(documentFile.getWordBucket(), documentFile.getWordObjectKey(), ".docx");
        }
        throw new BusinessException(ResultCode.NOT_FOUND, "文书文件不存在");
    }

    private String resolveTitle(DocumentGenerateRequest request, DocumentType documentType) {
        return StringUtils.hasText(request.getTitle()) ? request.getTitle() : documentType.getName();
    }

    private String generateDocumentNo(Long caseId, DocumentType documentType) {
        return "DOC-" + LocalDate.now().format(DATE_FORMATTER) + "-" + caseId + "-"
                + documentType.getShortName() + "-" + System.nanoTime();
    }

    private Map<String, Object> safeContext(Map<String, Object> context) {
        return context == null ? Map.of() : context;
    }

    private DocumentFileVO toVO(DocumentFile documentFile) {
        return DocumentFileVO.builder()
                .id(documentFile.getId())
                .caseId(documentFile.getCaseId())
                .documentNo(documentFile.getDocumentNo())
                .documentType(documentFile.getDocumentType())
                .title(documentFile.getTitle())
                .status(documentFile.getStatus())
                .templateId(documentFile.getTemplateId())
                .wordObjectKey(documentFile.getWordObjectKey())
                .pdfObjectKey(documentFile.getPdfObjectKey())
                .signedObjectKey(documentFile.getSignedObjectKey())
                .fileHash(documentFile.getFileHash())
                .createdBy(documentFile.getCreatedBy())
                .reviewedBy(documentFile.getReviewedBy())
                .signedBy(documentFile.getSignedBy())
                .generatedAt(documentFile.getGeneratedAt())
                .reviewedAt(documentFile.getReviewedAt())
                .signedAt(documentFile.getSignedAt())
                .effectiveAt(documentFile.getEffectiveAt())
                .createdAt(documentFile.getCreatedAt())
                .updatedAt(documentFile.getUpdatedAt())
                .build();
    }

    private DocumentArchiveVO toArchiveVO(DocumentArchive archive) {
        return DocumentArchiveVO.builder()
                .id(archive.getId())
                .documentId(archive.getDocumentId())
                .caseId(archive.getCaseId())
                .archiveNo(archive.getArchiveNo())
                .bucketName(archive.getBucketName())
                .objectKey(archive.getObjectKey())
                .fileHash(archive.getFileHash())
                .fileSize(archive.getFileSize())
                .archivedBy(archive.getArchivedBy())
                .archivedAt(archive.getArchivedAt())
                .retentionYears(archive.getRetentionYears())
                .downloadUrl(objectStorageService.presignedGetUrl(archive.getBucketName(), archive.getObjectKey()))
                .build();
    }

    private record FileLocation(String bucketName, String objectKey, String extension) {
    }
}
