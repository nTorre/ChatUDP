package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.controller.Controller;

import java.time.LocalDate;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/sample.fxml"));


        Parent root = loader.load();
        primaryStage.setTitle("Torre Chat");
        Scene scene = new Scene(root, 800, 550);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);

        Controller controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                controller.stop();
                controller.getReceiver().closeSocket();

            }
        });

    }


    public static void main(String[] args) {
        launch(args);
    }
}
