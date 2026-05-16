package com.tdep.evidence.hash;

import java.io.IOException;
import java.io.InputStream;

/**
 * Hash 计算服务。
 */
public interface HashService {

    /**
     * 使用 SHA-256 计算输入流 Hash。
     *
     * @param inputStream 输入流
     * @return Hash 结果
     * @throws IOException 读取异常
     */
    HashResult sha256(InputStream inputStream) throws IOException;

    /**
     * 对字符串做 SHA-256。
     *
     * @param content 字符串内容
     * @return 十六进制 Hash
     */
    String sha256Hex(String content);
}
