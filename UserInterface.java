/**
   Main user interface routines

   @author Stephen S. Lee (ssjlee@rawbw.com, ID#010013627)
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import static utils.GameFunctions.*;

public class UserInterface extends JFrame {
    // Constants for UI
    private static final int MESSAGE_ROWS    = 10;  // height of message box
    private static final int MESSAGE_COLUMNS = 80;  // width of message box
    private static final int MAX_MESSAGES    = 200; // maximum number of messages to be displayed in message box
    private static final int TEXT_HEIGHT     = 20;  // height of a text box in the info area
    private static final int TEXT_WIDTH      = 340; // width of a text box in the info area

    // fonts
    private static final Font MONOSPACED = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font SANS_SERIF = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    // size of the text box
    private static final Dimension STATUS_DIMENSION = new Dimension(TEXT_WIDTH, TEXT_HEIGHT);

    // Constants for pending actions
    private static final int ACTION_NONE      = 0; // no pending action
    private static final int ACTION_QUIT      = 1; // quit confirmation
    private static final int ACTION_SAVE      = 2; // save confirmation
    private static final int ACTION_DROP      = 3; // drop an item
    private static final int ACTION_EQUIP     = 4; // equip an item

    // Constants for ending the game
    private static final int GAME_OVER_QUIT    = 0; // chickened out
    private static final int GAME_OVER_SAVED   = 1; // saving the game
    private static final int GAME_OVER_DIED    = 2; // hero killed in the dungeon
    private static final int GAME_OVER_ESCAPED = 3; // hero escaped from the last level

    // These instance variables are here so methods can access and manipulate them
    private JTextArea map;
    private JTextArea messageText;
    private JPanel info;
    private JTextField nameLine;
    private JTextField hpLine;
    private JTextField accuracyLine;
    private JTextField damageLine;
    private JTextField armorClassLine;
    private JTextField weaponLine;
    private JTextField armorLine;
    private JTextField goldLine;
    private JTextField depthLine;
    private JTextArea inventoryText;

    // Input and action maps
    private InputMap imap;
    private ActionMap amap;

    private int action; // remembering which current action the player is taking
    private transient MessageQueue messages; // message queue
    private LinkedList<String> messagesDisplayed; // messages actually displayed
    private int displayed; // number of messages currently displayed
    private GamePlay gameplay; // gameplay object

    /**
       UserInterface constructor
       Instance variables:
         JPanel display            -- the entire window
         JTextArea map             -- area for displaying the visible part of the current Level (in CENTER)
         JTextArea messageText     -- area for displaying game messages (in SOUTH)
         JScrollPane messagePane   -- messageText wrapped in a JScrollPane
         JPanel info               -- area for displaying player information (in EAST)
         JTextArea helpText        -- area for displaying help (in WEST)
         JScrollPane helpPane      -- helpText wrapped in a JScrollPane
         JTextField nameLine       -- line for player name (in info)
         JTextField hpLine         -- line for player hit points (in info)
         JTextField accuracyLine   -- line for playing accuracy rating (in info)
         JTextField damageLine     -- line for player damage rating (in info)
         JTextField armorClassLine -- line for player armor class rating (in info)
         JTextField weaponLine     -- line for currently equipped weapon (in info)
         JTextField armorLine      -- line for currently equipped armor (in info)
         JTextField goldLine       -- line for player gold (in info)
         JTextField depthLine      -- line for depth in the dungeon (in info)
         JTextArea inventoryText   -- area for playing inventory (in info)
         JScrollPane inventoryPane -- inventoryText wrapped in a JScrollPane
       @author Stephen S. Lee (ssjlee@rawbw.com, ID#010013627)
       @param name The name of the player, from the introduction window
       @param monsterList List of monster statistics, comma-delimited
       @param itemList List of items statistics, comma-delimited
     */
    public UserInterface(String name, List<String> monsterList, List<String> itemList) {
        action = ACTION_NONE; // no initial action
        Generator.readMonsters(monsterList); // read monster list
        Generator.readItems(itemList); // read item list

        // Create main display
        JPanel display = new JPanel();
        display.setLayout(new BorderLayout());

        // Main display: text map (with monospaced text)
        map = new JTextArea(MAP_SIZE, MAP_SIZE);
        map.setEditable(false);
        map.setFocusable(false); // corrects bug preventing keybinding from working
        map.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14)); // make the symbols on the map larger
        map.setBackground(Color.BLACK); // make the map a constrasting color
        map.setForeground(Color.WHITE);
        display.add(map, BorderLayout.CENTER);

        // Message display: text panel (with proportional text)
        messageText = new JTextArea(MESSAGE_ROWS, MESSAGE_COLUMNS);
        messageText.setEditable(false);
		messageText.setFocusable(false); // corrects bug preventing keybinding from working
        messageText.setFont(SANS_SERIF);

        // messagePane: messageText with vertical scroll bars
        JScrollPane messagePane = new JScrollPane(messageText);
        messagePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        display.add(messagePane, BorderLayout.SOUTH);

        // information display
        info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));

        /**
           Inner class for status lines
           This defines what a status line looks like
           All status lines go in the info box
         */
        class StatusLine extends JTextField {
            StatusLine() { // constructor
                super();
                setEditable(false);
                setFocusable(false); // corrects bug preventing keybinding from working
                setFont(SANS_SERIF);
                setPreferredSize(STATUS_DIMENSION);
                setMaximumSize(getPreferredSize());
                info.add(this);
            }
        }

        // Set up status lines
        nameLine = new StatusLine();
        hpLine = new StatusLine();
        accuracyLine = new StatusLine();
        damageLine = new StatusLine();
        armorClassLine = new StatusLine();
        weaponLine = new StatusLine();
        armorLine = new StatusLine();
        goldLine = new StatusLine();
        depthLine = new StatusLine();

        // Info display: player inventory
        inventoryText = new JTextArea();
        inventoryText.setEditable(false);
        inventoryText.setFocusable(false); // corrects bug preventing keybinding from working
        inventoryText.setFont(MONOSPACED);

        // inventoryPane: inventoryText with vertical scroll bars
        JScrollPane inventoryPane = new JScrollPane(inventoryText);
        inventoryPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        info.add(inventoryPane);

        // Place info panel
        display.add(info, BorderLayout.EAST);

        // Help display: explanation of symbols and commands
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setFocusable(false); // corrects bug preventing keybinding from working
        helpText.setPreferredSize(STATUS_DIMENSION);
        helpText.setMaximumSize(getPreferredSize());
        helpText.setFont(SANS_SERIF);

        // helpPane: helpText with vertical scroll bars
        JScrollPane helpPane = new JScrollPane(helpText);
        helpPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        display.add(helpPane, BorderLayout.WEST);

        // Finish setting up the display
        setContentPane(display);
        setResizable(false);
        setTitle("CS 151 Game Project");

        messages = new MessageQueue();              // message queue
        Thread queueThread = new Thread(messages);  // thread for queue
        queueThread.start();                        // start thread
        
        messagesDisplayed = new LinkedList<>(); // message currently actually displayed
        displayed = 0;                          // size of messagesDisplayed

        final File savefile = new File(name + ".sav");
        if(savefile.exists() && !savefile.isDirectory()) { // a save file exists, load it
            try {
                ObjectInputStream reader = new ObjectInputStream(
                                           new FileInputStream(savefile));
                gameplay = (GamePlay) reader.readObject();
                reader.close();
                messages.add("Welcome back, " + gameplay.getHeroName() + "!");
            } catch (IOException ex) {
                System.err.println("ERROR: cannot read save file, creating brand new character");
                gameplay = new GamePlay(name);
                messages.add("Welcome, " + gameplay.getHeroName() + "!");
            } catch (Exception ex) {
                System.err.println("ERROR: problem reading save file, creating brand new character");
                gameplay = new GamePlay(name);
                messages.add("Welcome, " + gameplay.getHeroName() + "!");
            }
        } else { // no save file exists, create a brand new character
            gameplay = new GamePlay(name); // Create the game, passing along the name of the player
            messages.add("Welcome, " + gameplay.getHeroName() + "!");
        }

        gameplay.setMessageQueue(messages);   // Share the message queue with gameplay

        updateUI(); // Display starting map and other statistics

        // Set help panel text.  This SHOULD be implemented eventually as a file rather than hard-coded.
        helpText.setText("EXPLANATION OF SYMBOLS ON THE MAIN MAP:\n"
                       + Hero.SYMBOL_HERO + " -- you, the hero!\n"
                       + "letters -- monsters\n"
                       + Tile.SYMBOL_FLOOR + " -- empty floor\n"
                       + Tile.SYMBOL_WALL + " -- a wall\n"
                       + Tile.SYMBOL_STAIRS_DOWN + " -- stairs to the next level\n"
                       + ") -- a weapon\n"
                       + "[ -- an armor\n"
                       + Tile.SYMBOL_GOLD + " -- a pile of gold pieces\n"
                       + "\n"
                       + "KEY COMMANDS:\n"
                       + "direction keys -- lateral moves\n"
                       + "Home/PgUp/End/PgDn -- diagonal moves\n"
                       + "(moving directly into a monster will perform an attack upon it)\n"
                       + ". (period) -- pass a turn\n"
                       + "> -- go down a staircase leading down\n"
                       + ", (comma) -- pick up gold and items\n"
                       + "d -- drop an item from your backpack\n"
                       + "E -- equip a weapon or armor\n"
                       + "Q -- quit the game (confirm with '@')\n"
                       + "S -- save the game (confirm with '@')");

        // Create keybinding maps: input map and action map
        imap = display.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        amap = display.getActionMap();

        /**
           Inner class for direction keys
           Currently, direction keys are only for actual movement.
           Eventually we might want to support things like shooting bows in a given direction.
           @author Stephen S. Lee
         */
        class Direction extends AbstractAction {
            private final int rR, rC; // these are immutable

            /**
               Constructor
               @param rR change in row
               @param rC change in column
             */
            Direction(int rR, int rC) {
                super();
                this.rR = rR;
                this.rC = rC;
            }

            public void actionPerformed(ActionEvent e) {
                switch(action) {
                    case ACTION_NONE: // this is a normal movement command
                        gameplay.moveHero(rR, rC);
                        updateUI();
                    break;
                    case ACTION_QUIT:
                        messages.add("OK, continuing game.");
                        action = ACTION_NONE;
                    break;
                    case ACTION_SAVE:
                        messages.add("OK, continuing game.");
                        action = ACTION_NONE;
                    break;
                    case ACTION_DROP: // drop command doesn't recognize a direction
                        messages.add("That doesn't refer to an item.");
                        action = ACTION_NONE;
                    break;
                    case ACTION_EQUIP: // equip command doesn't recognize a direction
                        messages.add("That doesn't refer to an item.");
                        action = ACTION_NONE;
                    break;
                    default:
                        System.err.println("ERROR: unrecognized action taken");
                    break; // end switch on action
                }
            }
        }

        /**
           Inner class for letter keys
           @author Stephen S. Lee
         */
        class Letter extends AbstractAction {
            private final char letter; // this is immutable

            /**
               Constructor
               @param letter Must be 'a'-'z' or 'A'-'Z'
             */
            Letter(char letter) {
                super();
                this.letter = letter;
            }

            /**
               Method for interpreting the pressed key in the context of the current command.
             */
            private void interpretLetter() {
                switch(action) {
                    case ACTION_NONE: // no pending action, so this is a command
                        performCommand();
                    break;
                    case ACTION_QUIT:
                        messages.add("OK, continuing game.");
                        action = ACTION_NONE;
                    break;
                    case ACTION_SAVE:
                        messages.add("OK, continuing game.");
                        action = ACTION_NONE;
                    break;
                    case ACTION_DROP: // this is specific item to drop
                        gameplay.dropItem(getIndex(letter));
                        action = ACTION_NONE;
                    break;
                    case ACTION_EQUIP: // this is a specific item to equip
                        gameplay.equipItem(getIndex(letter));
                        action = ACTION_NONE;
                    break;
                    default:
                        System.err.println("ERROR: unrecognized action taken");
                    break; // end switch on action
                }
            }

            /**
               Method for interpreting the pressed key as a command.
             */
            private void performCommand() {
                switch(letter) {
                    case 'd':
                        messages.add("Select an item to drop.");
                        action = ACTION_DROP;
                    break;
                    case 'E':
                        messages.add("Select an item to equip from your inventory.");
                        action = ACTION_EQUIP;
                    break;
                    case 'Q':
                        messages.add("Do you really want to quit?  Type '@' to confirm.");
                        action = ACTION_QUIT;
                    break;
                    case 'S':
                        messages.add("Do you really want to save your game and leave?");
                        messages.add("Type '@' to confirm.");
                        action = ACTION_SAVE;
                    break;
                    default:
                        messages.add("Unrecognized command.");
                    break; // end switch on letter
                }
            }

            public void actionPerformed(ActionEvent e) {
                interpretLetter();
                updateUI();
            }
        }

        // Up arrow
        imap.put(getKey(KeyEvent.VK_UP, 0), "north");
        amap.put("north", new Direction(-1, 0));

        // Down arrow
        imap.put(getKey(KeyEvent.VK_DOWN, 0), "south");
        amap.put("south", new Direction(1, 0));

        // Left arrow
        imap.put(getKey(KeyEvent.VK_LEFT, 0), "west");
        amap.put("west", new Direction(0, -1));

        // Right arrow
        imap.put(getKey(KeyEvent.VK_RIGHT, 0), "east");
        amap.put("east", new Direction(0, 1));

        // Home key (up and left)
        imap.put(getKey(KeyEvent.VK_HOME, 0), "northwest");
        amap.put("northwest", new Direction(-1, -1));

        // PgUp key (up and right)
        imap.put(getKey(KeyEvent.VK_PAGE_UP, 0), "northeast");
        amap.put("northeast", new Direction(-1, 1));

        // End key (down and left)
        imap.put(getKey(KeyEvent.VK_END, 0), "southwest");
        amap.put("southwest", new Direction(1, -1));

        // PgDn key (down and right)
        imap.put(getKey(KeyEvent.VK_PAGE_DOWN, 0), "southeast");
        amap.put("southeast", new Direction(1, 1));

        // period key (pass turn)
        imap.put(getKey('.'), "pass");
        amap.put("pass", new Direction(0, 0));

        // Letter presses (a to z and A to Z)
        for (char i = 'a'; i <= 'z'; i++) {
            imap.put(getKey(i), "" + i);
            amap.put("" + i, new Letter(i));
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            imap.put(getKey(i), "" + i);
            amap.put("" + i, new Letter(i));
        }

        // '>' (go down stairs)
        imap.put(getKey('>'), "descend stairs");
        amap.put("descend stairs", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                gameplay.descendStairs();
                updateUI();
                action = ACTION_NONE;
            }
        });

        // ',' (pick up stuff that's on the dungeon floor)
        imap.put(getKey(','), "get");
        amap.put("get", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                gameplay.pickUpStuff();
                updateUI();
                action = ACTION_NONE;
            }
        });

        // '@' confirm quit
        imap.put(getKey('@'), "confirm quit");
        amap.put("confirm quit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                switch (action) {
                    case ACTION_QUIT: // quit command confirmed
                        gameOver(GAME_OVER_QUIT);
                    break;
                    case ACTION_SAVE: // save command confirmed
                        try {
                            ObjectOutputStream writer = new ObjectOutputStream(
                                                        new FileOutputStream(savefile));
                            writer.writeObject(gameplay);
                            writer.close();
                            gameOver(GAME_OVER_SAVED);
                        } catch (FileNotFoundException ex) {
                            System.err.println("ERROR: " + ex);
                        } catch (IOException ex) {
                            System.err.println("ERROR: " + ex);
                        }
                    break;
                    default:
                        messages.add("Unrecognized command.");
                        action = ACTION_NONE;
                    break;
                }
            }
        });

        requestFocusInWindow();
		setFocusable(true);
    }

    /**
       Executes KeyStroke.getKeyStroke(int, int)
       @param keyCode A key code; can be of form KeyEvent.VK_[whatever]
       @param modifiers modifiers; normally 0
     */
    public static KeyStroke getKey(int keyCode, int modifiers) {return KeyStroke.getKeyStroke(keyCode, modifiers);}

    /**
       Executes KeyStroke.getKeyStroke(char keyChar)
       @param keyChar A character
     */
    public static KeyStroke getKey(char keyChar) {return KeyStroke.getKeyStroke(keyChar);}

    /**
       Updates the info pane.
       @author Stephen S. Lee (ssjlee@rawbw.com, ID#010013627)
     */
    public void updateInfo() {
        nameLine.setText("Name: " + gameplay.getHeroName());
        hpLine.setText("Hit Points: " + gameplay.getHeroHealth() + " out of " + gameplay.getHeroMaxHealth());
        accuracyLine.setText("Accuracy rating: " + gameplay.getHeroAccuracy());
        damageLine.setText("Damage rating: " + gameplay.getHeroDamage());
        armorClassLine.setText("Armor class: " + gameplay.getHeroArmorClass());
        weaponLine.setText("Equipped weapon: " + (gameplay.getHeroWeapon() == null ? "hands and feet"
                                                                                   : gameplay.getHeroWeapon().getName()));
        armorLine.setText("Equipped armor: " + (gameplay.getHeroArmor() == null ? "none"
                                                                                : gameplay.getHeroArmor().getName()));
        goldLine.setText("Gold pieces carried: " + gameplay.getHeroGold());
        depthLine.setText("On dungeon level: " + gameplay.getDepth());
        inventoryText.setText(gameplay.getHeroInventory());
    }

    /**
       Updates both the info pane and the map display.
       Also ends the game if appropriate.
       @author Stephen S. Lee
     */
    public void updateUI() {
        map.setText(gameplay.getView()); // update map
        updateInfo();                    // update info panel

        while (!messages.isEmpty()) {
            messagesDisplayed.add(messages.getFirst());
            if (++displayed > MAX_MESSAGES ) { // remove oldest messages from list of displayed messages once there are too many
                messagesDisplayed.removeFirst();
                displayed--;
            }
        }

        // Construct message window text
        StringBuilder messageOutput = new StringBuilder(MAX_MESSAGES * 20);
        for (String i : messagesDisplayed) {
            messageOutput.append(i);
            messageOutput.append("\n");
        }

        messageText.setText(messageOutput.toString()); // let messageText know about it all

        // This command causes the messagePane to automatically scroll to the bottom
        messageText.setCaretPosition(messageText.getDocument().getLength());

        if (gameplay.isHeroDead()) { // end the game if hero died
            gameOver(GAME_OVER_DIED);
        } else if (gameplay.hasHeroEscaped()) { // end the game if the hero escaped the dungeon
            gameOver(GAME_OVER_ESCAPED);
        }
    }

    /**
       Routine to end the game.
       Creates a splash screen with the reason for ending the game.
       @author Stephen S. Lee
       @param reason Reason the game is over (use a defined constant)
     */
    public void gameOver(int reason) {
        if (imap != null) {imap.clear();} // Prevent further keystroke to the game screen
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // don't allow closing the main window

        // generate a new dialog box
        JFrame gameOverFrame = new JFrame();
        JPanel gameOverDisplay = new JPanel();
        gameOverFrame.getContentPane().add(gameOverDisplay);
        gameOverDisplay.setLayout(new BorderLayout());
        JTextArea gameOverText = new JTextArea();
        gameOverText.setEditable(false);
		gameOverText.setFocusable(false); // corrects bug preventing keybinding from working
        gameOverText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        gameOverDisplay.add(gameOverText, BorderLayout.CENTER);

        gameOverText.append("The game session is over.\n\n");
        switch (reason) {
            case GAME_OVER_QUIT:
                gameOverText.append("You quit the game prematurely.\n\n");
            break;
            case GAME_OVER_SAVED:
                gameOverText.append("You saved the game and can reload later.");
            break;
            case GAME_OVER_DIED:
                gameOverText.append("You were killed by a vicious " + gameplay.getKiller() + ".\n\n");
            break;
            case GAME_OVER_ESCAPED:
                gameOverText.append("You survived the dungeon!\n\n");
            break;
            default:
                System.err.println("ERROR: unreachable default in switch not so unreachable after all");
            break;
        }

        if (reason != GAME_OVER_SAVED) {
            gameOverText.append("You finished with " + gameplay.getHeroGold() + " gold pieces.\n\n");
        }

        // add button to exit game
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        okButton.requestFocus();
        gameOverDisplay.add(okButton, BorderLayout.SOUTH);

        // put box on screen
        gameOverFrame.setPreferredSize(new Dimension(300, 300));
        gameOverFrame.setVisible(true);
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.pack();
        gameOverFrame.setLocationRelativeTo(null); // center the window
    }
}