public class Banca {
    private double saldo; // saldo actual 50.000

    public Banca(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public synchronized double getSaldo() {
        return saldo;
    }

    // sumar dinero cuando un jugador pierde (cobra la banca)
    public synchronized void cobrar(double cantidad) {
        this.saldo += cantidad;
    }

    // restar dinero cuando un jugador gana, tiene que haber suficiente saldo
    public synchronized boolean pagar(double cantidad) {
        if (this.saldo >= cantidad) {
            this.saldo -= cantidad;
            return true; // se hace el pago
        }
        return false; // no hay saldo suficiente
    }
}
