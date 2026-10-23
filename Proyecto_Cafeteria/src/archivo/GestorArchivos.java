package archivo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import modelo.Producto;
import modelo.DetalleVenta;
import modelo.Venta;
import modelo.MovimientoInventario;

public class GestorArchivos {

    private File carpetaDatos;

    public GestorArchivos() {
        carpetaDatos = new File("datos");

        if (!carpetaDatos.exists()) {
            carpetaDatos.mkdirs();
        }
    }

    public ArrayList<Producto> cargarProductos() throws IOException {
        ArrayList<Producto> productos = new ArrayList<>();
        File archivo = new File(carpetaDatos, "productos.txt");

        if (!archivo.exists()) {
            return productos;
        }

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivo))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\\|", -1);

                if (datos.length != 4) {
                    continue;
                }

                try {
                    Producto producto = new Producto(
                            datos[0],
                            datos[1],
                            Double.parseDouble(datos[2]),
                            Integer.parseInt(datos[3]));

                    productos.add(producto);

                } catch (IllegalArgumentException e) {
                    System.out.println("Producto inválido: " + linea);
                }
            }
        }

        return productos;
    }

    public void guardarProductos(ArrayList<Producto> productos)
            throws IOException {

        File archivo = new File(carpetaDatos, "productos.txt");

        try (BufferedWriter escritor = new BufferedWriter(
                new FileWriter(archivo))) {

            for (Producto producto : productos) {
                escritor.write(
                        limpiarTexto(producto.getCodigo()) + "|"
                        + limpiarTexto(producto.getNombre()) + "|"
                        + producto.getPrecio() + "|"
                        + producto.getStock());

                escritor.newLine();
            }
        }
    }

    public ArrayList<Venta> cargarVentas(
            ArrayList<Producto> productos) throws IOException {

        ArrayList<Venta> ventas = new ArrayList<>();

        File archivoVentas = new File(carpetaDatos, "ventas.txt");
        File archivoDetalles = new File(carpetaDatos, "detalles_ventas.txt");

        if (!archivoVentas.exists()) {
            return ventas;
        }

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivoVentas))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\\|", -1);

                if (datos.length != 5) {
                    continue;
                }

                try {
                    Venta venta = new Venta(
                            Integer.parseInt(datos[0]),
                            LocalDateTime.parse(datos[1]),
                            datos[2],
                            datos[3]);

                    ventas.add(venta);

                } catch (IllegalArgumentException e) {
                    System.out.println("Venta inválida: " + linea);
                }
            }
        }

        if (!archivoDetalles.exists()) {
            return ventas;
        }

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivoDetalles))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\\|", -1);

                if (datos.length != 4) {
                    continue;
                }

                try {
                    int idVenta = Integer.parseInt(datos[0]);
                    String codigo = datos[1];
                    int cantidad = Integer.parseInt(datos[2]);
                    double precio = Double.parseDouble(datos[3]);

                    Venta venta = buscarVenta(ventas, idVenta);
                    Producto producto = buscarProducto(productos, codigo);

                    if (venta != null && producto != null) {
                        DetalleVenta detalle = new DetalleVenta(
                                producto, cantidad, precio);

                        venta.agregarDetalle(detalle);
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("Detalle inválido: " + linea);
                }
            }
        }

        return ventas;
    }

    public void guardarVentas(ArrayList<Venta> ventas)
            throws IOException {

        File archivoVentas = new File(carpetaDatos, "ventas.txt");
        File archivoDetalles = new File(carpetaDatos, "detalles_ventas.txt");

        try (BufferedWriter escritorVentas = new BufferedWriter(
                    new FileWriter(archivoVentas));
             BufferedWriter escritorDetalles = new BufferedWriter(
                    new FileWriter(archivoDetalles))) {

            for (Venta venta : ventas) {
                escritorVentas.write(
                        venta.getId() + "|"
                        + venta.getFecha() + "|"
                        + limpiarTexto(venta.getCliente()) + "|"
                        + limpiarTexto(venta.getResponsable()) + "|"
                        + venta.calcularTotal());

                escritorVentas.newLine();

                for (DetalleVenta detalle : venta.getDetalles()) {
                    escritorDetalles.write(
                            venta.getId() + "|"
                            + detalle.getProducto().getCodigo() + "|"
                            + detalle.getCantidad() + "|"
                            + detalle.getPrecioUnitario());

                    escritorDetalles.newLine();
                }
            }
        }
    }

    public ArrayList<MovimientoInventario> cargarMovimientos()
            throws IOException {

        ArrayList<MovimientoInventario> movimientos = new ArrayList<>();
        File archivo = new File(carpetaDatos, "movimientos.txt");

        if (!archivo.exists()) {
            return movimientos;
        }

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivo))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\\|", -1);

                if (datos.length != 7) {
                    continue;
                }

                try {
                    MovimientoInventario movimiento =
                            new MovimientoInventario(
                                    Integer.parseInt(datos[0]),
                                    LocalDateTime.parse(datos[1]),
                                    datos[2],
                                    datos[3],
                                    Integer.parseInt(datos[4]),
                                    datos[5],
                                    datos[6]);

                    movimientos.add(movimiento);

                } catch (IllegalArgumentException e) {
                    System.out.println("Movimiento inválido: " + linea);
                }
            }
        }

        return movimientos;
    }

    public void guardarMovimientos(
            ArrayList<MovimientoInventario> movimientos)
            throws IOException {

        File archivo = new File(carpetaDatos, "movimientos.txt");

        try (BufferedWriter escritor = new BufferedWriter(
                new FileWriter(archivo))) {

            for (MovimientoInventario movimiento : movimientos) {
                escritor.write(
                        movimiento.getId() + "|"
                        + movimiento.getFecha() + "|"
                        + limpiarTexto(movimiento.getCodigoProducto()) + "|"
                        + movimiento.getTipo() + "|"
                        + movimiento.getCantidad() + "|"
                        + limpiarTexto(movimiento.getResponsable()) + "|"
                        + limpiarTexto(movimiento.getObservacion()));

                escritor.newLine();
            }
        }
    }

    private Venta buscarVenta(ArrayList<Venta> ventas, int id) {
        for (Venta venta : ventas) {
            if (venta.getId() == id) {
                return venta;
            }
        }

        return null;
    }

    private Producto buscarProducto(
            ArrayList<Producto> productos, String codigo) {

        for (Producto producto : productos) {
            if (producto.getCodigo().equalsIgnoreCase(codigo)) {
                return producto;
            }
        }

        return null;
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("|", "/")
                    .replace("\n", " ")
                    .replace("\r", " ");
    }
}
