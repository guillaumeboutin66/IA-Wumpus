package wumpus;

import decisiontree.Line;

import java.awt.*;

public class FutureCellDecision {

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    private Point position;

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    private Float score = -1f ;

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    private Line line;

    private Action action;

    private Cell[] neighbors = new Cell[4];

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public FutureCellDecision(Point position,Cell[] neighbors,Action action){
        this.position=position;
        this.neighbors = neighbors;
        this.action = action;
        decisiontree.Cell[] events = new decisiontree.Cell[4];
        for(int i = 0; i<4;i++){
            events[i]= neighbors[i]==null ? decisiontree.Cell.Unreachable : neighbors[i].getPrincipalEvent();
        }
        line = new Line(events[0],events[1],events[2],events[3],true);
    }


}
