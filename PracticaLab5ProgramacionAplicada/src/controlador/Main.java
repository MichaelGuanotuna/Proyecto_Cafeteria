package controlador;
import java.util.ArrayList;

import controlador.GestorArchivos;
import controlador.HiloMecanico;
import controlador.ProcesoRevision;
import modelo.Mecanico;
import modelo.Vehiculo;
import vista.VistaConsola;

public class Main {

    public static void main(String[] args) {

        Thread hiloMain = Thread.currentThread();
        System.out.println(hiloMain);

        GestorArchivos gestor = new GestorArchivos();

        ArrayList<Mecanico> mecanicos = gestor.leerMecanicos();
        ArrayList<Vehiculo> vehiculos = gestor.leerVehiculos();

        if (mecanicos.size() != 10 || vehiculos.size() != 50) {
            System.out.println("Se necesitan 10 mecanicos y 50 vehiculos.");
            return;
        }

        VistaConsola vista = new VistaConsola();

        int posicion = 0;

        for (int i = 0; i < mecanicos.size(); i++) {

            Mecanico mecanico = mecanicos.get(i);

            ArrayList<Vehiculo> lote = new ArrayList<Vehiculo>();

            // Asignar cinco vehiculos
            for (int j = 0; j < 5; j++) {

                lote.add(vehiculos.get(posicion));
                posicion++;
            }

            // Crear el hilo del mecanico
            HiloMecanico hilo = new HiloMecanico(mecanico, lote, vista);

            // Crear la tarea de recepcion mediante Runnable
            ProcesoRevision proceso = new ProcesoRevision(hilo);

            Thread recepcion = new Thread(proceso, "Recepcion " + (i + 1));

            recepcion.start();
        }

        System.out.println("Tarea del main finalizada");
    }
}