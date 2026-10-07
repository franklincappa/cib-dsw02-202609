using System.Text;
using System.Xml;
using System.Xml.Linq;
using ProductosApi.Models;

namespace ProductosApi.Soap;

/// <summary>
/// Cliente SOAP 1.1 hecho con HttpClient + LINQ to XML.
/// La dirección del servicio se configura en Program.cs (HttpClient.BaseAddress).
/// </summary>
public sealed class ProductosSoapClient(HttpClient http, ILogger<ProductosSoapClient> logger)
    : IProductosSoapClient
{
    private static readonly XNamespace SoapEnv = "http://schemas.xmlsoap.org/soap/envelope/";

    /// <summary>targetNamespace del servicio (el mismo de @WebService en Java).</summary>
    private static readonly XNamespace Ws = "http://ejemplo.com/productos/ws";

    // ---------- Operaciones ----------

    public async Task<IReadOnlyList<Categoria>> ListarCategoriasAsync(CancellationToken ct)
    {
        var respuesta = await EnviarAsync(new XElement(Ws + "listarCategorias"), ct);
        return Hijos(respuesta, "categoria").Select(LeerCategoria).ToList();
    }

    public async Task<IReadOnlyList<Producto>> ListarProductosAsync(int? categoriaId, CancellationToken ct)
    {
        var operacion = new XElement(Ws + "listarProductos");
        if (categoriaId is not null)
            operacion.Add(new XElement("categoriaId", categoriaId.Value));

        var respuesta = await EnviarAsync(operacion, ct);
        return Hijos(respuesta, "producto").Select(LeerProducto).ToList();
    }

    public async Task<Producto> ObtenerProductoAsync(int id, CancellationToken ct)
    {
        var respuesta = await EnviarAsync(
            new XElement(Ws + "obtenerProducto", new XElement("id", id)), ct);
        return LeerProducto(Hijo(respuesta, "producto"));
    }

    public async Task<Producto> RegistrarProductoAsync(
        string nombre, decimal precio, int stock, int categoriaId, CancellationToken ct)
    {
        // El elemento de la operación lleva el namespace del servicio; los parámetros
        // van SIN namespace (así los define JAX-WS por defecto) y en el orden del método Java.
        // XElement escapa el texto y escribe los decimales con punto, sin depender de la cultura.
        var operacion = new XElement(Ws + "registrarProducto",
            new XElement("nombre", nombre),
            new XElement("precio", precio),
            new XElement("stock", stock),
            new XElement("categoriaId", categoriaId));

        var respuesta = await EnviarAsync(operacion, ct);
        return LeerProducto(Hijo(respuesta, "producto"));
    }

    // ---------- Transporte ----------

    /// <summary>Envuelve la operación en un sobre SOAP, la envía y devuelve el elemento de respuesta.</summary>
    private async Task<XElement> EnviarAsync(XElement operacion, CancellationToken ct)
    {
        var sobre = new XElement(SoapEnv + "Envelope",
            new XAttribute(XNamespace.Xmlns + "soapenv", SoapEnv),
            new XAttribute(XNamespace.Xmlns + "ws", Ws),
            new XElement(SoapEnv + "Body", operacion));

        using var request = new HttpRequestMessage(HttpMethod.Post, string.Empty)
        {
            Content = new StringContent(sobre.ToString(SaveOptions.DisableFormatting), Encoding.UTF8, "text/xml")
        };
        request.Headers.TryAddWithoutValidation("SOAPAction", "\"\"");

        logger.LogDebug("SOAP request: {Xml}", sobre);

        // Un SOAP Fault llega con HTTP 500: por eso se lee el cuerpo antes de mirar el status.
        using var response = await http.SendAsync(request, ct);
        var texto = await response.Content.ReadAsStringAsync(ct);

        logger.LogDebug("SOAP response ({Status}): {Xml}", (int)response.StatusCode, texto);

        XDocument documento;
        try
        {
            documento = XDocument.Parse(texto);
        }
        catch (XmlException ex)
        {
            throw new SoapProtocolException(
                $"El servicio respondió HTTP {(int)response.StatusCode} con un contenido que no es XML.", ex);
        }

        var contenido = documento.Root?.Element(SoapEnv + "Body")?.Elements().FirstOrDefault()
            ?? throw new SoapProtocolException("La respuesta no contiene un Body SOAP.");

        if (contenido.Name == SoapEnv + "Fault")
        {
            throw new SoapFaultException(
                codigo: TextoOpcional(contenido, "faultcode") ?? "desconocido",
                mensaje: TextoOpcional(contenido, "faultstring") ?? "El servicio devolvió un error.",
                detalle: Hijos(contenido, "detail").FirstOrDefault()?.Elements().FirstOrDefault()?.Name.LocalName);
        }

        return contenido;
    }

    // ---------- Lectura del XML ----------
    // Se busca por nombre local para no depender de si el servicio califica o no los elementos hijos.

    private static IEnumerable<XElement> Hijos(XElement padre, string nombre) =>
        padre.Elements().Where(e => e.Name.LocalName == nombre);

    private static XElement Hijo(XElement padre, string nombre) =>
        Hijos(padre, nombre).FirstOrDefault()
        ?? throw new SoapProtocolException($"Falta el elemento <{nombre}> dentro de <{padre.Name.LocalName}>.");

    private static string? TextoOpcional(XElement padre, string nombre) =>
        Hijos(padre, nombre).FirstOrDefault()?.Value;

    private static Categoria LeerCategoria(XElement e) => new(
        Id: (int)Hijo(e, "id"),
        Nombre: Hijo(e, "nombre").Value,
        Descripcion: TextoOpcional(e, "descripcion"));

    private static Producto LeerProducto(XElement e) => new(
        Id: (int)Hijo(e, "id"),
        Nombre: Hijo(e, "nombre").Value,
        Precio: (decimal)Hijo(e, "precio"),   // conversión XML: siempre con punto decimal
        Stock: (int)Hijo(e, "stock"),
        Categoria: LeerCategoria(Hijo(e, "categoria")));
}
