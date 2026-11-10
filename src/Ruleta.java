import java.util.Random;

public class Ruleta extends Thread {
    private Mesa mesa;
    private Banca banca;
    private Random random = new Random();

    public Ruleta(Mesa mesa, Banca banca) {
        super("Hilo-Ruleta");
        this.mesa = mesa;
        this.banca = banca;
    }

    @Override
    public void run() {
        try {
            while (mesa.isSimulacionActiva() && banca.getSaldo() > 0) {
                Thread.sleep(3000); // 1000 = 1 segundo (3 segundos)

                synchronized (mesa) {
                    mesa.esperarApuestasDeJugadores();
                }

                // numero aleatorio ganador del 1 al 36
                int numeroGanador = random.nextInt(37);
                System.out.println("Ha salido el numero " + numeroGanador);
                System.out.println("Saldo de la banca: " + banca.getSaldo());

                synchronized (mesa) {
                    mesa.realizarTiradaYNotificar(numeroGanador);
                }
                Thread.sleep(200); //

                // otra partida
                synchronized (mesa) {
                    mesa.reiniciarTurno();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mesa.detenerSimulacion();
        System.out.println("Se ha acabado el juego. Saldo final de la banca: " + banca.getSaldo());
    }
}