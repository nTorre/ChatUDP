package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.controller.view.ViewManager;
import sample.model.Chat;

import javax.swing.event.ChangeListener;

public class CreateGroupController {

    @FXML
    TextField textFieldIpCr;

    @FXML
    TextField  groupNameCr;

    private Chat chat;
    private Stage stage;
    ViewManager viewManager;

    public CreateGroupController(){

    }

    public void setChat(Chat chat){
        this.chat = chat;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @FXML
    public void createGr(){
        String ip = textFieldIpCr.getText();
        if (!ip.isEmpty()){

            chat.setIp(ip);
            chat.setNome(groupNameCr.getText());

            stage.close();
            viewManager.update(chat);

        }
    }

}
