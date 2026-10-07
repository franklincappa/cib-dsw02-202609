using ProductosApi.Infrastructure;
using ProductosApi.Models;
using ProductosApi.Soap;

var builder = WebApplication.CreateBuilder(args);

// ---------- Servicios ----------

var soapEndpoint = builder.Configuration["Soap:Endpoint"]
    ?? throw new InvalidOperationException("Falta la configuración Soap:Endpoint.");
var soapTimeout = builder.Configuration.GetValue("Soap:TimeoutSegundos", 15);

// Cliente tipado: IHttpClientFactory administra las conexiones.
builder.Services.AddHttpClient<IProductosSoapClient, ProductosSoapClient>(http =>
{
    http.BaseAddress = new Uri(soapEndpoint);
    http.Timeout = TimeSpan.FromSeconds(soapTimeout);
});

builder.Services.AddProblemDetails();
builder.Services.AddExceptionHandler<SoapExceptionHandler>();

// Solo hace falta si el front llama directo a esta API (sin el proxy de Vite).
var origenes = builder.Configuration.GetSection("Cors:Origenes").Get<string[]>() ?? [];
builder.Services.AddCors(cors => cors.AddDefaultPolicy(politica =>
    politica.WithOrigins(origenes).AllowAnyHeader().AllowAnyMethod()));

var app = builder.Build();

// ---------- Pipeline ----------

app.UseExceptionHandler();
app.UseCors();

// ---------- Endpoints REST (JSON) que por dentro consumen SOAP ----------

var api = app.MapGroup("/api");

api.MapGet("/categorias", async (IProductosSoapClient soap, CancellationToken ct) =>
    Results.Ok(await soap.ListarCategoriasAsync(ct)));

api.MapGet("/productos", async (int? categoriaId, IProductosSoapClient soap, CancellationToken ct) =>
    Results.Ok(await soap.ListarProductosAsync(categoriaId, ct)));

// Si el producto no existe, el SOAP Fault se convierte en 404 en SoapExceptionHandler.
api.MapGet("/productos/{id:int}", async (int id, IProductosSoapClient soap, CancellationToken ct) =>
    Results.Ok(await soap.ObtenerProductoAsync(id, ct)));

api.MapPost("/productos", async (NuevoProducto nuevo, IProductosSoapClient soap, CancellationToken ct) =>
{
    var errores = nuevo.Validar();
    if (errores.Count > 0)
        return Results.ValidationProblem(errores);

    try
    {
        var creado = await soap.RegistrarProductoAsync(
            nuevo.Nombre!.Trim(), nuevo.Precio!.Value, nuevo.Stock!.Value, nuevo.CategoriaId!.Value, ct);

        return Results.Created($"/api/productos/{creado.Id}", creado);
    }
    catch (SoapFaultException fault) when (fault.EsRecursoNoEncontrado)
    {
        // Aquí "no encontrado" se refiere a la categoría enviada: es un dato inválido, no un 404.
        return Results.ValidationProblem(new Dictionary<string, string[]>
        {
            ["categoriaId"] = [fault.Message]
        });
    }
});

app.Run();
