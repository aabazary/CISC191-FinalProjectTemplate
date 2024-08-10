package edu.sdccd.cisc191.server.controllers;

public class ClaimRequest {
    private Long characterId;
    private Long userId;

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}