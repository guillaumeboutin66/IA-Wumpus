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
public class Cell {
    
    private ArrayList<Event> events = new ArrayList<>();   
    private Point position;
    private Boolean dangerous;
    
    
    public Cell(int x, int y){
        position = new Point(x, y);
    }
    
    public ArrayList<Event> getEvents(){
        return events;
    }

    public void addEvent(Event e){
        events.add(e);
        if(e == Event.wumpus || e == Event.pit){
            dangerous = true;
        }
    }
    
    public Boolean isDangerous(){
        return dangerous;
    }
    
    public Point getPosition(){
        return position;
    }
    
    public enum Event{
        wumpus,
        gold,
        pit,
        smell,
        wind,
        agent,
        wall
    }
    
    @Override
    public String toString(){
        return "["+this.position.x+", "+this.position.y+"]/"+ getEvents().toString();
    }
}
