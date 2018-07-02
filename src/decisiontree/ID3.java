/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 *
 * @author azuron
 */
public class ID3 {
 
    // table des faits
    public Line[] totalFacts;
    public Tree tree;
    public Cell[] cellAvailable = {Cell.Empty, Cell.Player, Cell.Smell, Cell.Unknown, Cell.Wind,Cell.Unreachable,Cell.PlayerSmell, Cell.PlayerWind, Cell.PlayerWindSmell, Cell.WindSmell};
    public String[] propertyAvailable = {"up", "right", "bottom", "left"};

    public ID3(Line[] totalFacts){
        this.totalFacts = totalFacts;

        long startTime = System.currentTimeMillis();
        CreateTree();
        System.out.println(tree);
        long endTime = System.currentTimeMillis();

        System.out.println("Decision tree created in " + (endTime - startTime) + " milliseconds");
    }


    public float testDecisionAgainstTree(Line line){

        return seekWithinProperty(tree.rootProperty, line);
    }


    private float seekWithinProperty(Property prop, Line line){
        for(int i = 0; i < prop.getAttributes().size(); i++){
            if(line.surroundings[prop.getIndex()].toString().equals(prop.getAttributes().get(i).getName())){
                Property newProp = prop.getAttributes().get(i).getTarget();
                if(newProp == null){
                    return prop.getAttributes().get(i).getScore();
                }else {
                    return seekWithinProperty(prop.getAttributes().get(i).getTarget(), line);
                }
            }
        }

        System.out.println("Rien trouvé");
        return -1;
    }
    
    private void CreateTree(){
        
        float[] gains = new float[totalFacts[0].surroundings.length];
        float mainEntropy = CalculateEnthropy(totalFacts);
        for(int i = 0; i < totalFacts[0].surroundings.length;i++){
            gains[i] = CalculateGainforProperty(i, cellAvailable, totalFacts, mainEntropy);
        }

        int index = PickBestGain(gains);

        Property prop = new Property(propertyAvailable[index], index);
        ArrayList<String> propertyUsed = new ArrayList<>();
        propertyUsed.add(propertyAvailable[index]);
        for(int i = 0; i < cellAvailable.length; i++){
            prop.addAttribute(new Attribute(cellAvailable[i].toString(), prop));
        }

        tree = new Tree(prop);
        
        CalculateStateOfAttribute(prop, mainEntropy, propertyUsed);
    }


    private void CalculateStateOfAttribute(Property prop, float mainEnthropy, ArrayList<String> propertyUsed){
        ArrayList<Attribute> attrs = prop.getAttributes();
        for(int i = 0; i < attrs.size(); i++){
            ArrayList<Line> facts = new ArrayList<>();

            for(int j = 0; j < totalFacts.length; j++){
                Line line = totalFacts[j];
                if(line.surroundings[prop.getIndex()].toString().equals(attrs.get(i).getName())){
                    facts.add(line);
                }
            }
            
            Boolean fullDeath = true;
            Boolean fullSurvive = true;
            for(int j = 0; j < facts.size(); j++){
                if(facts.get(j).death){
                    fullSurvive = false;
                }else {
                    fullDeath = false;
                }
            }

            if(fullDeath){
                attrs.get(i).setEnd(false);
            }else if(fullSurvive){
                attrs.get(i).setEnd(true);
            } else {

                float[] gains = new float[totalFacts[0].surroundings.length];
                for(int j = 0; j < propertyAvailable.length; j++){
                    gains[j] = 0; //on initialise le tableau pour évité d'avoir des valeurs random dedans
                    if(propertyUsed.indexOf(propertyAvailable[j]) == -1){
                        gains[j] = CalculateGainforProperty(j, cellAvailable, totalFacts, mainEnthropy);
                    }
                }

                int index = PickBestGain(gains);
                
                ArrayList<String> newPropertyUsed =  new ArrayList<>();

                for(int j = 0; j < propertyUsed.size(); j++){
                    newPropertyUsed.add(propertyUsed.get(j));
                }

                Property newProp = new Property(propertyAvailable[index], index);
                newPropertyUsed.add(propertyAvailable[index]);
                for(int j = 0; j < cellAvailable.length; j++){
                    Attribute attr = new Attribute(cellAvailable[j].toString(), newProp);
                    newProp.addAttribute(attr);
                }

                attrs.get(i).setTarget(newProp);

                if(newPropertyUsed.size() != propertyAvailable.length){
                    CalculateStateOfAttribute(newProp, mainEnthropy, newPropertyUsed);
                }
            }

        }
    }

    private float CalculateGainforProperty(int index, Cell[] fields, Line[] facts, float mainEntropy){
        float[] entropy = new float[fields.length];
        float[] prevalence = new float[fields.length];
        for(int i = 0; i < fields.length; i++){
            Pair<Float, Float> pair =  CalculateEnthropyForField(index, fields[i], facts);
            entropy[i] = pair.getKey();
            prevalence[i] =  pair.getValue();

        }
        float gain = ComputeGain(index, mainEntropy, entropy, prevalence, facts.length);
        return gain;
    }

    private float ComputeGain(int index, float mainEntropy, float[] entropy, float[] prevalence, float factsSize){
        float gain = mainEntropy;
        for(int i = 0; i < entropy.length; i++){
            gain -= ((prevalence[i]/factsSize)*entropy[i]);
        }

        return gain;
    }


    private Pair<Float, Float> CalculateEnthropyForField(int index, Cell cell, Line[] facts){
        float probabilityCell = 0;
        float numberDeath = 0;
        float numberSurvive = 0;
        
        for(int i = 0; i < facts.length; i++){
            if(facts[i].surroundings[index] == cell){
                probabilityCell++;
                if(facts[i].death){
                    numberDeath++;
                }else{
                    numberSurvive++;
                }
            }
        }
        
        float ratioSurvive = probabilityCell != 0 ? numberSurvive / probabilityCell : 0;//pour éviter les NaN
        float ratioDeath = probabilityCell != 0 ? numberDeath / probabilityCell : 0;
        
        return new Pair<Float, Float>(-ratioDeath*log2(ratioDeath) - ratioSurvive*log2(ratioSurvive), probabilityCell);
    }


    /** Reusable */

    private float CalculateEnthropy(Line[] lines){
        float numberDeath = 0;
        float numberSurvive = 0;
        
        for(int i = 0; i < lines.length; i++){
            if(lines[i].death){
                numberDeath++;
            }else {
                numberSurvive++;
            }
        }
        
        float ratioSurvive = numberSurvive / lines.length;
        float ratioDeath = numberDeath / lines.length;

        
        return -ratioSurvive*log2(ratioSurvive) - ratioDeath*log2(ratioDeath);
        
    }


    private float log2(float number) {
        if (number == 0) {
                return number;
        }
        return (float) Math.log(number) / (float) Math.log(2);
    }
    
    private int PickBestGain(float[] gains){
        int index = 0;
        float max = gains[0];
        
        for(int i = 0; i < gains.length; i++){
            if(gains[i] > max){
                index = i;
            }
        }
        return index;
    }
    
}
