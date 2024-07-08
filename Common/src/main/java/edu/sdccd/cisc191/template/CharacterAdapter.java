package edu.sdccd.cisc191.template;

import com.google.gson.*;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CharacterAdapter extends TypeAdapter<Character> {

    @Override
    public void write(JsonWriter out, Character character) throws IOException {
        out.beginObject();
        out.name("type").value(character.getClass().getName());
        out.name("name").value(character.getName());
        out.name("health").value(character.getHealth());
        out.name("luck").value(character.getLuck());
        out.name("gold").value(character.getGold());

        if (character instanceof Mage) {
            Mage mage = (Mage) character;
            out.name("intelligence").value(mage.getIntelligence());
        } else if (character instanceof Warrior) {
            Warrior warrior = (Warrior) character;
            out.name("strength").value(warrior.getStrength());
        }
        out.endObject();
    }

    @Override
    public Character read(JsonReader in) throws IOException {
        in.beginObject();
        String type = "";
        String name = "";
        int health = 0;
        int luck = 0;
        int intelligence = 0;
        int strength = 0;
        int gold = 0;

        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "type":
                    type = in.nextString();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "health":
                    health = in.nextInt();
                    break;
                case "luck":
                    luck = in.nextInt();
                    break;
                case "gold":
                    gold = in.nextInt();
                    break;
                case "intelligence":
                    intelligence = in.nextInt();
                    break;
                case "strength":
                    strength = in.nextInt();
                    break;
            }
        }
        in.endObject();

        if (type.equals("Mage")) {
            return new Mage(name, health, luck, intelligence, gold);
        } else if (type.equals("Warrior")) {
            return new Warrior(name, health, luck, strength, gold);
        } else {
            throw new JsonParseException("Unknown character type: " + type);
        }
    }
}
