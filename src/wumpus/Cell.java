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

    public Boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    private Boolean isBlocked=false;

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    private boolean collision = false;

    public String getOriginalSprite() {
        return (originalSprite==null) ? "" : originalSprite;
    }

    private String originalSprite;
    
    
    public Cell(int x, int y,double sizeX,double sizeY){

        position = new Point(x, y);
        this.sizeX= new Double(sizeX);
        this.sizeY=new Double(sizeY);
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

    public Cell(Cell cell){ // change with new pour éviter les ref
        for(Event e : cell.getEvents()){
            this.events.add(e);
        }
        this.position = new Point(cell.getPosition());
        this.sizeX = new Double(cell.sizeX);
        this.sizeY = new Double(cell.sizeY);
        this.dangerous = new Boolean(cell.isDangerous());
        this.originalSprite = new String(cell.getOriginalSprite());
        this.setStyle(new String(cell.getStyle()));
        this.border = new Rectangle(sizeX-2,sizeY-2);
        border.setStroke(Color.LIGHTGRAY);
        text.setFont(Font.font(18));
        text.setVisible(true);
        border.setFill(null);
        getChildren().addAll(border, text);
        setTranslateX(this.position.x * sizeX);
        setTranslateY(this.position.y * sizeY);
    }

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
