package edu.sdccd.cisc191.server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int health;
    private int luck;
    private int gold;
    private int strength;
    private int intelligence;
    private boolean isPublic;
    private boolean isBeingUsed;
    private String type;

    @ManyToOne
    @JsonIgnoreProperties("claimedCharacter")
    private User claimedBy;


    //Constructors
    public Character(String name, int health, int luck, int gold, int strength, int intelligence, boolean isPublic, boolean isBeingUsed, String type, User claimedBy) {
        this.name = name;
        this.health = health;
        this.luck = luck;
        this.gold = gold;
        this.strength = strength;
        this.intelligence = intelligence;
        this.isPublic = isPublic;
        this.isBeingUsed = isBeingUsed;
        this.type = type;
        this.claimedBy = claimedBy;
    }

    public Character() {
    }
    // Getters and setters

    public User getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(User claimedBy) {
        this.claimedBy = claimedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public boolean isBeingUsed() {
        return isBeingUsed;
    }

    public void setBeingUsed(boolean beingUsed) {
        this.isBeingUsed = beingUsed;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", health=" + health +
                ", luck=" + luck +
                ", gold=" + gold +
                ", strength=" + strength +
                ", intelligence=" + intelligence +
                ", isPublic=" + isPublic +
                ", isBeingUsed=" + isBeingUsed +
                ", type='" + type + '\'' +
                ", claimedBy=" + (claimedBy != null ? claimedBy.getUsername() : "null") +
                '}';
    }
}