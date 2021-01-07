/**
 * Classe che controlla la modifca o il salvataggio di un contatto
 */

package sample.controller.view;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.controller.Controller;
import sample.model.Chat;

import javax.swing.text.View;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class NewContactController{

    private Stage stage;


    @FXML
    TextField textFieldIp;

    @FXML
    TextField textFieldPort;

    @FXML
    TextField textFieldName;

    @FXML
    ListView<String> listViewIps;

    @FXML
    Label labelInfo;

    static ArrayList<String> listaIp;

    Chat chat;

    public void setContatti(ArrayList<Chat> contatti) {
        this.contatti = contatti;
    }

    ArrayList<Chat> contatti;
    //Controller osservatore;

    boolean isNew = true;

    static volatile int i=0;
    private static final Boolean isComplete = false;

    private ViewManager viewManager;

    static CountDownLatch latch;

    public NewContactController(){
        chat = new Chat("p2p");
        contatti = new ArrayList<>();
        listaIp = new ArrayList<>();
    }

    public void initialize() throws InterruptedException {
        // la stessa cosa del controller
        textFieldPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                String port = textFieldPort.getText();
                int portInt = 50000;
                boolean isWrong = false;
                try {
                    portInt = Integer.parseInt(port);
                } catch (Exception e) {
                    textEditError(true);
                    isWrong = true;

                }

                if (portInt < 49152 || portInt > 65535) {
                    textEditError(true);
                } else if (!isWrong) {
                    //receiver.changePort(portInt, this);
                    textEditError(false);
                }
            }
        });


        // thread che aspetta che lo scanner degli ip finisca
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 254 sono i thread che devono terminare
                latch = new CountDownLatch(254);

                try {
                    // aspetto
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // una volta terminato la scansione, li aggiungo alla lista
                for (String s : listaIp) {
                    System.out.println(s);
                    listViewIps.getItems().add(s);
                }

            }
        }).start();

    }

    // pulsante conferma modifica o aggiunta
    @FXML
    public void confirm(){
        // bisogna gestire eventuali errori
        String ip = textFieldIp.getText();
        int porta = Integer.parseInt(textFieldPort.getText());
        String nome = textFieldName.getText();

        if (!isNew)
            contatti.remove(chat);

        chat.setValues(ip, porta, nome);
        viewManager.update(chat);

        stage.close();
    }

    // pulsante annulla
    @FXML
    public void cancel(){
        stage.close();
    }

    //recupero la lista contatti per salvarci dentro il contatto nuovo
    public void setContatto(Chat chat) {
        this.chat = chat;

    }

    // nel caso il contatto sia da modificare, setto i valori che possiedo
    public void setInfo(){
        if (!isNew) {
            labelInfo.setText("Modifica il contatto");
            // porta e ip ce li ho per forza, il nome no: se mi arrivasse un pacchetto da un contatto che on ho
            textFieldPort.setText(String.valueOf(chat.getPortaDestinatario()));
            textFieldIp.setText(chat.getIp());
            if (chat.getNome() != null) {
                textFieldName.setText(chat.getNome());
            }
        }
    }

    public void setObserver(ViewManager viewManager){
        //osservatore=controller;
        this.viewManager = viewManager;
    }

    private void textEditError(boolean isWrong) {
        ObservableList<String> styleClass = textFieldPort.getStyleClass();

        if (isWrong) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
                System.out.println("Ciao");
            }
        }

        else {
            // remove all occurrences:
            styleClass.removeAll(Collections.singleton("error"));
        }

    }

    public void isNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    // metodo preso da internet, leggermente adattato, ma molto veloce ed efficace
    public static void getNetworkIPs() {
        final byte[] ip;
        try {
            ip = InetAddress.getLocalHost().getAddress();
        } catch (Exception e) {
            return;     // exit method, otherwise "ip might not have been initialized"
        }

        for (int i = 1; i <= 254; i++) {
            final int j = i;  // i as non-final variable cannot be referenced from inner class
            Thread thread = new Thread(new Runnable() {   // new thread for parallel execution
                public void run() {
                    try {
                        ip[3] = (byte) j;
                        InetAddress address = InetAddress.getByAddress(ip);
                        String output = address.toString().substring(1);
                        if (address.isReachable(2500)) {
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    addIp(output, true);
                                }
                            });
                            System.out.println(output + " is on the network");
                        } else {
                            System.out.println("Not Reachable: " + output);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    addIp(output, false);

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();// dont forget to start the thread
        }
    }

    // metodo per forza synchronized per aggiungere alla lista l'ip e notificare la fine del thread
    public static synchronized void addIp(String ip, boolean yes){
        if(yes)
            listaIp.add(ip);
        i++;

        latch.countDown();

    }


    // prendo l'ip dalla lista
    @FXML
    public void listIpClicked(){
        textFieldIp.setText(listaIp.get(listViewIps.getSelectionModel().getSelectedIndex()));
    }


    public void scanIPs(){
        getNetworkIPs();
    }

}

