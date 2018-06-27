/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

import javafx.scene.Parent;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author azuron
 */
public class Agent {

    Point position;
    Cell[][] knowncells;
    ArrayList<Cell> path = new ArrayList<>();
    public Agent(Cell startingCell,int width, int height, double sizeX, double sizeY){
        int mapWidth = width+2;
        int mapHeight = height+2;
        position = new Point(startingCell.getPosition().x,startingCell.getPosition().y);
        knowncells = new Cell[mapWidth][mapHeight];
        knowncells[position.x][position.y] = new Cell(startingCell);
        fillPlayerMap(mapWidth,mapHeight,sizeX,sizeY);
    }
    
    public Action takeDecision(){
        return Action.right;
    }

    public Cell[][] getPlayerMap() {
        return knowncells;
    }

    public void discoverPosition(Cell[] cells){
        Point newPosition = cells[0].getPosition();
        if(!this.position.equals(newPosition)) {
            knowncells[newPosition.x][newPosition.y].getEvents().clear();
            knowncells[newPosition.x][newPosition.y].getEvents().addAll(cells[0].getEvents());
            knowncells[newPosition.x][newPosition.y].setOriginalSprite(cells[0].getOriginalSprite());
            knowncells[newPosition.x][newPosition.y].setStyle(cells[0].getStyle());
            knowncells[position.x][position.y].removeEvent(Cell.Event.agent);
            knowncells[position.x][position.y].setOriginalStyle();
            this.position = newPosition;
            System.out.println(knowncells[position.x][position.y].toString()+ " " + knowncells[position.x][position.y].getStyle() + knowncells[position.x][position.y].getParent().toString());
        }else{
            knowncells[newPosition.x][newPosition.y].setStyle(cells[0].getStyle());
            for(int i=1; i<cells.length;i++){
             if(cells[i].isCollision()){
                 knowncells[cells[i].getPosition().x][cells[i].getPosition().y].getEvents().clear();
                 knowncells[cells[i].getPosition().x][cells[i].getPosition().y].getEvents().addAll(cells[i].getEvents());
                 knowncells[cells[i].getPosition().x][cells[i].getPosition().y].setOriginalSprite(cells[i].getOriginalSprite());
                 knowncells[cells[i].getPosition().x][cells[i].getPosition().y].setStyle(cells[i].getStyle());
              }
            }
        }
    }
    public void fillPlayerMap(int width,int height,double sizeCaseHorizontal, double sizeCaseVertical){
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++) {
                if(knowncells[i][j] == null){
                    knowncells[i][j] = new Cell(i,j,sizeCaseHorizontal,sizeCaseVertical);
                    knowncells[i][j].addEvent(Cell.Event.unkonwn);
                    knowncells[i][j].setStyle("-fx-background-image:url(\"wumpus/resources/question.png\");-fx-background-size: " + knowncells[i][j].sizeX + " " + knowncells[i][j].sizeY +";");
                }
            }
        }
    }
}
