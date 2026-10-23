package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Venta {

    private int id;
    private LocalDateTime fecha;
    private String cliente;
    private String responsable;
    private ArrayList<DetalleVenta> detalles;
    private double total;

    public Venta(int id, LocalDateTime fecha, String cliente, String responsable) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        if (responsable == null || responsable.trim().isEmpty()) {
            throw new IllegalArgumentException("El responsable es obligatorio.");
        }

        this.id = id;
        this.fecha = fecha;
        this.cliente = cliente == null ? "" : cliente.trim();
        this.responsable = responsable.trim();
        this.detalles = new ArrayList<>();
        this.total = 0;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo.");
        }

        detalles.add(detalle);
        calcularTotal();
    }

    public double calcularTotal() {
        total = 0;

        for (DetalleVenta detalle : detalles) {
            total += detalle.calcularSubtotal();
        }

        return total;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public String getResponsable() {
        return responsable;
    }

    public ArrayList<DetalleVenta> getDetalles() {
        return new ArrayList<>(detalles);
    }

    public double getTotal() {
        return total;
    }
}
