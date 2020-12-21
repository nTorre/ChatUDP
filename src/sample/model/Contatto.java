/**
 * Classe essenziale per contenere le informazioni del contatto
 *
 * estende la classe osservabile perchè deve notificare la list view dei contatti una volta che avviene la modfica
 */

package sample.model;

import java.util.ArrayList;
import java.util.Observable;

public class Contatto extends Observable {

    private ArrayList<Messaggio> messaggi;

    private Messaggio lastReceived;


    String ip;
    int portaDestinatario;
    private int portAscolto;
    String nome;


    public Contatto(){
        messaggi = new ArrayList<>();
    }

    public void addMessaggio(Messaggio messaggio){
        messaggi.add(messaggio);
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

        // notifico gli osservatori (listView)
        setChanged();
        notifyObservers();

    }


    public void notifyMod(){
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return "Contatto{" +
                "ip='" + ip + '\'' +
                ", porta=" + portaDestinatario +
                ", nome='" + nome + '\'' +
                '}';
    }

    public ArrayList<Messaggio> getMessaggi() {
        return messaggi;
    }

    public void setMessaggi(ArrayList<Messaggio> messaggi) {
        this.messaggi = messaggi;
    }

    public Messaggio getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived(Messaggio lastReceived) {
        this.lastReceived = lastReceived;
    }

    public int getPortAscolto() {
        return portAscolto;
    }

    public void setPortAscolto(int portAscolto) {
        this.portAscolto = portAscolto;
    }
}
