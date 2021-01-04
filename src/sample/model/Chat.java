/**
 * Classe essenziale per contenere le informazioni del contatto
 *
 * estende la classe osservabile perchè deve notificare la list view dei contatti una volta che avviene la modfica
 */

package sample.model;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import sample.controller.Controller;
import sample.controller.view.ViewManager;

import java.util.ArrayList;

public class Chat{

    private ArrayList<Message> messaggi;
    private TextMessage lastReceived;

    private ViewManager viewManager;


    String ip;
    int portaDestinatario;
    String nome;
    final String TYPE;


    public Chat(String type){
        TYPE = type;
        messaggi = new ArrayList<>();
    }

    public void addMessaggio(Message textMessage){
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
        viewManager.update(this);
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "ip='" + ip + '\'' +
                ", porta=" + portaDestinatario +
                ", nome='" + nome + '\'' +
                '}';
    }

    public ArrayList<Message> getMessaggi() {
        return messaggi;
    }

    public void setMessaggi(ArrayList<Message> messaggi) {
        this.messaggi = messaggi;
    }

    public TextMessage getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived(TextMessage lastReceived) {
        this.lastReceived = lastReceived;
    }


    public void addListener(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

}
