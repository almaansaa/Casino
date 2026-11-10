public class Mesa {
    private int numeroGanador = -1; // numero de cada tirada
    private boolean tiradaRealizada = false;
    private int jugadoresListos = 0;
    private int totalJugadores;
    private Banca banca;
    private boolean simulacionActiva = true; // booleano para saber si el juego sigue o se ha acabado

    public Mesa(int totalJugadores, Banca banca) {
        this.totalJugadores = totalJugadores;
        this.banca = banca;
    }

    public synchronized void registrarApuestaYEsperarTirada() throws InterruptedException {
        jugadoresListos++;


        if (jugadoresListos >= totalJugadores) {
            notifyAll(); // si ya han jugado todos los jugadores, se activa el hilo
        }

        while (simulacionActiva && !tiradaRealizada) {
            wait();
        }
    }

    public synchronized void esperarApuestasDeJugadores() throws InterruptedException {
        while (jugadoresListos < totalJugadores) {
            wait(); // espera a que apuesten todos
        }
    }

    public synchronized void realizarTiradaYNotificar(int numero) {
        this.numeroGanador = numero;
        this.tiradaRealizada = true;
        notifyAll();
    }

    // reiniciamos las variables para la siguiente partida
    public synchronized void reiniciarTurno() {
        jugadoresListos = 0;
        tiradaRealizada = false;
        numeroGanador = -1;
    }

    public synchronized void detenerSimulacion() {
        simulacionActiva = false;
        notifyAll();
    }

    public synchronized boolean isSimulacionActiva() {
        return simulacionActiva;
    }

    public synchronized int getNumeroGanador() {
        return numeroGanador;
    }

    public void setNumeroGanador(int numeroGanador) {
        this.numeroGanador = numeroGanador;
    }

    public synchronized boolean isTiradaRealizada() {
        return tiradaRealizada;
    }
}