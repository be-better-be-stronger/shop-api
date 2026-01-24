package com.shop.common.upload;

public enum UploadDir {
	PRODUCTS("uploads/products"), 
	AVATARS("uploads/avatars");

	private final String relativePath;

	UploadDir(String relativePath) {
		this.relativePath = relativePath;
	}

	public String relativePath() {
		return relativePath;
	}

	public String publicPrefix() {
		return "/" + relativePath.replace("\\", "/");
	}
}

