/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

import javafx.scene.Parent;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author azuron
 */
public class Agent {

    Point position;
    Cell[][] knowncells;
    public Agent(Cell startingCell,int width, int height, double sizeX, double sizeY){
        int mapWidth = width+2;
        int mapHeight = height+2;
        position = new Point(startingCell.getPosition().x,startingCell.getPosition().y);
        System.out.println("starting postion : " + position);
        knowncells = new Cell[mapWidth][mapHeight];
        knowncells[position.x][position.y] = new Cell(position.x,position.y,sizeX,sizeY);
        for(Cell.Event e: startingCell.getEvents()){
            knowncells[position.x][position.y].addEvent(e);
        }
        knowncells[position.x][position.y].setStyle("-fx-background-image:url(\"wumpus/resources/player.png\")");
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
        this.position=newPosition;
        knowncells[newPosition.x][newPosition.y] = new Cell(cells[0].getPosition().x,cells[0].getPosition().y,cells[0].sizeX,cells[0].sizeY);
        for(Cell.Event e: cells[0].getEvents()){
            knowncells[newPosition.x][newPosition.y].addEvent(e);
        }
    }
    public void fillPlayerMap(int width,int height,double sizeCaseHorizontal, double sizeCaseVertical){
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++) {
                if(knowncells[i][j] == null){
                    knowncells[i][j] = new Cell(i,j,sizeCaseHorizontal,sizeCaseVertical);
                    knowncells[i][j].addEvent(Cell.Event.unkonwn);
                    knowncells[i][j].setStyle("-fx-background-image:url(\"wumpus/resources/question.png\")");
                }
            }
        }
    }
}
