package com.tdep.document.sign;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.SecurityContextUtil;
import com.tdep.common.security.TdepUserPrincipal;
import com.tdep.document.dto.DocumentSignRequest;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.entity.DocumentSign;
import com.tdep.document.enums.SignType;
import com.tdep.document.enums.SignVerifyStatus;
import com.tdep.document.storage.DocumentObjectStorageService;
import com.tdep.document.storage.DocumentStorageObject;
import com.tdep.document.storage.DocumentStorageProperties;
import com.tdep.document.util.DocumentHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Primary
@Service
@RequiredArgsConstructor
public class LocalPdfDocumentSignService implements DocumentSignService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DocumentObjectStorageService objectStorageService;

    private final DocumentStorageProperties storageProperties;

    @Override
    public DocumentSign sign(DocumentFile documentFile, DocumentSignRequest request) {
        if (!StringUtils.hasText(documentFile.getPdfBucket()) || !StringUtils.hasText(documentFile.getPdfObjectKey())) {
            throw new BusinessException(ResultCode.CONFLICT, "文书 PDF 文件不存在，无法签章");
        }
        TdepUserPrincipal principal = SecurityContextUtil.currentUser().orElse(null);
        LocalDateTime signedAt = LocalDateTime.now();
        try (InputStream pdfStream = objectStorageService.getObject(documentFile.getPdfBucket(), documentFile.getPdfObjectKey())) {
            byte[] originalBytes = pdfStream.readAllBytes();
            String beforeHash = DocumentHashUtil.sha256(originalBytes);
            byte[] signedBytes = applyVisibleSignature(originalBytes, documentFile, request, principal, signedAt);
            String afterHash = DocumentHashUtil.sha256(signedBytes);
            String objectKey = "signed/" + documentFile.getCaseId() + "/" + documentFile.getDocumentNo() + ".pdf";
            Map<String, String> metadata = Map.of(
                    "case-id", String.valueOf(documentFile.getCaseId()),
                    "document-no", documentFile.getDocumentNo(),
                    "document-type", documentFile.getDocumentType(),
                    "before-sha256", beforeHash,
                    "after-sha256", afterHash,
                    "signed-at", DATE_TIME_FORMATTER.format(signedAt)
            );
            DocumentStorageObject storageObject = objectStorageService.putObject(storageProperties.getSignedBucket(),
                    objectKey, signedBytes, "application/pdf", metadata);

            documentFile.setSignedBucket(storageObject.getBucketName());
            documentFile.setSignedObjectKey(storageObject.getObjectKey());
            documentFile.setFileHash(afterHash);

            DocumentSign sign = new DocumentSign();
            sign.setDocumentId(documentFile.getId());
            sign.setSignerId(principal == null ? null : principal.getUserId());
            sign.setSignerName(resolveSignerName(request, principal));
            sign.setSignType(StringUtils.hasText(request.getSignType()) ? request.getSignType() : SignType.JUDGE.name());
            sign.setCertSerialNo(StringUtils.hasText(request.getCertSerialNo()) ? request.getCertSerialNo() : "LOCAL-SIGN");
            sign.setSignatureField(StringUtils.hasText(request.getSignatureField()) ? request.getSignatureField() : "LOCAL_VISIBLE_SIGNATURE");
            sign.setBeforeHash(beforeHash);
            sign.setAfterHash(afterHash);
            sign.setTimestampToken("LOCAL-" + DATE_TIME_FORMATTER.format(signedAt));
            sign.setVerifyStatus(SignVerifyStatus.PASSED.name());
            sign.setSignedAt(signedAt);
            return sign;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "PDF 签章失败: " + ex.getMessage());
        }
    }

    private byte[] applyVisibleSignature(byte[] originalBytes, DocumentFile documentFile, DocumentSignRequest request,
                                         TdepUserPrincipal principal, LocalDateTime signedAt) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(originalBytes)), new PdfWriter(outputStream))) {
            int pageNumber = Math.max(1, pdfDocument.getNumberOfPages());
            Rectangle pageSize = pdfDocument.getPage(pageNumber).getPageSize();
            Rectangle rect = resolveSignatureRect(request, pageSize);
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNumber));
            pdfCanvas.saveState()
                    .setStrokeColor(ColorConstants.RED)
                    .setLineWidth(1.2f)
                    .rectangle(rect)
                    .stroke()
                    .restoreState();

            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            PdfFont font = createFont();
            String signerName = resolveSignerName(request, principal);
            Paragraph paragraph = new Paragraph()
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.RED)
                    .add("电子签章\n")
                    .add("签署人: " + signerName + "\n")
                    .add("时间: " + DATE_TIME_FORMATTER.format(signedAt) + "\n")
                    .add("编号: " + documentFile.getDocumentNo());
            canvas.showTextAligned(paragraph, rect.getLeft() + rect.getWidth() / 2,
                    rect.getBottom() + rect.getHeight() / 2, pageNumber,
                    TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
            canvas.close();
        }
        return outputStream.toByteArray();
    }

    private Rectangle resolveSignatureRect(DocumentSignRequest request, Rectangle pageSize) {
        float width = 170;
        float height = 86;
        float x = pageSize.getWidth() - width - 64;
        float y = 72;
        String signatureField = request.getSignatureField();
        if (StringUtils.hasText(signatureField) && signatureField.matches("\\d+(\\.\\d+)?,\\d+(\\.\\d+)?,\\d+(\\.\\d+)?,\\d+(\\.\\d+)?")) {
            String[] parts = signatureField.split(",");
            x = Float.parseFloat(parts[0]);
            y = Float.parseFloat(parts[1]);
            width = Float.parseFloat(parts[2]);
            height = Float.parseFloat(parts[3]);
        }
        return new Rectangle(x, y, width, height);
    }

    private PdfFont createFont() throws Exception {
        try {
            return PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        } catch (Exception ignored) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.WINANSI);
        }
    }

    private String resolveSignerName(DocumentSignRequest request, TdepUserPrincipal principal) {
        if (StringUtils.hasText(request.getSignerName())) {
            return request.getSignerName();
        }
        return principal == null ? "UNKNOWN" : principal.getUsername();
    }
}
