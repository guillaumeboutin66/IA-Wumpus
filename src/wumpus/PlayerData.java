package wumpus;

import decisiontree.Line;

import java.util.ArrayList;

public class PlayerData {


    private static PlayerData playerDataInstance = null;
    private ArrayList<String> playerData =new ArrayList<>();
    private ArrayList<Line> facts = new ArrayList<>();

    public static PlayerData getInstance(){
        if(playerDataInstance==null){
            playerDataInstance = new PlayerData();
        }
        return playerDataInstance;
    }

    public ArrayList<String> getPlayerData() {
        return playerData;
    }

    public void saveData(String newData){
        playerDataInstance.playerData.add(newData);
    }

    public void setPlayerData(ArrayList<String> playerData) {
        playerDataInstance.playerData = playerData;
    }

    public ArrayList<Line> getFacts() { return facts; }

    public void setFacts(ArrayList<Line> facts){ this.facts = facts; }

    public void addFact(Line line) { this.facts.add((line)); }
}
