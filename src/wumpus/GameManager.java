/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

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
    
    
    public GameManager(int width, int height,double wellRate){
        mapWidth = width+2;
        mapHeight = height+2;
        usableHeight = height;
        usableWidth = width;
        initMap(mapWidth, mapHeight,wellRate);
        System.out.println("Width = " + mapWidth);
        System.out.println("height = " + mapHeight);
        System.out.println("usableW = " + usableWidth);
        System.out.println("usableH= " + usableHeight);
        display();
    }
    
    public Boolean agentIsDead(){
        return map[agentPosition.x][agentPosition.y].isDangerous();
    }
    
    public Cell[] computeNewPosition(Action action){
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
        
        Cell[] cells = new Cell[3];
        cells[0] = map[agentPosition.x][agentPosition.y - 1];//up
        cells[1] = map[agentPosition.x + 1][agentPosition.y];//right
        cells[2] = map[agentPosition.x][agentPosition.y + 1];//bottom
        cells[3] = map[agentPosition.x - 1][agentPosition.y];//left
        
        return cells;
    }

    public void initMap(int width, int height,double wellRate) {

        ArrayList<Point> lockedPoints = new ArrayList<>();
        ArrayList<Point> dangerousPoints = new ArrayList<>();
        wellRate /= 100;
        System.out.println(wellRate);
        map = new Cell[width][height];

        // Fill the rest of the Map of normal map
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++) {
                if(map[i][j] == null){
                    map[i][j] = new Cell(i,j);
                }
            }
        }

        //Fill the borders
        for(int i = 0;i < width;i++){
            for(int j = 0; j< height;j++) {
                if(i==0) {
                    map[i][j].addEvent(Cell.Event.wall);
                    System.out.println(i+ " " + j);
                    lockedPoints.add(new Point(i,j));
                }
                if(i==width-1){
                    map[i][j].addEvent(Cell.Event.wall);
                    System.out.println(i+ " " + j);
                    lockedPoints.add(new Point(i,j));
                }
                if(j==0&&i!=0&&i!=width-1){
                    map[i][j].addEvent(Cell.Event.wall);
                    System.out.println(i+ " " + j);
                    lockedPoints.add(new Point(i,j));
                }
                if(j==height-1&&i!=0&&i!=width-1){
                    map[i][j].addEvent(Cell.Event.wall);
                    System.out.println(i+ " " + j);
                    lockedPoints.add(new Point(i,j));
                }
            }
        }

        //Agent
        agentPosition = new Point(1, height-1);
        map[1][usableHeight].addEvent(Cell.Event.agent);
        lockedPoints.add(agentPosition);

        // Wumpus
        Point posWumpus = generatePoint(usableWidth, usableHeight, lockedPoints);
        map[posWumpus.x][posWumpus.y].addEvent(Cell.Event.wumpus);
        lockedPoints.add(posWumpus);
        dangerousPoints.add(posWumpus);

        // Gold
        Point posGold = generatePoint(usableWidth, usableHeight, lockedPoints);
        map[posGold.x][posGold.y].addEvent(Cell.Event.gold);
        lockedPoints.add(posGold);

        // Pit
        // RANDOM INT BETWEEN 1 & 20% of the number of map
        int maxNumberOfPits = (int) Math.round(width*height*wellRate);
        int randomNumberOfPits = generateRandom(maxNumberOfPits);
        for(int i = 0; i < randomNumberOfPits; i++){
            Point posPit = generatePoint(usableWidth, usableHeight, lockedPoints);
            map[posPit.x][posPit.y].addEvent(Cell.Event.pit);
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
        }
        if(cell.getPosition().y > 1 && checkIfSideEventExistOnCell(map[cell.getPosition().x][cell.getPosition().y-1], event)){
            map[cell.getPosition().x][cell.getPosition().y-1].addEvent(event);
        }
        if(cell.getPosition().x < mapWidth-2 && checkIfSideEventExistOnCell(map[cell.getPosition().x+1][cell.getPosition().y], event)){
            map[cell.getPosition().x+1][cell.getPosition().y].addEvent(event);
        }
        if(cell.getPosition().y < mapHeight-2 && checkIfSideEventExistOnCell(map[cell.getPosition().x][cell.getPosition().y+1], event)){
            map[cell.getPosition().x][cell.getPosition().y+1].addEvent(event);
        }
    }

    private boolean checkIfSideEventExistOnCell(Cell cell, Cell.Event event){
        //Check pit, because we won't put the gold or the Wumpus on that cell (or event wind or smell)
        return (!cell.getEvents().contains(event) && !cell.getEvents().contains(Cell.Event.pit));
    }

    public void display(){

        Cell[][] mycells = map;

        for(int i = 0; i < mycells.length; i++) {
            for (int j = 0; j < mycells[i].length; j++) {
                String dangers = "";
                for (Cell.Event event :mycells[j][i].getEvents()) {
                    if (mycells[j][i].getEvents().contains(Cell.Event.agent)) {
                        dangers = dangers + "A";
                    }
                    if (mycells[j][i].getEvents().contains(Cell.Event.gold)) {
                        dangers = dangers + "G";
                    }
                    if (mycells[j][i].getEvents().contains(Cell.Event.wumpus)) {
                        dangers = dangers + "W";
                    }
                    if (mycells[j][i].getEvents().contains(Cell.Event.smell)) {
                        dangers = dangers + "S";
                    }
                    if (mycells[j][i].getEvents().contains(Cell.Event.pit)) {
                        dangers = dangers + "P";
                    }
                    if (mycells[j][i].getEvents().contains(Cell.Event.wind)) {
                        dangers = dangers + "I";
                    }
                    if(mycells[j][i].getEvents().contains(Cell.Event.wall)){
                        dangers = dangers + "(#)";
                    }
                }
                // Ajout du player
                if(dangers.equals("")){
                    dangers = "  ";
                }else if(dangers.equals("A")){
                    dangers = "A ";
                }else if(dangers.equals("G")){
                    dangers = "G ";
                }else if(dangers.equals("I")){
                    dangers = "I ";
                }else if(dangers.equals("P")){
                    dangers = "P ";
                }else if(dangers.equals("S")){
                    dangers = "S ";
                }else if(dangers.equals("GIGI")){
                    dangers = "GI";
                }else if(dangers.equals("SISI")){
                    dangers = "SI";
                }else if(dangers.equals("WIWI")){
                    dangers = "WI";
                }else if(dangers.equals("ASAS")){
                    dangers = "AS";
                }else if(dangers.equals("AIAI")){
                    dangers = "AI";
                }else if(dangers.equals("I(#)I(#)")){
                    dangers = "I(#)";
                }
                System.out.print("|  "+dangers+"  ");

            }
            System.out.println("| \n");
        }
    }
    
}
