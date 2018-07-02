package decisiontree;

import java.util.ArrayList;

/**
 * Property
 */
public class Property {

    private String name;
    private int index;
    private ArrayList<Attribute> attributes = new ArrayList<>();

    public Property(String name, int index){
        this.index = index;
        this.name = name;
    }

    public void addAttribute(Attribute attr){
        attributes.add(attr);
    }

    public ArrayList<Attribute> getAttributes(){
        return attributes;
    }

    public int getIndex(){
        return index;
    }
}