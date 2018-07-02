package decisiontree;

public class main {

    public static void main(String[] args) {
        Line[] totalFact = new Line[9];
        totalFact[0] =  new Line(Cell.Smell, Cell.Player, Cell.Smell, Cell.Smell, false);
        totalFact[1] =  new Line(Cell.Empty, Cell.Player, Cell.Empty, Cell.Empty, false);
        totalFact[2] =  new Line(Cell.Smell, Cell.Smell, Cell.Player, Cell.Empty, false);
        totalFact[3] =  new Line(Cell.Unknown, Cell.Unknown, Cell.Player, Cell.Empty, true);
        totalFact[4] =  new Line(Cell.Smell, Cell.Unknown, Cell.Smell, Cell.Player, true);
        totalFact[5] =  new Line(Cell.Empty, Cell.Player, Cell.Smell, Cell.Wind, true);
        totalFact[6] =  new Line(Cell.Unknown, Cell.Empty, Cell.Player, Cell.Empty, false);
        totalFact[7] =  new Line(Cell.Smell, Cell.Unknown, Cell.Player, Cell.Player, false);
        totalFact[8] =  new Line(Cell.Smell, Cell.Player, Cell.Smell, Cell.Empty, true);


        ID3 decision = new ID3(totalFact);
        float score = decision.testDecisionAgainstTree( new Line(Cell.Empty, Cell.Player, Cell.Player, Cell.Empty, false));
        System.out.println("Score : " + score);
    }

}
