import java.util.Random;

public class Jugador extends Thread {
    public enum Estrategia { NUMERO, PAR_IMPAR, MARTINGALA }

    private String nombre;
    private double saldo;
    private Banca banca;
    private Mesa mesa;
    private Estrategia estrategia;

    private int numeroElegido;
    private String tipoParImpar;
    private double apuestaActual = 10.0; //martingala

    public Jugador(String nombre, double saldoInicial, Banca banca, Mesa mesa, Estrategia estrategia) {
        super(nombre);
        this.nombre = nombre;
        this.saldo = saldoInicial;
        this.banca = banca;
        this.mesa = mesa;
        this.estrategia = estrategia;

        Random r = new Random();
        // numero aleatorio del 1 al 36
        if (estrategia == Estrategia.NUMERO || estrategia == Estrategia.MARTINGALA) {
            this.numeroElegido = 1 + r.nextInt(36);
        } else {
            if (r.nextBoolean()) {
                this.tipoParImpar = "PAR";
            } else {
                this.tipoParImpar = "IMPAR";
            }
        }
    }

    @Override
    public void run() {
        try {
            // condiciones: que el juego siga y que el saldo sea mayor que cero para el jugador y para la banca
            while (mesa.isSimulacionActiva() && saldo > 0 && banca.getSaldo() > 0) {

                double cantidadApostada = 10.0;
                if (estrategia == Estrategia.MARTINGALA) {
                    // si tiene menos de lo que le piden, apuesta todo su saldo
                    if (saldo < apuestaActual){
                        apuestaActual = saldo;
                    }
                    cantidadApostada = apuestaActual;
                }

                // el jugador paga y la banca cobra
                saldo = saldo - cantidadApostada;
                banca.cobrar(cantidadApostada);

                if (estrategia == Estrategia.PAR_IMPAR) {
                    System.out.println(nombre + " ha apostado " + cantidadApostada + " a " + tipoParImpar);
                } else {
                    System.out.println(nombre + " ha apostado " + cantidadApostada + " a " + numeroElegido);
                }

                mesa.registrarApuestaYEsperarTirada();

                // sacar el numero ganador
                int ganador = mesa.getNumeroGanador();
                boolean haGanado = false;
                double premio = 0;

                // comprobar los ganadores y poner el premio para cada situacion
                if (estrategia == Estrategia.PAR_IMPAR) {
                    boolean esPar = (ganador % 2 == 0);
                    if (((tipoParImpar.equals("PAR") && esPar) || (tipoParImpar.equals("IMPAR") && !esPar))) {
                        haGanado = true;
                        premio = 20.0; //premio fijo
                    }
                } else {
                    if (ganador == numeroElegido) {
                        haGanado = true;
                        if (estrategia == Estrategia.MARTINGALA) {
                            premio = cantidadApostada * 36; // martingala premio x36
                        } else {
                            premio = 360.0; //premio fijo
                        }
                    }
                }

                // mensaje de ganador + cobro o mensaje de perdedor
                if (haGanado) {
                    if (banca.pagar(premio)) {
                        saldo = saldo + premio;
                        System.out.println("Ha ganado "+nombre+". Cobra " + premio);
                    }
                    // si es martingala la apuesta se reinicia a 10 otra vez
                    if (estrategia == Estrategia.MARTINGALA){
                        apuestaActual = 10.0;
                    }
                } else {
                    System.out.println(nombre+" ha perdido. El ganador es " + ganador);
                    // si es martingala se duplica su siguiente apuesta
                    if (estrategia == Estrategia.MARTINGALA) {
                        apuestaActual = apuestaActual * 2;
                    }
                }

                // si alguno se queda sin dinero se para el juego
                if (saldo <= 0 || banca.getSaldo() <= 0) {
                    mesa.detenerSimulacion();
                    break;
                }

                // reinicio para la siguiente partida
                synchronized (mesa) {
                    while (mesa.isSimulacionActiva() && mesa.isTiradaRealizada()) {
                        mesa.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(nombre + " ha acabado con " + saldo);
    }
}