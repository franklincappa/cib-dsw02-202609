package com.ejemplo.soapproductos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ejemplo.soapproductos.entity.Categoria;
import com.ejemplo.soapproductos.entity.Producto;
import com.ejemplo.soapproductos.exception.RecursoNoEncontradoException;
import com.ejemplo.soapproductos.repository.CategoriaRepository;
import com.ejemplo.soapproductos.repository.ProductoRepository;
import com.ejemplo.soapproductos.ws.CategoriaDto;
import com.ejemplo.soapproductos.ws.ProductoDto;
import com.ejemplo.soapproductos.ws.RegistrarProductoRequest;

/**
 * Logica de negocio. Convierte entidades JPA a los DTO generados desde el XSD
 * dentro de la transaccion (la relacion LAZY se resuelve aqui).
 */
@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoDto> listarProductos(Integer categoriaId) {
        List<Producto> productos = (categoriaId == null)
                ? productoRepository.listarConCategoria()
                : productoRepository.listarPorCategoria(categoriaId);
        return productos.stream().map(this::toDto).toList();
    }

    public ProductoDto obtenerProducto(int id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
        return toDto(producto);
    }

    public List<CategoriaDto> listarCategorias() {
        return categoriaRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public ProductoDto registrarProducto(RegistrarProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la categoria con id " + request.getCategoriaId()));

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);

        return toDto(productoRepository.save(producto));
    }

    // ---------- Mapeo entidad -> DTO ----------

    private ProductoDto toDto(Producto producto) {
        ProductoDto dto = new ProductoDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoria(toDto(producto.getCategoria()));
        return dto;
    }

    private CategoriaDto toDto(Categoria categoria) {
        CategoriaDto dto = new CategoriaDto();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }
}
