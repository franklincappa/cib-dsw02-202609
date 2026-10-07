package com.ejemplo.soapcxf.ws;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ejemplo.soapcxf.service.ProductoService;
import com.ejemplo.soapcxf.ws.dto.CategoriaDto;
import com.ejemplo.soapcxf.ws.dto.ProductoDto;

import jakarta.jws.WebService;

/**
 * Implementacion del contrato. Solo delega: la logica y las transacciones
 * viven en ProductoService.
 */
@Component
@WebService(
        endpointInterface = "com.ejemplo.soapcxf.ws.ProductoWebService",
        serviceName = "ProductosService",
        portName = "ProductosPort",
        targetNamespace = Namespaces.PRODUCTOS)
public class ProductoWebServiceImpl implements ProductoWebService {

    private final ProductoService productoService;

    public ProductoWebServiceImpl(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Override
    public List<ProductoDto> listarProductos(Integer categoriaId) {
        return productoService.listarProductos(categoriaId);
    }

    @Override
    public ProductoDto obtenerProducto(int id) throws RecursoNoEncontradoException {
        return productoService.obtenerProducto(id);
    }

    @Override
    public List<CategoriaDto> listarCategorias() {
        return productoService.listarCategorias();
    }

    @Override
    public ProductoDto registrarProducto(String nombre, BigDecimal precio, int stock, int categoriaId)
            throws RecursoNoEncontradoException {
        return productoService.registrarProducto(nombre, precio, stock, categoriaId);
    }
}
