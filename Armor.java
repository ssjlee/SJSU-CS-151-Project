/**
   CS 151 project Armor
   @author Borum Chhay (bkchhay@gmail.com)
 */

import java.io.Serializable;
 
public class Armor extends Item implements Equippable, Serializable {

	private int armorClass;

    /**
       Default constructor.
       All Armor should be created with the createArmor method.
       This method is private to prevent it from being accessed.
     */
	private Armor() {}

    /**
       Method for creating an Armor.
       @author Stephen S. Lee
       @param pa PossibleArmor representing the desired Armor.
       @return Armor with the statistics of the specific PossibleArmor.
     */
    public static Armor createArmor(PossibleArmor pa) {
        // Generate a Armor from the indicated PossibleArmor
        Armor armor = new Armor();
        armor.setName(pa.getName());
        armor.setArmorClass(pa.getArmorClass());
        armor.setSymbol(pa.getSymbol());
        return armor;
    }

    // getters
	public int getArmorClass() {return armorClass;}

    // setters
    public void setArmorClass(int armorClass) {this.armorClass = armorClass;}

}
