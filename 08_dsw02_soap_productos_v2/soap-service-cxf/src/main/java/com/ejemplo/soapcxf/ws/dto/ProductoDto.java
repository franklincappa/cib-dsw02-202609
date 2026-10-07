package com.ejemplo.soapcxf.ws.dto;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/** Tipo XML "producto", con su categoria anidada. */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "producto", propOrder = {"id", "nombre", "precio", "stock", "categoria"})
public class ProductoDto {

    private int id;

    @XmlElement(required = true)
    private String nombre;

    @XmlElement(required = true)
    private BigDecimal precio;

    private int stock;

    @XmlElement(required = true)
    private CategoriaDto categoria;

    public ProductoDto() {
    }

    public ProductoDto(int id, String nombre, BigDecimal precio, int stock, CategoriaDto categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public CategoriaDto getCategoria() { return categoria; }
    public void setCategoria(CategoriaDto categoria) { this.categoria = categoria; }
}
