package edu.sdccd.cisc191.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.sdccd.cisc191.common.Character;
import edu.sdccd.cisc191.common.CharacterAdapter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 5000;
    private static final String CHARACTERS_JSON_PATH = "characters.json";
    private final Gson gson;

    public Server() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Character.class, new CharacterAdapter())
                .create();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started and waiting for connections on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                    System.out.println("Client connected");

                    String request = in.readUTF();
                    System.out.println("Received request: " + request);

                    if ("ADD_CHARACTER".equals(request)) {
                        String characterJson = in.readUTF();
                        System.out.println("Received character JSON: " + characterJson);

                        // Deserialize the received character
                        Character character = gson.fromJson(characterJson, Character.class);

                        // Check for duplicate character name
                        List<Character> characters = readCharactersFromFile();
                        boolean nameExists = characters.stream()
                                .anyMatch(c -> c.getName().equals(character.getName()));

                        if (nameExists) {
                            out.writeUTF("Error: Character name already exists");
                        } else {
                            // Add the character to the file
                            addCharacterToFile(character);
                            out.writeUTF("Character added successfully");
                        }
                        out.flush();
                    } else if ("GET_CHARACTERS".equals(request)) {
                        // Read characters from file and send them to the client
                        List<Character> characters = readCharactersFromFile();
                        String json = gson.toJson(characters);
                        out.writeUTF(json);
                        out.flush();
                    }
                } catch (IOException e) {
                    System.out.println("Error in communication with client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    private void addCharacterToFile(Character character) {
        List<Character> characters = readCharactersFromFile();
        characters.add(character);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHARACTERS_JSON_PATH))) {
            gson.toJson(characters, writer);
        } catch (IOException e) {
            System.out.println("Error writing to characters.json: " + e.getMessage());
        }
    }

    private List<Character> readCharactersFromFile() {
        List<Character> characters = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CHARACTERS_JSON_PATH))) {
            Character[] charactersArray = gson.fromJson(reader, Character[].class);
            if (charactersArray != null) {
                for (Character character : charactersArray) {
                    characters.add(character);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading characters.json: " + e.getMessage());
        }
        return characters;
    }
}
