/**
   Item template class
 */

import java.io.Serializable;
import static utils.RandomGen.*;

public abstract class Item implements Serializable {

    private String name;
    private char symbol;

    // getters and setters
    public String getName() {return name;}
    public char getSymbol() {return symbol;}
    public void setName(String name) {this.name = name;}
    protected void setSymbol(char symbol) {this.symbol = symbol;}
    
    // methods for identifying Items
    public boolean isWeapon() {return (this instanceof Weapon);}
    public boolean isArmor() {return (this instanceof Armor);}
    public boolean isEquippable() {return (this instanceof Equippable);}
    
    /**
       Overridden equals() method.
       Checks to make sure that object class, names, and symbols match.
       @author Stephen S. Lee
       @return true if and only if both objects are the same class, and have matching names and symbols
     */
    @Override public boolean equals(Object obj) {
        if (obj == null) {return false;} // null check
        if (obj == this) {return true;} // reflexivity
        if (this.getClass() != obj.getClass()) {return false;} // items are of different types and can't be the same
        
        Item item = (Item) obj; // If we got this far, obj must be an Item
        if (!(name.equals(item.getName()))) {return false;} // items with different names can't be the same
        if (getSymbol() != item.getSymbol()) {return false;} // items with different symbols can't be the same
        return true; // if we got here, they're the same
    }
    
    /**
       Overridden hashCode() method.
       Currently hashes only the name of the item and returns that.
       @author Stephen S. Lee
       @return name.hashCode()
     */
    @Override public int hashCode() {
       return name.hashCode();
    }
     
}
