/**
   GamePlay
   Responsible for central game logic.
   @author Everyone on the project, by this point
 */

import java.io.Serializable;
import java.util.LinkedList;
import static utils.RandomGen.*;
import static utils.GameFunctions.*;

public class GamePlay implements Serializable {

    private Level level; // physical level (not difficulty)
    private int depth;
    private Hero hero;
    private String killer;
    private boolean escaped;
    private LinkedList<Monster> monsters;
    private Generator gen; // factory that handles generating things on a level
    private transient MessageQueue messages; // this is meant to be shared with UserInterface

    /**
       Constructor.
       Creates an instance of GamePlay from scratch; Hero is assumed to not already exist.
       @param name Name of the Hero.
     */
    public GamePlay(String name) {
        hero = new Hero(name);  // Create our hero
        level = new Level(ROW_SIZE, COLUMN_SIZE);
        depth = 1;
        gen = new Generator(depth);
        escaped = false;
        monsters = new LinkedList<>();
        populateLevel();    // put hero, monsters, etc. on level
        messages = null;
    }

    // getter for depth
    public int getDepth() {return depth;}
    
    // setters for messageQueue
    public void setMessageQueue(MessageQueue mq) {messages = mq;}

    // getters for Hero attributes
    public String getHeroName() {return hero.getName();}
    public int getHeroHealth() {return hero.getHealth();}
    public int getHeroMaxHealth() {return hero.getMaxHealth();}
    public int getHeroAccuracy() {return hero.getAccuracy();} // this returns the modified, not base, rating
    public int getHeroDamage() {return hero.getDamage();}
    public int getHeroArmorClass() {return hero.getArmorClass();}
    public Weapon getHeroWeapon() {return hero.getWeapon();}
    public Armor getHeroArmor() {return hero.getArmor();}
    public String getHeroInventory() {return hero.getInventory();}
    public int getHeroGold() {return hero.getGold();}
    public boolean isHeroDead() {return hero.isDead();}
    public boolean hasHeroEscaped() {return escaped;}
    public String getKiller() {return killer;}

    /**
       Gets a view of the map centered on the Hero.
       This is currently represented as a String, but other representations are planned.
       @return String representation of map from Level.viewMap(int, int)
     */
    public String getView() {return level.viewMap(hero.getRow(), hero.getColumn());}

    /**
       Places a Creature at the specified coordinates of the level.
       This routine expects that the destination is valid, but will report errors if not.

       @precondition: level has been created
       @precondition: Creature is already somewhere on the level; if this isn't the case use setCreature instead
       @param creature : the Creature to be placed
       @param nextRow : the row number of the Tile on which the character is to be placed
       @param nextColumn : the column number of the Tile on which the character is to be placed
       @return true if successful, otherwise returns false
     */
    public boolean placeCreature(Creature creature, int nextRow, int nextColumn)
    {
        int currentRow = creature.getRow();
        int currentColumn = creature.getColumn();

        if (level == null) { // can't place creature without a generated level
            System.err.println("FATAL ERROR: placeCreature called without a level");
            return false;
        }

        if (currentRow == nextRow && currentColumn == nextColumn) { // you are allowed to "move" to your current location
            return true; // nothing needs to change
        }

        // Safety check: Check if we're currently in a wall (which isn't allowed by the current rules)
        if (level.getTile(currentRow, currentColumn).isWall()) {
            System.err.println("FATAL ERROR: " + creature.getName() + " is illegally starting in a wall");
            return false;
        }

        // Safety check: Check if we're crossing the map edge (shouldn't happen).
        if (nextRow >= level.getRowSize()) {
            System.err.println(creature.getName() + " has reached the south wall, cannot go further");
            return false;
        }
        if (nextRow < 0) {
            System.err.println(creature.getName() + " has reached the north wall, cannot go further");
            return false;
        }
        if (nextColumn >= level.getColumnSize()) {
            System.err.println(creature.getName() + " has reached the east wall, cannot go further");
            return false;
        }
        if (nextColumn < 0) {
            System.err.println(creature.getName() + " has reached the west wall, cannot go further");
            return false;
        }

        // Safety check: Check if we're walking into a wall.
        if (level.getTile(nextRow, nextColumn).isWall()) {
            System.err.println(creature.getName() + " cannot traverse a wall");
            return false;
        }

        // Safety check: Check if we're moving into a creature (which should be checked elsewhere; moving into the space of
        // another Creature should initiate a fight instead of moving)
        if (level.getTile(nextRow, nextColumn).hasCreature()) {
            System.err.println("Cannot move creature into index " + nextRow + " " + nextColumn);
            System.err.println("index " + nextRow + " " + nextColumn + " already contains a creature");
            return false;
        }

        // else place creature on requested tile and remove creature from current tile
        level.getTile(nextRow, nextColumn).setCreature(creature);
        level.getTile(currentRow, currentColumn).setCreature(null);

        creature.setRow(nextRow);//update creatures internal reference to the occupied columns
        creature.setColumn(nextColumn);//update creatures internal reference to the occupied row
        return true;
    }

    /**
       Creates and places all characters in initial positions on level.
       @precondition level has been instantiated
       @return true if successful, false otherwise
     */
    public boolean populateLevel() {

        int[] spot; // this holds the coordinates for a valid location

        if (level == null) {
            System.err.println("FATAL ERROR: cannot populate a non-existent level");
            return false;
        }

        // Place hero
        spot = level.getEmptyTile(); // Get random empty spot to place hero in
        hero.setLocation(spot); // Notify hero of the location at which he will be placed
        level.getTile(spot).setCreature(hero);  // place hero at spot

        // Place monsters
        for (int i = 0; i < randRange(MIN_MONSTERS, MAX_MONSTERS); i++) {
            Monster monster = gen.generateMonster(); // create monster
            spot = level.getEmptyTile(); // Get random empty spot to place monster in
            monster.setLocation(spot); //notify monster of the location at which he will be placed
            level.getTile(spot).setCreature(monster); //place monster at spot
            monsters.add(monster); // add monster to list of monsters
        }

        // Place stairs (only down stairs are currently supported; and only place one staircase)
        spot = level.getEmptyTile();
        level.getTile(spot).setDownStairs();

        // Place items
        for (int i = 0; i < randRange(MIN_ITEMS, MAX_ITEMS); i++) {
            spot = level.getEmptyTile(); // Get random empty spot to place item in
            level.getTile(spot).setItem(gen.generateItem()); // put a randomly generated Item there
        }

        // Place gold
        for (int i = 0; i < randRange(MIN_GOLD, MAX_GOLD); i++) {
            spot = level.getEmptyTile(); // Get random empty spot to place gold in
            level.getTile(spot).setGold(gen.generateGold());
        }

        return true;
    } //end of method populateLevel

    /**
       Move to another dungeon Level.
       The entire old Level, with the exception of the Hero, is forgotten; keeping the old level is currently unsupported.
       @author Stephen S. Lee
       @param depth The depth of the destination Level.
     */
    private void changeLevel(int depth) {
        if (hero == null || level == null) { // cannot change level without an already existing hero and level
            System.err.println("FATAL ERROR: cannot change level without an already existing hero and level");
        } else if (depth > MAX_LEVELS) { // cannot go beyond maximum level
            System.err.println("FATAL ERROR: cannot go beyond maximum depth of " + MAX_LEVELS);
        }
        level = new Level(ROW_SIZE, COLUMN_SIZE); // create brand new level
        this.depth = depth;
        monsters = new LinkedList<>(); // reset monster list
        populateLevel(); // put hero, monsters, etc. on level
    }

    /**
       Descend stairs.
       Currently, this simply creates a new Level with a depth 1 greater than the current one, then moves the Hero there.
       @author Stephen S. Lee
     */
    public void descendStairs() {
        if (level.getTile(hero.getRow(), hero.getColumn()).hasDownStairs()) {
            if (depth >= MAX_LEVELS) {
                messages.add("You are exiting the bottom level of the dungeon.");
                messages.add("You have escaped!");
                escaped = true;
            } else {
                depth++;
                messages.add("You go down the staircase and emerge upon a new dungeon level.");
                changeLevel(depth);
            }
        } else {
            messages.add("There is no down staircase here.");
        }
    }

    /**
       Moves all Monsters of the map.
       Each Monster is considered in turn, and tries to move toward and attack the Hero.
       @precondition    Creatures exist
       @precondition    level exists
     */
    public void moveMonsters() {
        // Monsters always attempt to converge on the hero's location, which we need to know
        int heroRow = hero.getRow();
        int heroCol = hero.getColumn();

        for (Monster mon : monsters) {
            int monRow = mon.getRow();
            int monCol = mon.getColumn();
            int[] destination = level.suggestMove(monRow, monCol, heroRow, heroCol);
            if (level.getTile(destination).getCreature() == hero) { // monster initiates fight with hero
                initiateFight(level.getTile(monRow, monCol).getCreature(), hero);
            } else { // monster moves towards the hero
                placeCreature(level.getTile(monRow, monCol).getCreature(), destination[0], destination[1]);
            }
        }

    } //end of method moveMonsters

    /**
       Initiates a fight between an attacking Creature and a defending Creature.
       This method will adjudicate fights between any two creatures.
       It will gracefully handle dead Monsters, but not Heroes; the latter should be checked elsewhere since it ends the game.
       @param attacker Attacking Creature
       @param defender Defending Creature
     */

    public void initiateFight(Creature attacker, Creature defender) {
        // Get creature names and add articles to monster names
        String attackerName = attacker.getName();
        killer = attackerName; // assign blame if the hero is killed
        String defenderName = defender.getName();
        if (attacker.isMonster()) {attackerName = "the " + attackerName;}
        if (defender.isMonster()) {defenderName = "the " + defenderName;}

        messages.add(attackerName + " is attacking " + defenderName);
        int[] result = attacker.attack(defender); // this returns the results of the attack as defined in Creature.java
        
        // result[] is specified in Creature.java
        // This should be rewritten to be more comprehensible but we only have so much time
        // Also, a lot of these combat messages are more for debugging than for actual gameplay        
        if (result[0] > result[1]) { // display combat results for a hit
            messages.add(attackerName + " hits " + defenderName + ".");
            messages.add("(Accuracy roll " + result[0] + "/" + attacker.getAccuracy()
                       + " versus evasion roll " + result[1] + "/" + defender.getEvasion() + ")");
            if (defender.getArmorClass() > 0) { // defender has armor
                if (result[3] == 0) { // but it didn't help
                    messages.add("The armor of " + defenderName + " fails to absorb any damage.");
                } else {
                    messages.add("The armor of " + defenderName + " absorbs " + result[3] + " damage.");
                }
            }
            messages.add(defenderName + " takes " + (result[2] - result[3]) + " damage "
                       + "(out of a maximum of " + attacker.getDamage() + ").");
            if ((result[2] - result[3]) > 0) {
                messages.add(defenderName + " has " + defender.getHealth() + " hit points remaining.");
            }
        } else { // display combat results for a miss
            messages.add(attackerName + " misses " + defenderName + ".");
            messages.add("(Accuracy roll " + result[0] + "/" + attacker.getAccuracy()
                       + " versus evasion roll " + result[1] + "/" + defender.getEvasion() + ")");
        }
        
        if (defender.isDead()) {
            messages.add(defenderName + " has been killed!");

            // We need to now delete the defender both from the level and the list of monsters
            level.getTile(defender.getRow(), defender.getColumn()).setCreature(null); // removed from level
            if (defender.isMonster()) {monsters.remove(defender);} // removed from internal monster list
        }
    }	//end of fight_sequence method

    /**
       Gives a movement command to the Hero.
       If the Hero moves into a Monster, this instead initiates an attack.
       After any successful move, all Monsters on the Level also get a move.
       @param rChange change in rows
       @param cChange change in columns
     */
    public void moveHero(int rChange, int cChange) {
        int destRow   = hero.getRow() + rChange;         // row of destination Tile
        int destCol   = hero.getColumn() + cChange;      // column of destination Tile
        Tile destTile = level.getTile(destRow, destCol); // actual destination Tile

        if (destTile.isWall()) { // you are not allowed to walk into a wall
            messages.add("You cannot walk there; there is a wall in your way.");
        } else if (rChange == 0 && cChange == 0) { // hero is passing a turn, the monsters do get to move
            messages.add("You rest for a moment.");
            moveMonsters();
        } else if (destTile.hasCreature()) { // hero is moving onto a tile with a monster
            initiateFight(hero, destTile.getCreature());
            moveMonsters();
        } else { // hero is moving onto a tile that can be walked into
            placeCreature(hero, destRow, destCol);
            
            // Describe what the hero sees on that tile
            if (destTile.hasGold()) {
                messages.add("You see " + destTile.getGold() + " gold pieces here.");
            }
            if (destTile.hasItem()) {
                messages.add("There is a " + destTile.getItem().getName() + " here.");
                if (destTile.getItem().isEquippable() && hero.hasItem(destTile.getItem())) {
                    messages.add("You already have one of those, however.");
                }
            }
            if (destTile.hasDownStairs()) { // inform player that there are stairs here
                messages.add("You see a staircase leading down here.");
            }
            moveMonsters();
        }
    }

    /**
       Picks up both gold and items that are on the floor and gives them to the Hero.
       If anything is picked up, this causes all Monsters to also get a move.
       @author Stephen S. Lee
       @return boolean true if anything was actually picked up, false if not
     */
    public boolean pickUpStuff() {
        Tile here = level.getTile(hero.getRow(), hero.getColumn());
        if (!(here.hasGold() || here.hasItem())) {
            messages.add("There is nothing to pick up here.");
            return false;
        } else { // pick up gold and items at the same time
            boolean pickedUp = false;
            if (here.hasGold()) {
                int goldAdded = here.getGold();
                messages.add("You pick up " + goldAdded + " gold pieces.");
                hero.addGold(goldAdded);
                here.setGold(0);
                pickedUp = true;
            }
            if (here.hasItem()) {
                if (hero.addItem(here.getItem())) {
                    messages.add("You pick up the " + here.getItem().getName() + ".");
                    pickedUp = true;
                    here.setItem(null); // delete item from the Tile
                } else {
                    messages.add("You have no space in your backpack for the " + here.getItem().getName() + ".");
                }
            }
            if (pickedUp) {moveMonsters();} // if we picked up stuff here, it takes a move and the monsters also get to move
            return pickedUp;
        }
    }

    /**
       Drops an Item on the dungeon floor.
       You currently cannot do this if there is already an Item on the floor.
       @author Stephen S. Lee
       @param index Inventory index of the item to be equipped.
       @return true if drop was successful, false if it wasn't
     */
    public boolean dropItem(int index) {
        Tile here = level.getTile(hero.getRow(), hero.getColumn());
        if (here.hasItem()) { // dropping an Item on a Tile that already has an Item isn't supported
            messages.add("There is already an item on the floor here.");
            return false;
        } else if (hero.getItem(index) == null) { // empty slot check
            messages.add("You aren't holding anything in that slot.");
            return false;
        } else {
            Item dropped = hero.getItem(index);
            hero.setItem(index, null);
            here.setItem(dropped);
            messages.add("You drop the " + dropped.getName() + " on the floor.");
            return true;
        }
    }

    /**
       Equips an inventory item.
       @author Stephen S. Lee
       @param index Inventory index of the item to be equipped
       @return true if equip was successful, false if it wasn't
     */
    public boolean equipItem(int index) {
        if (hero.getItem(index) == null) {
            messages.add("You aren't holding anything in that inventory slot.");
            return false;
        } else if (!hero.getItem(index).isEquippable()) {
            messages.add("You cannot equip that item.");
            return false;
        } else if (hero.getItem(index).isWeapon()) {
            hero.equipWeapon(index);
            messages.add("You wield the " + hero.getWeapon().getName() + ".");
        } else if (hero.getItem(index).isArmor()) {
            hero.equipArmor(index);
            messages.add("You put on the " + hero.getArmor().getName() + ".");
        } else {
            System.err.println("ERROR: tried to equip unknown type of item");
            return false;
        }
        moveMonsters();
        return true;
    }
    
} //end of class GamePlay