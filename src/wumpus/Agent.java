/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

import decisiontree.ID3;
import decisiontree.Line;
import javafx.scene.Parent;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 *
 * @author azuron
 */
public class Agent {

    Point position;
    Cell[][] knowncells;
    ID3 decision;
    FutureCellDecision lastDecision;
    ArrayList<FutureCellDecision> aroundCells = new ArrayList<>();


    public Agent(Cell startingCell,int width, int height, double sizeX, double sizeY){
        int mapWidth = width+2;
        int mapHeight = height+2;
        initDecisionTree();
        position = new Point(startingCell.getPosition().x,startingCell.getPosition().y);
        knowncells = new Cell[mapWidth][mapHeight];
        knowncells[position.x][position.y] = new Cell(startingCell);
        fillPlayerMap(mapWidth,mapHeight,sizeX,sizeY);
    }
    
    public Action takeDecision(){

        Action action = Action.hiddle;

        aroundCells.clear();

        ArrayList<FutureCellDecision> safeCells = new ArrayList<>();
        ArrayList<FutureCellDecision> unknownCells = new ArrayList<>();
        ArrayList<FutureCellDecision> blockedCells = new ArrayList<>();
        ArrayList<FutureCellDecision> knownSafeCells = new ArrayList<>();

        //cell adjacentes à mes cells adjcentes
        aroundCells.add(new FutureCellDecision(new Point(position.x,position.y - 1),getNeighbors(knowncells[position.x][position.y - 1]),Action.up));//up
        aroundCells.add(new FutureCellDecision(new Point(position.x + 1,position.y),getNeighbors(knowncells[position.x + 1][position.y]),Action.right));//right
        aroundCells.add(new FutureCellDecision(new Point(position.x,position.y+1),getNeighbors(knowncells[position.x][position.y + 1]),Action.bottom));//bottom
        aroundCells.add(new FutureCellDecision(new Point(position.x - 1,position.y),getNeighbors(knowncells[position.x - 1][position.y]),Action.left));//left

        if(decision == null){
            return randomAction(aroundCells);
        }

        for(FutureCellDecision futureCell : aroundCells){

            Cell knownAgentCell = knowncells[futureCell.getPosition().x][futureCell.getPosition().y];

            if(knownAgentCell.getEvents().contains(Cell.Event.unkonwn)){

                futureCell.setScore(decision.testDecisionAgainstTree(futureCell.getLine()));

                if(futureCell.getScore()==1){
                    safeCells.add(futureCell);
                }else if(futureCell.getScore()==0){
                    blockedCells.add(futureCell);
                }else{
                    unknownCells.add(futureCell);
                }
            }else if(!knownAgentCell.isCollision()||!knownAgentCell.isBlocked()){
                knownSafeCells.add(futureCell);
            }

        }

        if(safeCells.size()>0){
            action = randomAction(safeCells);
        }else if(unknownCells.size()>0){
            action = randomAction(unknownCells);
        }else if(knownSafeCells.size()>0){
            action = randomAction(knownSafeCells);
        }else{
            action = randomAction(blockedCells);
        }

        System.out.println(action);

        return action;
    }

    public Action randomAction(ArrayList<FutureCellDecision> futureCells){
        Action action;
        Random r = new Random();
        int low = 0;
        int high = futureCells.size();
        int Result = r.nextInt(high-low) + low;
        action = futureCells.get(Result).getAction();
        lastDecision = futureCells.get(Result);
        return action;
    }

    public Cell[][] getPlayerMap() {
        return knowncells;
    }

    public ArrayList<FutureCellDecision> getAroundCells() {
        return aroundCells;
    }

    public void setAroundCells(ArrayList<FutureCellDecision> aroundCells) {
        this.aroundCells = aroundCells;
    }

    public void discoverPosition(Cell[] cells,boolean advanced,boolean dead,Action action){
        Point newPosition = cells[0].getPosition();
        if(action!=Action.hiddle) {
            writeData(cells, advanced, dead,action);
        }
        if(!this.position.equals(newPosition)) {
            knowncells[newPosition.x][newPosition.y].getEvents().clear();
            knowncells[newPosition.x][newPosition.y].getEvents().addAll(cells[0].getEvents());
            knowncells[newPosition.x][newPosition.y].setOriginalSprite(cells[0].getOriginalSprite());
            knowncells[newPosition.x][newPosition.y].setStyle(cells[0].getStyle());
            knowncells[position.x][position.y].removeEvent(Cell.Event.agent);
            knowncells[position.x][position.y].setOriginalStyle();
            this.position = newPosition;
            }
            else{
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

    public String writeData(Cell[] cells, boolean advance, boolean dead,Action action){
        String data="";

        for(int i =1;i<5;i++){
            if(data.isEmpty()){
                data+=knowncells[cells[i].getPosition().x][cells[i].getPosition().y].getEvents().toString();
            }else{
                data+=" " + knowncells[cells[i].getPosition().x][cells[i].getPosition().y].getEvents().toString();
            }
        }

        data+=" " + action + " " + advance + " " + dead;

        PlayerData player = PlayerData.getInstance();
        player.saveData(data);
        System.out.println(data);

        return data;
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

    public Cell[] getNeighbors(Cell cell){
        Cell[] neighbors = new Cell[4];
        if(cell.getPosition().y-1>0) {
            neighbors[0] = knowncells[cell.getPosition().x][cell.getPosition().y - 1];//up
        }
        if(cell.getPosition().x+1<knowncells[0].length) {
            neighbors[1] = knowncells[cell.getPosition().x + 1][cell.getPosition().y];//right
        }
        if(cell.getPosition().y + 1<knowncells.length) {
            neighbors[2] = knowncells[cell.getPosition().x][cell.getPosition().y + 1];//bottom
        }
        if(cell.getPosition().x-1>0) {
            neighbors[3] = knowncells[cell.getPosition().x - 1][cell.getPosition().y];//left
        }
        return neighbors;
    }

    public void saveFact(boolean death){

        lastDecision.getLine().death = death;
        PlayerData.getInstance().addFact(lastDecision.getLine());

        decision = new ID3(PlayerData.getInstance().getFacts().toArray(new Line[PlayerData.getInstance().getFacts().size()]));
    }


    public void initDecisionTree(){
        /*Line[] totalFact = new Line[9];
        totalFact[0] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Player, decisiontree.Cell.Smell, decisiontree.Cell.Smell, false);
        totalFact[1] =  new Line(decisiontree.Cell.Empty, decisiontree.Cell.Player, decisiontree.Cell.Empty, decisiontree.Cell.Empty, false);
        totalFact[2] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Smell, decisiontree.Cell.Player, decisiontree.Cell.Empty, false);
        totalFact[3] =  new Line(decisiontree.Cell.Unknown, decisiontree.Cell.Unknown, decisiontree.Cell.Player, decisiontree.Cell.Empty, true);
        totalFact[4] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Unknown, decisiontree.Cell.Smell, decisiontree.Cell.Player, true);
        totalFact[5] =  new Line(decisiontree.Cell.Empty, decisiontree.Cell.Player, decisiontree.Cell.Smell, decisiontree.Cell.Wind, true);
        totalFact[6] =  new Line(decisiontree.Cell.Unknown, decisiontree.Cell.Empty, decisiontree.Cell.Player, decisiontree.Cell.Empty, false);
        totalFact[7] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Unknown, decisiontree.Cell.Player, decisiontree.Cell.Player, false);
        totalFact[8] =  new Line(decisiontree.Cell.Smell, decisiontree.Cell.Player, decisiontree.Cell.Smell, decisiontree.Cell.Empty, true);
        */

        if(PlayerData.getInstance().getFacts().size() > 0){
            decision = new ID3(PlayerData.getInstance().getFacts().toArray(new Line[PlayerData.getInstance().getFacts().size()]));
        }
    }
}
