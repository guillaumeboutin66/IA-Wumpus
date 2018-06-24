package wumpus;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameScene {

    int x;
    int y;
    int width;
    int height;
    double squareSizeX;
    double sqaureSizeY;
    double pitRate;
    GameManager gameManager;
    private Scene scene;
    @FXML
    AnchorPane reality;

    public GameScene(){
    }

    public void startLaFete() {
        System.out.println(squareSizeX + " " + sqaureSizeY);
    }

    // doesn't need to be called "start" any more...
    public void startGame(Stage window,int x,int y,double pitRate) throws Exception {
        FXMLLoader gamePaneLoader = new FXMLLoader(getClass().getResource("resources/FXML/game.fxml"));
        gamePaneLoader.setController(this);
        Parent gamePane = gamePaneLoader.load();
        Scene scene =  new Scene(gamePane, 1300 ,900);
        window.setScene(scene);
        window.show();
        this.x=x;
        this.y=y;
        this.pitRate=pitRate;
        this.width=(int)reality.getWidth();
        this.height=(int)reality.getHeight();
        squareSizeX=width/(x+2);
        sqaureSizeY=height/(y+2);
        gameManager = new GameManager(x,y,squareSizeX,sqaureSizeY,pitRate);
        initMap();
    }
    public void initMap(){
        for (int y = 0; y < gameManager.getMap()[0].length; y++) {
            for (int x = 0; x < gameManager.getMap().length; x++) {
                reality.getChildren().add(gameManager.getMap()[x][y]);
            }
        }
    }
}
