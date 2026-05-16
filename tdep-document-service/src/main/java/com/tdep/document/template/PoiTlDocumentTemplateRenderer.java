package com.tdep.document.template;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.Configure.ClearHandler;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.entity.DocumentTemplate;
import com.tdep.document.storage.DocumentObjectStorageService;
import com.tdep.document.storage.DocumentStorageObject;
import com.tdep.document.storage.DocumentStorageProperties;
import com.tdep.document.util.DocumentHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Service
@RequiredArgsConstructor
public class PoiTlDocumentTemplateRenderer implements DocumentTemplateRenderer {

    private static final int DEFAULT_IMAGE_WIDTH = 120;

    private static final int DEFAULT_IMAGE_HEIGHT = 60;

    private final DocumentObjectStorageService objectStorageService;

    private final DocumentStorageProperties storageProperties;

    @Override
    public RenderedDocument render(DocumentTemplate template, DocumentFile documentFile, Map<String, Object> context) {
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "当前文书类型未配置可用模板");
        }
        Map<String, Object> renderContext = normalizeContext(context);
        Configure configure = buildConfigure(renderContext);

        try (InputStream templateStream = objectStorageService.getObject(template.getBucketName(), template.getObjectKey());
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateStream, configure);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            xwpfTemplate.render(renderContext);
            xwpfTemplate.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            String objectKey = "word/" + documentFile.getCaseId() + "/" + documentFile.getDocumentNo() + ".docx";
            Map<String, String> metadata = Map.of(
                    "case-id", String.valueOf(documentFile.getCaseId()),
                    "document-no", documentFile.getDocumentNo(),
                    "document-type", documentFile.getDocumentType(),
                    "template-id", String.valueOf(template.getId()),
                    "sha256", DocumentHashUtil.sha256(bytes)
            );
            DocumentStorageObject storageObject = objectStorageService.putObject(storageProperties.getWordBucket(),
                    objectKey, bytes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", metadata);
            return RenderedDocument.builder()
                    .bucketName(storageObject.getBucketName())
                    .objectKey(storageObject.getObjectKey())
                    .fileHash(metadata.get("sha256"))
                    .build();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "Word 文书渲染失败: " + ex.getMessage());
        }
    }

    private Configure buildConfigure(Map<String, Object> renderContext) {
        com.deepoove.poi.config.ConfigureBuilder builder = Configure.builder()
                .useSpringEL()
                .setValidErrorHandler(new ClearHandler());
        for (String key : renderContext.keySet()) {
            Object value = renderContext.get(key);
            if (value instanceof Iterable<?>) {
                builder.bind(key, new LoopRowTableRenderPolicy());
            }
        }
        return builder.build();
    }

    private Map<String, Object> normalizeContext(Map<String, Object> context) {
        Map<String, Object> renderContext = new HashMap<>();
        if (context != null) {
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                renderContext.put(entry.getKey(), normalizeValue(entry.getValue()));
            }
        }
        renderContext.putIfAbsent("generatedAt", java.time.LocalDate.now().toString());
        return renderContext;
    }

    @SuppressWarnings("unchecked")
    private Object normalizeValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> normalized = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                normalized.put(String.valueOf(entry.getKey()), normalizeValue(entry.getValue()));
            }
            if (isImageMap(normalized)) {
                return toPicture(normalized);
            }
            return normalized;
        }
        if (value instanceof List<?> list) {
            return list.stream().map(this::normalizeValue).toList();
        }
        return value;
    }

    private boolean isImageMap(Map<String, Object> value) {
        return StringUtils.hasText(asString(value.get("base64")))
                || (StringUtils.hasText(asString(value.get("bucketName"))) && StringUtils.hasText(asString(value.get("objectKey"))));
    }

    private PictureRenderData toPicture(Map<String, Object> image) {
        byte[] bytes = readImageBytes(image);
        PictureType pictureType = resolvePictureType(asString(image.get("type")), bytes);
        int width = asInt(image.get("width"), DEFAULT_IMAGE_WIDTH);
        int height = asInt(image.get("height"), DEFAULT_IMAGE_HEIGHT);
        return Pictures.ofBytes(bytes, pictureType).size(width, height).create();
    }

    private byte[] readImageBytes(Map<String, Object> image) {
        String base64 = asString(image.get("base64"));
        if (StringUtils.hasText(base64)) {
            int commaIndex = base64.indexOf(',');
            String payload = commaIndex >= 0 ? base64.substring(commaIndex + 1) : base64;
            return Base64.getDecoder().decode(payload);
        }
        try (InputStream inputStream = objectStorageService.getObject(asString(image.get("bucketName")), asString(image.get("objectKey")))) {
            return inputStream.readAllBytes();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "图片读取失败: " + ex.getMessage());
        }
    }

    private PictureType resolvePictureType(String type, byte[] bytes) {
        if (StringUtils.hasText(type)) {
            try {
                return PictureType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                return PictureType.suggestFileType(bytes);
            }
        }
        return PictureType.suggestFileType(bytes);
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private int asInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
