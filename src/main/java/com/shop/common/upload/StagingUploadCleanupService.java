package com.shop.common.upload;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StagingUploadCleanupService {


    private final Path stagingDir;
    private final boolean enabled;
    private final Duration ttl;
    private final int maxDeletePerRun;

    public StagingUploadCleanupService(
            @Value("${app.upload.root:uploads}") String uploadRoot,
            @Value("${app.upload.cleanup.staging.enabled:true}") boolean enabled,
            @Value("${app.upload.cleanup.staging.ttl:PT6H}") Duration ttl,
            @Value("${app.upload.cleanup.staging.maxDeletePerRun:500}") int maxDeletePerRun
    ) {
        this.stagingDir = Paths.get(uploadRoot).resolve("staging").normalize().toAbsolutePath();
        this.enabled = enabled;
        this.ttl = ttl;
        this.maxDeletePerRun = maxDeletePerRun;
    }

    @Scheduled(cron = "${app.upload.cleanup.staging.cron:0 */30 * * * *}")
    public void cleanupStaging() {
        if (!enabled) return;

        if (!Files.exists(stagingDir)) {
            log.debug("cleanup-staging: staging dir not found: {}", stagingDir);
            return;
        }
        if (!Files.isDirectory(stagingDir)) {
            log.warn("cleanup-staging: staging path is not a directory: {}", stagingDir);
            return;
        }

        final Instant cutoff = Instant.now().minus(ttl);
        final AtomicInteger deleted = new AtomicInteger(0);
        final AtomicInteger scanned = new AtomicInteger(0);

        try {
            Files.walkFileTree(stagingDir, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    scanned.incrementAndGet();

                    // Chỉ xoá file nằm trong stagingDir (chống path traversal / symlink trick)
                    Path normalized = file.normalize().toAbsolutePath();
                    if (!normalized.startsWith(stagingDir)) {
                        log.warn("cleanup-staging: skip suspicious path: {}", normalized);
                        return FileVisitResult.CONTINUE;
                    }

                    // Nếu là symlink thì bỏ qua (an toàn)
                    if (Files.isSymbolicLink(file)) return FileVisitResult.CONTINUE;

                    Instant lastModified = Files.getLastModifiedTime(file).toInstant();
                    if (lastModified.isBefore(cutoff)) {
                        try {
                            Files.deleteIfExists(file);
                            int d = deleted.incrementAndGet();
                            if (d >= maxDeletePerRun) return FileVisitResult.TERMINATE;
                        } catch (IOException ex) {
                            log.warn("cleanup-staging: failed to delete file: {}", file, ex);
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    // Xoá folder rỗng (trừ staging root)
                    if (dir.equals(stagingDir)) return FileVisitResult.CONTINUE;

                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
                        if (!ds.iterator().hasNext()) {
                            try {
                                Files.deleteIfExists(dir);
                            } catch (IOException ex) {
                                log.debug("cleanup-staging: cannot delete empty dir: {}", dir, ex);
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("cleanup-staging: scanned={}, deleted={}, cutoff={}, dir={}",
                    scanned.get(), deleted.get(), cutoff, stagingDir);

        } catch (IOException ex) {
            log.error("cleanup-staging: error while cleaning dir={}", stagingDir, ex);
        }
    }
}
