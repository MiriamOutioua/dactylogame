
package org.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class App extends Application { 

    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("gamemode.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
            primaryStage.setTitle("Dactylogame");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(IOException e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Application.launch(args);
    }
}
