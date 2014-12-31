import java.io.Serializable;
import java.util.Random;
import static utils.RandomGen.*;

/**
 * Created by Alvin Ma
 */

abstract public class Creature implements Serializable {
    private String name;
    private int health;
    private int maxHealth;
    private int accuracy;
    private int damage;
    private int evasion;
    private int armorClass;
    private int row = -1; //the character will be informed of the its position on the map at all times
    private int col = -1; //the character will be informed of the its position on the map at all times

    /**
       Default constructor.
       Currently we're creating the objects in the inherited classes, not here.
     */
    protected Creature() {}

    // getters and setters
    public boolean isDead() {return (health < 1);}
    public String getName() {return name;}
    public int getHealth() {return health;}
    public int getMaxHealth() {return maxHealth;}
    public int getAccuracy() {return accuracy;}
    public int getDamage() {return damage;}
    public int getEvasion() {return evasion;}
    public int getArmorClass() {return armorClass;}
    abstract public char getSymbol(); // representation of this Creature

    public void setName(String name) {this.name = name;}
    public void setHealth(int health) {this.health = health;}
    public void setMaxHealth(int maxHealth) {this.maxHealth = maxHealth;}
    public void setAccuracy(int accuracy) {this.accuracy = accuracy;}
    public void setDamage(int damage) {this.damage = damage;}
    public void setEvasion(int evasion) {this.evasion = evasion;}
    public void setArmorClass(int armorClass) {this.armorClass = armorClass;}

    // getters and setters for row/columns
    public int getRow() {return row;}
    public int getColumn() {return col;}

    public void setRow(int row) {this.row = row;}
    public void setColumn(int col) {this.col = col;}

    /**
       Sets both Row and Column at the same time.
       @param location {row, column} with coordinates to set this Creature to
     */
    public void setLocation(int[] location) {
        row = location[0];
        col = location[1];
    }
    
    /**
       Checks if this Creature is a Monster or not.
       @return true if it is, false otherwise
     */
    public boolean isMonster() {return (this instanceof Monster);}
    
    /**
       Initiates an attack from this creature.
       @param cr The target Creature to be attacked.
       @return An int array with results of the attack:
                     0 - attacker accuracy roll;
                     1 - defender evasion roll.
               If the attack hits, the following is also defined:
                     2 - attacker damage inflicted, before armor is taken into account;
                     3 - defender armor absorbed.
     */
    public int[] attack(Creature cr) {
        int[] result = new int[4];
        result[0] = randRange(0, getAccuracy());
        result[1] = randRange(0, cr.getEvasion());
        result[2] = result[3] = 0;
        
        if (result[0] > result[1]) { // successful attack
            result[2] = randRange(1, damage); // calculate damage roll
            result[3] = randRange(0, cr.getArmorClass());
            if (result[3] > result[2]) {result[3] = result[2];} // cannot absorb more damage than is taken
            cr.takeDamage(result[2] - result[3]); // take damage equal to damage roll minus armor class roll
        }
        
        return result;
    }
    
    /**
       Inflict damage upon a creature.
       Any calculations about whether an attack hits, or if some damage is blocked, is done elsewhere.
       Creature health cannot drop below zero.
     */
    public void takeDamage(int damage) {
        if (health - damage <= 0) {
            health = 0;
        } else {
            health -= damage;
        }
    }
}