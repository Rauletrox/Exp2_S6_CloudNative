package com.appcloud.gestiondepedidosyguias.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuiaDespachoRequest(
		@NotBlank @Size(max = 50) String numeroGuia,
		@NotBlank @Size(max = 120) String transportista,
		@NotNull LocalDate fechaDespacho,
		@NotBlank @Size(max = 120) String cliente,
		@NotBlank @Size(max = 255) String direccionEntrega,
		@NotBlank @Size(max = 50) String estado) {
}
