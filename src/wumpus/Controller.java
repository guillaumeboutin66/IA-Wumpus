package wumpus;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class Controller {
    @FXML
    private Button newgame;

    private Scene secondScene;

    private Controller2 secondController;

    public void setSecondScene(Scene scene) {
        secondScene = scene;
    }

    public void setsecondController(Controller2 ontroller){
        secondController = ontroller;
    }


    public void initialize() {

    }
    public void launchSettings(Event event) {
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setScene(secondScene);
        secondController.initialize();
        String ssound = "src/wumpus/resources/sound/sound.mp3";
        Media sound = new Media(new File(ssound).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}
