package edu.sdccd.cisc191.server.controllers;

import edu.sdccd.cisc191.server.repositories.CharacterRepository;
import edu.sdccd.cisc191.server.models.Character;
import edu.sdccd.cisc191.server.models.User;
import edu.sdccd.cisc191.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {
    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private UserRepository userRepository;

    /**Get all characters
     * @route /api/characters
     */
    @GetMapping
    public List<Character> getAllCharacters() {
        return characterRepository.findAll();
    }

    /**Create Character
     * @param character Character type, missing values will result in null
     * @route /api/characters
     */
    @PostMapping
    public Character addCharacter(@RequestBody Character character) {
        return characterRepository.save(character);
    }

    /**Get character by id
     * @route /api/characters/:id
     * @param id Character Id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacter(@PathVariable Long id) {
        return characterRepository.findById(id)
                .map(character -> ResponseEntity.ok(character))
                .orElse(ResponseEntity.notFound().build());
    }

    /**Update User by Id
     * @route /api/characters/:id
     * @param id Character Id
     * @param updatedCharacter Desired Values to update
     */
    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacter(@PathVariable Long id, @RequestBody Character updatedCharacter) {
        Optional<Character> optionalCharacter = characterRepository.findById(id);

        if (optionalCharacter.isPresent()) {
            Character existingCharacter = optionalCharacter.get();

            existingCharacter.setName(updatedCharacter.getName());
            existingCharacter.setHealth(updatedCharacter.getHealth());
            existingCharacter.setLuck(updatedCharacter.getLuck());
            existingCharacter.setGold(updatedCharacter.getGold());
            existingCharacter.setStrength(updatedCharacter.getStrength());
            existingCharacter.setIntelligence(updatedCharacter.getIntelligence());
            existingCharacter.setType(updatedCharacter.getType());
            existingCharacter.setBeingUsed(updatedCharacter.isBeingUsed());

            Character savedCharacter = characterRepository.save(existingCharacter);

            return ResponseEntity.ok(savedCharacter);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**Claims a Character for a User
     * @route /api/characters/claim
     * @param claimRequest requires Character ID and User ID
     */
    @PostMapping("/claim")
    public ResponseEntity<String> claimCharacter(@RequestBody ClaimRequest claimRequest) {
        Long characterId = claimRequest.getCharacterId();
        Long userId = claimRequest.getUserId();

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (character.isBeingUsed()) {
            return ResponseEntity.status(409).body("Character is already claimed.");
        }

        character.setBeingUsed(true);
        character.setClaimedBy(user);

        user.setPlaying(true);
        user.setClaimedCharacter(character);

        characterRepository.save(character);
        userRepository.save(user);

        return ResponseEntity.ok("Character claimed successfully!");
    }

    /**Un-claims a Character for a User
     * @route /api/characters/unclaim
     * @param claimRequest requires Character ID and User ID
     */
    @PostMapping("/unclaim")
    public ResponseEntity<String> unclaimCharacter(@RequestBody ClaimRequest claimRequest) {
        Long characterId = claimRequest.getCharacterId();
        Long userId = claimRequest.getUserId();


        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!character.isBeingUsed() || !character.getClaimedBy().getId().equals(userId)) {
            return ResponseEntity.status(409).body("Character is not claimed by this user.");
        }

        character.setBeingUsed(false);
        character.setClaimedBy(null);

        user.setPlaying(false);
        user.setClaimedCharacter(null);

        characterRepository.save(character);
        userRepository.save(user);

        return ResponseEntity.ok("Character unclaimed successfully!");
    }


}
