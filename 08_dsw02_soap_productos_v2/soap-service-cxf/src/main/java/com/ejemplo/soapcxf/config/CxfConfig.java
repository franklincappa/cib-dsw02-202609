package com.ejemplo.soapcxf.config;

import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ejemplo.soapcxf.ws.ProductoWebServiceImpl;

import jakarta.xml.ws.Endpoint;

@Configuration
public class CxfConfig {

    /**
     * Publica el servicio en  {cxf.path}/productos  ->  http://localhost:8080/ws/productos
     * El WSDL lo genera CXF a partir de las anotaciones:  /ws/productos?wsdl
     */
    @Bean
    public Endpoint productosEndpoint(Bus bus, ProductoWebServiceImpl implementacion) {
        EndpointImpl endpoint = new EndpointImpl(bus, implementacion);

        LoggingFeature logging = new LoggingFeature();
        logging.setPrettyLogging(true);
        endpoint.getFeatures().add(logging);

        endpoint.publish("/productos");
        return endpoint;
    }
}
