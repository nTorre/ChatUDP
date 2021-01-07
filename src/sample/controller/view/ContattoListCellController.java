/**
 * Classe per il controllo del singolo item della list view personalizzata
 *
 * Alcune label non vengono utilizzate, in quanto non memorizzo ora del messaggio e non mostro l'ultimo arrivato,
 * tuttavia assolutamente fattibile
 */

package sample.controller.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sample.Main;
import sample.model.Chat;

import java.io.File;
import java.io.IOException;

public class ContattoListCellController extends ListCell<Chat> {

    @FXML
    Label labelName;

    @FXML
    Label labelIpPorta;

    @FXML
    Label labelLastMess;

    @FXML
    Label labelTime;

    @FXML
    AnchorPane root;

    @FXML
    ImageView imageProfile;

    private FXMLLoader mLLoader;


    // metodo della classe madre, necessaria per applicare le modifiche degli item
    @Override
    protected void updateItem(Chat chat, boolean empty) {
        super.updateItem(chat, empty);

        if(empty || chat == null) {

            // metodi utilizzati di default
            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                // carico il file fxml che verrà applicato ad ogni item
                mLLoader = new FXMLLoader(Main.class.getResource("view/contattoListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // se il nome è troppo lungo lo taglio
            String name = chat.getNome();
            if (name.length()>22)
                name = name.substring(0,22)+"...";
            labelName.setText(name);

            labelIpPorta.setText(chat.getIp()+": "+ chat.getPortaDestinatario());
            if (chat.getLastReceived()!=null) {
                labelLastMess.setText(chat.getLastReceived().getTesto());
                labelTime.setText(chat.getLastReceived().getTimestamp());
            }

            File file = new File("src/sample/icons/addcontatto.png");

            if(!chat.getType().equals("p2p")){
                file = new File("src/sample/icons/multicast.png");
            }

            Image image = new Image(file.toURI().toString());
            imageProfile.setImage(image);


            setText(null);
            // applico le modifiche
            setGraphic(root);
        }

    }

}
