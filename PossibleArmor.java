/**
   PossibleArmor
   immutable data structure for holding potential armors, straightforward
   @author Stephen S. Lee
 */

public class PossibleArmor {
    private final String name;
    private final int armorClass;
    private final char symbol;

    /**
       Constructor
     */
    public PossibleArmor(String name, int armorClass, char symbol) {
        this.name = name;
        this.armorClass = armorClass;
        this.symbol = symbol;
    }

    // getters
    public String getName() {return name;}
    public int getArmorClass() {return armorClass;}
    public char getSymbol() {return symbol;}
}
