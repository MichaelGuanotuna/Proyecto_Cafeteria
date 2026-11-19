package controlador;

import java.util.Random;
import modelo.Vehiculo;

public class EvaluadorGases {

    private Random aleatorio = new Random();

    // Generar valores de gases para un vehiculo
    public void generarGases(Vehiculo vehiculo) {

        double lambda = aleatorio.nextInt(201) / 100.0;
        double o2 = aleatorio.nextInt(501) / 100.0;
        double n2o = aleatorio.nextInt(101);
        double co2 = aleatorio.nextInt(301) / 10.0;

        vehiculo.setGases(lambda, o2, n2o, co2);
    }

    // Verificar fallas en los gases
    public boolean fallaLambda(Vehiculo vehiculo) {
        return vehiculo.getLambda() > 1;
    }

    public boolean fallaO2(Vehiculo vehiculo) {
        return vehiculo.getO2() >= 2;
    }

    public boolean fallaN2O(Vehiculo vehiculo) {
        return vehiculo.getN2o() >= 40;
    }

    public boolean fallaCO2(Vehiculo vehiculo) {
        return vehiculo.getCo2() >= 15;
    }
}