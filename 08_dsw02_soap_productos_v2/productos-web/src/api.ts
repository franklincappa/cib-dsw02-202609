import type { Categoria, ErroresCampo, NuevoProducto, Producto } from './types'

// Vacio = mismo origen (proxy de Vite). Para llamar directo a la API: VITE_API_URL=http://localhost:5080
const BASE = import.meta.env.VITE_API_URL ?? ''

export class ApiError extends Error {
  readonly status: number
  readonly errores: ErroresCampo | undefined

  constructor(mensaje: string, status: number, errores?: ErroresCampo) {
    super(mensaje)
    this.name = 'ApiError'
    this.status = status
    this.errores = errores
  }
}

async function solicitar<T>(ruta: string, init?: RequestInit): Promise<T> {
  let respuesta: Response
  try {
    respuesta = await fetch(BASE + ruta, init)
  } catch {
    throw new ApiError('No se pudo conectar con la API. Verifique que productos-api esté iniciada.', 0)
  }

  if (!respuesta.ok) {
    // La API responde los errores como ProblemDetails: { title, detail, errors? }
    const problema = await respuesta.json().catch(() => null)
    if (problema?.errors) {
      throw new ApiError('Revise los campos marcados.', respuesta.status, problema.errors)
    }
    const mensaje =
      problema?.detail ??
      problema?.title ??
      `La API respondió HTTP ${respuesta.status}. Verifique que productos-api esté iniciada.`
    throw new ApiError(mensaje, respuesta.status)
  }

  return respuesta.json() as Promise<T>
}

export function listarCategorias(): Promise<Categoria[]> {
  return solicitar('/api/categorias')
}

export function listarProductos(categoriaId: number | null): Promise<Producto[]> {
  const filtro = categoriaId === null ? '' : `?categoriaId=${categoriaId}`
  return solicitar(`/api/productos${filtro}`)
}

export function registrarProducto(nuevo: NuevoProducto): Promise<Producto> {
  return solicitar('/api/productos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(nuevo),
  })
}
