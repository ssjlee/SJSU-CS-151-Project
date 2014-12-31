/**
   MessageQueue
   Queue of messages
 
   @author Stephen S. Lee
 */

import java.util.LinkedList;

public class MessageQueue implements Runnable {

    private int current;                 // current number of messages in the queue
    private LinkedList<String> messages; // saved messages

    /**
       Constructor
     */
    public MessageQueue() {
        current = 0;
        messages = new LinkedList<String>();
    }
    
    /**
       Returns whether or not the queue is empty
       @return true if there are no messages in the queue, false otherwise
     */
    public boolean isEmpty() {return current == 0;}
    
    /**
       Adds a message to the back of the queue.
       @param s The String to be added to the message queue.
     */
    public synchronized void add(String s) {
        messages.addLast(s);
        current++;
    }
     
    /**
       Gets the first String in the list, and removes it from the list.
       @return String at the front of the queue (null if it is empty)
     */
    public synchronized String getFirst() {
        current--;
        return messages.pollFirst();
    }
     
    public void run() {
        while (true) {
            // try {
            //     if (isEmpty()) {
            //        wait();
            //     }
            // } catch (Exception ex) {
            // }
        }
    }
}
