package controlador;

import java.util.Random;

public class ProcesoRevision implements Runnable {

    private HiloMecanico hiloMecanico;

    public ProcesoRevision(HiloMecanico hiloMecanico) {
        this.hiloMecanico = hiloMecanico;
    }

    @Override
    public void run() {

        Random aleatorio = new Random();

        int tiempo = aleatorio.nextInt(3001);

        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            System.out.println("Recepcion interrumpida");
            return;
        }

        // Iniciar la revision cuando llegan los datos
        hiloMecanico.start();
    }
}