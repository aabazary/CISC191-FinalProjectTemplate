package edu.sdccd.cisc191.server.repositories;

import edu.sdccd.cisc191.common.models.Character;
import edu.sdccd.cisc191.server.models.CharacterEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends CrudRepository<CharacterEntity, Long> {
}
