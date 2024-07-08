package edu.sdccd.cisc191.common;

public class Mage extends Character {
    private int intelligence;
    private String type;

    public Mage(String name, int health, int luck, int intelligence, int gold) {
        super(name, health, luck, gold);
        this.intelligence = intelligence;
        this.type = "Mage";
    }

    public int getIntelligence() {
        return intelligence;
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
