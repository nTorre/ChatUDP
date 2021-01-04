package sample.controller.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Main;
import sample.controller.Controller;
import sample.controller.CreateGroupController;
import sample.controller.JoinController;
import sample.model.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class ViewManager {

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

    //puylsante invio/foto
    @FXML
    Button buttonSend;


    Controller controller;

    private Stage primaryStage;

    boolean isOn = false;

    // contatto "Principale", ovvero quello selezionato, dunque variabile
    static Chat chat;

    // stage per creare un nuovo contatto o modificarne uno presente
    Stage secondaryStage;



    public ViewManager(){
        controller = new Controller();
        controller.setViewManager(this);
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


        //anche quando premo il tasto invio il messaggio dev'essere inviato
        textFieldMsg.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                try {
                    send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        textFieldMsg.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                if (!s2.isEmpty()){
                    buttonSend.getStyleClass().clear();
                    buttonSend.getStyleClass().add("buttonSend");
                }
                else{
                    buttonSend.getStyleClass().clear();
                    buttonSend.getStyleClass().add("buttonCamera");
                }
            }
        });


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
                    controller.changePort(portInt);
                    System.out.println("porta cambiata: "+portInt);

                    // rendo il bordo del text edit normale
                    textEditError(false);
                }
            }
        });


        vBoxButtonsAdd.setVisible(false);

        // mostro o meno i pulsantini
        EventHandler eventEnter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                vBoxButtonsAdd.setVisible(true);
                buttonNew.removeEventFilter(MouseEvent.MOUSE_ENTERED, this);


            }
        };
        buttonNew.addEventFilter(MouseEvent.MOUSE_ENTERED, eventEnter);

        buttonNew.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                buttonNew.addEventFilter(MouseEvent.MOUSE_ENTERED, eventEnter);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!isOn){
                            vBoxButtonsAdd.setVisible(false);
                        }
                    }
                }).start();

            }
        });


        vBoxButtonsAdd.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isOn = false;
                vBoxButtonsAdd.setVisible(false);
                buttonNew.addEventFilter(MouseEvent.MOUSE_ENTERED, eventEnter);



            }
        });

        vBoxButtonsAdd.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isOn = true;

            }
        });


    }


    //metodo associato al pulsante che permette l'invio del messaggio
    @FXML
    public void send() throws IOException {
        String text = textFieldMsg.getText();
        System.out.println(text);
        if (!text.isEmpty()) {

            controller.sendText(chat.getPortaDestinatario(), chat.getIp(), new Packet(textFieldMsg.getText()));


            // aggiungo al contatto corrente il messaggio
            chat.addMessaggio(new TextMessage(textFieldMsg.getText(), "0", true));

            // lo aggiungo alla view
            drawMessage(true, "labelMsg", textFieldMsg.getText());

            //resetto testo
            textFieldMsg.setText("");

        }

        else{

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.jpeg");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {

                Packet packet = new Packet(selectedFile);
                controller.sendText(chat.getPortaDestinatario(), chat.getIp(), packet);
                chat.addMessaggio(new ImageMessage(selectedFile, "0", true));

                //FIXME: Inserire visualizzazione messaggio
                drawImage(selectedFile, true);


            }
            else {

            }

        }

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

    @FXML
    public void newGroup() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/createGroup.fxml"));

        secondaryStage = new Stage();
        Parent root = loader.load();
        secondaryStage.setTitle("Nuovo Gruppo");
        Scene scene = new Scene(root, 600, 400);
        //scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        secondaryStage.setScene(scene);
        secondaryStage.show();
        secondaryStage.setResizable(false);


        CreateGroupController createGroupController = loader.getController();


    }

    @FXML
    public void joinGroup() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/joinGroup.fxml"));

        secondaryStage = new Stage();
        Parent root = loader.load();
        secondaryStage.setTitle("Entra in un Gruppo");
        Scene scene = new Scene(root, 600, 400);
        //scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        secondaryStage.setScene(scene);
        secondaryStage.show();
        secondaryStage.setResizable(false);


        JoinController joinGroup = loader.getController();
    }


    // clicco su un elemento della lista
    @FXML
    public void listItemClicked(){

        if (!listViewChat.getItems().isEmpty()){
            // mostro la chat e nascondo la label iniziale
            paneToHide.setVisible(true);
            labelStart.setVisible(false);

            // imposto come contatto corrente quello che ho selezionato e persente nella lista
            chat = controller.getContatto(listViewChat.getSelectionModel().getSelectedIndex());
            System.out.println(chat.getIp());
            // carico i messaggi
            reloadMeassages();

            // imposto il nome del contatt in alto
            labelDestIp.setText(chat.getNome());


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

    public void drawImage(File file, boolean sent){

        ImageView imageView = new ImageView();
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(250);
        Pane pane = new Pane();
        pane.getChildren().add(imageView);
        pane.getStyleClass().clear();
        pane.getStyleClass().add("imageMsg");

        HBox hBox = new HBox();
        hBox.getChildren().add(pane);
        if (sent)
            hBox.setAlignment(Pos.BASELINE_RIGHT);
        else
            hBox.setAlignment(Pos.BASELINE_LEFT);

        hBox.setPadding(new Insets(10,5,5,10));

        vBoxDialogo.getChildren().add(hBox);

        // necessario per far scorrere la scroll view automaticamente all'ultimo messaggio,
        // che altrimenti rimarrebbe ferma in cima
        scrollPane.setVvalue(1.0);

    }

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
        newContactController.setContatti(controller.getContatti());
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



    public void update(Chat chat){
        System.out.println(chat);

        // pulisco la lista
        if (!listViewChat.getItems().isEmpty())
            listViewChat.getItems().clear();


        // aggiorno la lista
        controller.addContatto(chat);
        listViewChat.setItems(FXCollections.observableArrayList(controller.getContatti()));
        listViewChat.setCellFactory(studentListView -> new ContattoListCellController());
    }


    // metodo per ricaricare i messaggi nel momento in cui clicco su un altro contatto
    private void reloadMeassages() {

        ArrayList<Message> messaggi = chat.getMessaggi();

        // pulisco dalla vecchia conversazione
        vBoxDialogo.getChildren().clear();

        // per ogni messaggio lo inserisco con uno stile differente se ricevuto o inviato
        for (Message textMessage : messaggi) {
            String style = "labelMsg";
            if (!textMessage.isSent())
                style = "labelMsgReceived";
            drawMessage(textMessage.isSent(), style, ((TextMessage)textMessage).getTesto());
        }

    }

    public Chat getActiveChat(){
        return chat;
    }


    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Controller getController(){
        return controller;
    }


    /*public void bigButton(Button button, double dim){
        double lato = button.getHeight();
        button.setPrefSize(lato+dim, lato+dim);
        button.setLayoutX(button.getLayoutX()-dim/2);
        button.setLayoutY(button.getLayoutY()-dim/2);

    }

    public void smallButton(Button button, double dim){
        double lato = button.getHeight();
        button.setPrefSize(lato-dim, lato-dim);
        button.setLayoutX(button.getLayoutX()+dim/2);
        button.setLayoutY(button.getLayoutY()+dim/2);

    }*/


}
