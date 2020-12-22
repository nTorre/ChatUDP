/**
 * Classe essenziale per contenere le informazioni del contatto
 *
 * estende la classe osservabile perchè deve notificare la list view dei contatti una volta che avviene la modfica
 */

package sample.model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;

public class Chat implements Observable {

    private ArrayList<TextMessage> messaggi;
    private TextMessage lastReceived;


    String ip;
    int portaDestinatario;
    String nome;
    final String TYPE;


    public Chat(String type){
        TYPE = type;
        messaggi = new ArrayList<>();
    }

    public void addMessaggio(TextMessage textMessage){
        messaggi.add(textMessage);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPortaDestinatario() {
        return portaDestinatario;
    }

    public void setPortaDestinatario(int porta) {
        this.portaDestinatario = porta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setValues(String ip, int porta, String nome){
        this.ip = ip;
        this.nome = nome;
        this.portaDestinatario = porta;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "Contatto{" +
                "ip='" + ip + '\'' +
                ", porta=" + portaDestinatario +
                ", nome='" + nome + '\'' +
                '}';
    }

    public ArrayList<TextMessage> getMessaggi() {
        return messaggi;
    }

    public void setMessaggi(ArrayList<TextMessage> messaggi) {
        this.messaggi = messaggi;
    }

    public TextMessage getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived(TextMessage lastReceived) {
        this.lastReceived = lastReceived;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {

    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}
