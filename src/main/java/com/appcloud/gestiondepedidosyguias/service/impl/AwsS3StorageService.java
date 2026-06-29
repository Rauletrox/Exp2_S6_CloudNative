package com.appcloud.gestiondepedidosyguias.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.appcloud.gestiondepedidosyguias.exception.StorageException;
import com.appcloud.gestiondepedidosyguias.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class AwsS3StorageService implements S3StorageService {

	private final S3Client s3Client;

	@Value("${app.aws.s3.bucket-name}")
	private String bucketName;

	@Override
	public String upload(String key, InputStream inputStream, long contentLength, String contentType) {
		try {
			byte[] bytes = inputStream.readAllBytes();
			PutObjectRequest request = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.contentType(contentType)
					.build();
			s3Client.putObject(request, RequestBody.fromBytes(bytes));
			return key;
		} catch (IOException ex) {
			throw new StorageException("No fue posible leer el archivo para subirlo a S3", ex);
		} catch (RuntimeException ex) {
			throw new StorageException("No fue posible subir el archivo a S3", ex);
		}
	}

	@Override
	public StoredObject download(String key) {
		try {
			GetObjectRequest request = GetObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.build();
			ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
			String contentType = responseBytes.response().contentType();
			return new StoredObject(responseBytes.asByteArray(), contentType != null ? contentType : "application/octet-stream", extractFilename(key));
		} catch (RuntimeException ex) {
			throw new StorageException("No fue posible descargar el archivo desde S3", ex);
		}
	}

	private String extractFilename(String key) {
		int index = key.lastIndexOf('/');
		return index >= 0 ? key.substring(index + 1) : key;
	}
}
