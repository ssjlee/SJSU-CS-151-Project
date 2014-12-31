/**
   CS 151 project Weapon
   @author Borum Chhay (bkchhay@gmail.com)
 */

import java.io.Serializable;
 
public class Weapon extends Item implements Equippable, Serializable {

	private int accuracy;
	private int damage;

    /**
       Default constructor.
       All Weapons are to be created via the createWeapon method.
       This method is private to prevent it from being accessed.
     */
	private Weapon() {}

    /**
       Method for creating a weapon.
       @author Stephen S. Lee
       @param pw PossibleWeapon representing the desired Weapon
       @return Weapon with the statistics of the specific PossibleWeapon
     */
    public static Weapon createWeapon(PossibleWeapon pw) {
        // Generate a Weapon from the indicated PossibleWeapon
        Weapon weapon = new Weapon();
        weapon.setName(pw.getName());
        weapon.setAccuracy(pw.getAccuracy());
        weapon.setDamage(pw.getDamage());
        weapon.setSymbol(pw.getSymbol());
        return weapon;
    }

    // getters
	public int getAccuracy() {return accuracy;}
    public int getDamage() {return damage;}

    // setters - for factory use only
    private void setAccuracy(int accuracy) {this.accuracy = accuracy;}
    private void setDamage(int damage) {this.damage = damage;}

}
