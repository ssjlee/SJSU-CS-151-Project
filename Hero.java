/**
   This is the Creature that is controlled by the game player.
   There can only be one Hero.
 */

import java.io.Serializable;
import static utils.GameFunctions.*;
 
public class Hero extends Creature implements Serializable {
    
    private static final int STARTING_HEALTH    = 300; // hero starting hit points
    private static final int STARTING_ACCURACY  = 80;  // hero starting accuracy
    private static final int ATTACK_BARE_HANDED = 10;  // attack strength without any weapon
    private static final int ARMOR_CLASS_NAKED  = 0;   // armor class without any armor
    
    public static final char SYMBOL_HERO = '@';
    
    private static boolean exists = false; // prevent instantiation of more than one hero
    
    private Weapon weapon;       // weapon currently equipped by the hero
    private Armor armor;         // armor currently equipped by the hero
    private Item[] inv;          // hero inventory of items
    private int gold;            // treasure carried by the hero

    /**
       Constructor for creating the hero.
       @param name Hero's name
       @param health Hero's starting maximum health.
     */
	public Hero(String name) {
        if (exists) {
            System.err.println("ERROR: cannot create more than one hero");
            throw new Error(); // this is very clumsy and should be rewritten
        }
        setName(name);
        setHealth(STARTING_HEALTH);
        setMaxHealth(STARTING_HEALTH);
        setAccuracy(STARTING_ACCURACY);
        weapon = null;
        setDamage();
        setEvasion(50); // variable evasion not yet implemented
        armor = null;
        setArmorClass();
        inv = new Item[HERO_MAX_ITEMS];
        for (int i = 0; i < HERO_MAX_ITEMS; i++) { // generate empty inventory
            inv[i] = null;
        }
        gold = 0;
	}

    // getters
    public int getGold() {return gold;}
    public Weapon getWeapon() {return weapon;}
    public boolean hasWeapon() {return (weapon != null);}
    public Armor getArmor() {return armor;}
    public boolean hasArmor() {return (armor != null);}
    public Item getItem(int index) {return inv[index];} // individual item from inventory
    @Override public int getAccuracy() {
        // This version of getAccuracy instead returns it with the hero's modifiers
        int weaponMod = (weapon == null ? 0
                                        : weapon.getAccuracy());
        return super.getAccuracy() + weaponMod;
    }
    
    // setters
    public void setWeapon(Weapon weapon) {this.weapon = weapon;}
    public void setItem(int index, Item item) {inv[index] = item;} // individual item from inventory
    
    /**
       Sets damage rating, corresponding to the equipped weapon (if any).
       This overloads setDamage() from Creature.
       @author Stephen S. Lee
     */
    public void setDamage() {
        if (weapon == null) { // hero is bare-handed
            setDamage(ATTACK_BARE_HANDED);    // bare-handed attack strength
        } else {
            setDamage(weapon.getDamage());
        }
    }
    
    /**
       Sets armor class rating, corresponding to the equipped armor (if any).
       This overloads setArmorClass() from Creature.
       @author Stephen S. Lee
     */
    public void setArmorClass() {
        if (armor == null) { // hero has no armor
            setArmorClass(ARMOR_CLASS_NAKED); // naked armor
        } else {
            setArmorClass(armor.getArmorClass());
        }
    }
    
    /**
       Checks if the hero is already carrying a given item, either in inventory or equipped.
       @author Stephen S. Lee
       @return true if hero has the indicated item in inventory or equipped, false otherwise
     */
    public boolean hasItem(Item item) {
        for (int i = 0; i < HERO_MAX_ITEMS; i++) {
            if (!(inv[i] == null)) { // there is an item in this slot
                if (inv[i].equals(item)) {return true;} // we found the same item, let
            }
        }
        if (hasWeapon() && weapon.equals(item)) {return true;} // item in weapon slot
        if (hasArmor() && armor.equals(item)) {return true;} // item in armor slot
        return false; // no such item
    }
    
    /**
       Gets a String representation of inventory.
       @author Stephen S. Lee
       @return String representation of playing inventory, with a newline after every entry.
     */
    public String getInventory() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < HERO_MAX_ITEMS; i++) {
            if (!(inv[i] == null)) { // there is an item in this slot
                output.append(getLetter(i) + " - " + inv[i].getName() + "\n");
            }
        }
        if (output.length() == 0) {
            output.append("Your backpack is currently empty.");
        } else {
            output.insert(0, "You are carrying the following items:\n");
        }
        return output.toString();
    }
    
    /**
       Returns symbol for the hero.  Currently '@', as is traditional.
     */
    public char getSymbol() {return SYMBOL_HERO;}
        
    /**
       Returns the index corresponding to the first empty inventory slot.
       @author Stephen S. Lee
       @return index for the first empty inventory slot, or -1 if no such slot exists
     */
    private int emptySlot() {
        for (int i = 0; i < HERO_MAX_ITEMS; i++) {
            if (inv[i] == null) {return i;}
        }
        return -1;
    }
    
    /**
       Adds gold to hero's purse.
       @author Stephen S. Lee
       @param gold Amount of gold pieces added to the hero's purse
     */
    public void addGold(int gold) {this.gold += gold;}
    
    /**
       Attempts to add the given item to the hero's inventory.
       @author Stephen S. Lee
       @param item The item to try to add to the hero's inventory
       @return true if the item was successfully added to inventory, else false
     */
    public boolean addItem(Item item) {
       int index = emptySlot(); // get a slot to place the Item in
       if (index != -1) {
           inv[index] = item; // place item in slot
           return true;
       } else { // no slot to place Item in
           return false;
       }
    }
    
    /**
       Equips a weapon from the hero's inventory.
       Any currently equipped weapon is placed in the first available inventory slot.
       @author Stephen S. Lee
       @param index Inventory index of the weapon to be equipped.
       @return true if equip was successful, false if it wasn't (because it isn't a weapon)
     */
     public boolean equipWeapon(int index) {
         if (inv[index] == null) {
             return false; // you can't equip a nonexistent item
         } else if (inv[index].isWeapon()) {
             Weapon oldweapon = weapon; // remember what the old weapon was
             weapon = (Weapon) inv[index]; // equip the item in that slot
             inv[index] = null;   // delete the item from that slot
             addItem(oldweapon); // put old weapon in inventory; there must be at least one empty inventory slot now
             setDamage(); // establish damage for the new weapon
             return true;    
         }
         return false; // you can't equip something that isn't a Weapon
     }
     
    /**
       Equips an armor from the hero's inventory.
       Any currently equipped armor is placed in the first available inventory slot.
       @author Stephen S. Lee
       @param index Inventory index of the armor to be equipped.
       @return true if equip was successful, false if it wasn't (because it isn't an armor)
     */
     public boolean equipArmor(int index) {
         if (inv[index] == null) {
             return false; // you can't equip a nonexistent item
         } else if (inv[index].isArmor()) {
             Armor oldarmor = armor; // remember what the old armor was
             armor = (Armor) inv[index]; // equip the item in that slot
             inv[index] = null;   // delete the item from that slot
             addItem(oldarmor); // put old armor in inventory; there must be at least one empty inventory slot now
             setArmorClass(); // establish damage for the new weapon
             return true;    
         }
         return false; // you can't equip something that isn't an armor
     }
}
