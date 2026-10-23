package interfaz;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import servicio.SistemaCafeteria;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println(
                        "No se pudo aplicar el estilo del sistema.");
            }

            SistemaCafeteria sistema = new SistemaCafeteria();

            VentanaPrincipal ventana = new VentanaPrincipal(sistema);
            ventana.setVisible(true);
        });
    }
}

