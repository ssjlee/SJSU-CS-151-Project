import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Alvin Ma
 * 
 */

public class CreatureHeroMonsterTest {

	Hero hero = new Hero("PowerMan");
	PossibleMonster something = new PossibleMonster("Bubbles", 5, 10, 15, 20, 'b');	
	
	@Test
	public void HeroHealthtest() {		
		hero.setHealth(100);
		int health = hero.getHealth();
		assertEquals(100, health);
	}
	
	@Test
	public void HeroAccuracyTest(){		
		hero.setAccuracy(10);
		int accuracy = hero.getAccuracy();
		assertEquals(10, accuracy);	
	}
	
	@Test
	public void HeroDamageTest(){		
		hero.setDamage(5);
		int damage = hero.getDamage();
		assertEquals(5, damage);	
	}
	
	@Test
	public void HeroEvasionTest(){		
		hero.setEvasion(1);
		int evasion = hero.getEvasion();
		assertEquals(1, evasion);	
	}
	
	@Test
	public void HeroArmorTest(){		
		hero.setAccuracy(10);
		int accuracy = hero.getAccuracy();
		assertEquals(10, accuracy);	
	}
	
	@Test
	public void HeroNameTest(){
		String name = hero.getName();
		assertEquals("PowerMan", name);
	}
	
	@Test
	public void MonsterTest(){		
		String name = something.getName();
		int accuracy = something.getAccuracy();
		int damage = something.getDamage();
		int armor = something.getArmorClass();
		int health = something.getHealth();
		char symbol = something.getSymbol();
		
		assertEquals("Bubbles", name);
		assertEquals(5, accuracy);
		assertEquals(10, damage);
		assertEquals(15, armor);
		assertEquals(20, health);
		assertEquals('b', symbol);
	}
	
	
	@Test
	public void MonsterCreationTest(){
		assertNotNull(Monster.createMonster(something));
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(CreatureHeroMonsterTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}
