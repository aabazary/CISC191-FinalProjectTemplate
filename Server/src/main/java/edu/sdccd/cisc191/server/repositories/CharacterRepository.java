package edu.sdccd.cisc191.server.repositories;

import edu.sdccd.cisc191.server.models.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
}
