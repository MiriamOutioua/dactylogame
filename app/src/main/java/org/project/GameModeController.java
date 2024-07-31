/**
 * This class will change the app's scene according to the game mode chosen by the player
 */
package org.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameModeController {
	private Stage stage;
    private Scene scene;
    private Parent root;

	@FXML
	private Button normalMode;

	@FXML
	private Button gameMode;

    @FXML 
    private Button easyBtn;
    
    @FXML
    private Button normalBtn;

    @FXML
    private Button hardBtn;
	
    /**
     * Launches the normal mode (training mode)
     * @param event
     */
	public void switchToNormalMode(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("dactyloscene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow(); 
            scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
			stage.setTitle("Dactylogame");
            stage.setScene(scene);
            stage.show();
        } catch(IOException e){e.printStackTrace();}
    }

    /**
     * If the player chose the game mode he will have to chose the difficulty, this method will show the player the buttons to make their choice
     * @param event
     */
	public void switchToGameMode(ActionEvent event) {
        normalMode.setVisible(false);
        gameMode.setVisible(false);

        easyBtn.setVisible(true);
        normalBtn.setVisible(true);
        hardBtn.setVisible(true);
    }

    /*
     * The next three methods have the same pattern, they create a GameController with the right difficulty and set the fxml controller.
     * They also add an event filter to the window to stop the timer from the game mode.
     */

    public void easyGameMode(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dactylogame.fxml"));
            GameController controller = new GameController("easy");
            fxmlLoader.setController(controller);
            root = (Parent)fxmlLoader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow(); 
            scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
			stage.setTitle("Dactylogame");
            stage.setScene(scene);
            scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> controller.closeAndStopTimer(e));
            stage.show();
        } catch(IOException e){e.printStackTrace();}
    }

    public void normalGameMode(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dactylogame.fxml"));
            GameController controller = new GameController("normal");
            fxmlLoader.setController(controller);
            root = (Parent)fxmlLoader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow(); 
            scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
			stage.setTitle("Dactylogame");
            stage.setScene(scene);
            scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> controller.closeAndStopTimer(e));
            stage.show();
        } catch(IOException e){e.printStackTrace();}
    }

    public void hardGameMode(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dactylogame.fxml"));
            GameController controller = new GameController("hard");
            fxmlLoader.setController(controller);
            root = (Parent)fxmlLoader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow(); 
            scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
			stage.setTitle("Dactylogame");
            stage.setScene(scene);
            scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> controller.closeAndStopTimer(e));
            stage.show();
        } catch(IOException e){e.printStackTrace();}
    }
}
