package edu.sdccd.cisc191.server.controllers;

import edu.sdccd.cisc191.common.models.Character;

import edu.sdccd.cisc191.common.models.Mage;
import edu.sdccd.cisc191.server.dtos.CharacterDTO;
import edu.sdccd.cisc191.server.models.CharacterEntity;
import edu.sdccd.cisc191.server.models.MageEntity;
import edu.sdccd.cisc191.server.repositories.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/characters")
public class CharacterController {

    @Autowired
    private CharacterRepository characterRepository;

    @PostMapping
    public CharacterEntity createCharacter(@RequestBody CharacterDTO characterDTO) {
        CharacterEntity character;

        character = new MageEntity(characterDTO.getName(), characterDTO.getHealth(), characterDTO.getLuck(), characterDTO.getGold(), 20);
        return characterRepository.save(character);

    }

    // Other CRUD
}
