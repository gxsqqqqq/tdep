package com.tdep.evidence.hash;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * SHA-256 Hash 计算服务实现。
 */
@Service
public class Sha256HashService implements HashService {

    private static final String SHA_256 = "SHA-256";

    private static final int BUFFER_SIZE = 8192;

    @Override
    public HashResult sha256(InputStream inputStream) throws IOException {
        MessageDigest digest = newDigest();
        byte[] buffer = new byte[BUFFER_SIZE];
        long size = 0L;
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
            size += read;
        }
        return HashResult.builder()
                .algorithm(SHA_256)
                .hashValue(HexFormat.of().formatHex(digest.digest()))
                .fileSize(size)
                .build();
    }

    @Override
    public String sha256Hex(String content) {
        MessageDigest digest = newDigest();
        return HexFormat.of().formatHex(digest.digest(content.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 创建 SHA-256 摘要器。
     *
     * @return 摘要器
     */
    private MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256", exception);
        }
    }
}
