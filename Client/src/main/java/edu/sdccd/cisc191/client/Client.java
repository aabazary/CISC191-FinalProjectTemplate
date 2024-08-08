
package edu.sdccd.cisc191.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.sdccd.cisc191.common.models.Character;
import edu.sdccd.cisc191.common.models.CharacterAdapter;
import edu.sdccd.cisc191.common.models.Mage;
import edu.sdccd.cisc191.common.models.Warrior;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class Client extends Application {

    private static final int PORT = 8080;
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Character.class, new CharacterAdapter())
            .create();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Character Selection");
        showCharacterSelectionPanel(primaryStage);
        primaryStage.show();

    }


    //Game Panels:
    /**
     * Character Selection Panel
     * @param primaryStage
     */
    private void showCharacterSelectionPanel(Stage primaryStage) {

        Text title = new Text("Choose Your Character");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Button loadSavedButton = new Button("Load Saved");
        Button newCharacterButton = new Button("New Character");

        loadSavedButton.setOnAction(e -> showLoadSavedCharactersPanel(primaryStage));
        newCharacterButton.setOnAction(e -> showCreateNewCharacterPanel());


        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loadSavedButton, newCharacterButton);


        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(title, buttonBox);

        Scene mainScene = new Scene(mainLayout, 300, 200);
        primaryStage.setScene(mainScene);
    }

    /**
     * Load Saved Characters panel
     * @param primaryStage
     */
    private void showLoadSavedCharactersPanel(Stage primaryStage) {
        try (Socket socket = new Socket("localhost", PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeUTF("GET_CHARACTERS");
            out.flush();

            String response = in.readUTF();
            System.out.println("Received response: " + response);

            List<Character> characters = gson.fromJson(response, new TypeToken<List<Character>>(){}.getType());

            BorderPane borderPane = new BorderPane();
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            int colCount = 4;

            for (int i = 0; i < characters.size(); i++) {
                Character character = characters.get(i);
                Button selectButton = new Button("Select " + character.getName());
                selectButton.setOnAction(e -> showCharacterDetailsPanel(character));

                int colIndex = i % colCount;
                int rowIndex = i / colCount;

                gridPane.add(selectButton, colIndex, rowIndex);
            }

            // Create a Scroll and Back Button for the Grid pane
            ScrollPane scrollPane = new ScrollPane(gridPane);
            scrollPane.setFitToWidth(true);
            Button backButton = new Button("Back");
            backButton.setAlignment(Pos.CENTER);
            backButton.setOnAction(e -> showCharacterSelectionPanel(primaryStage));

            borderPane.setCenter(scrollPane);
            borderPane.setBottom(backButton);
            BorderPane.setAlignment(backButton, Pos.CENTER);

            Scene characterScene = new Scene(borderPane, 500, 400); // Adjusted size to better fit the grid
            primaryStage.setScene(characterScene);

        } catch (IOException e) {
            System.out.println("Error loading saved characters: " + e.getMessage());
        }
    }

    /**
     * Create New Character Panel
     */
    private void showCreateNewCharacterPanel() {
        Stage newCharacterStage = new Stage();
        newCharacterStage.setTitle("Create New Character");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        ComboBox<String> characterTypeComboBox = new ComboBox<>();
        characterTypeComboBox.getItems().addAll("Mage", "Warrior");
        characterTypeComboBox.setValue("Mage");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button saveButton = new Button("Save");

        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            if (name.isEmpty()) {
                showAlert("Error", "Name cannot be empty.");
                return;
            }

            int luck = (int) (Math.random() * 5) + 1; // Random luck value between 1 and 5
            String type = characterTypeComboBox.getValue();
            String newCharacterJson;
            if (type.equals("Mage")) {
                int health = 20;
                int intelligence = 50;
                int gold = 0;
                newCharacterJson = String.format("{\"type\":\"%s\",\"name\":\"%s\",\"health\":%d,\"luck\":%d,\"intelligence\":%d,\"gold\":%d}",
                        type, name, health, luck, intelligence, gold);
            } else { // Warrior
                int health = 50;
                int strength = 30;
                int gold = 0;
                newCharacterJson = String.format("{\"type\":\"%s\",\"name\":\"%s\",\"health\":%d,\"luck\":%d,\"strength\":%d,\"gold\":%d}",
                        type, name, health, luck, strength, gold);
            }

            String response = sendCharacterToServer(newCharacterJson);
            if (response.equals("Error: Character name already exists")) {
                errorLabel.setText("Character name already exists. Please choose a different name.");
            } else {
                newCharacterStage.close();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(nameField, characterTypeComboBox, saveButton, errorLabel);

        Scene scene = new Scene(layout, 400, 200);
        newCharacterStage.setScene(scene);
        newCharacterStage.show();
    }

    /**
     * Character Details Panel
     * @param character
     */
    private void showCharacterDetailsPanel(Character character) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Character Details");

        VBox detailsLayout = new VBox(10);
        detailsLayout.setPadding(new Insets(20));
        Label nameLabel = new Label("Name: " + character.getName());
        Label healthLabel = new Label("Health: " + character.getHealth());
        Label luckLabel = new Label("Luck: " + character.getLuck());
        Label goldLabel = new Label("Gold: " + character.getGold());

        //Conditionally Render stats based on type
        if (character instanceof Mage) {
            Mage mage = (Mage) character;
            Label intelligenceLabel = new Label("Intelligence: " + mage.getIntelligence());
            detailsLayout.getChildren().addAll(nameLabel, healthLabel, luckLabel, goldLabel, intelligenceLabel);
        } else if (character instanceof Warrior) {
            Warrior warrior = (Warrior) character;
            Label strengthLabel = new Label("Strength: " + warrior.getStrength());
            detailsLayout.getChildren().addAll(nameLabel, healthLabel, luckLabel, goldLabel, strengthLabel);
        }

        // Add a "Start Gathering" button: Will Eventually be "Start Game/Enter World" butoon
        Button startGatheringButton = new Button("Start Gathering");
        startGatheringButton.setOnAction(e -> {
            detailsStage.close();
            showGatheringPanel(character);
        });

        detailsLayout.getChildren().add(startGatheringButton);

        Scene detailsScene = new Scene(detailsLayout, 300, 250);
        detailsStage.setScene(detailsScene);
        detailsStage.show();
    }

    /**
     * Gathering Panel
     * @param character
     */
    private void showGatheringPanel(Character character) {
        Stage gatheringStage = new Stage();
        gatheringStage.setTitle("Gathering");

        Button level1Button = new Button("Level 1");
        level1Button.setOnAction(e -> showLevel1Panel(character, gatheringStage, new Stage())); // Pass the new Stage for details

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(level1Button);

        Scene scene = new Scene(layout, 200, 150);
        gatheringStage.setScene(scene);
        gatheringStage.show();
    }

    /**
     *  Level 1 Panel
     * @param character
     * @param gatheringStage
     * @param detailsStage
     */
    private void showLevel1Panel(Character character, Stage gatheringStage, Stage detailsStage) {
        gatheringStage.close();

        Stage level1Stage = new Stage();
        level1Stage.setTitle("Level 1");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label guessesLabel = new Label("Guesses Remaining: 2");
        guessesLabel.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(guessesLabel, gridPane);

        VBox.setVgrow(gridPane, Priority.ALWAYS);

        // Gather Game Layout, logic in handleButtonClick: Probably will refactor into a function
        Random random = new Random();
        int goldPosition = random.nextInt(4);

        //Button Creation for Gathering Game
        for (int i = 0; i < 4; i++) {
            Button button = new Button("Click");
            button.setMinSize(70, 70);
            button.setOnAction(e -> handleGatherButtonClick(character, button, guessesLabel, level1Stage, detailsStage));
            if (i == goldPosition) {
                button.setUserData("gold"); // Mark this button as the gold button
            } else {
                button.setUserData("click");
            }
            gridPane.add(button, i % 2, i / 2);
        }

        Scene level1Scene = new Scene(layout, 300, 250);
        level1Stage.setScene(level1Scene);
        level1Stage.show();
    }

    /**
     * Logic to play Gathering game. Will save updated character(gold) to json file
     * @param character
     * @param clickedButton
     * @param guessesLabel
     * @param level1Stage
     * @param detailsStage
     */

    void handleGatherButtonClick(Character character, Button clickedButton, Label guessesLabel, Stage level1Stage, Stage detailsStage) {
        String buttonType = (String) clickedButton.getUserData();
        int remainingGuesses = Integer.parseInt(guessesLabel.getText().replaceAll("\\D", ""));

        // User found the gold
        if ("gold".equals(buttonType)) {
            character.setGold(character.getGold() + 1);
            guessesLabel.setText("You found 1 gold! Gold: " + character.getGold());

            for (Node node : ((GridPane) clickedButton.getParent()).getChildren()) {
                Button button = (Button) node;
                if ("gold".equals(button.getUserData())) {
                    button.setStyle("-fx-background-color: gold;");
                    button.setText("Gold");
                } else {
                    button.setStyle("-fx-background-color: red;");
                    button.setText("X");
                }
                button.setDisable(true);
            }

            // Save the updated character to characters.json
            saveCharacterToFile(character);

            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> {
                level1Stage.close();
                refreshCharacterDetailsPanel(character, detailsStage);
            });
            ((VBox) clickedButton.getParent().getParent()).getChildren().add(closeButton);
            VBox.setMargin(closeButton, new Insets(10, 0, 0, 0));

        } else {
            // User didnt find gold
            remainingGuesses--;
            if (remainingGuesses <= 0) {
                guessesLabel.setText("You did not find gold!");

                // Disable all buttons and show which one was gold and which ones were wrong
                for (Node node : ((GridPane) clickedButton.getParent()).getChildren()) {
                    Button button = (Button) node;
                    if ("gold".equals(button.getUserData())) {
                        button.setStyle("-fx-background-color: gold;");
                        button.setText("Gold");
                    } else {
                        button.setStyle("-fx-background-color: red;");
                        button.setText("X");
                    }
                    button.setDisable(true);
                }


                Button closeButton = new Button("Close");
                closeButton.setOnAction(e -> {
                    level1Stage.close();
                    refreshCharacterDetailsPanel(character, detailsStage);
                });
                ((VBox) clickedButton.getParent().getParent()).getChildren().add(closeButton);
                VBox.setMargin(closeButton, new Insets(10, 0, 0, 0));

            } else {
                guessesLabel.setText("Incorrect! Guesses Remaining: " + remainingGuesses);
            }
        }
    }

    /**
     * Close existing
     * @param character
     * @param detailsStage
     */
    private void refreshCharacterDetailsPanel(Character character, Stage detailsStage) {
        // Close existing Character Details window
        if (detailsStage != null && detailsStage.isShowing()) {
            detailsStage.close();
        }
        showCharacterDetailsPanel(character);
    }

    /**
     * Saves character to json
     * @param character
     */
    public void saveCharacterToFile(Character character) {
        try {
            // Read existing characters from the JSON file
            List<Character> characters;
            try (BufferedReader reader = new BufferedReader(new FileReader("characters.json"))) {
                characters = gson.fromJson(reader, new TypeToken<List<Character>>(){}.getType());
            }

            // Update the character in the list
            for (int i = 0; i < characters.size(); i++) {
                if (characters.get(i).getName().equals(character.getName())) {
                    characters.set(i, character);
                    break;
                }
            }

            // Write the updated list back to the JSON file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("characters.json"))) {
                gson.toJson(characters, writer);
            }
        } catch (IOException e) {
            System.out.println("Error saving character to file: " + e.getMessage());
        }
    }

    /**
     * Function to send character to the Server
     * @param newCharacterJson
     * @return Server response
     */
    private String sendCharacterToServer(String newCharacterJson) {
        try (Socket socket = new Socket("localhost", PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {
            out.writeUTF("ADD_CHARACTER");
            out.writeUTF(newCharacterJson);
            out.flush();
            return in.readUTF();
        } catch (IOException ex) {
            System.out.println("Error saving new character: " + ex.getMessage());
            return "Error: Unable to save character";
        }
    }

    /**
     * Alert to show duplicate Character Name
     * @param title
     * @param content
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
//
    }
}

