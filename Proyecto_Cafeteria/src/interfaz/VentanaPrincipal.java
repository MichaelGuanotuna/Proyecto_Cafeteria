package interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import modelo.Producto;
import modelo.DetalleVenta;
import modelo.Venta;
import servicio.SistemaCafeteria;

public class VentanaPrincipal extends JFrame {

    private SistemaCafeteria sistema;

    private JTextField txtBuscar;
    private JTextField txtCliente;
    private JTextField txtResponsable;
    private JTextField txtCantidad;

    private JTable tablaProductos;
    private JTable tablaPedido;

    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloPedido;

    private JLabel lblTotal;

    private ArrayList<DetalleVenta> pedido;

    public VentanaPrincipal(SistemaCafeteria sistema) {
        this.sistema = sistema;
        this.pedido = new ArrayList<>();

        setTitle("El Buen Sabor - Sistema de Cafetería");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearInterfaz();
        actualizarCatalogo();
        actualizarPedido();
    }

    private void crearInterfaz() {
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel(
                "EL BUEN SABOR - SISTEMA DE CAFETERÍA",
                SwingConstants.CENTER);

        titulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));

        panelCentral.add(crearPanelProductos());
        panelCentral.add(crearPanelPedido());

        add(panelCentral, BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Catálogo"));

        JPanel panelBusqueda = new JPanel(new FlowLayout());

        txtBuscar = new JTextField(12);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar todos");

        panelBusqueda.add(new JLabel("Código o nombre:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        modeloProductos = new DefaultTableModel(
                new String[]{"Código", "Producto", "Precio", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloProductos);
        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel panelAgregar = new JPanel(new FlowLayout());

        txtCantidad = new JTextField("1", 5);
        JButton btnAgregar = new JButton("Agregar al pedido");

        panelAgregar.add(new JLabel("Cantidad:"));
        panelAgregar.add(txtCantidad);
        panelAgregar.add(btnAgregar);

        panel.add(panelAgregar, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> buscarEnCatalogo());
        btnMostrarTodos.addActionListener(e -> actualizarCatalogo());
        btnAgregar.addActionListener(e -> agregarAlPedido());

        return panel;
    }

    private JPanel crearPanelPedido() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Pedido actual"));

        JPanel panelDatos = new JPanel(new GridLayout(2, 2, 5, 5));

        txtCliente = new JTextField();
        txtResponsable = new JTextField();

        panelDatos.add(new JLabel("Cliente (opcional):"));
        panelDatos.add(txtCliente);
        panelDatos.add(new JLabel("Responsable:"));
        panelDatos.add(txtResponsable);

        panel.add(panelDatos, BorderLayout.NORTH);

        modeloPedido = new DefaultTableModel(
                new String[]{"Código", "Producto", "Cant.", "Precio", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaPedido = new JTable(modeloPedido);
        panel.add(new JScrollPane(tablaPedido), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnQuitar = new JButton("Quitar seleccionado");
        JButton btnVaciar = new JButton("Vaciar pedido");

        panelBotones.add(btnQuitar);
        panelBotones.add(btnVaciar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        btnQuitar.addActionListener(e -> quitarDelPedido());
        btnVaciar.addActionListener(e -> vaciarPedido());

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnConfirmar = new JButton("Confirmar venta");
        JButton btnHistorial = new JButton("Historial de ventas");
        JButton btnAdmin = new JButton("Acceso administrador");

        panelBotones.add(btnConfirmar);
        panelBotones.add(btnHistorial);
        panelBotones.add(btnAdmin);

        panel.add(lblTotal, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);

        btnConfirmar.addActionListener(e -> confirmarVenta());
        btnHistorial.addActionListener(e -> mostrarHistorial());
        btnAdmin.addActionListener(e -> abrirAdministrador());

        return panel;
    }

    private void actualizarCatalogo() {
        modeloProductos.setRowCount(0);

        for (Producto producto : sistema.listarProductos()) {
            modeloProductos.addRow(new Object[]{
                producto.getCodigo(),
                producto.getNombre(),
                String.format("%.2f", producto.getPrecio()),
                producto.getStock()
            });
        }
    }

    private void buscarEnCatalogo() {
        String texto = txtBuscar.getText().trim().toLowerCase();

        modeloProductos.setRowCount(0);

        for (Producto producto : sistema.listarProductos()) {
            if (producto.getCodigo().toLowerCase().contains(texto)
                    || producto.getNombre().toLowerCase().contains(texto)) {

                modeloProductos.addRow(new Object[]{
                    producto.getCodigo(),
                    producto.getNombre(),
                    String.format("%.2f", producto.getPrecio()),
                    producto.getStock()
                });
            }
        }
    }

    private void agregarAlPedido() {
        int fila = tablaProductos.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un producto del catálogo.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero.");
            }

            String codigo = modeloProductos.getValueAt(fila, 0).toString();
            Producto producto = sistema.buscarProducto(codigo);

            if (producto == null) {
                JOptionPane.showMessageDialog(
                        this, "El producto ya no existe.");
                actualizarCatalogo();
                return;
            }

            int cantidadEnPedido = 0;

            for (DetalleVenta detalle : pedido) {
                if (detalle.getProducto().getCodigo()
                        .equalsIgnoreCase(codigo)) {
                    cantidadEnPedido += detalle.getCantidad();
                }
            }

            if (cantidad + cantidadEnPedido > producto.getStock()) {
                JOptionPane.showMessageDialog(
                        this, "No hay suficiente stock disponible.");
                return;
            }

            boolean encontrado = false;

            for (DetalleVenta detalle : pedido) {
                if (detalle.getProducto().getCodigo()
                        .equalsIgnoreCase(codigo)) {
                    detalle.setCantidad(detalle.getCantidad() + cantidad);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                pedido.add(new DetalleVenta(producto, cantidad));
            }

            actualizarPedido();
            txtCantidad.setText("1");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Introduce una cantidad válida.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void actualizarPedido() {
        modeloPedido.setRowCount(0);

        double total = 0;

        for (DetalleVenta detalle : pedido) {
            double subtotal = detalle.calcularSubtotal();
            total += subtotal;

            modeloPedido.addRow(new Object[]{
                detalle.getProducto().getCodigo(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                String.format("%.2f", detalle.getPrecioUnitario()),
                String.format("%.2f", subtotal)
            });
        }

        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    private void quitarDelPedido() {
        int fila = tablaPedido.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un producto del pedido.");
            return;
        }

        pedido.remove(fila);
        actualizarPedido();
    }

    private void vaciarPedido() {
        pedido.clear();
        actualizarPedido();
    }

    private void confirmarVenta() {
        if (pedido.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "El pedido está vacío.");
            return;
        }

        String responsable = txtResponsable.getText().trim();

        if (responsable.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Introduce el nombre del responsable.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas confirmar la venta por " + lblTotal.getText() + "?",
                "Confirmar venta",
                JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Venta venta = sistema.registrarVenta(
                    txtCliente.getText(),
                    responsable,
                    new ArrayList<>(pedido));

            JOptionPane.showMessageDialog(
                    this,
                    "Venta registrada correctamente.\n"
                    + "Número de venta: " + venta.getId()
                    + "\nTotal: $" + String.format("%.2f", venta.getTotal()));

            pedido.clear();
            txtCliente.setText("");
            txtResponsable.setText("");

            actualizarPedido();
            actualizarCatalogo();

        } catch (IOException | IllegalArgumentException
                | IllegalStateException e) {

            JOptionPane.showMessageDialog(
                    this, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);

            actualizarCatalogo();
        }
    }

    private void mostrarHistorial() {
        ArrayList<Venta> ventas = sistema.listarVentas();

        if (ventas.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Todavía no hay ventas registradas.");
            return;
        }

        String[] columnas = {
            "N.º", "Fecha", "Cliente", "Responsable", "Total"
        };

        DefaultTableModel modeloHistorial =
                new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int fila, int columna) {
                        return false;
                    }
                };

        for (Venta venta : ventas) {
            modeloHistorial.addRow(new Object[]{
                venta.getId(),
                venta.getFecha().toString().replace('T', ' '),
                venta.getCliente(),
                venta.getResponsable(),
                String.format("%.2f", venta.getTotal())
            });
        }

        JTable tablaHistorial = new JTable(modeloHistorial);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(tablaHistorial),
                "Historial de ventas",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirAdministrador() {
        VentanaLogin ventanaLogin = new VentanaLogin(sistema, this);
        ventanaLogin.setVisible(true);
    }
}
