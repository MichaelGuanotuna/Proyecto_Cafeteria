package modelo;

import java.time.LocalDateTime;

public class MovimientoInventario {

    private int id;
    private LocalDateTime fecha;
    private String codigoProducto;
    private String tipo;
    private int cantidad;
    private String responsable;
    private String observacion;

    public MovimientoInventario(int id, LocalDateTime fecha,
            String codigoProducto, String tipo, int cantidad,
            String responsable, String observacion) {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }

        if (!"ENTRADA".equals(tipo) && !"SALIDA".equals(tipo)) {
            throw new IllegalArgumentException("El tipo debe ser ENTRADA o SALIDA.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        if (responsable == null || responsable.trim().isEmpty()) {
            throw new IllegalArgumentException("El responsable es obligatorio.");
        }

        this.id = id;
        this.fecha = fecha;
        this.codigoProducto = codigoProducto.trim();
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.responsable = responsable.trim();
        this.observacion = observacion == null ? "" : observacion.trim();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getResponsable() {
        return responsable;
    }

    public String getObservacion() {
        return observacion;
    }
}
