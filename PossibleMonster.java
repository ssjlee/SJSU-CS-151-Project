/**
   PossibleMonster
   immutable data structure for holding potential monsters, straightforward
   @author Stephen S. Lee
 */

public class PossibleMonster {
    private final String name;
    private final int accuracy;
    private final int damage;
    private final int armorClass;
    private final int health;
    private final char symbol;

    /**
       Constructor
     */
    public PossibleMonster(String name, int accuracy, int damage, int armorClass, int health, char symbol) {
        this.name = name;
        this.accuracy = accuracy;
        this.damage = damage;
        this.armorClass = armorClass;
        this.health = health;
        this.symbol = symbol;
    }

    // getters
    public String getName() {return name;}
    public int getAccuracy() {return accuracy;}
    public int getDamage() {return damage;}
    public int getArmorClass() {return armorClass;}
    public int getHealth() {return health;}
    public char getSymbol() {return symbol;}
}
