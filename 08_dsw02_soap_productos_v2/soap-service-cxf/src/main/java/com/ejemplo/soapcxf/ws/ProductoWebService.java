package com.ejemplo.soapcxf.ws;

import java.math.BigDecimal;
import java.util.List;

import com.ejemplo.soapcxf.ws.dto.CategoriaDto;
import com.ejemplo.soapcxf.ws.dto.ProductoDto;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;


@WebService(name = "ProductosPortType", targetNamespace = Namespaces.PRODUCTOS)
public interface ProductoWebService {

    /** categoriaId es opcional (Integer): si no se envia, lista todo. */
    @WebMethod
    @WebResult(name = "producto")
    List<ProductoDto> listarProductos(@WebParam(name = "categoriaId") Integer categoriaId);

    @WebMethod
    @WebResult(name = "producto")
    ProductoDto obtenerProducto(@WebParam(name = "id") int id) throws RecursoNoEncontradoException;

    @WebMethod
    @WebResult(name = "categoria")
    List<CategoriaDto> listarCategorias();

    @WebMethod
    @WebResult(name = "producto")
    ProductoDto registrarProducto(
            @WebParam(name = "nombre") String nombre,
            @WebParam(name = "precio") BigDecimal precio,
            @WebParam(name = "stock") int stock,
            @WebParam(name = "categoriaId") int categoriaId) throws RecursoNoEncontradoException;
}
