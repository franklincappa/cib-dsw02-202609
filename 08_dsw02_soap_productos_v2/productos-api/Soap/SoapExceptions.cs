namespace ProductosApi.Soap;

/// <summary>El servicio respondió con un SOAP Fault.</summary>
public sealed class SoapFaultException(string codigo, string mensaje, string? detalle)
    : Exception(mensaje)
{
    /// <summary>Contenido de &lt;faultcode&gt;, por ejemplo "soap:Server".</summary>
    public string Codigo { get; } = codigo;

    /// <summary>Nombre del elemento dentro de &lt;detail&gt;; identifica la excepción Java (@WebFault).</summary>
    public string? Detalle { get; } = detalle;

    public bool EsRecursoNoEncontrado => Detalle == "RecursoNoEncontradoFault";
}

/// <summary>La respuesta no es un sobre SOAP válido.</summary>
public sealed class SoapProtocolException(string mensaje, Exception? inner = null)
    : Exception(mensaje, inner);
