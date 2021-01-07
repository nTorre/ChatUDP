package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.controller.view.ViewManager;
import sample.model.Chat;

public class JoinController {

    private Chat chat;

    @FXML
    TextField textFieldIpJoin;

    @FXML
    TextField groupName;

    private Stage stage;
    private ViewManager viewManager;

    public JoinController(){

    }

    public void setChat(Chat chat){
        this.chat = chat;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setViewManager(ViewManager viewManager){
        this.viewManager = viewManager;
    }

    @FXML
    public void joinGr(){
        String ip = textFieldIpJoin.getText();
        if (!ip.isEmpty()){
            chat.setIp(ip);
            chat.setNome(groupName.getText());
            viewManager.update(chat);

            stage.close();

        }
    }
}
