package com.ejemplo.soapproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejemplo.soapproductos.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
