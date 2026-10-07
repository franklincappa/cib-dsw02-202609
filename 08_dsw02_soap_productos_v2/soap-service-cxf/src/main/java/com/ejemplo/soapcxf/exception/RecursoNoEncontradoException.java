package com.ejemplo.soapcxf.ws;

import jakarta.xml.ws.WebFault;

/**
 * Excepcion declarada en el contrato: aparece en el WSDL como <wsdl:fault>
 * y viaja como SOAP Fault con el elemento <RecursoNoEncontradoFault> en el detail.
 */
@WebFault(name = "RecursoNoEncontradoFault", targetNamespace = Namespaces.PRODUCTOS)
public class RecursoNoEncontradoException extends Exception {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
