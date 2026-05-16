package com.tdep.evidence.seal;

import com.tdep.evidence.entity.EvidenceFile;
import com.tdep.evidence.entity.EvidenceHash;

import java.io.InputStream;

/**
 * 证据 PDF 固化凭证服务。
 */
public interface EvidencePdfSealService {

    /**
     * 生成 PDF 固化凭证。
     *
     * @param evidenceFile 证据文件
     * @param evidenceHash 证据 Hash
     * @return PDF 输入流
     */
    InputStream buildCertificate(EvidenceFile evidenceFile, EvidenceHash evidenceHash);
}
