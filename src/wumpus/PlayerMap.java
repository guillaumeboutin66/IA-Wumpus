package wumpus;

import java.util.ArrayList;

public class PlayerMap {
    ArrayList<ArrayList<Cell>> cells;
    int length;
    int width;

    public void PlayerMap(int largeur, int hauteur,double sizeX,double sizeY){
        for(int j =0;j<hauteur+1;j++) {
            for (int i = 0; i < largeur + 1; i++) {
                cells.get(j).add(new Cell(j,i,sizeX,sizeY));
            }
        }
        this.length=hauteur+1;
        this.width=largeur+1;
    }
    public ArrayList<Cell> getRow(int row){
        return cells.get(row);
    }
    public ArrayList<Cell> getColum(int col){
        ArrayList<Cell> column = new ArrayList<>();
        for(int i = 0;i<length;i++){
            column.add(cells.get(col).get(i));
        }
        return column;
    }
    public ArrayList<Cell> adjCells(int x, int y){
        ArrayList<Cell> casesAjacentes = new ArrayList<>();
        casesAjacentes.add(cells.get(x).get(y+1));
        casesAjacentes.add(cells.get(x+1).get(y));
        casesAjacentes.add(cells.get(x).get(y-1));
        casesAjacentes.add(cells.get(x-1).get(y));
        return casesAjacentes;
    }

    public ArrayList<Cell> adjCellsFromCell(Cell cell){
        return adjCells(cell.getPosition().x,cell.getPosition().y);
    }

}

