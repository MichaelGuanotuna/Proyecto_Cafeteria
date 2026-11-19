package modelo;

public class Vehiculo {

    private String marca;
    private String placa;
    private double lambda;
    private double o2;
    private double n2o;
    private double co2;

    public Vehiculo(String marca, String placa) {
        this.marca = marca;
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getPlaca() {
        return placa;
    }

    public double getLambda() {
        return lambda;
    }

    public double getO2() {
        return o2;
    }

    public double getN2o() {
        return n2o;
    }

    public double getCo2() {
        return co2;
    }

    public void setGases(double lambda, double o2, double n2o, double co2) {
        this.lambda = lambda;
        this.o2 = o2;
        this.n2o = n2o;
        this.co2 = co2;
    }
}