/**
   This class represents the most basic position of an element in the game, each level is made up of an array of tiles,
   and any in-game element (player, item, bad guy, etc...) will stand/walk/sit on a tile.
  
   @author Simranjit Singh
 */

import java.io.Serializable;
 
public class Tile implements Serializable {

    // character representations of features in a tile
    public static final char SYMBOL_FLOOR       = '.';
    public static final char SYMBOL_WALL        = '#';
    public static final char SYMBOL_STAIRS_DOWN = '>';
    public static final char SYMBOL_GOLD        = '$';

    // constants for what feature is in a tile
    private static final int FEATURE_NONE        = 0;
    private static final int FEATURE_WALL        = 1;
    private static final int FEATURE_STAIRS_DOWN = 2;

    // Instance variables
    private int feature;
    private Creature creature;
    private Item item;
    private int gold;

    /**
       Default Constructor.
       This will create a wall in a space by default; nothing else is placed.
     */
    public Tile() {
        feature = FEATURE_WALL;
        creature = null;
        item = null;
        gold = 0;
    } //end of default constructor

    // getters
    public Creature getCreature() {return creature;}
    public Item getItem() {return item;}
    public int getGold() {return gold;}

    // setters
    public void setCreature(Creature creature) {this.creature = creature;}
    public void setItem(Item item) {this.item = item;}
    public void setGold(int gold) {this.gold = gold;}
    public void setDownStairs() {
        if (!isWall()) {
            feature = FEATURE_STAIRS_DOWN;
        } else { // not allowed to put stairs on a tile with a wall
            System.err.println("ERROR: cannot place stairs down on the same square as a wall");
        }
    }

    /**
       Checks if the current Tile contains a wall.
       @return true if Tile contains a wall, else false
     */
    public boolean isWall() {return (feature == FEATURE_WALL);}

    /**
       Checks if the current Tile contains stairs down.
       @return true if Tile contains stairs down, else false
     */
    public boolean hasDownStairs() {return (feature == FEATURE_STAIRS_DOWN);}

    /**
       Checks if the current Tile contains a creature.
       @author Stephen S. Lee
       @return true if Tile contains a creature, else false
     */
    public boolean hasCreature() {return creature != null;}

    /**
       Checks if the current Tile contains an Item.
       @author Stephen S. Lee
       @return true if Tile contains an Item, else false
     */
    public boolean hasItem() {return item != null;}

    /**
       Checks if the current Tile contains a Monster.
       @author Stephen S. Lee
       @return true if Tile contains a Monster, else false
     */
    public boolean hasMonster() {
        if (!hasCreature()) {return false;}
        return (creature.isMonster());
    }

    /**
       Checks if the current Tile is empty.
       @author Stephen S. Lee
       @return false if this Tile contains any feature, monster, or item; else true
     */
    public boolean isEmpty() {return (feature == FEATURE_NONE && creature == null && item == null);}
    
    /**
       Checks if the current Tile has gold.
       @author Stephen S. Lee
       @return true if this Tile contains any gold; else false
     */
    public boolean hasGold() {return (gold > 0);}
    
    /**
       Clears the wall in the Tile if there is one
       Does nothing if there isn't a wall in this Tile
       @author Stephen S. Lee
     */
    public void createSpace() {
        if (feature == FEATURE_WALL) {feature = FEATURE_NONE;}
    }

    /**
       Returns the symbol encoded by this tile.
       @author Stephen S. Lee
       @return The character that this Tile should display.
     */
    public char getSymbol() {
        if (isWall()) { // a wall covers up anything else in a Tile
            return SYMBOL_WALL;
        } else if (hasCreature()) { // a Creature stands on top of other stuff in a Tile
            return creature.getSymbol();
        } else if (hasItem()) { // an Item sits on top of gold and stairs
            return item.getSymbol();
        } else if (hasGold()) {
            return SYMBOL_GOLD;
        } else if (hasDownStairs()) {
            return SYMBOL_STAIRS_DOWN;
        }
        return SYMBOL_FLOOR; // empty accessible ground
    }

}//end of class Tile
