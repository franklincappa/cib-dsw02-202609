namespace ProductosApi.Models;

public sealed record Categoria(int Id, string Nombre, string? Descripcion);

public sealed record Producto(int Id, string Nombre, decimal Precio, int Stock, Categoria Categoria);

/// <summary>Cuerpo del POST /api/productos. Todo nullable para poder validar campo por campo.</summary>
public sealed record NuevoProducto(string? Nombre, decimal? Precio, int? Stock, int? CategoriaId)
{
    public Dictionary<string, string[]> Validar()
    {
        var errores = new Dictionary<string, string[]>();

        if (string.IsNullOrWhiteSpace(Nombre))
            errores["nombre"] = ["El nombre es obligatorio."];
        else if (Nombre.Trim().Length > 150)
            errores["nombre"] = ["El nombre admite 150 caracteres como máximo."];

        if (Precio is null)
            errores["precio"] = ["El precio es obligatorio."];
        else if (Precio < 0)
            errores["precio"] = ["El precio no puede ser negativo."];

        if (Stock is null)
            errores["stock"] = ["El stock es obligatorio."];
        else if (Stock < 0)
            errores["stock"] = ["El stock no puede ser negativo."];

        if (CategoriaId is null or <= 0)
            errores["categoriaId"] = ["Seleccione una categoría."];

        return errores;
    }
}
