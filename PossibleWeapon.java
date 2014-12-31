/**
   PossibleWeapon
   immutable data structure for holding potential weapons, straightforward
   @author Stephen S. Lee
 */

public class PossibleWeapon {
    private final String name;
    private final int accuracy;
    private final int damage;
    private final char symbol;

    /**
       Constructor
     */
    public PossibleWeapon(String name, int accuracy, int damage, char symbol) {
        this.name = name;
        this.accuracy = accuracy;
        this.damage = damage;
        this.symbol = symbol;
    }

    // getters
    public String getName() {return name;}
    public int getAccuracy() {return accuracy;}
    public int getDamage() {return damage;}
    public char getSymbol() {return symbol;}
}
