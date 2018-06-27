/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author azuron
 */
public class GameManager {
    private Cell[][] map;
    private Point agentPosition;
    int mapWidth;
    int mapHeight;
    int usableHeight;
    int usableWidth;
    String orientation = "down";
    ArrayList<Cell> playerPath = new ArrayList<>();
    
    
    public GameManager(int width, int height,double sizeX,double sizeY,double wellRate){
        mapWidth = width+2;
        mapHeight = height+2;
        usableHeight = height;
        usableWidth = width;
        initMap(mapWidth, mapHeight,sizeX,sizeY,wellRate);
    }
    
    public Boolean agentIsDead(){
        return map[agentPosition.x][agentPosition.y].isDangerous();
    }
    
    public Cell[] computeNewPosition(Action action){
        Cell previousCell = map[agentPosition.x][agentPosition.y];
        Point nextPosition = new Point(0,0);
        switch(action){
            case up:
                nextPosition = new Point(agentPosition.x, agentPosition.y - 1);
                orientation="up";
                break;
            case right:
                nextPosition = new Point(agentPosition.x + 1, agentPosition.y);
                orientation="right";
                break;
            case bottom:
                nextPosition = new Point(agentPosition.x, agentPosition.y + 1);
                orientation="down";
                break;
            case left:
                nextPosition = new Point(agentPosition.x - 1, agentPosition.y);
                orientation="left";
                break;
            case hiddle:
                nextPosition = new Point(agentPosition.x,agentPosition.y);
        }

        if(!map[nextPosition.x][nextPosition.y].getEvents().contains(Cell.Event.wall)){
            agentPosition = new Point(nextPosition.x,nextPosition.y);
        }else{
            map[nextPosition.x][nextPosition.y].setCollision(true);
        }

        upadtePlayer(previousCell,map[agentPosition.x][agentPosition.y],orientation);
        
        Cell[] cells = new Cell[5];
        cells[0] = map[agentPosition.x][agentPosition.y]; //nouvelle position
        cells[1] = map[agentPosition.x][agentPosition.y - 1];//up
        cells[2] = map[agentPosition.x + 1][agentPosition.y];//right
        cells[3] = map[agentPosition.x][agentPosition.y + 1];//bottom
        cells[4] = map[agentPosition.x - 1][agentPosition.y];//left
        
        return cells;
    }

    public void upadtePlayer(Cell previousCell,Cell currentCell,String s){

        if(previousCell!=currentCell) {
            currentCell.addEvent(Cell.Event.agent);
            previousCell.removeEvent(Cell.Event.agent);
            currentCell.setStyle("-fx-background-image:url(\"wumpus/resources/player_" + s + ".png\");-fx-background-size: " + currentCell.sizeX + " " + currentCell.sizeY +";");
            previousCell.setOriginalStyle();
            addToPath(currentCell);
        }else{
            currentCell.setStyle("-fx-background-image:url(\"wumpus/resources/player_" + s + ".png\");-fx-background-size: " + currentCell.sizeX + " " + currentCell.sizeY +";");
        }
    }

    public void initMap(int width, int height,double sizeX,double sizeY,double wellRate) {

        ArrayList<Point> lockedPoints = new ArrayList<>();
        ArrayList<Point> dangerousPoints = new ArrayList<>();
        wellRate /= 100;
        map = new Cell[width][height];

        // Fill the rest of the Map of normal map
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++) {
                if(map[i][j] == null){
                    map[i][j] = new Cell(i,j,sizeX,sizeY);
                }
            }
        }

        //Fill the borders
        for(int i = 0;i < width;i++){
            for(int j = 0; j< height;j++) {
                if(i==0||i==width-1||j==0||j==height-1) {
                    map[i][j].addEvent(Cell.Event.wall);
                    map[i][j].setStyle("-fx-background-image:url(\"wumpus/resources/rock.png\");-fx-background-size: " + map[i][j].sizeX + " " + map[i][j].sizeY +";");
                    lockedPoints.add(new Point(i,j));
                }
            }
        }

        //Agent
        agentPosition = new Point(1, usableHeight);
        map[1][usableHeight].addEvent(Cell.Event.agent);
        map[1][usableHeight].setStyle("-fx-background-image:url(\"wumpus/resources/player_down.png\");-fx-background-size: " + map[1][usableHeight].sizeX + " " + map[1][usableHeight].sizeY +";");
        lockedPoints.add(agentPosition);

        // Wumpus
        Point posWumpus = generatePoint(usableWidth, usableHeight, lockedPoints);
        map[posWumpus.x][posWumpus.y].addEvent(Cell.Event.wumpus);
        map[posWumpus.x][posWumpus.y].setStyle("-fx-background-image:url(\"wumpus/resources/miniwumpus.png\");-fx-background-size: " + map[posWumpus.x][posWumpus.y].sizeX + " " + map[posWumpus.x][posWumpus.y].sizeY +";");
        map[posWumpus.x][posWumpus.y].setOriginalSprite("-fx-background-image:url(\"wumpus/resources/miniwumpus.png\");-fx-background-size: " + map[posWumpus.x][posWumpus.y].sizeX + " " + map[posWumpus.x][posWumpus.y].sizeY +";");
        lockedPoints.add(posWumpus);
        dangerousPoints.add(posWumpus);

        // Gold
        Point posGold = generatePoint(usableWidth, usableHeight, lockedPoints);
        map[posGold.x][posGold.y].addEvent(Cell.Event.gold);
        map[posGold.x][posGold.y].setStyle("-fx-background-image:url(\"wumpus/resources/gold.png\");-fx-background-size: " + map[posGold.x][posGold.y].sizeX + " " + map[posGold.x][posGold.y].sizeY +";");
        map[posGold.x][posGold.y].setOriginalSprite("-fx-background-image:url(\"wumpus/resources/gold.png\");-fx-background-size: " + map[posGold.x][posGold.y].sizeX + " " + map[posGold.x][posGold.y].sizeY +";");
        lockedPoints.add(posGold);

        // Pit
        // RANDOM INT BETWEEN 1 & 20% of the number of map
        int maxNumberOfPits = (int) Math.round(usableWidth*usableHeight*wellRate);
        int randomNumberOfPits = generateRandom(maxNumberOfPits);
        for(int i = 0; i < randomNumberOfPits; i++){
            Point posPit = generatePoint(usableWidth, usableHeight, lockedPoints);
            map[posPit.x][posPit.y].addEvent(Cell.Event.pit);
            map[posPit.x][posPit.y].setStyle("-fx-background-image:url(\"wumpus/resources/trou.png\");-fx-background-size: " + map[posPit.x][posPit.y].sizeX + " " + map[posPit.x][posPit.y].sizeY +";");
            map[posPit.x][posPit.y].setOriginalSprite("-fx-background-image:url(\"wumpus/resources/trou.png\");-fx-background-size: " + map[posPit.x][posPit.y].sizeX + " " + map[posPit.x][posPit.y].sizeY +";");
            lockedPoints.add(posPit);
            dangerousPoints.add(posPit);
        }

        // Fill the Map of side event map
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (map[i][j].getEvents().contains(Cell.Event.wumpus)) {
                    addAdjacentEvent(map[i][j], Cell.Event.smell);
            }
                if (map[i][j].getEvents().contains(Cell.Event.pit)) {
                    addAdjacentEvent(map[i][j], Cell.Event.wind);
                }
            }
        }
    }

    private Point generatePoint(int maxWidth, int maxLength, ArrayList<Point> avoidPoints){
        int x = generateRandom(maxWidth);
        int y = generateRandom(maxLength);

        boolean alreadyExist = false;

        for (Point avoidPoint: avoidPoints) {
            if(avoidPoint.x == x && avoidPoint.y == y){
                alreadyExist = true;
            }
        }

        if(alreadyExist){
            return generatePoint(maxWidth,maxLength, avoidPoints);
        } else {
            return new Point(x,y);
        }

    }

    private int generateRandom(int Max){
        return (int) Math.round((Math.random() * (Max - 1)));
    }

    private void addAdjacentEvent(Cell cell, Cell.Event event){
        if(cell.getPosition().x > 1 && checkIfSideEventExistOnCell(map[cell.getPosition().x-1][cell.getPosition().y], event)){
            map[cell.getPosition().x-1][cell.getPosition().y].addEvent(event);
            addPictureAdjacent(map[cell.getPosition().x-1][cell.getPosition().y]);
        }
        if(cell.getPosition().y > 1 && checkIfSideEventExistOnCell(map[cell.getPosition().x][cell.getPosition().y-1], event)){
            map[cell.getPosition().x][cell.getPosition().y-1].addEvent(event);
            addPictureAdjacent(map[cell.getPosition().x][cell.getPosition().y-1]);
        }
        if(cell.getPosition().x < mapWidth-2 && checkIfSideEventExistOnCell(map[cell.getPosition().x+1][cell.getPosition().y], event)){
            map[cell.getPosition().x+1][cell.getPosition().y].addEvent(event);
            addPictureAdjacent(map[cell.getPosition().x+1][cell.getPosition().y]);
        }
        if(cell.getPosition().y < mapHeight-2 && checkIfSideEventExistOnCell(map[cell.getPosition().x][cell.getPosition().y+1], event)){
            map[cell.getPosition().x][cell.getPosition().y+1].addEvent(event);
            addPictureAdjacent(map[cell.getPosition().x][cell.getPosition().y+1]);
        }
    }

    private void addPictureAdjacent(Cell cell){
        if(!(cell.getEvents().contains(Cell.Event.agent)||cell.getEvents().contains(Cell.Event.wumpus)||cell.getEvents().contains(Cell.Event.gold))) {
            if (cell.getEvents().contains(Cell.Event.wind) && cell.getEvents().contains(Cell.Event.smell)) {
                cell.setStyle("-fx-background-image:url(\"wumpus/resources/smellwind.png\");-fx-background-size: " + cell.sizeX + " " + cell.sizeY +";");
                cell.setOriginalSprite("-fx-background-image:url(\"wumpus/resources/smellwind.png\");-fx-background-size: " + cell.sizeX + " " + cell.sizeY +";");
            } else if (cell.getEvents().contains(Cell.Event.wind)) {
                cell.setStyle("-fx-background-image:url(\"wumpus/resources/wind.png\");-fx-background-size: cover;");
                cell.setOriginalSprite("-fx-background-image:url(\"wumpus/resources/wind.png\");-fx-background-size: " + cell.sizeX + " " + cell.sizeY +";");
            } else if (cell.getEvents().contains(Cell.Event.smell)) {
                cell.setStyle("-fx-background-image:url(\"wumpus/resources/smell.png\");-fx-background-size: cover;");
                cell.setOriginalSprite("-fx-background-image:url(\"wumpus/resources/smell.png\");-fx-background-size: " + cell.sizeX + " " + cell.sizeY +";");
            }
        }
    }

    private boolean checkIfSideEventExistOnCell(Cell cell, Cell.Event event){
        //Check pit, because we won't put the gold or the Wumpus on that cell (or event wind or smell)
        return (!cell.getEvents().contains(event) && !cell.getEvents().contains(Cell.Event.pit));
    }

    public Cell[][] getMap(){
        return this.map;
    }

    public void addToPath(Cell cell){
        this.playerPath.add(cell);
    }

    public ArrayList<Cell> getPlayerPath(){
        return playerPath;
    }

}
