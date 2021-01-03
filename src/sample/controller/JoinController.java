package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class JoinController {

    @FXML
    TextField textFieldIpJoin;

    public JoinController(){

    }


    @FXML
    public void joinGr(){
        String ip = textFieldIpJoin.getText();
        if (!ip.isEmpty()){

        }
    }
}
