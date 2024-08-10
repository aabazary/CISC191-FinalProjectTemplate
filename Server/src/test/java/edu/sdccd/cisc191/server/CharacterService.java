package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.server.models.Character;
import edu.sdccd.cisc191.server.repositories.CharacterRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Async
    public CompletableFuture<Character> processCharacter(Long characterId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found"));

        // Simulate some time-consuming processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        character.setGold(character.getGold() + 10); // Sample update
        characterRepository.save(character);

        return CompletableFuture.completedFuture(character);
    }
}
