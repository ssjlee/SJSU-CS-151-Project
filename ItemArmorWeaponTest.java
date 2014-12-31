import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Borum Chhay
 * 
 */

public class ItemArmorWeaponTest {
	
	PossibleArmor armor = new PossibleArmor("Chainmail", 20, '[');
	PossibleWeapon weapon = new PossibleWeapon("Chainsaw", 25, 50, ']');
	
	@Test
	public void ArmorTest(){
		String name = armor.getName();
		int armorstrength = armor.getArmorClass();
		char symbol = armor.getSymbol();
		
		assertEquals("Chainmail", name);
		assertEquals(20, armorstrength);
		assertEquals('[', symbol);
	}
	
	@Test
	public void WeaponTest(){
		String name = weapon.getName();
		int accuracy = weapon.getAccuracy();
		int damage = weapon.getDamage();
		char symbol = weapon.getSymbol();
		
		assertEquals("Chainsaw", name);
		assertEquals(25, accuracy);
		assertEquals(50, damage);
		assertEquals(']', symbol);
	}
	
	@Test
	public void ArmorCreationTest(){
		assertNotNull(Armor.createArmor(armor));
	}
	
	@Test
	public void WeaponCreationTest(){
		assertNotNull(Weapon.createWeapon(weapon));
	}

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(ItemArmorWeaponTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
}
