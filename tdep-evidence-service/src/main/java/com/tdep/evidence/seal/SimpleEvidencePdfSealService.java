package com.tdep.evidence.seal;

import com.tdep.evidence.entity.EvidenceFile;
import com.tdep.evidence.entity.EvidenceHash;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 简易 PDF 固化凭证服务，不依赖外部 PDF 库，生成可被 PDF 阅读器识别的文本凭证。
 */
@Service
public class SimpleEvidencePdfSealService implements EvidencePdfSealService {

    @Override
    public InputStream buildCertificate(EvidenceFile evidenceFile, EvidenceHash evidenceHash) {
        String text = """
                TDEP Trusted Digital Evidence Seal Certificate
                Evidence No: %s
                Case Id: %s
                File Name: %s
                File Size: %s
                SHA-256: %s
                Uploaded At: %s
                """.formatted(
                evidenceFile.getEvidenceNo(),
                evidenceFile.getCaseId(),
                evidenceFile.getFileName(),
                evidenceFile.getFileSize(),
                evidenceHash.getHashValue(),
                evidenceFile.getUploadedAt()
        );
        byte[] pdf = minimalPdf(text);
        return new ByteArrayInputStream(pdf);
    }

    /**
     * 构造最小 PDF 文档。
     *
     * @param text 文本内容
     * @return PDF 字节
     */
    private byte[] minimalPdf(String text) {
        String escaped = text.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", "")
                .replace("\n", ") Tj T* (");
        String stream = "BT /F1 11 Tf 50 780 Td 14 TL (" + escaped + ") Tj ET";
        String pdf = """
                %%PDF-1.4
                1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj
                2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj
                3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj
                4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj
                5 0 obj << /Length %d >> stream
                %s
                endstream endobj
                trailer << /Root 1 0 R >>
                %%EOF
                """.formatted(stream.getBytes(StandardCharsets.UTF_8).length, stream);
        return pdf.getBytes(StandardCharsets.UTF_8);
    }
}
