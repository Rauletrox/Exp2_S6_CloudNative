package com.appcloud.gestiondepedidosyguias.service;

import java.io.InputStream;

public interface S3StorageService {

	String upload(String key, InputStream inputStream, long contentLength, String contentType);

	StoredObject download(String key);

	record StoredObject(byte[] content, String contentType, String originalFilename) {
	}
}
