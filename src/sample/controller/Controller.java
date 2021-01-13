/**
 * Classe principale, associata al file view/sample.fxml
 *
 * Si occupa di gestire e controllare la view prncipale
 */

package sample.controller;
import javafx.application.Platform;
import sample.model.*;
import sample.controller.net.SocketManager;
import sample.controller.view.ViewManager;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class Controller {

    boolean done;
    SocketManager socketManager;
    static ViewManager viewManager;


    //array che contiene tutti i contatti e relativi messaggi e informazioni
    private final ArrayList<Chat> contatti;


    //costruttore
    public Controller(){
        //inizializzo il vettore
        contatti = new ArrayList<>();
        socketManager = new SocketManager();
        initialize();
    }

    public void setViewManager(ViewManager viewManager){
        Controller.viewManager = viewManager;
    }

    public void initialize(){

        // thread di ascolto
        Thread thread = new Thread(() -> {
            while(!done) {

                // FIXME: 22/12/2020 Il SocketManager cosa deve restituire? io pensavo i vari formati, receiveText, receiveJpeg....
                // FIXME: 22/12/2020 Sistemare chiamate Controller -> socketManager
                DatagramPacket packet = socketManager.receive();
                System.out.println("*******************");
                System.out.println(socketManager.getPort());

                // converte il buf in stringa

                // FIXME: 22/12/2020  spostere il codice del cambio nel SocketManager? che errore risolve?
                // questo if è necessario per evitare errori durante il cambio della porta di ascolto
                String address = packet.getAddress().getHostAddress();
                int porta = packet.getPort();

                boolean isNew = true;

                System.out.println("**************************");

                // scannerizzo i contatti e verifico se il messaggio è stato inviato da un
                // contatto nuovo o meno

                byte[] received = packet.getData();
                Packet packet1 = new Packet(received);
                for (Chat contatto : contatti) {
                    // stesso ip = contatto già presente nella lista
                    if (contatto.getIp().equals(address)) {
                        isNew = false;
                        //addChat();
                        if(packet1.getType().equals(Packet.Type.IMAGE)) {
                            try {
                                File file = (File) packet1.getFormattedContent();
                                contatto.addMessaggio(new ImageMessage(file, Long.toString(System.currentTimeMillis()), false));
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewManager.drawImage(file, false);

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try {
                                String text = (String) packet1.getFormattedContent();
                                contatto.addMessaggio(new TextMessage(text, Long.toString(System.currentTimeMillis()), false));

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        viewManager.drawMessage(false,"labelMsgReceived", text);


                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        // esco, in quanto è già stato trovato il contatto
                        break;
                    }
                }

                // se è nuovo, creo un nuovo contatto
                if (isNew) {
                    Chat chat = new Chat("p2p");
                    // aggiungo il controller come osservatore per notificare la lista
                    // di modo tale da aggungerlo
                    // aggiungo alla lista dei messaggi del contatto il messaggio nuovo
                    if(packet1.getType().equals(Packet.Type.IMAGE)) {
                        try {
                            chat.addMessaggio(new ImageMessage((File) packet1.getFormattedContent(), Long.toString(System.currentTimeMillis()), false));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            chat.addMessaggio(new TextMessage((String) packet1.getFormattedContent(), Long.toString(System.currentTimeMillis()), false));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    addContatto(chat);
                    System.out.println(received);
                    // imposto porta e ip, ma non il nome, aggiungibile in seguito modificando il contatto
                    chat.setValues(address, porta, "");


                }


            }
        });
        thread.start();
    }



    public void sendText(int port, String destIP, Packet packet){
        socketManager.sendText(port, destIP, packet);
    }

    public void changePort(int newPort){
        socketManager.changePort(newPort);
    }

    public static String getActiveChatType(){
        Chat chat = viewManager.getActiveChat();
        if(chat==null){
            return "p2p";
        }else{
            return chat.getType();
        }
    }







    // metodo evocato quando notifico una modifica nella classe Contatto


    // clicco su un elemento della lista

    // metodo per ricaricare i messaggi nel momento in cui clicco su un altro contatto

    public void stop() {
        done = true;
    }


    // come per il new contact, ma modifica il contatto


    public void endSocketManager(){
        socketManager.close();
    }

    public SocketManager getSocketManager(){
        return socketManager;
    }


    public Chat getContatto(int i){
        return contatti.get(i);
    }

    public ArrayList<Chat> getContatti(){
        return contatti;
    }

    public void addContatto(Chat chat){
        contatti.add(chat);
    }

}