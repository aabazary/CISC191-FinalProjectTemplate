package edu.sdccd.cisc191.server.models;

import jakarta.persistence.Entity;

@Entity
public class MageEntity extends CharacterEntity {
    private int intelligence;
    private String type;

    public MageEntity(String name, int health, int luck, int intelligence, int gold) {
        super(name, health, luck, gold);
        this.intelligence = intelligence;
        this.type = "Mage";
    }

    public MageEntity(){

    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Mage{" +
                "name='" + getName() + '\'' +
                ", health=" + getHealth() +
                ", luck=" + getLuck() +
                ", gold=" + getGold() +
                ", intelligence=" + intelligence +
                '}';
    }
}
