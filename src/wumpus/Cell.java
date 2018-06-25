/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author azuron
 */
public class Cell extends StackPane{
    
    private ArrayList<Event> events = new ArrayList<>();   
    private Point position;
    public double sizeX;
    public double sizeY;
    private Boolean dangerous=false;
    private Rectangle border;
    private Text text = new Text();
    private String originalSprite;
    
    
    public Cell(int x, int y,double sizeX,double sizeY){

        position = new Point(x, y);
        this.sizeX=sizeX;
        this.sizeY=sizeY;
        this.border = new Rectangle(sizeX-2,sizeY-2);
        border.setStroke(Color.LIGHTGRAY);
        text.setFont(Font.font(18));
        text.setVisible(true);
        border.setFill(null);
        getChildren().addAll(border, text);
        setTranslateX(x * sizeX);
        setTranslateY(y * sizeY);

    }
    public Cell(){}

    public Rectangle getBorderRectangle(){
        return this.border;
    }
    public ArrayList<Event> getEvents(){
        return events;
    }

    public void removeEvent(Event e){
        events.remove(e);
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
        wall,
        unkonwn
    }

    public void setOriginalStyle(){
            this.setStyle(originalSprite);
    }

    public void setOriginalSprite(String s){
        this.originalSprite = s;
    }

    @Override
    public String toString(){
        return "["+this.position.x+", "+this.position.y+"]/"+ getEvents().toString();
    }
}
