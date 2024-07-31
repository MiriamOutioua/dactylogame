/*
 * This is the controller for the normal/training mode. It implements initializable so we can fill our fxml components when the fxml file is loaded.
 */
package org.project;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import org.apache.commons.lang3.time.StopWatch;
import org.fxmisc.richtext.StyleClassedTextArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Controller implements Initializable {
    private Model model;
    private Stage stage;
    private Scene scene;
    private Parent root;
    // this timer is used for the calculation of the player's statistics
    private StopWatch timer = new StopWatch();
    // firstType is used to know if the player typed their first letter
    private boolean firstType = false;

    public Controller() {
        model = new Model();
    }

    public Controller(Model model) {
        this.model = model;
    }

    @FXML 
    private Label MPMlbl;

    @FXML
    private Label precisionLbl;

    @FXML
    private Label regulariteLbl;

    @FXML
    private Label typedWordsLbl;

    @FXML
    private StyleClassedTextArea textarea;

    public void initialize(URL location, ResourceBundle resources) {
        if (textarea != null) {
            for (Word w : model.getListeDeMots()) {
                textarea.append(w.toString(), "grey");
                textarea.append("   ", "");
            }
        } else {
            // this part will only happen when the player is done playing (when the endscene is loaded)
            MPMlbl.setText(String.valueOf(model.getMotsParMinute()));
            precisionLbl.setText(String.valueOf(model.getPrecision()));
			if(model.getRegularite() < 0) {
				regulariteLbl.setText("X");
			} else {
                double round = (double)Math.round(model.getRegularite() * 100) / 100;
            	regulariteLbl.setText(String.valueOf(round));
			}
        }
    }

    /**
     * Loads the fxml file in charge of displaying the statistics of the player.
     * It creates a new Controller with the same model used during the player's game.
     * @param event
     */
    public void switchToEndgame(KeyEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("endscene.fxml"));
            Controller controller = new Controller(this.model);
            fxmlLoader.setController(controller);
            root = (Parent)fxmlLoader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow(); 
            scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
			stage.setTitle("Dactylogame");
            stage.setScene(scene);
            stage.show();
        } catch(IOException e){e.printStackTrace();}
    }

    /**
     * Reload the fxml file, the Controller is created automatically when the file is loaded.
     * @param event
     */
    public void replayGame(ActionEvent event) {
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
     * This method loads the menu fxml file ("gamemode.fxml") so that the player can choose a new mode to play.
     * @param event
     */
    public void switchToMenu(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("gamemode.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow(); 
            scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
			stage.setTitle("Dactylogame");
            stage.setScene(scene);
            stage.show();
        } catch(IOException e){}
    }

    /**
     * This method handles every keyboard input from the player and updates the model and view according to our rules
     * @param event
     */
    @FXML
    public void inputFromKeyboard(KeyEvent event) {

        // we initialize an iterator to go through the first word's letters
        ListIterator<Letter> it = model.getListeDeMots().get(0).getIterator();
        model.incrNbKeyPressed();
        
        switch(event.getCode()) {
            case SPACE: 
                // if the player didn't write their first letter they can't press space
                if (firstType) {
                    model.incrTypedWords();

                    // we update the label showing how many words are left when the player validates a word
                    typedWordsLbl.setText("Mots restants : " + String.valueOf(30 - model.getTypedWords()));

                    if (model.getListeDeMots().get(0).WTWord()) {

                        model.addWellTypedLetters(model.getListeDeMots().get(0));
                        // we update the consecutiveLetterTime list for the word that is being validated
                        model.getListeDeMots().get(0).consecutiveArriveTime();
                        // we use an aux list to avoid concurrent modifications
                        List<Double> auxTime = model.getListeDeMots().get(0).getConsecutiveLetterTime();
                        // we add every time between the consecutive welltyped letters of the word to the list of the model
						auxTime.forEach(n -> model.getTotalConsecutiveTime().add(n));

                    }

                    model.getListeDeMots().remove(0);

                    // if the player wrote 30 words they are done playing, we update our models statistics and we can show the results with switchToEndgame
                    if(model.getTypedWords() == 30) {
                        timer.stop();
                        long stop = timer.getTime();
                        float sec = stop/1000F;
                        float min = sec/60F;
                        model.calculateMotsParMinute(min);
                        model.calculatePrecision();
                        model.calculateRegularite();

                        switchToEndgame(event);
                    }
                    
                    model.getListeDeMots().add(model.getNextWord());

                    // we clear the textarea and write the words again to match our model's list of words
                    textarea.clear();
                    for (Word w : model.getListeDeMots()) {
                        textarea.append(w.toString(), "grey");
                        textarea.append("   ", "");
                    }

                }
                break;
            case BACK_SPACE: 
                if(it.hasPrevious()) {
                    int ind = it.previousIndex();
                    Letter l = it.previous();
                    l.setState(LetterState.NOT_TYPED);
                    // we reset the time since our letter is not welltyped anymore
                    l.resetArriveTime();
                    l.setErased(true);
                    // finally we set the color of the letter
                    textarea.setStyleClass(ind, ind+1, l.getLetterColor());
                }
                break;
            default:
                // we start the timer when the first letter is typed
                if (!firstType) {
                    timer.start();
                    firstType = true;
                }
                String s = event.getCode().getChar().toLowerCase();
                if(it.hasNext()) {
                    int ind = it.nextIndex();
                    Letter l = it.next();
                    if(s.equals(l.toString())) {
                        l.setState(LetterState.WELLTYPED);

                        // add the time in the letter that was typed, we use a double for the rounding
                        l.setArriveTime(((double)timer.getTime()/1000));
                    } else {
                        l.setState(LetterState.MISTYPED);
                    }

                    textarea.setStyleClass(ind,ind+1,l.getLetterColor());
                }
        }
    }
}
