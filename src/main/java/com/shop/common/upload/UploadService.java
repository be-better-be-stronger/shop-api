package com.shop.common.upload;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
	
	String uploadImage(MultipartFile file, UploadDir dir);

//xóa file theo public URL (/uploads/...)
	void deleteByUrl(String publicUrl);
	
	// move image between dirs (staging -> products / avatars)
    String moveImage(String publicUrl, UploadDir targetDir);
}
