package interfaz;

import java.awt.GridLayout;

import javax.swing.*;

import servicio.SistemaCafeteria;

public class VentanaLogin extends JFrame {

    private SistemaCafeteria sistema;
    private VentanaPrincipal ventanaPrincipal;

    private JTextField txtUsuario;
    private JPasswordField txtClave;

    public VentanaLogin(
            SistemaCafeteria sistema,
            VentanaPrincipal ventanaPrincipal) {

        this.sistema = sistema;
        this.ventanaPrincipal = ventanaPrincipal;

        setTitle("Acceso de administrador");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(ventanaPrincipal);
        setResizable(false);

        crearInterfaz();
    }

    private void crearInterfaz() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtUsuario = new JTextField();
        txtClave = new JPasswordField();

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnCancelar = new JButton("Cancelar");

        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        panel.add(txtClave);

        panel.add(btnIngresar);
        panel.add(btnCancelar);

        add(panel);

        btnIngresar.addActionListener(e -> iniciarSesion());
        btnCancelar.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnIngresar);
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Completa el usuario y la contraseña.");
            return;
        }

        if (sistema.autenticarAdmin(usuario, clave)) {
            JOptionPane.showMessageDialog(
                    this, "Bienvenido al área administrativa.");

            VentanaAdmin ventanaAdmin = new VentanaAdmin(
                    sistema, ventanaPrincipal);

            ventanaAdmin.setVisible(true);
            dispose();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos.",
                    "Acceso denegado",
                    JOptionPane.ERROR_MESSAGE);

            txtClave.setText("");
            txtClave.requestFocus();
        }
    }
}

