package com.appcloud.gestiondepedidosyguias.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "guia_despacho")
@Getter
@Setter
public class GuiaDespacho {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, unique = true, length = 50)
	private String numeroGuia;

	@NotBlank
	@Size(max = 120)
	@Column(nullable = false, length = 120)
	private String transportista;

	@NotNull
	@Column(nullable = false)
	private LocalDate fechaDespacho;

	@NotBlank
	@Size(max = 120)
	@Column(nullable = false, length = 120)
	private String cliente;

	@NotBlank
	@Size(max = 255)
	@Column(nullable = false, length = 255)
	private String direccionEntrega;

	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, length = 50)
	private String estado;

	@Column(length = 1024)
	private String archivoS3;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
