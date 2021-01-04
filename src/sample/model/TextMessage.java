/**
 * Contiene informazioni relative al messaggio:
 *
 * testo
 * ora
 * se inviato o ricevuto
 */

package sample.model;

public class TextMessage extends Message{

    private String testo;


    public TextMessage(String testo, String timestamp, boolean sent) {
        super(timestamp, sent);
        this.testo = testo;

    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

}
