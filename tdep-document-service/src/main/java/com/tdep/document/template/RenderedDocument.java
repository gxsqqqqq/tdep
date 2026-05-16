package com.tdep.document.template;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RenderedDocument {

    private String bucketName;

    private String objectKey;

    private String fileHash;
}
