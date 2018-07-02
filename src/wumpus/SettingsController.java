package wumpus;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    private TextField hauteur;
    @FXML
    private TextField largeur;
    @FXML
    private Slider puits;
    @FXML
    private TextField nbp;
    @FXML
    private Label wellrate;

    private Scene firstscene;

    private GameSceneController gamescene ;

    public void setFirstScene(Scene scene) {
        firstscene = scene;
    }


    public void initialize() {
        wellrate.setText(Double.toString(puits.getValue()));
        puits.valueProperty().addListener((observable, oldValue, newValue) -> {
            wellrate.setText(Double.toString(newValue.intValue()));
        });
    }

    public void launchGame(Event event)throws Exception{
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                gamescene = new GameSceneController();
                gamescene.startGame(stage, Integer.parseInt(largeur.getText()), Integer.parseInt(hauteur.getText()), puits.getValue(), wellrate.getScene(), Integer.parseInt(nbp.getText()) - 1);
        }catch (java.lang.Exception e){
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
