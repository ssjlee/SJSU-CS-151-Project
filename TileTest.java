import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Manan Bhagat
 * @author Simranjit Singh
 */
public class TileTest
{

    @Test
    public void testSetandGet_Creature() throws Exception
    {
        Tile testTile = new Tile();
        PossibleMonster protoRat = new PossibleMonster("rat", 10, 10, 10, 10, 'r');
        Monster monsterRat  = Monster.createMonster(protoRat);
        testTile.setCreature(monsterRat);
        assertEquals(testTile.getCreature(), monsterRat);
    }

    @Test
    public void testSetandGet_Item() throws Exception
    {
        Tile testTile = new Tile();
        PossibleWeapon protoSword = new PossibleWeapon("sword", 10, 10 , 'r');
        Weapon sword = Weapon.createWeapon(protoSword);
        testTile.setItem(sword);
        assertEquals(testTile.getItem(), sword);
    }

    @Test
    public void testSetandGet_Gold() throws Exception
    {
        Tile testTile = new Tile();
        int gold = 10;
        testTile.setGold(10);
        assertEquals(testTile.getGold(), gold);
    }

    @Test
    public void testSetDownStairs() throws Exception
    {
        Tile testTile = new Tile();
        testTile.createSpace();
        testTile.setDownStairs();
        assertTrue(testTile.hasDownStairs());
    }

    @Test
    public void testIsWall() throws Exception
    {
        Tile testTile = new Tile();
        assertTrue(testTile.isWall());
    }

    @Test
    public void testHasDownStairs() throws Exception
    {
        Tile testTile = new Tile();
        testTile.createSpace();
        testTile.setDownStairs();
        assertTrue(testTile.hasDownStairs());
    }

    @Test
    public void testHasCreature() throws Exception
    {
        Tile testTile = new Tile();
        PossibleMonster protoRat = new PossibleMonster("rat", 10, 10, 10, 10, 'r');
        Monster monsterRat  = Monster.createMonster(protoRat);
        assertFalse(testTile.hasCreature());
        testTile.setCreature(monsterRat);
        assertTrue(testTile.hasCreature());
    }


    @Test
    public void testIsEmpty() throws Exception
    {
        Tile testTile = new Tile();
        testTile.createSpace();
        assertTrue(testTile.isEmpty());
    }

    @Test
    public void testCreateSpace() throws Exception
    {
        Tile testTile = new Tile();
        assertFalse(testTile.isEmpty());
        testTile.createSpace();
        assertTrue(testTile.isEmpty());
    }

    @Test
    public void testGetSymbol() throws Exception
    {
        Tile testTile = new Tile();
        assertEquals('#', testTile.getSymbol());
    }
}//TileTest