using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using ProductosApi.Soap;

namespace ProductosApi.Infrastructure;

/// <summary>
/// Traduce los errores del cliente SOAP a respuestas HTTP con formato ProblemDetails,
/// para que el front reciba siempre el mismo JSON de error.
/// </summary>
public sealed class SoapExceptionHandler(ILogger<SoapExceptionHandler> logger) : IExceptionHandler
{
    public async ValueTask<bool> TryHandleAsync(HttpContext context, Exception exception, CancellationToken ct)
    {
        ProblemDetails? problema = exception switch
        {
            SoapFaultException { EsRecursoNoEncontrado: true } fault => new ProblemDetails
            {
                Status = StatusCodes.Status404NotFound,
                Title = "Recurso no encontrado",
                Detail = fault.Message
            },
            SoapFaultException fault => new ProblemDetails
            {
                Status = StatusCodes.Status502BadGateway,
                Title = "El servicio SOAP devolvió un error",
                Detail = fault.Message
            },
            SoapProtocolException protocolo => new ProblemDetails
            {
                Status = StatusCodes.Status502BadGateway,
                Title = "Respuesta SOAP no válida",
                Detail = protocolo.Message
            },
            HttpRequestException => new ProblemDetails
            {
                Status = StatusCodes.Status503ServiceUnavailable,
                Title = "Servicio SOAP no disponible",
                Detail = "No se pudo conectar con el servicio SOAP. Verifique que esté iniciado."
            },
            TaskCanceledException when !context.RequestAborted.IsCancellationRequested => new ProblemDetails
            {
                Status = StatusCodes.Status504GatewayTimeout,
                Title = "Tiempo de espera agotado",
                Detail = "El servicio SOAP no respondió a tiempo."
            },
            _ => null
        };

        if (problema is null)
            return false;   // lo maneja el middleware por defecto (500)

        logger.LogWarning(exception, "Error al consumir el servicio SOAP: {Titulo}", problema.Title);

        context.Response.StatusCode = problema.Status!.Value;
        await context.Response.WriteAsJsonAsync(problema, options: null, contentType: "application/problem+json", ct);
        return true;
    }
}
