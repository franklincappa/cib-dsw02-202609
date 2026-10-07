import { useState } from 'react'
import type { FormEvent } from 'react'
import { ApiError, registrarProducto } from '../api'
import type { Categoria, ErroresCampo, Producto } from '../types'

interface Props {
  categorias: Categoria[]
  onRegistrado: (producto: Producto) => void | Promise<void>
}

const VACIO = { nombre: '', precio: '', stock: '', categoriaId: '' }

export default function ProductoForm({ categorias, onRegistrado }: Props) {
  const [campos, setCampos] = useState(VACIO)
  const [errores, setErrores] = useState<ErroresCampo>({})
  const [mensaje, setMensaje] = useState<{ tipo: 'ok' | 'error'; texto: string } | null>(null)
  const [enviando, setEnviando] = useState(false)

  function cambiar(campo: keyof typeof VACIO, valor: string) {
    setCampos((actual) => ({ ...actual, [campo]: valor }))
    setErrores((actual) => ({ ...actual, [campo]: [] }))
  }

  async function enviar(evento: FormEvent) {
    evento.preventDefault()
    setEnviando(true)
    setMensaje(null)
    setErrores({})

    try {
      // Los campos vacios viajan como null: la validacion la decide la API.
      const creado = await registrarProducto({
        nombre: campos.nombre,
        precio: campos.precio === '' ? null : Number(campos.precio),
        stock: campos.stock === '' ? null : Number(campos.stock),
        categoriaId: campos.categoriaId === '' ? null : Number(campos.categoriaId),
      })
      setCampos(VACIO)
      setMensaje({ tipo: 'ok', texto: `Producto registrado con el id ${creado.id}.` })
      await onRegistrado(creado)
    } catch (e) {
      if (e instanceof ApiError && e.errores) {
        setErrores(e.errores)
      }
      setMensaje({ tipo: 'error', texto: e instanceof Error ? e.message : 'Error inesperado.' })
    } finally {
      setEnviando(false)
    }
  }

  const errorDe = (campo: string) => errores[campo]?.[0]

  return (
    <form className="formulario" onSubmit={enviar} noValidate>
      <div className="campo">
        <label htmlFor="nombre">Nombre</label>
        <input
          id="nombre"
          value={campos.nombre}
          maxLength={150}
          onChange={(e) => cambiar('nombre', e.target.value)}
          aria-invalid={Boolean(errorDe('nombre'))}
          aria-describedby="error-nombre"
        />
        <p id="error-nombre" className="campo-error">{errorDe('nombre')}</p>
      </div>

      <div className="campo-doble">
        <div className="campo">
          <label htmlFor="precio">Precio (S/)</label>
          <input
            id="precio"
            type="number"
            min="0"
            step="0.01"
            inputMode="decimal"
            value={campos.precio}
            onChange={(e) => cambiar('precio', e.target.value)}
            aria-invalid={Boolean(errorDe('precio'))}
            aria-describedby="error-precio"
          />
          <p id="error-precio" className="campo-error">{errorDe('precio')}</p>
        </div>

        <div className="campo">
          <label htmlFor="stock">Stock</label>
          <input
            id="stock"
            type="number"
            min="0"
            step="1"
            inputMode="numeric"
            value={campos.stock}
            onChange={(e) => cambiar('stock', e.target.value)}
            aria-invalid={Boolean(errorDe('stock'))}
            aria-describedby="error-stock"
          />
          <p id="error-stock" className="campo-error">{errorDe('stock')}</p>
        </div>
      </div>

      <div className="campo">
        <label htmlFor="categoria">Categoría</label>
        <select
          id="categoria"
          value={campos.categoriaId}
          onChange={(e) => cambiar('categoriaId', e.target.value)}
          aria-invalid={Boolean(errorDe('categoriaId'))}
          aria-describedby="error-categoria"
        >
          <option value="">Seleccione…</option>
          {categorias.map((c) => (
            <option key={c.id} value={c.id}>
              {c.nombre}
            </option>
          ))}
        </select>
        <p id="error-categoria" className="campo-error">{errorDe('categoriaId')}</p>
      </div>

      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Registrando…' : 'Registrar producto'}
      </button>

      <p className={mensaje ? `mensaje mensaje-${mensaje.tipo}` : 'mensaje'} role="status">
        {mensaje?.texto}
      </p>
    </form>
  )
}
