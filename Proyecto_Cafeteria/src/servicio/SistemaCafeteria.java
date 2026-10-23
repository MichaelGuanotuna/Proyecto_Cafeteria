
package servicio;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import archivo.GestorArchivos;
import modelo.Producto;
import modelo.DetalleVenta;
import modelo.Venta;
import modelo.MovimientoInventario;

public class SistemaCafeteria {

    private ArrayList<Producto> productos;
    private ArrayList<Venta> ventas;
    private ArrayList<MovimientoInventario> movimientos;

    private GestorArchivos gestorArchivos;

    private boolean adminAutenticado;

    private int siguienteCodigoProducto;
    private int siguienteIdVenta;
    private int siguienteIdMovimiento;

    public SistemaCafeteria() {
        productos = new ArrayList<>();
        ventas = new ArrayList<>();
        movimientos = new ArrayList<>();

        gestorArchivos = new GestorArchivos();

        adminAutenticado = false;
        siguienteCodigoProducto = 1;
        siguienteIdVenta = 1;
        siguienteIdMovimiento = 1;

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            productos = gestorArchivos.cargarProductos();
            ventas = gestorArchivos.cargarVentas(productos);
            movimientos = gestorArchivos.cargarMovimientos();

            actualizarContadores();

        } catch (IOException e) {
            System.out.println("Error al cargar los datos: " + e.getMessage());
        }
    }

    private void actualizarContadores() {
        for (Producto producto : productos) {
            String codigo = producto.getCodigo();

            if (codigo.startsWith("P")) {
                try {
                    int numero = Integer.parseInt(codigo.substring(1));

                    if (numero >= siguienteCodigoProducto) {
                        siguienteCodigoProducto = numero + 1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Código de producto inválido: " + codigo);
                }
            }
        }

        for (Venta venta : ventas) {
            if (venta.getId() >= siguienteIdVenta) {
                siguienteIdVenta = venta.getId() + 1;
            }
        }

        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getId() >= siguienteIdMovimiento) {
                siguienteIdMovimiento = movimiento.getId() + 1;
            }
        }
    }

    public boolean autenticarAdmin(String usuario, String clave) {
        if ("admin".equals(usuario) && "1234".equals(clave)) {
            adminAutenticado = true;
            return true;
        }

        return false;
    }

    public boolean esAdminAutenticado() {
        return adminAutenticado;
    }

    public void cerrarSesionAdmin() {
        adminAutenticado = false;
    }

    private void comprobarAdmin() {
        if (!adminAutenticado) {
            throw new IllegalStateException(
                    "Debe iniciar sesión como administrador.");
        }
    }

    public Producto buscarProducto(String codigo) {
        if (codigo == null) {
            return null;
        }

        for (Producto producto : productos) {
            if (producto.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return producto;
            }
        }

        return null;
    }

    public ArrayList<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    public ArrayList<Venta> listarVentas() {
        return new ArrayList<>(ventas);
    }

    public ArrayList<MovimientoInventario> listarMovimientos() {
        return new ArrayList<>(movimientos);
    }

    public Producto registrarProducto(String nombre, double precio, int stock)
            throws IOException {

        comprobarAdmin();

        Producto producto = new Producto(
                generarCodigoProducto(), nombre, precio, stock);

        productos.add(producto);

        try {
            gestorArchivos.guardarProductos(productos);
        } catch (IOException e) {
            productos.remove(producto);
            throw e;
        }

        return producto;
    }

    private String generarCodigoProducto() {
        String codigo = String.format("P%05d", siguienteCodigoProducto);
        siguienteCodigoProducto++;

        return codigo;
    }

    public void modificarProducto(String codigo, String nombre, double precio)
            throws IOException {

        comprobarAdmin();

        Producto producto = buscarProducto(codigo);

        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }

        String nombreAnterior = producto.getNombre();
        double precioAnterior = producto.getPrecio();

        producto.setNombre(nombre);
        producto.setPrecio(precio);

        try {
            gestorArchivos.guardarProductos(productos);
        } catch (IOException e) {
            producto.setNombre(nombreAnterior);
            producto.setPrecio(precioAnterior);
            throw e;
        }
    }

    public void reponerStock(
            String codigo, int cantidad, String responsable)
            throws IOException {

        comprobarAdmin();

        Producto producto = buscarProducto(codigo);

        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        if (responsable == null || responsable.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El responsable es obligatorio.");
        }

        producto.sumarUnidades(cantidad);

        MovimientoInventario movimiento = new MovimientoInventario(
                siguienteIdMovimiento,
                LocalDateTime.now(),
                producto.getCodigo(),
                "ENTRADA",
                cantidad,
                responsable,
                "Reposición de inventario");

        movimientos.add(movimiento);

        try {
            gestorArchivos.guardarProductos(productos);
            gestorArchivos.guardarMovimientos(movimientos);
            siguienteIdMovimiento++;
        } catch (IOException e) {
            producto.disminuirUnidades(cantidad);
            movimientos.remove(movimiento);
            throw e;
        }
    }

    public Venta registrarVenta(
            String cliente,
            String responsable,
            ArrayList<DetalleVenta> detalles) throws IOException {

        if (responsable == null || responsable.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe indicar el responsable de la venta.");
        }

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta debe tener al menos un producto.");
        }

        for (DetalleVenta detalle : detalles) {
            if (detalle == null) {
                throw new IllegalArgumentException(
                        "La venta contiene un detalle inválido.");
            }

            Producto producto = buscarProducto(
                    detalle.getProducto().getCodigo());

            if (producto == null) {
                throw new IllegalArgumentException(
                        "Uno de los productos ya no existe.");
            }

            if (!producto.hayStock(detalle.getCantidad())) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para: " + producto.getNombre());
            }
        }

        Venta venta = new Venta(
                siguienteIdVenta,
                LocalDateTime.now(),
                cliente,
                responsable);

        for (DetalleVenta detalle : detalles) {
            venta.agregarDetalle(new DetalleVenta(
                    buscarProducto(detalle.getProducto().getCodigo()),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario()));
        }

        ArrayList<Producto> productosModificados = new ArrayList<>();
        ArrayList<Integer> stocksAnteriores = new ArrayList<>();
        ArrayList<MovimientoInventario> nuevosMovimientos =
                new ArrayList<>();

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();

            productosModificados.add(producto);
            stocksAnteriores.add(producto.getStock());

            producto.disminuirUnidades(detalle.getCantidad());

            MovimientoInventario movimiento = new MovimientoInventario(
                    siguienteIdMovimiento + nuevosMovimientos.size(),
                    LocalDateTime.now(),
                    producto.getCodigo(),
                    "SALIDA",
                    detalle.getCantidad(),
                    responsable,
                    "Venta #" + siguienteIdVenta);

            nuevosMovimientos.add(movimiento);
        }

        ventas.add(venta);
        movimientos.addAll(nuevosMovimientos);

        try {
            gestorArchivos.guardarProductos(productos);
            gestorArchivos.guardarVentas(ventas);
            gestorArchivos.guardarMovimientos(movimientos);

            siguienteIdVenta++;
            siguienteIdMovimiento += nuevosMovimientos.size();

        } catch (IOException e) {
            ventas.remove(venta);
            movimientos.removeAll(nuevosMovimientos);

            for (int i = 0; i < productosModificados.size(); i++) {
                Producto producto = productosModificados.get(i);
                int stockAnterior = stocksAnteriores.get(i);

                producto.sumarUnidades(
                        stockAnterior - producto.getStock());
            }

            try {
                gestorArchivos.guardarProductos(productos);
                gestorArchivos.guardarVentas(ventas);
                gestorArchivos.guardarMovimientos(movimientos);
            } catch (IOException errorRestauracion) {
                System.out.println(
                        "Advertencia: no se pudieron restaurar todos los archivos.");
            }

            throw e;
        }

        return venta;
    }

    public double calcularTotalVentas() {
        double total = 0;

        for (Venta venta : ventas) {
            total += venta.getTotal();
        }

        return total;
    }
}
