package com.tdep.document.archive;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tdep.document.archive")
public class DocumentArchiveProperties {

    private boolean autoEnabled = false;

    private String cron = "0 0 2 * * ?";

    private int batchSize = 100;
}
