/**
 * Contiene informazioni relative al messaggio:
 *
 * testo
 * ora
 * se inviato o ricevuto
 */

package sample.model;

public class TextMessage {

    private String testo;
    private String timestamp;
    private boolean sent;

    public TextMessage(String testo, String timestamp, boolean sent) {
        this.testo = testo;
        this.timestamp = timestamp;
        this.sent = sent;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
