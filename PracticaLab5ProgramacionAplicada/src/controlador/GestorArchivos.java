package controlador;

import modelo.Mecanico;
import modelo.Vehiculo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GestorArchivos {

    // Leer los mecanicos
    public ArrayList<Mecanico> leerMecanicos() {

        ArrayList<Mecanico> lista = new ArrayList<Mecanico>();

        try {
            File archivo = new File("Archivo/mecanicos");
            Scanner leer = new Scanner(archivo);

            int id = 1;

            while (leer.hasNextLine()) {

                String nombre = leer.nextLine().trim();

                if (!nombre.isEmpty()) {
                    Mecanico mecanico = new Mecanico(id, nombre);
                    lista.add(mecanico);
                    id++;
                }
            }

            leer.close();

        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de mecanicos.");
        }

        return lista;
    }

    // Leer los vehiculos
    public ArrayList<Vehiculo> leerVehiculos() {

        ArrayList<Vehiculo> lista = new ArrayList<Vehiculo>();

        try {
            File archivo = new File("Archivo/vehiculos");
            Scanner leer = new Scanner(archivo);

            while (leer.hasNextLine()) {

                String linea = leer.nextLine().trim();

                if (!linea.isEmpty()) {

                    String[] datos = linea.split(";");

                    if (datos.length == 2) {

                        String marca = datos[0].trim();
                        String placa = datos[1].trim();

                        Vehiculo vehiculo = new Vehiculo(marca, placa);
                        lista.add(vehiculo);
                    }
                }
            }

            leer.close();

        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de vehiculos.");
        }

        return lista;
    }
}