package interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import modelo.Producto;
import modelo.Venta;
import modelo.MovimientoInventario;
import servicio.SistemaCafeteria;

public class VentanaAdmin extends JFrame {

    private SistemaCafeteria sistema;
    private VentanaPrincipal ventanaPrincipal;

    
    private JLabel lblVentas;
    private JLabel lblIngresos;
    private JLabel lblProductos;
    private JLabel lblUnidades;
    
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextField txtCodigoModificar;

    private JTextField txtCodigoReposicion;
    private JTextField txtCantidadReposicion;
    private JTextField txtResponsable;

    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;

    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;

    //private JLabel lblReporte;

    public VentanaAdmin(
            SistemaCafeteria sistema,
            VentanaPrincipal ventanaPrincipal) {

        this.sistema = sistema;
        this.ventanaPrincipal = ventanaPrincipal;

        setTitle("El Buen Sabor - Administración");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(ventanaPrincipal);

        crearInterfaz();
        actualizarProductos();
        actualizarMovimientos();
        actualizarReporte();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                sistema.cerrarSesionAdmin();
            }
        });
    }

    private void crearInterfaz() {
        JTabbedPane pestañas = new JTabbedPane();

        pestañas.addTab("Productos", crearPanelProductos());
        pestañas.addTab("Inventario", crearPanelInventario());
        pestañas.addTab("Reportes", crearPanelReportes());
        pestañas.addTab("Movimientos", crearPanelMovimientos());

        add(pestañas, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.add(btnCerrarSesion);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        modeloProductos = new DefaultTableModel(
                new String[]{"Código", "Nombre", "Precio", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloProductos);

        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));

        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        txtStock = new JTextField();

        txtCodigoModificar = new JTextField();
        txtCodigoModificar.setEditable(false);

        panelFormulario.add(new JLabel("Nombre del producto:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Precio:"));
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Stock inicial:"));
        panelFormulario.add(txtStock);

        panelFormulario.add(new JLabel("Código seleccionado:"));
        panelFormulario.add(txtCodigoModificar);

        JButton btnRegistrar = new JButton("Registrar producto");
        JButton btnModificar = new JButton("Modificar nombre y precio");

        panelFormulario.add(btnRegistrar);
        panelFormulario.add(btnModificar);

        panel.add(panelFormulario, BorderLayout.SOUTH);

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProductoSeleccionado();
            }
        });

        btnRegistrar.addActionListener(e -> registrarProducto());
        btnModificar.addActionListener(e -> modificarProducto());

        return panel;
    }

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        txtCodigoReposicion = new JTextField();
        txtCantidadReposicion = new JTextField();
        txtResponsable = new JTextField();

        JButton btnReponer = new JButton("Reponer existencias");

        panel.add(new JLabel("Código del producto:"));
        panel.add(txtCodigoReposicion);

        panel.add(new JLabel("Cantidad que ingresará:"));
        panel.add(txtCantidadReposicion);

        panel.add(new JLabel("Responsable:"));
        panel.add(txtResponsable);

        panel.add(new JLabel("Tipo de movimiento:"));
        panel.add(new JLabel("ENTRADA"));

        panel.add(new JLabel(""));
        panel.add(btnReponer);

        btnReponer.addActionListener(e -> reponerInventario());

        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel titulo = new JLabel(
                "REPORTE GENERAL",
                SwingConstants.CENTER);

        titulo.setFont(
                new java.awt.Font("Arial", java.awt.Font.BOLD, 20));

        lblVentas = new JLabel("", SwingConstants.CENTER);
        lblIngresos = new JLabel("", SwingConstants.CENTER);
        lblProductos = new JLabel("", SwingConstants.CENTER);
        lblUnidades = new JLabel("", SwingConstants.CENTER);

        JButton btnActualizar = new JButton("Actualizar reporte");

        panel.add(titulo);
        panel.add(lblVentas);
        panel.add(lblIngresos);
        panel.add(lblProductos);
        panel.add(lblUnidades);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.add(panel, BorderLayout.CENTER);
        panelPrincipal.add(btnActualizar, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> actualizarReporte());

        return panelPrincipal;
    }

    private JPanel crearPanelMovimientos() {
        JPanel panel = new JPanel(new BorderLayout());

        modeloMovimientos = new DefaultTableModel(
                new String[]{
                    "ID", "Fecha", "Código", "Tipo",
                    "Cantidad", "Responsable", "Observación"
                }, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaMovimientos = new JTable(modeloMovimientos);

        panel.add(new JScrollPane(tablaMovimientos), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar movimientos");

        btnActualizar.addActionListener(e -> actualizarMovimientos());

        panel.add(btnActualizar, BorderLayout.SOUTH);

        return panel;
    }

    private void actualizarProductos() {
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

    private void cargarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();

        if (fila == -1) {
            return;
        }

        txtCodigoModificar.setText(
                modeloProductos.getValueAt(fila, 0).toString());

        txtNombre.setText(
                modeloProductos.getValueAt(fila, 1).toString());

        txtPrecio.setText(
                modeloProductos.getValueAt(fila, 2).toString());

        txtStock.setText(
                modeloProductos.getValueAt(fila, 3).toString());
    }

    private void registrarProducto() {
        try {
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            Producto producto = sistema.registrarProducto(
                    nombre, precio, stock);

            JOptionPane.showMessageDialog(
                    this,
                    "Producto registrado correctamente.\nCódigo: "
                    + producto.getCodigo());

            limpiarFormularioProducto();
            actualizarProductos();

        } catch (NumberFormatException e) {
            mostrarError("El precio y el stock deben ser números válidos.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        } catch (IOException e) {
            mostrarError("No se pudo guardar el producto: " + e.getMessage());
        }
    }

    private void modificarProducto() {
        String codigo = txtCodigoModificar.getText().trim();

        if (codigo.isEmpty()) {
            mostrarError("Selecciona un producto de la tabla.");
            return;
        }

        try {
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            sistema.modificarProducto(codigo, nombre, precio);

            JOptionPane.showMessageDialog(
                    this, "Producto modificado correctamente.");

            actualizarProductos();

        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número válido.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        } catch (IOException e) {
            mostrarError("No se pudo guardar el cambio: " + e.getMessage());
        }
    }

    private void reponerInventario() {
        try {
            String codigo = txtCodigoReposicion.getText().trim();
            int cantidad = Integer.parseInt(
                    txtCantidadReposicion.getText().trim());
            String responsable = txtResponsable.getText().trim();

            sistema.reponerStock(codigo, cantidad, responsable);

            JOptionPane.showMessageDialog(
                    this, "Inventario actualizado correctamente.");

            txtCodigoReposicion.setText("");
            txtCantidadReposicion.setText("");
            txtResponsable.setText("");

            actualizarProductos();
            actualizarMovimientos();
            actualizarReporte();

        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        } catch (IOException e) {
            mostrarError("No se pudo guardar la reposición: " + e.getMessage());
        }
    }

    private void actualizarReporte() {
        ArrayList<Venta> ventas = sistema.listarVentas();

        int unidades = 0;

        for (Producto producto : sistema.listarProductos()) {
            unidades += producto.getStock();
        }

        lblVentas.setText(
                "Ventas registradas: " + ventas.size());

        lblIngresos.setText(
                "Ingresos por ventas: $"
                + String.format("%.2f", sistema.calcularTotalVentas()));

        lblProductos.setText(
                "Productos diferentes en el catálogo: "
                + sistema.listarProductos().size());

        lblUnidades.setText(
                "Unidades disponibles en inventario: " + unidades);
    }

    private void actualizarMovimientos() {
        modeloMovimientos.setRowCount(0);

        for (MovimientoInventario movimiento : sistema.listarMovimientos()) {
            modeloMovimientos.addRow(new Object[]{
                movimiento.getId(),
                movimiento.getFecha().toString().replace('T', ' '),
                movimiento.getCodigoProducto(),
                movimiento.getTipo(),
                movimiento.getCantidad(),
                movimiento.getResponsable(),
                movimiento.getObservacion()
            });
        }
    }

    private void limpiarFormularioProducto() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtCodigoModificar.setText("");
        tablaProductos.clearSelection();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void cerrarSesion() {
        sistema.cerrarSesionAdmin();
        dispose();
    }
}

