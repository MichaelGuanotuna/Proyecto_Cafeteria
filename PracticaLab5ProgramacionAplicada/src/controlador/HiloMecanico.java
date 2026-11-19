package controlador;

import java.util.ArrayList;
import modelo.Mecanico;
import modelo.Vehiculo;
import vista.VistaConsola;

public class HiloMecanico extends Thread {

    private Mecanico mecanico;
    private ArrayList<Vehiculo> vehiculos;
    private ControladorRevision controlador;
    private VistaConsola vista;

    public HiloMecanico(Mecanico mecanico,
            ArrayList<Vehiculo> vehiculos, VistaConsola vista) {

        super(mecanico.getNombre());

        this.mecanico = mecanico;
        this.vehiculos = vehiculos;
        this.vista = vista;
        this.controlador = new ControladorRevision();
    }

    @Override
    public void run() {

        EvaluadorGases evaluador = new EvaluadorGases();

        // Generar los valores de gases
        for (int i = 0; i < vehiculos.size(); i++) {

            Vehiculo vehiculo = vehiculos.get(i);
            evaluador.generarGases(vehiculo);
        }

        // Evaluar los cinco vehiculos
        String resultado = controlador.evaluarLote(vehiculos);

        // Mostrar la tabla mediante la vista
        vista.mostrarRevision(mecanico, vehiculos, resultado);
    }
}