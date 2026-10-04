package com.ejemplo.soapproductos.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.ejemplo.soapproductos.config.WebServiceConfig;
import com.ejemplo.soapproductos.service.ProductoService;
import com.ejemplo.soapproductos.ws.ListarCategoriasRequest;
import com.ejemplo.soapproductos.ws.ListarCategoriasResponse;
import com.ejemplo.soapproductos.ws.ListarProductosRequest;
import com.ejemplo.soapproductos.ws.ListarProductosResponse;
import com.ejemplo.soapproductos.ws.ObtenerProductoRequest;
import com.ejemplo.soapproductos.ws.ObtenerProductoResponse;
import com.ejemplo.soapproductos.ws.RegistrarProductoRequest;
import com.ejemplo.soapproductos.ws.RegistrarProductoResponse;

/**
 * Endpoint SOAP. Cada metodo se enruta por el elemento raiz del Body
 * (namespace + localPart), no por la URL.
 */
@Endpoint
public class ProductoEndpoint {

    private static final String NS = WebServiceConfig.NAMESPACE_URI;

    private final ProductoService productoService;

    public ProductoEndpoint(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PayloadRoot(namespace = NS, localPart = "listarProductosRequest")
    @ResponsePayload
    public ListarProductosResponse listarProductos(@RequestPayload ListarProductosRequest request) {
        ListarProductosResponse response = new ListarProductosResponse();
        response.getProducto().addAll(productoService.listarProductos(request.getCategoriaId()));
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "obtenerProductoRequest")
    @ResponsePayload
    public ObtenerProductoResponse obtenerProducto(@RequestPayload ObtenerProductoRequest request) {
        ObtenerProductoResponse response = new ObtenerProductoResponse();
        response.setProducto(productoService.obtenerProducto(request.getId()));
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "listarCategoriasRequest")
    @ResponsePayload
    public ListarCategoriasResponse listarCategorias(@RequestPayload ListarCategoriasRequest request) {
        ListarCategoriasResponse response = new ListarCategoriasResponse();
        response.getCategoria().addAll(productoService.listarCategorias());
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "registrarProductoRequest")
    @ResponsePayload
    public RegistrarProductoResponse registrarProducto(@RequestPayload RegistrarProductoRequest request) {
        RegistrarProductoResponse response = new RegistrarProductoResponse();
        response.setProducto(productoService.registrarProducto(request));
        return response;
    }
}
