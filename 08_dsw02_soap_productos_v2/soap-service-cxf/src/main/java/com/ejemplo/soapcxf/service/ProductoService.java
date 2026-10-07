package com.ejemplo.soapcxf.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ejemplo.soapcxf.entity.Categoria;
import com.ejemplo.soapcxf.entity.Producto;
import com.ejemplo.soapcxf.repository.CategoriaRepository;
import com.ejemplo.soapcxf.repository.ProductoRepository;
import com.ejemplo.soapcxf.ws.RecursoNoEncontradoException;
import com.ejemplo.soapcxf.ws.dto.CategoriaDto;
import com.ejemplo.soapcxf.ws.dto.ProductoDto;


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

    public ProductoDto obtenerProducto(int id) throws RecursoNoEncontradoException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
        return toDto(producto);
    }

    public List<CategoriaDto> listarCategorias() {
        return categoriaRepository.findAll().stream().map(this::toDto).toList();
    }

    /** rollbackFor: por defecto Spring no hace rollback con excepciones checked. */
    @Transactional(rollbackFor = Exception.class)
    public ProductoDto registrarProducto(String nombre, BigDecimal precio, int stock, int categoriaId)
            throws RecursoNoEncontradoException {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoria con id " + categoriaId));

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);

        return toDto(productoRepository.save(producto));
    }

    // ---------- Mapeo entidad -> DTO ----------

    private ProductoDto toDto(Producto p) {
        return new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), toDto(p.getCategoria()));
    }

    private CategoriaDto toDto(Categoria c) {
        return new CategoriaDto(c.getId(), c.getNombre(), c.getDescripcion());
    }
}
