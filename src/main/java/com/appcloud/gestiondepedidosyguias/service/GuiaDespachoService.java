package com.appcloud.gestiondepedidosyguias.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.appcloud.gestiondepedidosyguias.dto.GuiaDespachoRequest;
import com.appcloud.gestiondepedidosyguias.dto.GuiaDespachoResponse;

public interface GuiaDespachoService {

	GuiaDespachoResponse crear(GuiaDespachoRequest request);

	List<GuiaDespachoResponse> listar();

	GuiaDespachoResponse actualizar(Long id, GuiaDespachoRequest request);

	void eliminar(Long id);

	List<GuiaDespachoResponse> buscarPorTransportistaYFecha(String transportista, LocalDate fecha);

	GuiaDespachoResponse subirArchivo(Long id, MultipartFile file);

	ArchivoGenerado generarArchivo(Long id);

	ArchivoDescargado descargarArchivo(Long id);

	record ArchivoGenerado(Resource resource, String nombreArchivo, String contentType) {
	}

	record ArchivoDescargado(Resource resource, String nombreArchivo, String contentType) {
	}
}
