import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Banca banca = new Banca(50000.0);
        Mesa mesa = new Mesa(12, banca);

        List<Jugador> jugadores = new ArrayList<>();

        // 12 jugadores y 4 de cada tipo de apuesta
        for (int i = 1; i <= 4; i++) {
            jugadores.add(new Jugador("Jugador normal numero " + i, 1000.0, banca, mesa, Jugador.Estrategia.NUMERO));
        }
        for (int i = 1; i <= 4; i++) {
            jugadores.add(new Jugador("Jugador par o impar numero " + i, 1000.0, banca, mesa, Jugador.Estrategia.PAR_IMPAR));
        }
        for (int i = 1; i <= 4; i++) {
            jugadores.add(new Jugador("Jugador martingala numero " + i, 1000.0, banca, mesa, Jugador.Estrategia.MARTINGALA));
        }

        // iniciar los hilos
        for (Jugador j : jugadores) {
            j.start();
        }

        // correr el hilo independiente de la ruleta
        Ruleta ruleta = new Ruleta(mesa, banca);
        ruleta.start();
    }
}