package com.shop.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.upload.cleanup.staging")
public class UploadCleanupProperties {
    private boolean enabled = true;
    private String cron = "0 */30 * * * *";
    private Duration ttl = Duration.ofHours(6);
    private int maxDeletePerRun = 500;
}
