package edu.sdccd.cisc191.common.models;

public class Mage extends Character {
    private int intelligence;
    private String type;

    public Mage(String name, int health, int luck, int intelligence, int gold,String type) {
        super(name, health, luck, gold,type);
        this.intelligence = intelligence;
        this.type = "Mage";
    }

    public Mage(){

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
