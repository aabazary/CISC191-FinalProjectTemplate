package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CharacterTest {

    @Test
    void testMageInitialization() {
        Mage mage = new Mage("Gandalf", 20, 50,50,100);
        assertEquals("Gandalf", mage.getName());
        assertEquals(20, mage.getHealth());
        assertEquals(50, mage.getIntelligence());
    }

    @Test
    void testWarriorInitialization() {
        Warrior warrior = new Warrior("Aragorn", 50, 30,30,900);
        assertEquals("Aragorn", warrior.getName());
        assertEquals(50, warrior.getHealth());
        assertEquals(30, warrior.getStrength());
    }

    @Test
    void testMageInheritance() {
        Mage mage = new Mage("Gandalf", 20, 50,50,100);
        assertTrue(mage instanceof Character);
    }

    @Test
    void testWarriorInheritance() {
        Warrior warrior = new Warrior("Aragorn", 50, 30,30,900);
        assertTrue(warrior instanceof Character);
    }
}
