/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

/**
 *
 * @author azuron
 */
public class main {

    public static void main(String[] args) {
        Boolean end = false;
        GameManager gameManager = new GameManager(10,10);
        Agent agent = new Agent();
        
        while(!end){
           Action action = agent.takeDecision();
           if(action == Action.takeGold){
               end = true;//gold is taken
           }else{
               Cell[] newNeighbors = gameManager.computeNewPosition(action);
               if(gameManager.agentIsDead()){
                   end = true;//agent is dead
               }
               agent.discoverPosition(newNeighbors);
           }
        }
        
        
    }
    
}
