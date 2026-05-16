package com.tdep.document.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.document.dto.DocumentTemplateListRequest;
import com.tdep.document.dto.DocumentTemplateUploadRequest;
import com.tdep.document.entity.DocumentTemplate;
import com.tdep.document.enums.DocumentType;
import com.tdep.document.enums.TemplateEngineType;
import com.tdep.document.mapper.DocumentTemplateMapper;
import com.tdep.document.storage.DocumentObjectStorageService;
import com.tdep.document.storage.DocumentStorageObject;
import com.tdep.document.storage.DocumentStorageProperties;
import com.tdep.document.template.DocumentTemplateParser;
import com.tdep.document.util.DocumentHashUtil;
import com.tdep.document.vo.DocumentTemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentTemplateServiceImpl implements com.tdep.document.service.DocumentTemplateService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final DocumentTemplateMapper documentTemplateMapper;

    private final DocumentObjectStorageService objectStorageService;

    private final DocumentStorageProperties storageProperties;

    private final DocumentTemplateParser templateParser;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentTemplateVO upload(DocumentTemplateUploadRequest request) {
        DocumentType documentType = parseDocumentType(request.getDocumentType());
        MultipartFile file = request.getFile();
        validateTemplateFile(file);

        int nextVersion = nextVersion(documentType.name());
        String templateCode = StringUtils.hasText(request.getTemplateCode())
                ? request.getTemplateCode()
                : documentType.name() + "-V" + nextVersion;
        String objectKey = "template/" + documentType.name().toLowerCase() + "/" + LocalDate.now().format(DATE_FORMATTER)
                + "/" + templateCode + "-v" + nextVersion + ".docx";

        byte[] bytes = readFile(file);
        List<String> placeholders = templateParser.parse(new java.io.ByteArrayInputStream(bytes));
        Map<String, String> metadata = new HashMap<>();
        metadata.put("document-type", documentType.name());
        metadata.put("template-name", request.getTemplateName());
        metadata.put("template-code", templateCode);
        metadata.put("version", String.valueOf(nextVersion));
        metadata.put("sha256", DocumentHashUtil.sha256(bytes));
        metadata.put("placeholders", String.join(",", placeholders));

        DocumentStorageObject storageObject = objectStorageService.putObject(storageProperties.getTemplateBucket(),
                objectKey, bytes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", metadata);

        boolean setCurrent = request.getCurrentVersion() == null || Boolean.TRUE.equals(request.getCurrentVersion());
        if (setCurrent) {
            clearCurrentVersion(documentType.name());
        }

        DocumentTemplate template = new DocumentTemplate();
        template.setTemplateCode(templateCode);
        template.setDocumentType(documentType.name());
        template.setTemplateName(request.getTemplateName());
        template.setVersion(nextVersion);
        template.setBucketName(storageObject.getBucketName());
        template.setObjectKey(storageObject.getObjectKey());
        template.setEngineType(StringUtils.hasText(request.getEngineType()) ? request.getEngineType() : TemplateEngineType.POI_TL.name());
        template.setEnabled(1);
        template.setCurrentVersion(setCurrent ? 1 : 0);
        documentTemplateMapper.insert(template);
        return toVO(template, true);
    }

    @Override
    public List<DocumentTemplateVO> list(DocumentTemplateListRequest request) {
        LambdaQueryWrapper<DocumentTemplate> wrapper = new LambdaQueryWrapper<>();
        if (request != null && StringUtils.hasText(request.getDocumentType())) {
            wrapper.eq(DocumentTemplate::getDocumentType, parseDocumentType(request.getDocumentType()).name());
        }
        if (request != null && request.getEnabled() != null) {
            wrapper.eq(DocumentTemplate::getEnabled, request.getEnabled());
        }
        wrapper.orderByDesc(DocumentTemplate::getDocumentType)
                .orderByDesc(DocumentTemplate::getVersion);
        return documentTemplateMapper.selectList(wrapper).stream()
                .map(template -> toVO(template, false))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentTemplateVO enable(Long id) {
        DocumentTemplate template = requireTemplate(id);
        clearCurrentVersion(template.getDocumentType());
        template.setEnabled(1);
        template.setCurrentVersion(1);
        documentTemplateMapper.updateById(template);
        return toVO(template, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentTemplateVO disable(Long id) {
        DocumentTemplate template = requireTemplate(id);
        template.setEnabled(0);
        template.setCurrentVersion(0);
        documentTemplateMapper.updateById(template);
        return toVO(template, true);
    }

    @Override
    public DocumentTemplateVO preview(Long id) {
        return toVO(requireTemplate(id), true);
    }

    private DocumentTemplate requireTemplate(Long id) {
        DocumentTemplate template = documentTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文书模板不存在");
        }
        return template;
    }

    private int nextVersion(String documentType) {
        DocumentTemplate latest = documentTemplateMapper.selectOne(new LambdaQueryWrapper<DocumentTemplate>()
                .eq(DocumentTemplate::getDocumentType, documentType)
                .orderByDesc(DocumentTemplate::getVersion)
                .last("limit 1"));
        return latest == null ? 1 : latest.getVersion() + 1;
    }

    private void clearCurrentVersion(String documentType) {
        List<DocumentTemplate> currentTemplates = documentTemplateMapper.selectList(new LambdaQueryWrapper<DocumentTemplate>()
                .eq(DocumentTemplate::getDocumentType, documentType)
                .eq(DocumentTemplate::getCurrentVersion, 1));
        for (DocumentTemplate template : currentTemplates) {
            template.setCurrentVersion(0);
            documentTemplateMapper.updateById(template);
        }
    }

    private void validateTemplateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "模板文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename) || !filename.toLowerCase().endsWith(".docx")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "模板文件必须是 docx 格式");
        }
    }

    private byte[] readFile(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "模板文件读取失败: " + ex.getMessage());
        }
    }

    private DocumentType parseDocumentType(String documentType) {
        try {
            return DocumentType.valueOf(documentType);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的文书类型");
        }
    }

    private DocumentTemplateVO toVO(DocumentTemplate template, boolean includePreviewUrl) {
        String previewUrl = includePreviewUrl
                ? objectStorageService.presignedGetUrl(template.getBucketName(), template.getObjectKey())
                : null;
        List<String> placeholders = includePreviewUrl ? parsePlaceholders(template) : List.of();
        return DocumentTemplateVO.builder()
                .id(template.getId())
                .templateCode(template.getTemplateCode())
                .documentType(template.getDocumentType())
                .templateName(template.getTemplateName())
                .version(template.getVersion())
                .bucketName(template.getBucketName())
                .objectKey(template.getObjectKey())
                .engineType(template.getEngineType())
                .enabled(template.getEnabled())
                .currentVersion(template.getCurrentVersion())
                .previewUrl(previewUrl)
                .placeholders(placeholders)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }

    private List<String> parsePlaceholders(DocumentTemplate template) {
        try (InputStream inputStream = objectStorageService.getObject(template.getBucketName(), template.getObjectKey())) {
            return templateParser.parse(inputStream);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "模板文件读取失败: " + ex.getMessage());
        }
    }
}
