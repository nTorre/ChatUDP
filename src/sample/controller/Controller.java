/**
 * Classe principale, associata al file view/sample.fxml
 *
 * Si occupa di gestire e controllare la view prncipale
 */

package sample.controller;
import sample.controller.net.SocketManager;
import sample.controller.view.NewContactController;
import sample.controller.view.ViewManager;
import sample.model.Chat;
import sample.model.TextMessage;

import javax.swing.text.View;
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
                String received = new String(packet.getData(), 0, packet.getLength());

                // FIXME: 22/12/2020  spostere il codice del cambio nel SocketManager? che errore risolve?
                // questo if è necessario per evitare errori durante il cambio della porta di ascolto
                String address = packet.getAddress().getHostAddress();
                int porta = packet.getPort();

                boolean isNew = true;

                System.out.println("**************************");

                // scannerizzo i contatti e verifico se il messaggio è stato inviato da un
                // contatto nuovo o meno
                for (Chat contatto : contatti) {
                    // stesso ip = contatto già presente nella lista
                    if (contatto.getIp().equals(address)) {
                        isNew = false;
                        addChat();
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
                    chat.addMessaggio(new TextMessage(received, "0", false));
                    System.out.println(received);
                    // imposto porta e ip, ma non il nome, aggiungibile in seguito modificando il contatto
                    chat.setValues(address, porta, "");
                }


            }
        });
        thread.start();





    }



    public void sendText(int port, String destIP, String msg){
        socketManager.sendText(port, destIP, msg);
    }

    private void addChat(){
        Chat tmp = new Chat("p2p");
        contatti.add(new Chat("p2p"));
        //colllegare con i parametri che arrivano da interfaccia
    }

    public void changePort(int newPort){
        socketManager.changePort(newPort);
    }

    public static String getActiveChatType(){
        return viewManager.getActiveChat().getType();
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
