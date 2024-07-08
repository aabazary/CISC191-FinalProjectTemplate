package edu.sdccd.cisc191.template;

import java.io.Serializable;

public abstract class Character implements Serializable {

    private String name;
    private int health;
    private int luck;
    private int gold;

    public Character(String name, int health, int luck, int gold) {
        this.name = name;
        this.health = health;
        this.luck = luck;
        this.gold = gold;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getLuck() {
        return luck;
    }

    public int getGold() {
        return gold;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name='" + name + "', health=" + health + ", luck=" + luck + ", gold=" + gold + "}";
    }
}
