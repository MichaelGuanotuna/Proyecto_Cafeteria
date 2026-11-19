package vista;

import java.util.ArrayList;
import modelo.Mecanico;
import modelo.Vehiculo;

public class VistaConsola {

    // Mostrar los resultados de cada mecanico
    public synchronized void mostrarRevision(Mecanico mecanico,
            ArrayList<Vehiculo> vehiculos, String resultado) {

        System.out.println();
        System.out.println("===============================================================");
        System.out.println("MECANICO: " + mecanico.getNombre());
        System.out.println("===============================================================");

        System.out.printf("%-15s %-12s %-8s %-8s %-8s %-8s%n",
                "MARCA", "PLACA", "LAMBDA", "O2", "N2O", "CO2");

        System.out.println("---------------------------------------------------------------");

        for (int i = 0; i < vehiculos.size(); i++) {

            Vehiculo vehiculo = vehiculos.get(i);

            System.out.printf("%-15s %-12s %-8.2f %-8.2f %-8.2f %-8.2f%n",
                    vehiculo.getMarca(),
                    vehiculo.getPlaca(),
                    vehiculo.getLambda(),
                    vehiculo.getO2(),
                    vehiculo.getN2o(),
                    vehiculo.getCo2());
        }

        System.out.println("---------------------------------------------------------------");
        System.out.println("PORCENTAJES DE ERROR Y RESULTADO:");
        System.out.println(resultado);
        System.out.println("===============================================================");
    }
}