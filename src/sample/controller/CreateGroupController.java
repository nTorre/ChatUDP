package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateGroupController {

    @FXML
    TextField textFieldIpCr;

    public CreateGroupController(){

    }

    @FXML
    public void createGr(){
        String ip = textFieldIpCr.getText();
        if (!ip.isEmpty()){

        }
    }

}
