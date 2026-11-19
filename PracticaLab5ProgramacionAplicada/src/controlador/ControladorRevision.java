package controlador;

import java.util.ArrayList;
import modelo.Vehiculo;

public class ControladorRevision {

    private EvaluadorGases evaluador = new EvaluadorGases();

    // Evaluar los 5 vehiculos asignados al mecanico
    public String evaluarLote(ArrayList<Vehiculo> vehiculos) {

        int fallasLambda = 0;
        int fallasO2 = 0;
        int fallasN2O = 0;
        int fallasCO2 = 0;

        int total = vehiculos.size();

        if (total == 0) {
            return "No existen vehiculos para evaluar";
        }

        for (int i = 0; i < total; i++) {

            Vehiculo vehiculo = vehiculos.get(i);

            if (evaluador.fallaLambda(vehiculo)) {
                fallasLambda++;
            }

            if (evaluador.fallaO2(vehiculo)) {
                fallasO2++;
            }

            if (evaluador.fallaN2O(vehiculo)) {
                fallasN2O++;
            }

            if (evaluador.fallaCO2(vehiculo)) {
                fallasCO2++;
            }
        }

        // Calcular porcentajes de error
        double porcentajeLambda = fallasLambda * 100.0 / total;
        double porcentajeO2 = fallasO2 * 100.0 / total;
        double porcentajeN2O = fallasN2O * 100.0 / total;
        double porcentajeCO2 = fallasCO2 * 100.0 / total;

        String resultado = "";

        resultado += "Lambda: " + porcentajeLambda + "%\n";
        resultado += "O2: " + porcentajeO2 + "%\n";
        resultado += "N2O: " + porcentajeN2O + "%\n";
        resultado += "CO2: " + porcentajeCO2 + "%\n";

        // Comparar con los porcentajes de tolerancia
        if (porcentajeLambda <= 70 &&
            porcentajeO2 <= 30 &&
            porcentajeN2O <= 80 &&
            porcentajeCO2 <= 50) {

            resultado += "Pasa en Primera Revision";

        } else {

            resultado += "Segunda Revision Asignada";
        }

        return resultado;
    }
}