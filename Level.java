/**
   This class represents the abstract concept of a generic level in this Rogue-like game

   @author Simranjit Singh
 */

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import static utils.RandomGen.*;
import static utils.GameFunctions.*;

public class Level implements Serializable {

    // A level is a 2-dimensional matrix of Tiles
    private Tile matrix[][];

    /**
       Constructor for creating lLvel.
       @param rows    Number of rows for the level
       @param columns Number of columns for the level
     */
    public Level(int rows, int columns) {
        // not allowed to create levels that are too small
        if (rows < (ROOM_MIN_HEIGHT + 2) || columns < (ROOM_MIN_WIDTH + 2)) {
            System.err.println("FATAL ERROR: attempted to create level that is too small");
            throw new IllegalArgumentException();
        }

        matrix = new Tile[rows][columns];

        // Create a wall in every location
        for (int i = 0; i < getRowSize(); i++) {
            for (int j = 0; j < getColumnSize(); j++) {
                matrix[i][j] = new Tile();
            }
        }

        createRooms(); // "dig out" empty space on the level

    } //end of default constructor

    /**
       Takes a freshly generated level (all walls) and creates room for Creatures, Items, and other features.
       @author Stephen S. Lee
     */
    private void createRooms() {
        // Pick a number of rooms, and create array for level generation
        int[][] room = new int[randRange(ROOMS_MIN, ROOMS_MAX)][];
        for (int i = 0; i < room.length; i++) {
            room[i] = defineRoom(); // pick room coordinates; room coordinates may or may not overlap

            // This clears the room
            for (int j = room[i][0]; j <= room[i][2]; j++) {
                for (int k = room[i][1]; k <= room[i][3]; k++) {
                    matrix[j][k].createSpace();
                }
            }

            // Guarantee the room is connected to at least one of the other rooms by generating corridors
            if (i > 0) { // don't generate a corridor if there is just one room
                int d  = randRange(0, i - 1);               // pick a random destination room
                int r1 = randRange(room[d][0], room[d][2]); // pick a row in destination room
                int c1 = randRange(room[d][1], room[d][3]); // pick a column in destination room
                int r2 = randRange(room[i][0], room[i][2]); // pick a row in the just-generated room
                int c2 = randRange(room[i][1], room[i][3]); // pick a column in the just-generated room

                if (rand.nextBoolean()) { // flip a coin to select one of two different ways to connect the two locations
                    createSpaceRow(r1, c1, c2);
                    createSpaceColumn(c2, r1, r2);
                } else {
                    createSpaceRow(r2, c1, c2);
                    createSpaceColumn(c1, r1, r2);
                }
            }
        }
    } // end of method createRooms

    /**
       Generates coordinates for a room.
       The location of the room is completely random, no attempt is made to check for overlap.
       @author Stephen S. Lee
       @return int[4] as follows:
                      0 - upper left row
                      1 - upper left column
                      2 - lower right row
                      3 - lower right column
     */
    private int[] defineRoom() {
        int[] c = new int[4];
        
        int height = randRange(ROOM_MIN_HEIGHT, ROOM_MAX_HEIGHT); // room height
        int width  = randRange(ROOM_MIN_WIDTH, ROOM_MAX_WIDTH);   // room width
        
        // Row/column index cannot be 0 or maximum, as the edge of the level is surrounded by walls
        c[0] = randRange(1, getRowSize() - height - 1);
        c[1] = randRange(1, getColumnSize() - width - 1);
        c[2] = c[0] + height - 1;
        c[3] = c[1] + width - 1;
        return c;
    }
    
    /**
       Creates space in a row (helper method for corridor generation)
       @author Stephen S. Lee
       @param r Row to create space in
       @param c1 Origin column to create space in
       @param c2 Destination column to create space in
     */
    private void createSpaceRow(int r, int c1, int c2) {
        if (c1 > c2) { // swap if necessary
            int temp = c1;
            c1 = c2;
            c2 = temp;
        }
        for (int i = c1; i <= c2; i++) {
            matrix[r][i].createSpace();
        }
    }

    /**
       Creates space in a column (helper method for corridor generation)
       @author Stephen S. Lee
       @param c Column to create space in
       @param r1 Origin row to create space in
       @param r2 Destination row to create space in
     */
    private void createSpaceColumn(int c, int r1, int r2) {
        if (r1 > r2) { // swap if necessary
            int temp = r1;
            r1 = r2;
            r2 = temp;
        }
        for (int i = r1; i <= r2; i++) {
            matrix[i][c].createSpace();
        }
    }

    /**
       Returns a String representation of the currently visible part of the Level.
       The view is centered at the supplied coordinates; the rest of the Level is not visible.
       The is specifically done by iterating through the 2d array of tiles and printing each tile.
       An explicit newline is placed at the end of each row of tiles.
       The maximum size of the map display is MAP_SIZE.
       @author Stephen S. Lee
       @param r row to center map around
       @param c column to center map around
       @return String the representation of the currently visible map
     */
    public String viewMap(int r, int c) {
        StringBuilder representation = new StringBuilder(MAP_SIZE * (MAP_SIZE + 1));
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                int rRep = r - (MAP_SIZE / 2) + i;
                int cRep = c - (MAP_SIZE / 2) + j;
                // First check if this is a valid value
                if (rRep < 0 || rRep >= getRowSize() || cRep < 0 || cRep >= getColumnSize()) {
                    representation.append(' '); // a blank space is space off the map
                } else { // get appropriate tile symbol
                    representation.append(matrix[rRep][cRep].getSymbol());
                }
            } //end of inner for loop
            representation.append("\n");
        }//end of outer for loop
        return representation.toString();
    }//end of method viewMap


    /**
       Returns the number of rows in the level.
       @return rows in Tile matrix
     */
    public int getRowSize() {return matrix.length;}

    /**
       Returns the number of columns in the level.
       @return columns in Tile matrix
     */
    public int getColumnSize() {return matrix[0].length;}

    /**
       Finds a random empty tile on the current level.
       This is going to be really inefficient if the level is nearly full, and there is no support for the case in which the
         level actually IS full
       @author Stephen S. Lee
       @return {row, column} of a location that does not have a wall, creature, or item
     */
    public int[] getEmptyTile() {
       int[] c = new int[2];
       int tries = 0;
       do {
           c[0] = rand.nextInt(getRowSize());
           c[1] = rand.nextInt(getColumnSize());
       } while (!matrix[c[0]][c[1]].isEmpty());
       return c;
    }

    /**
       Returns Tile with the given coordinates.
       @param r row of matrix
       @param c column of matrix
       @return Tile that was requested
     */
    public Tile getTile(int r, int c) {return matrix[r][c];}

    /**
       Another version of getTile
       @param {row, column} of matrix
       @return Tile that was requested
     */
    public Tile getTile(int[] location) {return matrix[location[0]][location[1]];}

    /**
       Returns a number representing the square of the distance between two locations.
       (no square root as per the Pythagorean theorem, since that requires fp math)
       @author Stephen S. Lee
       @param r1 row of 1st coordinate
       @param c1 column of 1st coordinate
       @param r2 row of 2nd coordinate
       @param c2 column of 2nd coordinate
       @return square of the distance between 1st and 2nd coordinates
     */
    private static int distance(int r1, int c1, int r2, int c2) {return (r1 - r2) * (r1 - r2) + (c1 - c2) * (c1 - c2);}

    /**
       Suggests a 1-square move for a monster.  Returns the current square if no move is chosen.
       Prints an error if the origin tile does not actually contain a monster.
       Walls and other monsters may not be moved onto.
       Heroes CAN be moved onto; this represents an attack.
       @author Stephen S. Lee
       @param r1 row of monster
       @param c1 column of monster
       @param r2 row of target
       @param c2 column of target
       @return int[] with {row, column} of suggested destination
     */
    public int[] suggestMove(int r1, int c1, int r2, int c2) {
       int[] move = {r1, c1};
       int distance = Integer.MAX_VALUE;
       if (!matrix[r1][c1].hasMonster()) {
           System.err.println("FATAL ERROR: suggestMove called without a monster");
           return move; // panic default to origin square
       }

       // Iterate through all locations adjacent to origin
       try {
       Method isWall = Tile.class.getMethod("isWall"); // Includes completely gratuitous use of reflection
       for (int dR = -1; dR <= 1; dR++) {
           for (int dC = -1; dC <= 1; dC++) {
               int pR = r1 + dR; // possible destination row
               int pC = c1 + dC; // possible destination column
               if ((Boolean) isWall.invoke(matrix[pR][pC]) ||     // skip walls
                   matrix[pR][pC].hasMonster()) { // skip monsters
                   continue;
               };
               int pD = distance(pR, pC, r2, c2); // ok, we can move there, but is it a better place to move to?
               if (pD < distance) { // it is!
                   distance = pD;
                   move[0] = pR;
                   move[1] = pC;
               }
           }
       }
       } catch (Exception ex) {
           System.err.println(ex);
       }

       return move;
    }

} //end of class Level
