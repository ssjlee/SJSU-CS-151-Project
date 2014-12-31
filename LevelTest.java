/**
*
*@author Simranjit Singh
*/
import org.junit.Test;
import utils.GameFunctions;
import static org.junit.Assert.*;

public class LevelTest {

    @Test
    public void testGetRowSize() throws Exception
    {
        Level level = new Level(GameFunctions.ROW_SIZE, GameFunctions.COLUMN_SIZE);
        assertEquals(level.getRowSize(), GameFunctions.ROW_SIZE);
    }

    @Test
    public void testGetColumnSize() throws Exception
    {
        Level level = new Level(GameFunctions.COLUMN_SIZE, GameFunctions.ROW_SIZE);
        assertEquals(level.getColumnSize(), GameFunctions.COLUMN_SIZE);
    }

    @Test
    public void testGetEmptyTile() throws Exception
    {
        Level level = new Level(GameFunctions.COLUMN_SIZE, GameFunctions.ROW_SIZE);
        int index[] = level.getEmptyTile();
        Tile empty = level.getTile(index[0], index[1]);
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testGetTile() throws Exception
    {
        Level level = new Level(GameFunctions.COLUMN_SIZE, GameFunctions.ROW_SIZE);
        Tile tile = level.getTile(0,0);
        assertTrue(tile.equals(level.getTile(0,0)));
    }
}//end of LevelTest