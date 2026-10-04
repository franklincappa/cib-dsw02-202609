package com.ejemplo.soapproductos.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * Spring WS convierte esta excepcion en un SOAP Fault (faultcode Client)
 * y usa el mensaje como faultstring.
 */
@SoapFault(faultCode = FaultCode.CLIENT)
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
