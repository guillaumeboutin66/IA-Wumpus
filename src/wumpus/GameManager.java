/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

import java.awt.Point;

/**
 *
 * @author azuron
 */
public class GameManager {
    private Cell[][] map;
    private Point agentPosition;
    
    public GameManager(int width, int height){
        agentPosition = new Point(0, height);
        initMap(width, height);
    }
    
    private void initMap(int width, int height){
        map = new Cell[width][height];
    }
    
    public Cell[] computeNewPosition(Action action){
        
        /* faire déplacer et retourner les 4 cells */
        return new Cell[3];
    }
}
