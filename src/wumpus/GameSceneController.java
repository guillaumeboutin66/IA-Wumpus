package wumpus;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GameSceneController {

    int x;
    int y;
    int width;
    int height;
    double squareSizeX;
    double sqaureSizeY;
    double pitRate;
    GameManager gameManager;
    Agent agent;
    private Scene scene;
    private Stage window;
    Boolean pause = false;
    Boolean end = false;
    Action manualAction = Action.hiddle;
    Action finalAction = Action.hiddle;

    private Scene settingsScene;

    @FXML
    Label gameover;
    @FXML
    Button savebutton;
    @FXML
    AnchorPane reality;
    @FXML
    AnchorPane expectation;
    @FXML
    RadioButton auto;
    @FXML
    RadioButton manuel;

    public GameSceneController(){
    }

    // doesn't need to be called "start" any more...
    public void startGame(Stage window,int x,int y,double pitRate,Scene settingsScene) throws Exception {
        FXMLLoader gamePaneLoader = new FXMLLoader(getClass().getResource("resources/FXML/game.fxml"));
        this.settingsScene = settingsScene;
        gamePaneLoader.setController(this);
        Parent gamePane = gamePaneLoader.load();
        Scene scene =  new Scene(gamePane, 1300 ,900);
        window.setScene(scene);
        this.window=window;
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case Q:
                    manualAction = Action.left;
                    break;
                case S:
                    manualAction = Action.bottom;
                    break;
                case D:
                    manualAction = Action.right;
                    break;
                case Z:
                    manualAction = Action.up;
                    break;
            }
        });
        window.show();
        initItems(x,y,pitRate);
        initMap();
        initPlayerMap();
        new AnimationTimer(){
            @Override
            public void handle(long currentNanoTime){
                if(!end) {
                    try {
                        Action action = Action.hiddle;
                        if(auto.isSelected()) {
                            action = agent.takeDecision();
                            Thread.sleep(1000);
                        }else if(manuel.isSelected()){
                            action = manualAction;
                        }
                        if (action == Action.takeGold) {
                            end = true;//gold is taken
                        } else {
                            Cell[] newNeighbors = gameManager.computeNewPosition(action);
                            if (gameManager.agentIsDead()) {
                                end = true;//agent is dead
                                finalAction = action;
                            }
                            agent.discoverPosition(newNeighbors);
                            manualAction = Action.hiddle;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }else{
                        gameOver();
                }
            }
        }.start();

    }

    public void initItems(int x,int y, double pitRate){
        this.x=x;
        this.y=y;
        this.pitRate=pitRate;
        this.width=(int)reality.getWidth();
        this.height=(int)reality.getHeight();
        squareSizeX=width/(x+2);
        sqaureSizeY=height/(y+2);
        gameManager = new GameManager(x,y,squareSizeX,sqaureSizeY,pitRate);
        agent = new Agent(gameManager.getMap()[1][y],x,y,squareSizeX,sqaureSizeY);

    }

    public void initMap(){
        for (int y = 0; y < gameManager.getMap()[0].length; y++) {
            for (int x = 0; x < gameManager.getMap().length; x++) {
                reality.getChildren().add(gameManager.getMap()[x][y]);
            }
        }
    }
    public void initPlayerMap(){
        Cell [][] playerMap = agent.getPlayerMap();
        for (int y = 0; y < playerMap[0].length; y++) {
            for (int x = 0; x < playerMap.length; x++) {
                expectation.getChildren().add(playerMap[x][y]);
            }
        }
    }

    public void gameOver(){
        gameover.setVisible(true);
        savebutton.setVisible(true);
    }

    public void saveState() throws IOException{
        String str = gameManager.getPlayerPath().get(gameManager.getPlayerPath().size()-2).toString();
        String finaleAction = finalAction.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter("D:/Documents/Cours/I4/ProjetIAGauthierAdrien/src/wumpus/training/train.txt", true));
        writer.append(System.lineSeparator());
        writer.append(str);
        writer.append(" ");
        writer.append(finaleAction);

        writer.close();
    }

    public void returnToSettings(Event event){
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(settingsScene);
    }
}
