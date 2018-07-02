package wumpus;

import decisiontree.ID3;
import decisiontree.Line;
import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.awt.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GameSceneController {

    int x;
    int y;
    int width;
    int height;
    int nbParties;
    double squareSizeX;
    double sqaureSizeY;
    double pitRate;
    GameManager gameManager;
    Agent agent;
    ArrayList<Point> lastBestPath = new ArrayList<>();

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

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
    @FXML
    Label win;

    public GameSceneController(){
    }

    // doesn't need to be called "start" any more...
    public void startGame(Stage window,int x,int y,double pitRate,Scene settingsScene, int nbp) throws Exception {
        FXMLLoader gamePaneLoader = new FXMLLoader(getClass().getResource("resources/FXML/game.fxml"));
        this.settingsScene = settingsScene;
        gamePaneLoader.setController(this);
        Parent gamePane = gamePaneLoader.load();
        scene =  new Scene(gamePane, 1300 ,900);
        window.setScene(scene);
        this.window=window;
        this.nbParties = nbp;
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
        initDecisionTree();
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
                            }else{
                                if (gameManager.getMap()[agent.position.x][agent.position.y].getEvents().contains(Cell.Event.gold)) {
                                    end = true;
                                    win.setVisible(true);
                                }
                            }
                            agent.discoverPosition(newNeighbors,gameManager.getPlayerHasAdvanced(),gameManager.agentIsDead(),action);
                            manualAction = Action.hiddle;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }else{
                    try {
                        gameOver();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void gameOver() throws Exception{
        gameover.setVisible(true);
        savebutton.setVisible(true);
        if(nbParties>0){
            GameSceneController newGame = new GameSceneController();
            newGame.startGame(window,x,y,pitRate,settingsScene,nbParties-1);
            nbParties=0;
        }
    }

    public void saveState() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("D:/Documents/Cours/I4/ProjetIAGauthierAdrien/src/wumpus/training/train.txt", true));

        PlayerData player = PlayerData.getInstance();
        for(String s : player.getPlayerData()) {
            writer.append(System.lineSeparator());
            writer.append(s);
        }
        writer.close();
    }

    public void returnToSettings(Event event){
        window.setScene(settingsScene);
    }

    private ArrayList<Point> searchShortPath(Cell[][] map){
        Cell[][] mapCells = map;
        ArrayList<Point> result2 = new ArrayList<>();
        for(int i = 1; i < mapCells.length-1; i++) {
            for (int j = 1; j < mapCells[i].length-1; j++) {
                for (Cell.Event event : mapCells[i][j].getEvents()) {
                    if (event == Cell.Event.gold) {
                        result2 = AlgoA.getSolution(x+1, y+1, agent.position.x, agent.position.y, i, j, gameManager.getDangerousCells());
                    }
                }
            }
        }
        return result2;
    }

    public void showBestChemin(){
        ArrayList<Point> solution = searchShortPath(gameManager.getMap());
        lastBestPath = solution;
        for (Point point : solution){
            System.out.println(point.getLocation());
            gameManager.getMap()[point.x][point.y].setBorder(new Border(new BorderStroke(Color.GREEN,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 2, 2, 2, false, false, false, false))));
        }
    }

    public void resetPath(){
        for (Point point : lastBestPath){
            gameManager.getMap()[point.x][point.y].setBorder(null);
        }
    }

    public void initDecisionTree(){
        Line[] totalFact = new Line[9];
        totalFact[0] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Player, decisiontree.Cell.Smell, decisiontree.Cell.Smell, false);
        totalFact[1] =  new Line(decisiontree.Cell.Empty, decisiontree.Cell.Player, decisiontree.Cell.Empty, decisiontree.Cell.Empty, false);
        totalFact[2] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Smell, decisiontree.Cell.Player, decisiontree.Cell.Empty, false);
        totalFact[3] =  new Line(decisiontree.Cell.Unknown, decisiontree.Cell.Unknown, decisiontree.Cell.Player, decisiontree.Cell.Empty, true);
        totalFact[4] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Unknown, decisiontree.Cell.Smell, decisiontree.Cell.Player, true);
        totalFact[5] =  new Line(decisiontree.Cell.Empty, decisiontree.Cell.Player, decisiontree.Cell.Smell, decisiontree.Cell.Wind, true);
        totalFact[6] =  new Line(decisiontree.Cell.Unknown, decisiontree.Cell.Empty, decisiontree.Cell.Player, decisiontree.Cell.Empty, false);
        totalFact[7] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Unknown, decisiontree.Cell.Player, decisiontree.Cell.Player, false);
        totalFact[8] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Player, decisiontree.Cell.Smell, decisiontree.Cell.Empty, true);
        agent.setDecision(new ID3(totalFact));

    }
}
