import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {
  private ComboBox<String> typeBox, commandBox;
  private TextField valueField;
  private TextArea drawArea;
  private Circle statusCircle;

  private final String SERVER_HOST = "localhost";
  private final int SERVER_PORT = 1234;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    typeBox = new ComboBox<>();
    typeBox.getItems().addAll("int", "double", "string");
    typeBox.setValue("int");

    commandBox = new ComboBox<>();
    commandBox.getItems().addAll("insert", "delete", "search", "draw");
    commandBox.setValue("insert");

    valueField = new TextField();

    Button sendButton = new Button("Wyslij");
    sendButton.setOnAction(_ -> sendRequest());

    statusCircle = new Circle(8, Color.GRAY);

    HBox controls = new HBox(
      10,
      new Label("Operacja:"),
      commandBox,
      new Label("Typ:"),
      typeBox,
      new Label("Wartosc:"),
      valueField,
      sendButton,
      statusCircle
    );

    controls.setPadding(new Insets(10));
    controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    drawArea = new TextArea();
    drawArea.setEditable(false);
    drawArea.setPrefRowCount(12);
    drawArea.setPromptText("...");

    VBox root = new VBox(10, controls, drawArea);
    root.setPadding(new Insets(10));

    commandBox.setOnAction(_ -> {
      if (commandBox.getValue().equals("draw")) {
        valueField.setDisable(true);
        valueField.clear();
      } else {
        valueField.setDisable(false);
      }
    });

    Scene scene = new Scene(root, 700, 400);
    primaryStage.setTitle("Klient - drzewo binarne");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void sendRequest() {
    String command = commandBox.getValue();
    String type = typeBox.getValue();
    String value = valueField.getText().trim();

    if (!command.equals("draw") && value.isEmpty()) {
      setStatus(false);
      return;
    }

    if (!command.equals("draw")) {
      if (type.equals("int")) {
        try {
          Integer.parseInt(value);
        } catch (NumberFormatException e) {
          setStatus(false);
          return;
        }
      } else if (type.equals("double")) {
        try {
          Double.parseDouble(value);
        } catch (NumberFormatException e) {
          setStatus(false);
          return;
        }
      }
    }

    String request = command + " " + type + (command.equals("draw") ? "" : " " + value);

    javafx.application.Platform.runLater(() -> valueField.clear());

    Thread t = new Thread(() -> {
      try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
          PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
          ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

        writer.println(request);
        writer.flush();

        Response response = (Response) in.readObject();
        boolean success = response.status == Response.Status.SUCCESS;
        if (command.equals("draw")) {
          setDrawArea(response.tree != null ? response.tree : "Drzewo puste");
        }
        setStatus(success);
      } catch (IOException | ClassNotFoundException e) {
        setStatus(false);
      }
    });

    t.setDaemon(true);
    t.start();
  }

  private void setDrawArea(String text) {
    javafx.application.Platform.runLater(() -> drawArea.setText(text));
  }

  private void setStatus(boolean success) {
    javafx.application.Platform.runLater(() -> {
      statusCircle.setFill(success ? Color.GREEN : Color.RED);
    });
  }
}