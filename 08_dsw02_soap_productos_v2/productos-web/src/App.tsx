import { useCallback, useEffect, useState } from 'react'
import { listarCategorias, listarProductos } from './api'
import ProductoForm from './components/ProductoForm'
import ProductosTabla from './components/ProductosTabla'
import type { Categoria, Producto } from './types'

export default function App() {
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [productos, setProductos] = useState<Producto[]>([])
  const [filtro, setFiltro] = useState<number | null>(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [recienRegistrado, setRecienRegistrado] = useState<number | null>(null)

  const cargarProductos = useCallback(async (categoriaId: number | null) => {
    setCargando(true)
    setError(null)
    try {
      setProductos(await listarProductos(categoriaId))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error inesperado.')
    } finally {
      setCargando(false)
    }
  }, [])

  // Carga inicial: categorias (para el filtro y el formulario) y productos, en paralelo.
  useEffect(() => {
    let vigente = true
    Promise.all([listarCategorias(), listarProductos(null)])
      .then(([listaCategorias, listaProductos]) => {
        if (!vigente) return
        setCategorias(listaCategorias)
        setProductos(listaProductos)
      })
      .catch((e: Error) => vigente && setError(e.message))
      .finally(() => vigente && setCargando(false))
    return () => {
      vigente = false
    }
  }, [])

  function cambiarFiltro(categoriaId: number | null) {
    setFiltro(categoriaId)
    void cargarProductos(categoriaId)
  }

  async function reintentar() {
    if (categorias.length === 0) {
      // Si fallo la carga inicial, tambien faltan las categorias.
      await listarCategorias().then(setCategorias).catch(() => undefined)
    }
    await cargarProductos(filtro)
  }

  async function alRegistrar(producto: Producto) {
    setRecienRegistrado(producto.id)
    await cargarProductos(filtro)
  }

  return (
    <div className="pagina">
      <header className="cabecera">
        <h1>Inventario de productos</h1>
        <p>
          React consulta la API .NET, y la API obtiene los datos del servicio SOAP en Java.
        </p>
      </header>

      <main className="contenido">
        <section className="panel" aria-labelledby="titulo-listado">
          <div className="panel-barra">
            <h2 id="titulo-listado">
              Productos{!cargando && !error && <span className="conteo">{productos.length}</span>}
            </h2>

            <label className="filtro">
              <span>Categoría</span>
              <select
                value={filtro ?? ''}
                onChange={(e) => cambiarFiltro(e.target.value === '' ? null : Number(e.target.value))}
              >
                <option value="">Todas</option>
                {categorias.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nombre}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {error ? (
            <div className="aviso aviso-error" role="alert">
              <p>{error}</p>
              <button type="button" className="boton-secundario" onClick={() => void reintentar()}>
                Reintentar
              </button>
            </div>
          ) : (
            <ProductosTabla productos={productos} cargando={cargando} resaltado={recienRegistrado} />
          )}
        </section>

        <aside className="panel panel-formulario" aria-labelledby="titulo-registro">
          <h2 id="titulo-registro">Registrar producto</h2>
          <ProductoForm categorias={categorias} onRegistrado={alRegistrar} />
        </aside>
      </main>
    </div>
  )
}
