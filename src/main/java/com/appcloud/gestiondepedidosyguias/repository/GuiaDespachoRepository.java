package com.appcloud.gestiondepedidosyguias.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appcloud.gestiondepedidosyguias.entity.GuiaDespacho;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

	List<GuiaDespacho> findByTransportistaIgnoreCaseAndFechaDespacho(String transportista, LocalDate fechaDespacho);
}
