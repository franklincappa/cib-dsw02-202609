// Mismas formas que devuelve la API .NET (JSON en camelCase)

export interface Categoria {
  id: number
  nombre: string
  descripcion: string | null
}

export interface Producto {
  id: number
  nombre: string
  precio: number
  stock: number
  categoria: Categoria
}

export interface NuevoProducto {
  nombre: string
  precio: number | null
  stock: number | null
  categoriaId: number | null
}

/** Errores por campo, como llegan en ValidationProblemDetails.errors */
export type ErroresCampo = Record<string, string[]>
