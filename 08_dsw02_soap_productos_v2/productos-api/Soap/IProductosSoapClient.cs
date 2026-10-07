using ProductosApi.Models;

namespace ProductosApi.Soap;

/// <summary>Las cuatro operaciones del servicio SOAP, ya convertidas a objetos .NET.</summary>
public interface IProductosSoapClient
{
    Task<IReadOnlyList<Categoria>> ListarCategoriasAsync(CancellationToken ct);

    Task<IReadOnlyList<Producto>> ListarProductosAsync(int? categoriaId, CancellationToken ct);

    Task<Producto> ObtenerProductoAsync(int id, CancellationToken ct);

    Task<Producto> RegistrarProductoAsync(string nombre, decimal precio, int stock, int categoriaId, CancellationToken ct);
}
