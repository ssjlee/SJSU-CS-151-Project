/**
   GameMain
   CS 151 project executable
   This and UserInterface shouldn't really be separate objects like this, but it's too late to fix.
   @author Stephen S. Lee (ssjlee@rawbw.com, ID#010013627)
 */

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JFrame;

public class GameMain {

    private static final String MONSTER_LIST = "monster-list.txt";
    private static final String ITEM_LIST    = "item-list.txt";

    public static void main(String[] args) throws IOException {

        String name = "";

        // Introduction screen and query for character name
        Introduction introduction = new Introduction();
        introduction.setSize(100, 200);
        introduction.setVisible(true);
        introduction.pack();
        introduction.setLocationRelativeTo(null); // center the window

        // Hack - pause execution until character enters name
        while (name.length() == 0) {
            name = introduction.getName();
            System.out.print(""); // don't delete this, this is necessary for some completely mysterious reason
        }
        introduction.dispatchEvent(new WindowEvent(introduction, WindowEvent.WINDOW_CLOSING)); // get rid of introduction window

        // Read monster list
        List<String> monsters = Files.readAllLines(Paths.get(MONSTER_LIST), Charset.defaultCharset());
        List<String> items = Files.readAllLines(Paths.get(ITEM_LIST), Charset.defaultCharset());

        // Set up main user interface screen
        UserInterface ui = new UserInterface(name, monsters, items);
        ui.setVisible(true);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.pack();
        ui.setLocationRelativeTo(null); // center the window
    }

}
