package edu.sdccd.cisc191.template;

public class Warrior extends Character {
    private int strength;
    private String type;

    public Warrior(String name, int health, int luck, int strength, int gold) {
        super(name, health, luck, gold);
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
