package edu.sdccd.cisc191.common.models;

public class Warrior extends Character {
    private int strength;
    private String type;

    public Warrior(String name, int health, int luck, int strength, int gold,String type) {
        super(name, health, luck, gold, type);
        this.strength = strength;
        this.type = "Warrior";
    }

    public int getStrength() {
        return strength;
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
