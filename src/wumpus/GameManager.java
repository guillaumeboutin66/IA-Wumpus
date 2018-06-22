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
    
    public Boolean agentIsDead(){
        return map[agentPosition.x][agentPosition.y].isDangerous();
    }
    
    private void initMap(int width, int height){
        map = new Cell[width][height];
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
}
