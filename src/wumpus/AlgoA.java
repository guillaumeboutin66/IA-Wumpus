package wumpus;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by guillaumeboutin on 13/03/2018.
 */

public class AlgoA {
    private static final int V_H_COST = 10;

    static class Cellule{
        int heuristicCost = 0; //Heuristic cost
        int finalCost = 0; //G+H
        int i, j;
        Cellule parent;

        Cellule(int i, int j){
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString(){
            return "["+this.i+", "+this.j+"]";
        }
    }

    static class Path{
        int x, y;

        Path(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString(){
            return "["+this.x+", "+this.y+"]";
        }
    }

    //Blocked cells are just null Cell values in grid
    private static Cellule [][] grid;

    private static PriorityQueue<Cellule> open;

    private static boolean closed[][];
    private static int startI, startJ;
    private static int endI, endJ;

    private static void setBlocked(int i, int j){
        grid[i][j] = null;
    }

    private static void setStartCell(int i, int j){
        startI = i;
        startJ = j;
    }

    private static void setEndCell(int i, int j){
        endI = i;
        endJ = j;
    }

    private static void checkAndUpdateCost(Cellule current, Cellule t, int cost){
        if(t == null || closed[t.i][t.j])return;
        int t_final_cost = t.heuristicCost+cost;

        boolean inOpen = open.contains(t);
        if(!inOpen || t_final_cost<t.finalCost){
            t.finalCost = t_final_cost;
            t.parent = current;
            if(!inOpen)open.add(t);
        }
    }

    // A* without DIAGONAL
    private static void AStar(){

        if(grid[startI][startJ] != null) {
            //add the start location to open list.
            open.add(grid[startI][startJ]);

            Cellule current;

            while (true) {
                current = open.poll();
                if (current == null) break;
                closed[current.i][current.j] = true;

                if (current.equals(grid[endI][endJ])) {
                    return;
                }

                Cellule t;
                if (current.i - 1 >= 0) {
                    t = grid[current.i - 1][current.j];
                    checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
                }

                if (current.j - 1 >= 0) {
                    t = grid[current.i][current.j - 1];
                    checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
                }

                if (current.j + 1 < grid[0].length) {
                    t = grid[current.i][current.j + 1];
                    checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
                }

                if (current.i + 1 < grid.length) {
                    t = grid[current.i + 1][current.j];
                    checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
                }
            }
        }else{
            System.out.println("grid["+startI+"]["+startJ+"] is null");
        }
    }

    public static ArrayList<Point> getSolution(int weigh, int height, int startX, int startY, int endX, int endY, ArrayList<Point> blocked){
        //Reset
        grid = new Cellule[weigh][height];
        closed = new boolean[weigh][height];
        open = new PriorityQueue<>((Object o1, Object o2) -> {
            Cellule c1 = (Cellule)o1;
            Cellule c2 = (Cellule)o2;

            return c1.finalCost<c2.finalCost?-1: c1.finalCost>c2.finalCost?1:0;
        });
        //Set start position
        setStartCell(startX, startY);  //Setting to 0,0 by default. Will be useful for the UI part

        //Set End Location
        setEndCell(endX, endY);

        for(int j=0;j<height;++j){
            for(int i=0;i<weigh;++i){
                grid[i][j] = new Cellule(i, j);
                grid[i][j].heuristicCost = Math.abs(i-endI)+Math.abs(j-endJ);
            }
        }
        //grid[startX][startY].finalCost = 0;

        /*
           Set blocked cells. Simply set the cell values to null
           for blocked cells.
        */
        for (Point aBlocked : blocked) {
            setBlocked(aBlocked.x, aBlocked.y);
        }

        AStar();

        List<Path> pathFind = new ArrayList<>();

        if(closed[endI][endJ]){
            //Trace back the path

            Cellule current = grid[endI][endJ];

            pathFind.add(new Path(current.i, current.j));
            while(current.parent!=null){

                pathFind.add(new Path(current.parent.i, current.parent.j));
                current = current.parent;
            }

        }else System.out.println("No possible path");


        ArrayList<Point> pathPointFind = new ArrayList<>();
        for(Path path: pathFind){
            pathPointFind.add(new Point(path.x, path.y));
        }
        Collections.reverse(pathPointFind);
        return pathPointFind;
    }
}