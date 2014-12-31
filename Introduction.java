/**
   Introduction screen object: welcome player and ask for a name.

   @author Stephen S. Lee (ssjlee@rawbw.com, ID#010013627)
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Introduction extends JFrame
{

    private String name = "";

    public String getName() {return name;}
    private void setTheName(String s) {name = s;} // JFrame already has a setName method, annoyingly

    public Introduction() {
        // Introduction screen and query for character name
        JPanel display = new JPanel();
        display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));

        JTextArea splash = new JTextArea(); // To be used to display a greeting
        splash.setEditable(false);
        display.add(splash);
        splash.append("Welcome to the CS 151 Game Project\n");
        splash.setFont(new Font("Serif", Font.BOLD, 24));

        JTextArea splash2 = new JTextArea(); // To be used to display the name prompt
        splash2.setEditable(false);
        display.add(splash2);
        splash2.append("Please enter your character's name:\n");
        splash2.append("A save game will be loaded if it exists.");
        splash2.setFont(new Font("Serif", Font.PLAIN, 12));

        final JTextField entry = new JTextField(); // To be used to print user input

        ActionListener introListen = new ActionListener()
        { // records the entered name once Enter is hit
            @Override public void actionPerformed(ActionEvent e)  //on the event that user types on the attached Component
            {
                setTheName(entry.getText());
            }
        };
        entry.addActionListener(introListen); //attach action listener to JTextField responsible for receiving user name

        addWindowListener(new WindowAdapter()
        { // this forces focus on the text entry field upon startup
            @Override public void windowOpened(WindowEvent e)
            {
                entry.requestFocus();
            }
        });
        display.add(entry);
        setContentPane(display);
        setResizable(false);
        setTitle("CS 151 Game Project");
    }
}
