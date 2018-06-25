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
    
    
    public GameManager(int width, int height,double sizeX,double sizeY,double wellRate){
        mapWidth = width+2;
        mapHeight = height+2;
        usableHeight = height;
        usableWidth = width;
        initMap(mapWidth, mapHeight,sizeX,sizeY,wellRate);
        System.out.println("Width = " + mapWidth);
        System.out.println("height = " + mapHeight);
        System.out.println("usableW = " + usableWidth);
        System.out.println("usableH= " + usableHeight);
    }
    
    public Boolean agentIsDead(){
        return map[agentPosition.x][agentPosition.y].isDangerous();
    }
    
    public Cell[] computeNewPosition(Action action){
        System.out.println("je fais une action" + agentPosition.x + " " + agentPosition.y);
        Cell previousCell = map[agentPosition.x][agentPosition.y];
        switch(action){
            case up:
                agentPosition = new Point(agentPosition.x, agentPosition.y - 1);
                break;
            case right:
                agentPosition = new Point(agentPosition.x + 1, agentPosition.y);
                break;
            case bottom:
                agentPosition = new Point(agentPosition.x, agentPosition.y + 1);
                break;
            case left:
                agentPosition = new Point(agentPosition.x - 1, agentPosition.y);
                break;
        }

        upadtePlayer(previousCell,map[agentPosition.x][agentPosition.y]);
        
        Cell[] cells = new Cell[5];
        System.out.println("noubelle position : " + agentPosition.x + " " + agentPosition.y);
        cells[0] = map[agentPosition.x][agentPosition.y]; //nouvelle position
        cells[1] = map[agentPosition.x][agentPosition.y - 1];//up
        cells[2] = map[agentPosition.x + 1][agentPosition.y];//right
        cells[3] = map[agentPosition.x][agentPosition.y + 1];//bottom
        cells[4] = map[agentPosition.x - 1][agentPosition.y];//left
        
        return cells;
    }

    public void upadtePlayer(Cell previousCell,Cell currentCell){

        currentCell.addEvent(Cell.Event.agent);
        previousCell.removeEvent(Cell.Event.agent);
        currentCell.setStyle("-fx-background-image:url(\"wumpus/resources/player.png\");");
        previousCell.setOriginalStyle();
    }

    public void initMap(int width, int height,double sizeX,double sizeY,double wellRate) {

        ArrayList<Point> lockedPoints = new ArrayList<>();
        ArrayList<Point> dangerousPoints = new ArrayList<>();
        wellRate /= 100;
        System.out.println(wellRate);
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
                    map[i][j].setStyle("-fx-background-image:url(\"wumpus/resources/rock.png\");");
                    lockedPoints.add(new Point(i,j));
                }
            }
        }

        //Agent
        agentPosition = new Point(1, usableHeight);
        System.out.println("initialisation agent dans la map" + agentPosition.y);
        map[1][usableHeight].addEvent(Cell.Event.agent);
        map[1][usableHeight].setStyle("-fx-background-image:url(\"wumpus/resources/player.png\")");
        lockedPoints.add(agentPosition);

        // Wumpus
        Point posWumpus = generatePoint(usableWidth, usableHeight, lockedPoints);
        map[posWumpus.x][posWumpus.y].addEvent(Cell.Event.wumpus);
        map[posWumpus.x][posWumpus.y].setStyle("-fx-background-image:url(\"wumpus/resources/miniwumpus.png\")");
        map[posWumpus.x][posWumpus.y].setOriginalSprite("-fx-background-image:url(\"wumpus/resources/miniwumpus.png\")");
        lockedPoints.add(posWumpus);
        dangerousPoints.add(posWumpus);

        // Gold
        Point posGold = generatePoint(usableWidth, usableHeight, lockedPoints);
        map[posGold.x][posGold.y].addEvent(Cell.Event.gold);
        map[posGold.x][posGold.y].setStyle("-fx-background-image:url(\"wumpus/resources/gold.png\")");
        map[posGold.x][posGold.y].setOriginalSprite("-fx-background-image:url(\"wumpus/resources/gold.png\")");
        lockedPoints.add(posGold);

        // Pit
        // RANDOM INT BETWEEN 1 & 20% of the number of map
        int maxNumberOfPits = (int) Math.round(usableWidth*usableHeight*wellRate);
        int randomNumberOfPits = generateRandom(maxNumberOfPits);
        for(int i = 0; i < randomNumberOfPits; i++){
            Point posPit = generatePoint(usableWidth, usableHeight, lockedPoints);
            map[posPit.x][posPit.y].addEvent(Cell.Event.pit);
            map[posPit.x][posPit.y].setStyle("-fx-background-image:url(\"wumpus/resources/trou.png\")");
            map[posPit.x][posPit.y].setOriginalSprite("-fx-background-image:url(\"wumpus/resources/trou.png\")");
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
                cell.setStyle("-fx-background-image:url(\"wumpus/resources/smellwind.png\")");
                cell.setOriginalSprite("-fx-background-image:url(\"wumpus/resources/smellwind.png\")");
            } else if (cell.getEvents().contains(Cell.Event.wind)) {
                cell.setStyle("-fx-background-image:url(\"wumpus/resources/wind.png\")");
                cell.setOriginalSprite("-fx-background-image:url(\"wumpus/resources/wind.png\")");
            } else if (cell.getEvents().contains(Cell.Event.smell)) {
                cell.setStyle("-fx-background-image:url(\"wumpus/resources/smell.png\")");
                cell.setOriginalSprite("-fx-background-image:url(\"wumpus/resources/smell.png\")");
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

}
