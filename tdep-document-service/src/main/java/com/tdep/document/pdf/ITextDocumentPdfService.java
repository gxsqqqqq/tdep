package com.tdep.document.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.document.entity.DocumentFile;
import com.tdep.document.storage.DocumentObjectStorageService;
import com.tdep.document.storage.DocumentStorageObject;
import com.tdep.document.storage.DocumentStorageProperties;
import com.tdep.document.util.DocumentHashUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Primary
@Service
@RequiredArgsConstructor
public class ITextDocumentPdfService implements DocumentPdfService {

    private static final String WATERMARK = "TDEP LEGAL DOCUMENT";

    private final DocumentObjectStorageService objectStorageService;

    private final DocumentStorageProperties storageProperties;

    @Override
    public PdfExportResult exportPdf(DocumentFile documentFile) {
        if (!StringUtils.hasText(documentFile.getWordBucket()) || !StringUtils.hasText(documentFile.getWordObjectKey())) {
            throw new BusinessException(ResultCode.CONFLICT, "文书 Word 文件不存在，无法导出 PDF");
        }
        try (InputStream wordStream = objectStorageService.getObject(documentFile.getWordBucket(), documentFile.getWordObjectKey());
             XWPFDocument wordDocument = new XWPFDocument(wordStream);
             ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream()) {
            createPdf(wordDocument, documentFile, pdfOutput);
            byte[] bytes = pdfOutput.toByteArray();
            String objectKey = "pdf/" + documentFile.getCaseId() + "/" + documentFile.getDocumentNo() + ".pdf";
            Map<String, String> metadata = Map.of(
                    "case-id", String.valueOf(documentFile.getCaseId()),
                    "document-no", documentFile.getDocumentNo(),
                    "document-type", documentFile.getDocumentType(),
                    "source-word", documentFile.getWordObjectKey(),
                    "sha256", DocumentHashUtil.sha256(bytes)
            );
            DocumentStorageObject storageObject = objectStorageService.putObject(storageProperties.getPdfBucket(),
                    objectKey, bytes, "application/pdf", metadata);
            return PdfExportResult.builder()
                    .bucketName(storageObject.getBucketName())
                    .objectKey(storageObject.getObjectKey())
                    .fileHash(metadata.get("sha256"))
                    .build();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "PDF 导出失败: " + ex.getMessage());
        }
    }

    private void createPdf(XWPFDocument wordDocument, DocumentFile documentFile, ByteArrayOutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument, PageSize.A4);
        document.setMargins(72, 64, 72, 64);
        PdfFont font = createFont();
        document.setFont(font);

        for (IBodyElement element : wordDocument.getBodyElements()) {
            if (element.getElementType() == BodyElementType.PARAGRAPH) {
                addParagraph(document, (XWPFParagraph) element);
            } else if (element.getElementType() == BodyElementType.TABLE) {
                addTable(document, (XWPFTable) element);
            }
        }
        addWatermarkAndFooter(pdfDocument, font, documentFile);
        document.close();
    }

    private PdfFont createFont() throws Exception {
        try {
            return PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        } catch (Exception ignored) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.WINANSI);
        }
    }

    private void addParagraph(Document document, XWPFParagraph paragraph) {
        String text = paragraph.getText();
        if (!StringUtils.hasText(text)) {
            document.add(new Paragraph(" ").setFixedLeading(12));
            return;
        }
        Paragraph pdfParagraph = new Paragraph(text)
                .setFontSize(resolveFontSize(paragraph))
                .setMultipliedLeading(1.4f)
                .setTextAlignment(resolveTextAlignment(paragraph));
        document.add(pdfParagraph);
    }

    private float resolveFontSize(XWPFParagraph paragraph) {
        if (!paragraph.getRuns().isEmpty() && paragraph.getRuns().get(0).getFontSize() > 0) {
            return paragraph.getRuns().get(0).getFontSize();
        }
        return 12f;
    }

    private TextAlignment resolveTextAlignment(XWPFParagraph paragraph) {
        if (paragraph.getAlignment() == null) {
            return TextAlignment.LEFT;
        }
        return switch (paragraph.getAlignment()) {
            case CENTER -> TextAlignment.CENTER;
            case RIGHT -> TextAlignment.RIGHT;
            case BOTH -> TextAlignment.JUSTIFIED;
            default -> TextAlignment.LEFT;
        };
    }

    private void addTable(Document document, XWPFTable wordTable) {
        List<XWPFTableRow> rows = wordTable.getRows();
        int columnCount = rows.stream()
                .mapToInt(row -> row.getTableCells().size())
                .max()
                .orElse(1);
        Table table = new Table(columnCount).useAllAvailableWidth();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (int i = 0; i < columnCount; i++) {
                String text = i < cells.size() ? cells.get(i).getText() : "";
                table.addCell(new Cell().add(new Paragraph(text == null ? "" : text).setFontSize(10)));
            }
        }
        document.add(table);
        document.add(new Paragraph(" ").setFixedLeading(6));
    }

    private void addWatermarkAndFooter(PdfDocument pdfDocument, PdfFont font, DocumentFile documentFile) {
        int totalPages = pdfDocument.getNumberOfPages();
        for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) {
            PdfPage page = pdfDocument.getPage(pageNumber);
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page);
            PdfExtGState state = new PdfExtGState().setFillOpacity(0.12f);
            pdfCanvas.saveState();
            pdfCanvas.setExtGState(state);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            Paragraph watermark = new Paragraph(WATERMARK)
                    .setFont(font)
                    .setFontSize(34)
                    .setFontColor(ColorConstants.LIGHT_GRAY);
            canvas.showTextAligned(watermark, pageSize.getWidth() / 2, pageSize.getHeight() / 2,
                    pageNumber, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0.65f);
            canvas.close();
            pdfCanvas.restoreState();

            PdfCanvas footerCanvas = new PdfCanvas(page);
            footerCanvas.beginText()
                    .setFontAndSize(font, 9)
                    .moveText(64, 36)
                    .showText(documentFile.getDocumentNo() + "  Page " + pageNumber + "/" + totalPages)
                    .endText();
        }
    }
}
