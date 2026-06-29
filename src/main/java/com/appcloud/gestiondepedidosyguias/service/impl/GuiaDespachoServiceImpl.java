package com.appcloud.gestiondepedidosyguias.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.appcloud.gestiondepedidosyguias.dto.GuiaDespachoRequest;
import com.appcloud.gestiondepedidosyguias.dto.GuiaDespachoResponse;
import com.appcloud.gestiondepedidosyguias.entity.GuiaDespacho;
import com.appcloud.gestiondepedidosyguias.exception.ResourceNotFoundException;
import com.appcloud.gestiondepedidosyguias.repository.GuiaDespachoRepository;
import com.appcloud.gestiondepedidosyguias.service.GuiaDespachoService;
import com.appcloud.gestiondepedidosyguias.service.S3StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuiaDespachoServiceImpl implements GuiaDespachoService {

	private final GuiaDespachoRepository guiaDespachoRepository;
	private final S3StorageService s3StorageService;

	@Override
	@Transactional
	public GuiaDespachoResponse crear(GuiaDespachoRequest request) {
		GuiaDespacho guia = new GuiaDespacho();
		guia.setNumeroGuia(request.numeroGuia());
		guia.setTransportista(request.transportista());
		guia.setFechaDespacho(request.fechaDespacho());
		guia.setCliente(request.cliente());
		guia.setDireccionEntrega(request.direccionEntrega());
		guia.setEstado(request.estado());
		return toResponse(guiaDespachoRepository.save(guia));
	}

	@Override
	@Transactional(readOnly = true)
	public List<GuiaDespachoResponse> listar() {
		return guiaDespachoRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public GuiaDespachoResponse actualizar(Long id, GuiaDespachoRequest request) {
		GuiaDespacho guia = obtenerOError(id);
		guia.setNumeroGuia(request.numeroGuia());
		guia.setTransportista(request.transportista());
		guia.setFechaDespacho(request.fechaDespacho());
		guia.setCliente(request.cliente());
		guia.setDireccionEntrega(request.direccionEntrega());
		guia.setEstado(request.estado());
		return toResponse(guiaDespachoRepository.save(guia));
	}

	@Override
	@Transactional
	public void eliminar(Long id) {
		GuiaDespacho guia = obtenerOError(id);
		guiaDespachoRepository.delete(guia);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GuiaDespachoResponse> buscarPorTransportistaYFecha(String transportista, LocalDate fecha) {
		return guiaDespachoRepository.findByTransportistaIgnoreCaseAndFechaDespacho(transportista, fecha)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public GuiaDespachoResponse subirArchivo(Long id, MultipartFile file) {
		GuiaDespacho guia = obtenerOError(id);
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Debe enviar un archivo para subir");
		}

		String filename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "guia.pdf");
		String key = "guias/" + guia.getId() + "/" + filename;
		String storedKey = s3StorageService.upload(key, toInputStream(file), file.getSize(),
				file.getContentType() != null ? file.getContentType() : "application/pdf");
		guia.setArchivoS3(storedKey);
		return toResponse(guiaDespachoRepository.save(guia));
	}

	@Override
	@Transactional(readOnly = true)
	public ArchivoDescargado descargarArchivo(Long id) {
		GuiaDespacho guia = obtenerOError(id);
		if (!StringUtils.hasText(guia.getArchivoS3())) {
			throw new ResourceNotFoundException("La guia no tiene archivo asociado en S3");
		}

		S3StorageService.StoredObject storedObject = s3StorageService.download(guia.getArchivoS3());
		Resource resource = new ByteArrayResource(storedObject.content());
		return new ArchivoDescargado(resource, storedObject.originalFilename(), storedObject.contentType());
	}

	private GuiaDespacho obtenerOError(Long id) {
		return guiaDespachoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la guia de despacho con id " + id));
	}

	private GuiaDespachoResponse toResponse(GuiaDespacho guia) {
		return new GuiaDespachoResponse(
				guia.getId(),
				guia.getNumeroGuia(),
				guia.getTransportista(),
				guia.getFechaDespacho(),
				guia.getCliente(),
				guia.getDireccionEntrega(),
				guia.getEstado(),
				guia.getArchivoS3(),
				guia.getCreatedAt());
	}

	private java.io.InputStream toInputStream(MultipartFile file) {
		try {
			return file.getInputStream();
		} catch (java.io.IOException ex) {
			throw new IllegalArgumentException("No fue posible leer el archivo adjunto", ex);
		}
	}
}
