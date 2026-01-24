package com.shop.common.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;

@Service
public class LocalUploadService implements UploadService {

	private static final Set<String> ALLOWED_CT = Set.of("image/jpeg", "image/png", "image/webp");
	private static final long MAX_BYTES = 3 * 1024 * 1024; // 3MB

	@Override
	public String uploadImage(MultipartFile file, UploadDir dir) {
		if (file == null || file.isEmpty())
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "FILE_EMPTY");

		if (file.getSize() > MAX_BYTES)
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "FILE_TOO_LARGE");

		String ct = file.getContentType();
		if (ct == null || !ALLOWED_CT.contains(ct))
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, "FILE_TYPE_NOT_ALLOWED");

		String ext = guessExt(ct); // ".jpg" ".png" ".webp"
		String filename = UUID.randomUUID() + ext;

		Path folder = Paths.get(dir.relativePath());
		try {
			Files.createDirectories(folder);
			Path target = folder.resolve(filename);
			Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new ApiException(ErrorCode.ERR_SERVER, "UPLOAD_FAILED");
		}

		// public url mapping /uploads/** -> file:uploads/
		String publicPrefix = "/" + dir.relativePath().replace("\\", "/");
		return publicPrefix + "/" + filename;
	}

	private String guessExt(String contentType) {
		return switch (contentType) {
		case "image/jpeg" -> ".jpg";
		case "image/png" -> ".png";
		case "image/webp" -> ".webp";
		default -> "";
		};
	}

	@Override
	public void deleteByUrl(String publicUrl) {
		if (publicUrl == null)
			return;

		String u = publicUrl.trim();
		if (u.isEmpty())
			return;

		// chỉ cho phép xóa dưới /uploads/
		if (!u.startsWith("/uploads/"))
			return;

		// map "/uploads/xxx" -> "uploads/xxx"
		String rel = u.substring(1); // bỏ dấu "/" đầu
		Path p = Paths.get(rel).normalize();

		// chặn path traversal: đảm bảo nằm trong thư mục uploads
		Path uploadsRoot = Paths.get("uploads").toAbsolutePath().normalize();
		Path abs = p.toAbsolutePath().normalize();
		if (!abs.startsWith(uploadsRoot))
			return;

		try {
			Files.deleteIfExists(abs);
		} catch (Exception ignored) {
			// không quăng exception vì xóa file fail không nên làm hỏng nghiệp vụ
		}
	}
}
