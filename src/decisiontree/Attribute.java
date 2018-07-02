package decisiontree;

/**
 * Attribute, represente une branche.
 */
public class Attribute {
    
    private Property mainProperty;
    private Property connectedProperty;
    private String name;
    private Boolean isEnd = false;
    private Boolean survive = false;

    public Attribute(String name, Property mainProperty){
        this.name = name;
        this.mainProperty = mainProperty;
    }

    public Attribute(String name, Property mainProperty, Property connectedProperty){
        this.name = name;     
        this.mainProperty = mainProperty;
        this.connectedProperty = connectedProperty;
    }

    public Property getSource(){
        return mainProperty;
    }

    public Property getTarget(){
        return connectedProperty;
    }
    
    public void setTarget(Property prop){
        connectedProperty = prop;
    }

    public String getName(){
        return name;
    }

    public void setEnd(Boolean survive){
        this.survive = survive;
        this.isEnd = true;
    }

    public float getScore(){
        if(isEnd && survive){
            return 1;
        } else if(isEnd && !survive){
            return 0;
        }

        return -1;
    }
}