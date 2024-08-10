package edu.sdccd.cisc191.server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String username;
    private String password;
    private int score;
    private boolean isPlaying = false;

    @OneToOne
    @JsonIgnoreProperties("claimedBy")
    private Character claimedCharacter;

    // Constructors
    public User() {
    }

    public User(String email, String username, String password, int score, boolean isPlaying, Character claimedCharacter) {
        this.email = email;
        this.username = username;
        setPassword(password); // Ensure the password is hashed when setting
        this.score = score;
        this.isPlaying = isPlaying;
        this.claimedCharacter = claimedCharacter;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Character getClaimedCharacter() {
        return claimedCharacter;
    }

    public void setClaimedCharacter(Character claimedCharacter) {
        this.claimedCharacter = claimedCharacter;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", score=" + score +
                ", isPlaying=" + isPlaying +
                ", claimedCharacter=" + (claimedCharacter != null ? claimedCharacter.getName() : "null") +
                '}';
    }
}
