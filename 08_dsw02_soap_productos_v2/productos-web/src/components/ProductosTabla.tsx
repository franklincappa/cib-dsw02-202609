import type { Producto } from '../types'

const STOCK_BAJO = 10

const moneda = new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' })

interface Props {
  productos: Producto[]
  cargando: boolean
  /** Id del producto recien registrado, para resaltar su fila. */
  resaltado: number | null
}

export default function ProductosTabla({ productos, cargando, resaltado }: Props) {
  if (cargando && productos.length === 0) {
    return <p className="aviso">Cargando productos…</p>
  }

  if (productos.length === 0) {
    return <p className="aviso">No hay productos en esta categoría. Registre el primero con el formulario.</p>
  }

  // La barra de stock es relativa al mayor stock del listado visible.
  const mayorStock = Math.max(...productos.map((p) => p.stock), 1)

  return (
    <div className="tabla-contenedor" aria-busy={cargando}>
      <table className="tabla">
        <thead>
          <tr>
            <th scope="col" className="col-id">Id</th>
            <th scope="col">Producto</th>
            <th scope="col">Categoría</th>
            <th scope="col" className="col-numero">Precio</th>
            <th scope="col" className="col-stock">Stock</th>
          </tr>
        </thead>
        <tbody>
          {productos.map((p) => {
            const bajo = p.stock < STOCK_BAJO
            return (
              <tr key={p.id} className={p.id === resaltado ? 'fila-nueva' : undefined}>
                <td className="col-id">{p.id}</td>
                <td className="col-nombre">{p.nombre}</td>
                <td>{p.categoria.nombre}</td>
                <td className="col-numero">{moneda.format(p.precio)}</td>
                <td className="col-stock">
                  <div className={bajo ? 'stock stock-bajo' : 'stock'}>
                    <span className="stock-cifra">{p.stock}</span>
                    <span className="stock-barra" aria-hidden="true">
                      <span style={{ width: `${Math.max((p.stock / mayorStock) * 100, 2)}%` }} />
                    </span>
                    {bajo && <span className="stock-nota">bajo</span>}
                  </div>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
