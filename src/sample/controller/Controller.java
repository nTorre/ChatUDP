/**
 * Classe principale, associata al file view/sample.fxml
 *
 * Si occupa di gestire e controllare la view prncipale
 */

package sample.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import sample.Main;
import sample.controller.view.ContattoListCellController;
import sample.model.Chat;
import sample.model.TextMessage;

import java.io.IOException;
import java.net.*;
import java.util.*;

/* l'implementazione dell'interfaccia observer è necessaria
   per il la notifica alla classe Controller (questa) quando
   aggiungo un nuovo contatto. Vedi codice più avanti*/
public class Controller implements InvalidationListener {

    private Stage primaryStage;
    boolean done;

    // textField adibito all'inserimento del testo del messaggio
    @FXML
    TextField textFieldMsg;

    // vertical box che contiene le label dei messaggi inviati/ricevuti
    @FXML
    VBox vBoxDialogo;

    // la listView dei contatti (a sinistra della schermata)
    @FXML
    ListView<Chat> listViewChat;

    // pane che contiene la grafica che permette di inviare un messaggio
    // andrà a sostituire la label che segue (labelStart), nel momento che
    @FXML
    Pane paneToHide;

    // label contenente il testo "clicca su una chat per avviare una conversazione"
    @FXML
    Label labelStart;

    // label contenente il nome del contatto nel momento in cui lo apro
    @FXML
    Label labelDestIp;

    // label contenetne il mio ip, per comodità del mittente quando io sono il destinatario
    // (non serve reperirlo da cmd o impostazioni)
    @FXML
    Label labelMyIp;

    // textField per cambiare la mia porta di ascolto
    @FXML
    TextField textFieldPort;

    // scroll pane che conterrà le varie chat, permettendo di scorrere sui messaggi vecchi
    @FXML
    ScrollPane scrollPane;

    // pulsanti che salgono stando sopra al pulsante grosso
    @FXML
    VBox vBoxButtonsAdd;

    // pulsante grosso per aggiungere
    @FXML
    Button buttonNew;

    // contatto "Principale", ovvero quello selezionato, dunque variabile
    static Chat chat;

    // stage per creare un nuovo contatto o modificarne uno presente
    Stage secondaryStage;

    // classe adibita all'invio e alla ricezione di pacchetti
    SocketManager socketManager;


    //array che contiene tutti i contatti e relativi messaggi e informazioni
    private final ArrayList<Chat> contatti;


    //costruttore
    public Controller(){
        //inizializzo il vettore
        contatti = new ArrayList<>();
        socketManager = new SocketManager();
    }


    public void setStage(Stage primaryStage){
        this.primaryStage=primaryStage;
    }

    //metodo associato al pulsante che permette l'invio del messaggio
    @FXML
    public void send(){
        if (!textFieldMsg.getText().isEmpty()) {

            // aggiungo al contatto corrente il messaggio
            chat.addMessaggio(new TextMessage(textFieldMsg.getText(), "0", true));

            // lo aggiungo alla view
            drawMessage(true, "labelMsg", textFieldMsg.getText());

            // metodo che effettivamente invia il datagramma
            socketManager.sendText(chat.getPortaDestinatario(), chat.getIp(), textFieldMsg.getText());

            //resetto testo
            textFieldMsg.setText("");

        }

    }

    public void initialize(){

        //recupero ip per impostare la label
        try {
            labelMyIp.setText("Ip: "+ InetAddress.getLocalHost().getHostAddress());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //nessuna chat selezionata,
        paneToHide.setVisible(false);

        vBoxButtonsAdd.setVisible(false);

        //anche quando premo il tasto invio il messaggio dev'essere inviato
        textFieldMsg.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER))
                send();
        });


        Controller controller = this;

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
                if (packet.getAddress() != null) {
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
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    // aggiungo il messaggio alla lista e alla view
                                    contatto.addMessaggio(new TextMessage(received, "0",false));
                                    drawMessage(false, "labelMsgReceived", received);
                                }
                            });
                            // esco, in quanto è già stato trovato il contatto
                            break;
                        }
                    }

                    // se è nuovo, creo un nuovo contatto
                    if (isNew) {
                        Chat chat = new Chat("p2p");
                        // aggiungo il controller come osservatore per notificare la lista
                        // di modo tale da aggungerlo
                        chat.addListener(Controller.this);
                        // aggiungo alla lista dei messaggi del contatto il messaggio nuovo
                        chat.addMessaggio(new TextMessage(received, "0", false));
                        System.out.println(received);
                        // imposto porta e ip, ma non il nome, aggiungibile in seguito modificando il contatto
                        chat.setValues(address, porta, "");
                    }
                }
            }


        });
        thread.start();


        // listener per la porta su cui sono in ascolto
        textFieldPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                String port = textFieldPort.getText();
                int portInt = 50000;
                boolean isWrong = false;

                // verifico che il testo sia numerico
                try {
                    portInt = Integer.parseInt(port);
                } catch (Exception e) {
                    textEditError(true);
                    isWrong = true;

                }

                // verifico che sia compreso nei valori delle porte dinamiche
                if (portInt < 49152 || portInt > 65535) {
                    // imposto il bordo del text edit rosso per segnalare l'errore
                    textEditError(true);
                } else if (!isWrong) {
                    // chiudo il socket
                    socketManager.close();
                    // cambio il socket creandone uno nuovo
                    socketManager.changePort(portInt);
                    System.out.println("porta cambiata: "+portInt);

                    // rendo il bordo del text edit normale
                    textEditError(false);
                }
            }
        });


        // mostro o meno i pulsantini
        buttonNew.setOnMouseEntered(e->vBoxButtonsAdd.setVisible(true));
        buttonNew.setOnMouseExited(e->vBoxButtonsAdd.setVisible(false));

        // ora mantenere aperto se vado sulla vbox


    }

    // quando clicco sul pulsante per aggiungere un contatto
    @FXML
    public void newContact() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/newcontact.fxml"));

        secondaryStage = new Stage();
        Parent root = loader.load();
        secondaryStage.setTitle("Nuovo Contatto");
        Scene scene = new Scene(root, 400, 300);
        //scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        secondaryStage.setScene(scene);
        secondaryStage.show();
        secondaryStage.setResizable(false);


        chat = new Chat("p2p");
        NewContactController newContactController = loader.getController();
        newContactController.setContatto(chat);
        newContactController.setObserver(this);
        newContactController.setStage(secondaryStage);
        newContactController.scanIPs();


    }


    // metodo evocato quando notifico una modifica nella classe Contatto
    @Override
    public void invalidated(javafx.beans.Observable observable) {
        System.out.println(chat);

        // pulisco la lista
        if (!listViewChat.getItems().isEmpty())
            listViewChat.getItems().clear();


        // aggiorno la lista
        contatti.add((Chat) observable);
        listViewChat.setItems(FXCollections.observableArrayList(contatti));
        listViewChat.setCellFactory(studentListView -> new ContattoListCellController());
    }

    // clicco su un elemento della lista
    @FXML
    public void listItemClicked(){

        if (!listViewChat.getItems().isEmpty()){
            // mostro la chat e nascondo la label iniziale
            paneToHide.setVisible(true);
            labelStart.setVisible(false);

            // imposto come contatto corrente quello che ho selezionato e persente nella lista
            chat = contatti.get(listViewChat.getSelectionModel().getSelectedIndex());
            System.out.println(chat.getIp());
            // carico i messaggi
            reloadMeassages();

            // imposto il nome del contatt in alto
            labelDestIp.setText(chat.getNome());


        }
    }

    // metodo per ricaricare i messaggi nel momento in cui clicco su un altro contatto
    private void reloadMeassages() {

        ArrayList<TextMessage> messaggi = chat.getMessaggi();

        // pulisco dalla vecchia conversazione
        vBoxDialogo.getChildren().clear();

        // per ogni messaggio lo inserisco con uno stile differente se ricevuto o inviato
        for (TextMessage textMessage : messaggi) {
            String style = "labelMsg";
            if (!textMessage.isSent())
                style = "labelMsgReceived";
            drawMessage(textMessage.isSent(), style, textMessage.getTesto());
        }

    }

    public void drawMessage(boolean sent, String styleClass, String msg){
        //Creo label e assegno caratteristiche anche grazie al css
        Label label = new Label();
        label.setText(msg);
        label.getStyleClass().clear();
        label.getStyleClass().add(styleClass);
        label.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
        label.setPadding(new Insets(10,10,10,10));

        //inserisco la label in un hbox per dare allineamento a destra o sinistra se inviato
        HBox hBox = new HBox();
        hBox.getChildren().add(label);
        if (sent)
            hBox.setAlignment(Pos.BASELINE_RIGHT);
        else
            hBox.setAlignment(Pos.BASELINE_LEFT);

        // caratteristiche di grafica, distanziamento tra messaggi
        hBox.setPadding(new Insets(10,5,5,10));

        vBoxDialogo.getChildren().add(hBox);

        // necessario per far scorrere la scroll view automaticamente all'ultimo messaggio,
        // che altrimenti rimarrebbe ferma in cima
        scrollPane.setVvalue(1.0);


    }

    public void stop() {
        done = true;
    }

    public void start(){
        done = false;
    }

    // come per il new contact, ma modifica il contatto
    @FXML
    public void modifyContact() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/newcontact.fxml"));

        secondaryStage = new Stage();
        Parent root = loader.load();
        secondaryStage.setTitle("Modifica Contatto");
        Scene scene = new Scene(root, 400, 300);
        //scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        secondaryStage.setScene(scene);
        secondaryStage.show();
        secondaryStage.setResizable(false);


        NewContactController newContactController = loader.getController();
        newContactController.setContatto(chat);
        newContactController.setContatti(contatti);
        newContactController.setStage(secondaryStage);
        // il seguente metodo serve per riferire che il contatto va modificato e non è nuovo
        newContactController.isNew(false);
        newContactController.setInfo();
        newContactController.setObserver(this);
    }

    // colorazione rossa del text edit per la porta
    private void textEditError(boolean isWrong) {
        ObservableList<String> styleClass = textFieldPort.getStyleClass();

        if (isWrong) {
            if (!styleClass.contains("error")) {
                styleClass.add("error");
            }
        }

        else {
            styleClass.removeAll(Collections.singleton("error"));
        }

    }

    public void endSocketManager(){
        socketManager.close();
    }

    public static String getActiveChatType(){
        return chat.getType();
    }
}
