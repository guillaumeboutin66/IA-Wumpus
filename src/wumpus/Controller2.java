package wumpus;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller2 {
    @FXML
    private TextField hauteur;
    @FXML
    private TextField largeur;
    @FXML
    private Slider puits;
    @FXML
    private Label wellrate;

    private Scene firstscene;

    private GameScene gamescene ;

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
            gamescene = new GameScene();
            Stage scene = (Stage) ((Node) event.getSource()).getScene().getWindow();
            gamescene.startGame(scene,Integer.parseInt(largeur.getText()),Integer.parseInt(hauteur.getText()),puits.getValue());
        }catch (java.lang.Exception e){
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
