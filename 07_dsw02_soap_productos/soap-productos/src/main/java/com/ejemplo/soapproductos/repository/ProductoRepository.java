package com.ejemplo.soapproductos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ejemplo.soapproductos.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /** JOIN FETCH: trae producto + categoria en una sola consulta (evita N+1). */
    @Query("select p from Producto p join fetch p.categoria order by p.id")
    List<Producto> listarConCategoria();

    @Query("select p from Producto p join fetch p.categoria c where c.id = :categoriaId order by p.id")
    List<Producto> listarPorCategoria(@Param("categoriaId") Integer categoriaId);
}
