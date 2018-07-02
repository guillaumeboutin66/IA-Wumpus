package wumpus;

import java.util.ArrayList;

public class PlayerData {


    private static PlayerData playerDataInstance = null;
    private ArrayList<String> playerData =new ArrayList<>();

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
}
