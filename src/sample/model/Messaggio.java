/**
 * Contiene informazioni relative al messaggio:
 *
 * testo
 * ora
 * se inviato o ricevuto
 */

package sample.model;

public class Messaggio {

    private String testo;
    private String orario;

    private boolean sent;

    public Messaggio() {

    }

    public Messaggio(String testo, boolean sent) {
        this.testo = testo;
        this.sent = sent;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
