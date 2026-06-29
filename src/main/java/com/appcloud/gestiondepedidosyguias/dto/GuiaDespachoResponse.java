package com.appcloud.gestiondepedidosyguias.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GuiaDespachoResponse(
		Long id,
		String numeroGuia,
		String transportista,
		LocalDate fechaDespacho,
		String cliente,
		String direccionEntrega,
		String estado,
		String archivoS3,
		LocalDateTime createdAt) {
}
