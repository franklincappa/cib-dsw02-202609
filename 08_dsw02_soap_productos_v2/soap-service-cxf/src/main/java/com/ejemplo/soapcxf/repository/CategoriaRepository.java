package com.ejemplo.soapcxf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejemplo.soapcxf.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
