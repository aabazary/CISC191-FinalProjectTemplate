package edu.sdccd.cisc191.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client extends Application {

    private String userToken;
    private Long userId;
    private static final String SERVER_URL = "http://localhost:8080";

    /**Start Window(Login/Signup)
     * @param primaryStage Primary Stage, used to switch our primary between different scenes
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sign Up or Log In");

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> showSignUpForm());

        Button logInButton = new Button("Log In");
        logInButton.setOnAction(e -> showLogInForm(primaryStage));

        VBox vbox = new VBox(10, signUpButton, logInButton);
        vbox.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handler to Show SignupForm, Calls addUIControlsForSignUp
     */
    private void showSignUpForm() {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");

        GridPane gridPane = createFormPane();
        addUIControlsForSignUp(gridPane);

        Scene scene = new Scene(gridPane, 400, 300);
        signUpStage.setScene(scene);
        signUpStage.show();
    }

    /**
     * Handler to Show LoginForm, Calls addUIControlsForLogIn
     */
    private void showLogInForm(Stage primaryStage) {
        Stage logInStage = new Stage();
        logInStage.setTitle("Log In");

        GridPane gridPane = createFormPane();
        addUIControlsForLogIn(gridPane, logInStage, primaryStage);

        Scene scene = new Scene(gridPane, 400, 300);
        logInStage.setScene(scene);
        logInStage.show();
    }

    /**
     * Basic GridPane
     * @return gridPane with padding and gap set
     */
    private GridPane createFormPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        return gridPane;
    }

    /**
     * Create the UI for the Sign-Up Form
     * @param gridPane Basic GridPane
     */
    private void addUIControlsForSignUp(GridPane gridPane) {
        Label emailLabel = new Label("Email:");
        gridPane.add(emailLabel, 0, 0);
        TextField emailField = new TextField();
        gridPane.add(emailField, 1, 0);

        Label usernameLabel = new Label("Username:");
        gridPane.add(usernameLabel, 0, 1);
        TextField usernameField = new TextField();
        gridPane.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        gridPane.add(passwordLabel, 0, 2);
        PasswordField passwordField = new PasswordField();
        gridPane.add(passwordField, 1, 2);

        Button submitButton = new Button("Sign Up");
        Label responseLabel = new Label();
        gridPane.add(responseLabel, 1, 4);

        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            String response = sendRequest(SERVER_URL+ "/api/users/signup", email, username, password);
            responseLabel.setText(response);
        });

        gridPane.add(submitButton, 1, 3);
    }

    /**
     * Add UI logic for the Log In Form
     * @param gridPane Basic Grid Pane
     * @param logInStage Stage for Login
     * @param primaryStage Main Stage
     */
    private void addUIControlsForLogIn(GridPane gridPane, Stage logInStage, Stage primaryStage) {

        Label emailLabel = new Label("Email:");
        gridPane.add(emailLabel, 0, 0);
        TextField emailField = new TextField();
        gridPane.add(emailField, 1, 0);

        Label passwordLabel = new Label("Password:");
        gridPane.add(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        gridPane.add(passwordField, 1, 1);

        Button submitButton = new Button("Log In");
        Label responseLabel = new Label();
        gridPane.add(responseLabel, 1, 3);

        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            System.out.println("Attempting to log in with email: " + email);
            String response = sendRequest(SERVER_URL + "/api/users/login", email, "", password);
            System.out.println("Login response: " + response);

            if (response.contains("Login successful")) {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                String token = jsonObject.get("token").getAsString();
                this.userId = jsonObject.get("userId").getAsLong();
                this.userToken= token;

                System.out.println("Token: " + token);
                logInStage.close();
                showMainMenu(primaryStage,token);
            } else {
                responseLabel.setText("Login failed: " + response);
            }
        });

        gridPane.add(submitButton, 1, 2);
    }

    /**
     * Main Menu Window
     * @param primaryStage Main Stage
     * @param userToken Token, Currently not working(route not protected)
     */
    private void showMainMenu(Stage primaryStage, String userToken) {
        primaryStage.setTitle("Main Menu");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        String characterData = fetchCharacterData(userToken);

        Button selectCharacterButton = new Button("Select Your Character");
        selectCharacterButton.setDisable(false);

        boolean isCharacterClaimed = false;

        if (characterData != null && !characterData.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(characterData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (jsonObject.has("claimedBy") && !jsonObject.isNull("claimedBy")) {
                        JSONObject claimedByUser = jsonObject.getJSONObject("claimedBy");
                        Long claimedById = claimedByUser.getLong("id");

                        if (claimedById.equals(this.userId)) {
                            String name = jsonObject.getString("name");
                            Long characterID = jsonObject.getLong("id");

                            HBox characterCard = new HBox(10);
                            characterCard.setPadding(new Insets(10));

                            Label characterLabel = new Label(name);
                            Button unclaimButton = new Button("Unclaim");

                            unclaimButton.setOnAction(e -> {
                                unclaimCharacter(characterID, userToken);
                                selectCharacterButton.setDisable(false);
                                showMainMenu(primaryStage, userToken);
                            });

                            characterCard.getChildren().addAll(characterLabel, unclaimButton);
                            vbox.getChildren().add(characterCard);

                            isCharacterClaimed = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Label errorLabel = new Label("Error parsing character data.");
                vbox.getChildren().add(errorLabel);
            }
        }

        selectCharacterButton.setDisable(isCharacterClaimed);

        selectCharacterButton.setOnAction(e -> showCharacterSelection(primaryStage, userToken));

        Button backToLoginButton = new Button("Back to Login/Signup");
        backToLoginButton.setOnAction(e -> start(primaryStage));

        vbox.getChildren().addAll(selectCharacterButton, backToLoginButton);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Un-claims a Character from a User, updates both models
     * @param characterId Characters ID
     * @param userToken Token, Currently not working(route not protected)
     */
    private void unclaimCharacter(Long characterId, String userToken) {
        try {
            URL url = new URL(SERVER_URL + "/api/characters/unclaim");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + userToken);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setInstanceFollowRedirects(false);
            connection.setDoOutput(true);

            String jsonInputString = String.format("{\"characterId\": %d, \"userId\": %d}", characterId, userId);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Character unclaimed successfully!");
            } else {
                System.out.println("Failed to unclaim character. Response Code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Character Selection Pane, Shows all characters, if a character is already claimed, user cannot claim
     * @param primaryStage Main Stage
     * @param userToken Token, Currently not working(route not protected)
     */
    private void showCharacterSelection(Stage primaryStage, String userToken) {
        System.out.println("show character selection");
        primaryStage.setTitle("Select Your Character");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        String characterData = fetchCharacterData(userToken);
        if (characterData == null || characterData.isEmpty()) {
            System.out.println("No character data received.");
            Label errorLabel = new Label("Failed to load character data.");
            vbox.getChildren().add(errorLabel);
        } else {
            System.out.println("Character data received: " + characterData);

            try {
                JSONArray jsonArray = new JSONArray(characterData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    int health = jsonObject.getInt("health");
                    int luck = jsonObject.getInt("luck");
                    int gold = jsonObject.getInt("gold");
                    String type = jsonObject.getString("type");
                    int strength = jsonObject.getInt("strength");
                    int intelligence = jsonObject.getInt("intelligence");
                    boolean isBeingUsed = jsonObject.getBoolean("beingUsed");
                    Long characterID = jsonObject.getLong("id");


                    HBox characterCard = new HBox(10);
                    characterCard.setPadding(new Insets(10));
                    Label characterLabel = new Label(name);
                    Button selectButton = new Button("Select");
                    if (isBeingUsed) {
                        selectButton.setDisable(true);
                    } else {
                        selectButton.setOnAction(e -> {
                            primaryStage.close();
                            showCharacterDetails(primaryStage, name, type, health, luck, gold, strength, intelligence, isBeingUsed, characterID);
                        });
                    }
                    characterCard.getChildren().addAll(characterLabel, selectButton);
                    vbox.getChildren().add(characterCard);
                }
                Button backButton = new Button("Back");
                backButton.setOnAction(e -> showMainMenu(primaryStage,userToken));
                vbox.getChildren().add(backButton);

            } catch (Exception e) {
                e.printStackTrace();
                Label errorLabel = new Label("Error parsing character data.");
                vbox.getChildren().add(errorLabel);
            }
        }

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Detail Pane for character, location for user to Claim character
     * @param primaryStage Main Stage
     * @param name Character Name
     * @param type Character Type
     * @param health Character Health
     * @param luck Character Luck
     * @param gold Character Gold
     * @param strength Character Strength
     * @param intelligence Character Intelligence
     * @param isBeingUsed Is Character Being Used
     * @param characterId Character ID
     */
    private void showCharacterDetails(Stage primaryStage, String name, String type, int health, int luck, int gold, int strength, int intelligence, boolean isBeingUsed, Long characterId) {
        primaryStage.setTitle("Character Details");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label characterLabel = new Label(String.format("Character: %s (%s)", name, type));
        Label healthLabel = new Label("Health: " + health);
        Label luckLabel = new Label("Luck: " + luck);
        Label goldLabel = new Label("Gold: " + gold);
        Label strengthLabel = new Label("Strength: " + strength);
        Label intelligenceLabel = new Label("Intelligence: " + intelligence);

        Label statusLabel = new Label();

        Button claimButton = new Button("Claim");
        claimButton.setOnAction(e -> {
            boolean success = claimCharacter(characterId, this.userId);
            if (success) {
                statusLabel.setText("Success!");
                showMainMenu(primaryStage, userToken);
            } else {
                statusLabel.setText("Failure");
            }
        });

        vbox.getChildren().addAll(characterLabel, healthLabel, luckLabel, goldLabel, strengthLabel, intelligenceLabel, claimButton, statusLabel);

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Claims a Character, updating both the User and Character
     * @param characterId Character's ID
     * @param userId User's ID
     */
    private boolean claimCharacter(Long characterId, Long userId) {
        try {
            URL url = new URL(SERVER_URL + "/api/characters/claim");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            String jsonInputString = String.format("{\"characterId\": %d, \"userId\": %d}", characterId, userId);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Character claimed successfully!");
                return true;
            } else {
                System.out.println("Failed to claim character.");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Getting Character Data,eventually as a protected route
     * @param token Token, Currently not working(route not protected)
     */
    private String fetchCharacterData(String token) {
        System.out.println("Fetching character data from " + SERVER_URL+ "/api/characters");
        try {
            System.out.println("token: "+token);
            URL url = new URL(SERVER_URL +"/api/characters");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                    return scanner.useDelimiter("\\A").next();
                }
            } else {
                try (Scanner scanner = new Scanner(connection.getErrorStream(), StandardCharsets.UTF_8.name())) {
                    return "Error: " + scanner.useDelimiter("\\A").next();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * HTTP Post request to urlString endpoint
     * @param urlString Specified URL for request
     * @param email User Email
     * @param username User Username
     * @param password User Password
     */
    private String sendRequest(String urlString, String email, String username, String password) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = String.format("{\"email\":\"%s\", \"username\":\"%s\", \"password\":\"%s\"}", email, username, password);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            System.out.println(connection.getResponseCode());
            int responseCode = connection.getResponseCode();

            StringBuilder response = new StringBuilder();
            try (Scanner scanner = new Scanner(
                    responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream(),
                    StandardCharsets.UTF_8.name())) {
                while (scanner.hasNext()) {
                    response.append(scanner.next()).append(" ");
                }
            }

            System.out.println(response);

            return response.toString();
        } catch (IOException e) {

            return "Error: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
    launch(args);
}
}