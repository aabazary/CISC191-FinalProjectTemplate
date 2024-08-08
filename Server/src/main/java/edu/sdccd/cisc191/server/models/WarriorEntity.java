package edu.sdccd.cisc191.server.models;

import jakarta.persistence.Entity;

@Entity
public class WarriorEntity extends CharacterEntity {
    private int strength;
    private String type;

    public WarriorEntity(String name, int health, int luck, int strength, int gold) {
        super(name, health, luck, gold);
        this.strength = strength;
        this.type = "Warrior";
    }

    public WarriorEntity() {

    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Warrior{" +
                "name='" + getName() + '\'' +
                ", health=" + getHealth() +
                ", luck=" + getLuck() +
                ", gold=" + getGold() +
                ", strength=" + strength +
                '}';
    }
}
