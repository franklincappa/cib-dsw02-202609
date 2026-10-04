package com.ejemplo.soapproductos.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig {

    public static final String NAMESPACE_URI = "http://ejemplo.com/productos/ws";

    /**
     * Servlet de Spring WS: atiende todo lo que llegue a /ws/*.
     * El DispatcherServlet normal de Spring MVC sigue sirviendo "/" (pagina estatica).
     */
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /**
     * El nombre del bean define la URL del WSDL: /ws/productos.wsdl
     */
    @Bean(name = "productos")
    public DefaultWsdl11Definition productosWsdl(XsdSchema productosSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("ProductosPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace(NAMESPACE_URI);
        wsdl.setSchema(productosSchema);
        return wsdl;
    }

    @Bean
    public XsdSchema productosSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/productos.xsd"));
    }
}
