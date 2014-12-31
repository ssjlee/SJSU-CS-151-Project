/**
   Represents all Creatures opposing the protagonist Hero.
 */

import java.io.Serializable;
 
public class Monster extends Creature implements Serializable {

    private char symbol;   // what this Monster is represented by on the screen

    /**
       Default constructor.
       All Monsters are to be created with the createMonster method.
       This is private to prevent it from being accessed from outside.
     */
	private Monster() {}

    /**
       Method for creating a monster
       @author Stephen S. Lee
       @param PossibleMonster representing the desired Monster
       @return Monster with the statistics of the PossibleMonster
     */
    public static Monster createMonster(PossibleMonster pm) {
        // Generate a Monster from the indicated PossibleMonster
        Monster monster = new Monster();
        monster.setName(pm.getName());
        monster.setAccuracy(pm.getAccuracy());
        monster.setDamage(pm.getDamage());
        monster.setEvasion(50); // variable evasion not yet implemented implemented
        monster.setArmorClass(pm.getArmorClass());
        monster.setHealth(pm.getHealth());
        monster.setMaxHealth(pm.getHealth());
        monster.setSymbol(pm.getSymbol());
        return monster;
    }

    // getters and setters
    public char getSymbol() {return symbol;}
    private void setSymbol(char symbol) {this.symbol = symbol;} // this should not be changeable, so it's private
}
