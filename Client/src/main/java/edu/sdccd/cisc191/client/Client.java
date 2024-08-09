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
    private static final String SERVER_URL = "http://localhost:8080";

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

    private void showSignUpForm() {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");

        GridPane gridPane = createFormPane();
        addUIControlsForSignUp(gridPane);

        Scene scene = new Scene(gridPane, 400, 300);
        signUpStage.setScene(scene);
        signUpStage.show();
    }

    private void showLogInForm(Stage primaryStage) {
        Stage logInStage = new Stage();
        logInStage.setTitle("Log In");

        GridPane gridPane = createFormPane();
        addUIControlsForLogIn(gridPane, logInStage, primaryStage);

        Scene scene = new Scene(gridPane, 400, 300);
        logInStage.setScene(scene);
        logInStage.show();
    }

    private GridPane createFormPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        return gridPane;
    }

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

            String response = sendRequest("http://localhost:8080/api/users/signup", email, username, password);
            responseLabel.setText(response);
        });

        gridPane.add(submitButton, 1, 3);
    }

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
            String response = sendRequest("http://localhost:8080/api/users/login", email, "", password);
            System.out.println("Login response: " + response);

            if (response.contains("Loginsuccessful")) {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                String token = jsonObject.get("token").getAsString();

                System.out.println("Token: " + token);
                logInStage.close();
                showCharacterSelection(primaryStage,token);
            } else {
                responseLabel.setText("Login failed: " + response);
            }
        });

        gridPane.add(submitButton, 1, 2);
    }


    private void showCharacterSelection(Stage primaryStage, String userToken) {
        System.out.println("show character selection");
        primaryStage.setTitle("Select Your Character");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Fetch character data from the server using userToken
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


                    HBox characterCard = new HBox(10);
                    characterCard.setPadding(new Insets(10));
                    Label characterLabel = new Label(name);
                    Button selectButton = new Button("Select");
                    selectButton.setOnAction(e -> {
                        // Handle character selection
                        primaryStage.close();
                        showCharacterDetails(primaryStage, name, type,health, luck, gold, strength, intelligence);
                    });
                    characterCard.getChildren().addAll(characterLabel, selectButton);
                    vbox.getChildren().add(characterCard);
                }
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



    private void showCharacterDetails(Stage primaryStage, String name, String type, int health, int luck, int gold, int strength, int intelligence) {
        primaryStage.setTitle("Character Details");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label characterLabel = new Label(String.format("Character: %s (%s)", name, type));
        Label healthLabel = new Label("Health: " + health);
        Label luckLabel = new Label("Luck: " + luck);
        Label goldLabel = new Label("Gold: " + gold);
        Label strengthLabel = new Label("Strength: " + strength);
        Label intelligenceLabel = new Label("Intelligence: " + intelligence);

        vbox.getChildren().addAll(characterLabel, healthLabel, luckLabel, goldLabel, strengthLabel, intelligenceLabel);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private String fetchCharacterData(String token) {
        System.out.println("Fetching character data from " + "http://localhost:8080/api/characters");
        try {
            System.out.println("token: "+token);
            URL url = new URL("http://localhost:8080/api/characters");
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




    private String sendRequest(String urlString, String email, String username, String password) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Construct the JSON request body
            String jsonInputString = String.format("{\"email\":\"%s\", \"username\":\"%s\", \"password\":\"%s\"}", email, username, password);


            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check the response code
            System.out.println(connection.getResponseCode());
            int responseCode = connection.getResponseCode();


            StringBuilder response = new StringBuilder();
            try (Scanner scanner = new Scanner(
                    responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream(),
                    StandardCharsets.UTF_8.name())) {
                while (scanner.hasNext()) {
                    response.append(scanner.next());
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