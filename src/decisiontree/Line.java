package decisiontree;

/**
 * Line
 */
public class Line {

    public Cell[] surroundings;
    public Boolean death;

    public Line(Cell top, Cell right, Cell bottom, Cell left, Boolean death){  
        surroundings = new Cell[4];
        surroundings[0] = top;
        surroundings[1] = right;
        surroundings[2] = bottom;
        surroundings[3] = left;
        this.death = death;
    }
    
}