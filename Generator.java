/**
   This contains routines responsible for generating dungeon-appropriate objects in a given context.
   Currently, the only supported context is the depth the Hero is on.
   @author Stephen S. Lee
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static utils.GameFunctions.*;
import static utils.RandomGen.*;

public class Generator implements Serializable {

    // Instance variables that influence what can be generated
    // Depth is currently the only supported variable
    private int depth;

    private static ArrayList<PossibleMonster> monsterList; // list of possible monsters
    private static ArrayList<PossibleWeapon> weaponList;   // list of possible weapons
    private static ArrayList<PossibleArmor> armorList;     // list of possible armors

    /**
       Constructor.
       Supplied parameters affect the context in which Monsters, Items, etc. are created.
       @param depth Depth of the dungeon the Hero is currently on.
     */
    public Generator(int depth) {
        this.depth = depth;
    }

    /**
       Read monster list text into monsterList.
       This makes no attempt to trap errors in the input.
       @author Stephen S. Lee
       @param input Strings with comma-delimited monster information
     */
    public static void readMonsters(List<String> input) {
        monsterList = new ArrayList<>();
        for (String i : input) {
            // Lines that begin with a '#' are comments; skip them
            if (i.length() > 0 && i.charAt(0) == '#') {continue;}

            String[] tokens = i.split(",");
            PossibleMonster pm = new PossibleMonster(tokens[0],                    // name
                                                     Integer.parseInt(tokens[1]),  // accuracy
                                                     Integer.parseInt(tokens[2]),  // damage
                                                     Integer.parseInt(tokens[3]),  // armor class
                                                     Integer.parseInt(tokens[4]),  // health
                                                     tokens[5].charAt(0));         // symbol
            monsterList.add(pm);
        }
    }

    /**
       Read item list text into the lists of possible different items.
       This makes no attempt to trap errors in the input.
       @author Stephen S. Lee
       @param input Strings with comma-delimited item information
     */
    public static void readItems(List<String> input) {
        // Initialize item lists
        weaponList = new ArrayList<>();
        armorList = new ArrayList<>();
        
        for (String i : input) {
            // Lines that begin with a '#' are comments; skip them
            if (i.length() > 0 && i.charAt(0) == '#') {continue;}

            String[] tokens = i.split(",");
            switch (tokens[0]) { // the first entry will say what kind of item this is
                case "weapon":
                    addPossibleWeapon(i);
                break;
                case "armor":
                    addPossibleArmor(i);
                break;
                default:
                    System.err.println("ERROR: unrecognized item type in items list");
                break;
            }
        }
    }

    /**
       Adds a weapon to weaponList.
       @author Stephen S. Lee
       @param weapon Original unparsed String from the item list
     */
    private static void addPossibleWeapon(String weapon) {
        String[] tokens = weapon.split(",");      // tokens[0] just says this is a weapon
        PossibleWeapon pw = new PossibleWeapon(tokens[1],                    // name
                                               Integer.parseInt(tokens[2]),  // accuracy
                                               Integer.parseInt(tokens[3]),  // damage
                                               tokens[4].charAt(0));         // symbol
        weaponList.add(pw);
    }

    /**
       Adds an armor to armorList.
       @author Stephen S. Lee
       @param armor Original unparsed String from the item list
     */
    private static void addPossibleArmor(String armor) {
        String[] tokens = armor.split(",");      // tokens[0] just says this is a armor
        PossibleArmor pa = new PossibleArmor(tokens[1],                    // name
                                             Integer.parseInt(tokens[2]),  // armor class
                                             tokens[3].charAt(0));         // symbol
        armorList.add(pa);
    }

    /**
       Factory method for selecting and creating a monster.
       A monster is chosen randomly from the list of possible monsters.
       This should eventually support increasing difficulty by parameters, which isn't currently implemented.
       @return Monster with the statistics of the chosen monster
     */
    public Monster generateMonster() {
        PossibleMonster pm = monsterList.get(rand.nextInt(monsterList.size())); // Pick a random monster from monsterList
        return Monster.createMonster(pm);
    }

    /**
       Factory method for selecting and creating a weapon.
       A weapon is chosen randomly from the list of possible weapons.
       This should eventually support increasing difficulty by parameters, which isn't currently implemented.
       @return Weapon with the statistics of the chosen weapon
     */
    public Weapon generateWeapon() {
        PossibleWeapon pw = weaponList.get(rand.nextInt(weaponList.size()));// Pick a random weapon from weaponList
        return Weapon.createWeapon(pw);
    }

    /**
       Factory method for selecting and creating an armor.
       An armor is chosen randomly from the list of possible armors.
       This should eventually support increasing difficulty by parameters, which isn't currently implemented.
       @return Armor with the statistics of the chosen armor
     */
    public Armor generateArmor() {
        PossibleArmor pa = armorList.get(rand.nextInt(armorList.size())); // Pick a random armor from armorList
        return Armor.createArmor(pa);
    }

    /**
       Factory method for creating a random item of any existing type.
       A specific Item class is chosen randomly, then an Item of that class is chosen randomly from the chosen class.
       @author Stephen S. Lee
       @return Item that has been randomly chosen
     */
    public Item generateItem() {
        // select an item type to generate (currently very simple)
        int type = rand.nextInt(MAX_TYPES);

        Item item = null;
        switch (type) {
            case ITEM_WEAPON:
                item = generateWeapon();
            break;
            case ITEM_ARMOR:
                item = generateArmor();
            break;
            default:
                System.err.println("ERROR: attempted to generate item type that doesn't exist");
            break;
        }

        return item;
    }

    /**
       Creates gold with a random amount of gold, with more gold on deeper levels.
       @return int with amount of gold to be created
     */
    public int generateGold() {
        return rollDice(depth * 2 + 2, 10);
    }
}