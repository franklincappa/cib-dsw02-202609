//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.10.03 a las 06:20:17 PM PET 
//


package com.ejemplo.soapproductos.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="categoriaId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "categoriaId"
})
@XmlRootElement(name = "listarProductosRequest")
public class ListarProductosRequest {

    protected Integer categoriaId;

    /**
     * Obtiene el valor de la propiedad categoriaId.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCategoriaId() {
        return categoriaId;
    }

    /**
     * Define el valor de la propiedad categoriaId.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCategoriaId(Integer value) {
        this.categoriaId = value;
    }

}
