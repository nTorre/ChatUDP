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

import java.net.DatagramPacket;
import java.util.ArrayList;

public class Controller {

    boolean done;
    SocketManager socketManager;
    ViewManager viewManager;


    //array che contiene tutti i contatti e relativi messaggi e informazioni
    private final ArrayList<Chat> contatti;


    //costruttore
    public Controller(){
        //inizializzo il vettore
        contatti = new ArrayList<>();
        socketManager = new SocketManager();
        viewManager = new ViewManager();
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


            }});
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
        return viewManager.getActiveChat.getType();
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
        newContactController.setStage(secondaryStage);
        newContactController.scanIPs();
        newContactController.setObserver(this);


    }


    // metodo evocato quando notifico una modifica nella classe Contatto


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

    public void update(Chat chat){
        System.out.println(chat);

        // pulisco la lista
        if (!listViewChat.getItems().isEmpty())
            listViewChat.getItems().clear();


        // aggiorno la lista
        contatti.add(chat);
        listViewChat.setItems(FXCollections.observableArrayList(contatti));
        listViewChat.setCellFactory(studentListView -> new ContattoListCellController());
    }

}
