package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.server.models.User;
import edu.sdccd.cisc191.server.models.Character;
import edu.sdccd.cisc191.server.repositories.UserRepository;
import edu.sdccd.cisc191.server.repositories.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
public class CRUDTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CharacterRepository characterRepository;

    private User user;
    private Character character;

    @BeforeEach
    public void setup() {
        user = new User("test@example.com", "testuser", "password", 100);
        character = new Character("Hero", 100, 10, 50, 20, 15, true,true, "Warrior");
    }

    @Test
    public void testCreateUser() {
        userRepository.save(user);
        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void testCreateCharacter() {
        characterRepository.save(character);
        assertThat(character.getId()).isNotNull();
    }

    @Test
    public void testReadUser() {
        userRepository.save(user);
        User foundUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void testReadCharacter() {
        characterRepository.save(character);
        Character foundCharacter = characterRepository.findById(character.getId()).orElse(null);
        assertThat(foundCharacter).isNotNull();
        assertThat(foundCharacter.getName()).isEqualTo(character.getName());
    }

    @Test
    public void testUpdateUser() {
        userRepository.save(user);
        user.setScore(200);
        userRepository.save(user);
        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(updatedUser.getScore()).isEqualTo(200);
    }

    @Test
    public void testUpdateCharacter() {
        characterRepository.save(character);
        character.setHealth(200);
        characterRepository.save(character);
        Character updatedCharacter = characterRepository.findById(character.getId()).orElse(null);
        assertThat(updatedCharacter.getHealth()).isEqualTo(200);
    }

    @Test
    public void testDeleteUser() {
        userRepository.save(user);
        userRepository.delete(user);
        User deletedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(deletedUser).isNull();
    }

    @Test
    public void testDeleteCharacter() {
        characterRepository.save(character);
        characterRepository.delete(character);
        Character deletedCharacter = characterRepository.findById(character.getId()).orElse(null);
        assertThat(deletedCharacter).isNull();
    }
}
