package com.appcloud.gestiondepedidosyguias.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.appcloud.gestiondepedidosyguias.dto.GuiaDespachoRequest;
import com.appcloud.gestiondepedidosyguias.dto.GuiaDespachoResponse;
import com.appcloud.gestiondepedidosyguias.service.GuiaDespachoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/guias")
@RequiredArgsConstructor
public class GuiaDespachoController {

	private final GuiaDespachoService guiaDespachoService;

	@PostMapping
	public ResponseEntity<GuiaDespachoResponse> crear(@Valid @RequestBody GuiaDespachoRequest request) {
		return ResponseEntity.ok(guiaDespachoService.crear(request));
	}

	@GetMapping
	public ResponseEntity<List<GuiaDespachoResponse>> listar() {
		return ResponseEntity.ok(guiaDespachoService.listar());
	}

	@PutMapping("/{id}")
	public ResponseEntity<GuiaDespachoResponse> actualizar(
			@PathVariable("id") Long id,
			@Valid @RequestBody GuiaDespachoRequest request) {
		return ResponseEntity.ok(guiaDespachoService.actualizar(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
		guiaDespachoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/buscar")
	public ResponseEntity<List<GuiaDespachoResponse>> buscar(
			@RequestParam String transportista,
			@RequestParam LocalDate fecha) {
		return ResponseEntity.ok(guiaDespachoService.buscarPorTransportistaYFecha(transportista, fecha));
	}

	@PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<GuiaDespachoResponse> subirArchivo(
			@PathVariable("id") Long id,
			@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(guiaDespachoService.subirArchivo(id, file));
	}

	@GetMapping("/{id}/download")
	public ResponseEntity<Resource> descargarArchivo(@PathVariable("id") Long id) {
		GuiaDespachoService.ArchivoDescargado archivo = guiaDespachoService.descargarArchivo(id);

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(archivo.contentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(archivo.nombreArchivo()).build().toString())
				.body(archivo.resource());
	}
}
