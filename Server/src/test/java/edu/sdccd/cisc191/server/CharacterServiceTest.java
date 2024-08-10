package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.server.repositories.CharacterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import edu.sdccd.cisc191.server.models.Character;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CharacterServiceTest {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private CharacterRepository characterRepository;

    @Test
    public void testProcessCharacterMultiThreading() throws Exception {
        // Create some test data
        Character character1 = new Character("Character1", 100, 10, 10, 20, 15, true,true, "Warrior", null);
        Character character2 = new Character("Character2", 100, 10, 20, 20, 15, true,true, "Warrior", null);
        characterRepository.saveAll(List.of(character1, character2));

        // Execute the service method concurrently
        CompletableFuture<Character> future1 = characterService.processCharacter(character1.getId());
        CompletableFuture<Character> future2 = characterService.processCharacter(character2.getId());

        // Wait for both tasks to complete
        CompletableFuture.allOf(future1, future2).join();

        // Verify that both characters were processed
        assertTrue(future1.isDone());
        assertTrue(future2.isDone());

        // Fetch the updated characters
        Character updatedCharacter1 = characterRepository.findById(character1.getId()).orElseThrow();
        Character updatedCharacter2 = characterRepository.findById(character2.getId()).orElseThrow();

        // Verify the updates
        assertEquals(20, updatedCharacter1.getGold());
        assertEquals(30, updatedCharacter2.getGold());
    }
}
