/**
 * Classe per il controllo del singolo item della list view personalizzata
 *
 * Alcune label non vengono utilizzate, in quanto non memorizzo ora del messaggio e non mostro l'ultimo arrivato,
 * tuttavia assolutamente fattibile
 */

package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import sample.Main;
import sample.model.Contatto;

import java.io.IOException;

public class ContattoListCellController extends ListCell<Contatto> {

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

    private FXMLLoader mLLoader;


    // metodo della classe madre, necessaria per applicare le modifiche degli item
    @Override
    protected void updateItem(Contatto contatto, boolean empty) {
        super.updateItem(contatto, empty);

        if(empty || contatto == null) {

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
            String name = contatto.getNome();
            if (name.length()>22)
                name = name.substring(0,22)+"...";
            labelName.setText(name);

            labelIpPorta.setText(contatto.getIp()+": "+contatto.getPortaDestinatario());
            if (contatto.getLastReceived()!=null) {
                labelLastMess.setText(contatto.getLastReceived().getTesto());
                labelTime.setText(contatto.getLastReceived().getOrario());
            }


            setText(null);
            // applico le modifiche
            setGraphic(root);
        }

    }

}
