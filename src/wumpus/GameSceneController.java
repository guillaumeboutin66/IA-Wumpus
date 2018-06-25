package wumpus;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
        this.window=window;
        window.show();
        initItems(x,y,pitRate);
        initMap();
        initPlayerMap();
        new AnimationTimer(){
            @Override
            public void handle(long currentNanoTime){
                try {
                    Action action = agent.takeDecision();
                    if (action == Action.takeGold) {
                        end = true;//gold is taken
                    } else {
                        Cell[] newNeighbors = gameManager.computeNewPosition(action);
                        for (int i = 0; i < x + 2; i++) {
                            for (int j = 0; j < y + 2; j++) {
                                System.out.println(agent.knowncells[i][j]);
                            }
                        }
                        if (gameManager.agentIsDead()) {
                            end = true;//agent is dead
                        }
                        agent.discoverPosition(newNeighbors);
                        Thread.sleep(1000);
                    }
                }catch (Exception e){
                    //do something
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

}
