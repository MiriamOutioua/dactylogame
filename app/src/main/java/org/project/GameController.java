/* 
 * This class is the controller used for the game mode. Like the Controller class it implements initializable to fill the fxml components.
 * The main difference between this class and the Controller class is the additional timer (lvlTimer) which allows us to add words in the list overtime.
 */
package org.project;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.time.StopWatch;
import org.fxmisc.richtext.StyleClassedTextArea;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;

public class GameController implements Initializable {
	private Model model;
    private Stage stage;
    private Scene scene;
    private Parent root;
	private StopWatch timer = new StopWatch();
	private boolean firstType = false;
	// this timer adds a word in the list every 3 * 0.9^(model.level) seconds
	Timer lvlTimer = new Timer();

	public GameController(String difficulty) {
		if(difficulty.equals("easy")) {
			model = Model.easyMode();
		} 
		else if(difficulty.equals("normal")) {
			model = Model.normalMode();
		}
		else {
			model = Model.hardMode();
		}
	}

	public GameController(Model model) {
		this.model = model;
	}

	@FXML 
    private Label MPMlbl;

    @FXML
    private Label precisionLbl;

    @FXML
    private Label regulariteLbl;

	@FXML
    private Label nbLivesLbl;

	@FXML
    private StyleClassedTextArea textarea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (textarea != null) {
            for (Word w : model.getListeDeMots()) {
				if(w.getBonus()) {
					textarea.append(w.toString(), "blue");
				}
				else {
					textarea.append(w.toString(), "grey");
				}
                textarea.append("   ", "");
            }
			nbLivesLbl.setText("Nombre de vies : " + String.valueOf(model.getLives()));
        } else {
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
	 * This method is used to make sure the timer is stopped when the window of the application is closed
	 * @param event
	 */
	public void closeAndStopTimer(WindowEvent event) {
		lvlTimer.cancel();
	}

	/**
	 * Changes the current scene to the ending scene showing the statistics of the user
	 * @param event
	 */
	public void switchToEndgame(KeyEvent event) {
        try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("endscene.fxml"));
            GameController controller = new GameController(this.model);
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
     * Reload the fxml file and create a new GameController with the same difficulty that the player just played
     * @param event
     */
	public void replayGame(ActionEvent event) {
        try {
			String difficulty = model.getDifficulty();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dactylogame.fxml"));
            GameController controller = new GameController(difficulty);
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

    public void switchToMenu(ActionEvent event) {
		lvlTimer.cancel();
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
	 * 
	 * @return the delay between every addition of words according to the current level
	 */
	public long lvlTime() {
		return (long)((3 * (Math.pow(0.9, model.getLevel()))) * 1000); // multiplie par 1000 pour avoir le temps en millisecondes
	}

	/**
	 * This function does every calculation for the statistics to be displayed in the end scene
	 */
	public void endCalcul() {
		timer.stop();
		lvlTimer.cancel();
		long stop = timer.getTime();
		float sec = stop/1000F;
		float min = sec/60F;
		model.calculateMotsParMinute(min);
		model.calculatePrecision();
		model.calculateRegularite();
	}

	/**
	 * This function schedules a timer task on the current timer, or creates a new one and schedules a timer task to change the period of the task
	 * @param event
	 * @param delay
	 */
	public void timerMethod(KeyEvent event, long delay) {
		if(firstType) {
			lvlTimer.cancel();
			// we create a new timer because the level increased, we need to schedule again with the right timing
			lvlTimer = new Timer();
		}
		lvlTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// if the list is already full we have to force the validation of the word
				if(model.getListeDeMots().size() == 10) {
					model.loseLives(model.getListeDeMots().get(0).getMTletters());
					Platform.runLater(() -> nbLivesLbl.setText("Nombre de vies : " + String.valueOf(model.getLives())));

					if(model.getListeDeMots().get(0).WTWord()) {
						model.addWellTypedLetters(model.getListeDeMots().get(0));
						model.getListeDeMots().get(0).consecutiveArriveTime();
						List<Double> auxTime = model.getListeDeMots().get(0).getConsecutiveLetterTime();
						auxTime.forEach(n -> model.getTotalConsecutiveTime().add(n));

						// here we check if the word is a bonus and if the player wrote it well in one go
						if(model.getListeDeMots().get(0).getBonus() && !model.getListeDeMots().get(0).getBackspace()) {
							model.gainLives(model.getListeDeMots().get(0).size());
							Platform.runLater(() -> nbLivesLbl.setText("Nombre de vies : " + String.valueOf(model.getLives())));
						}

						model.incrWTWords();
					}
					
					synchronized(model.getListeDeMots()) {model.getListeDeMots().remove(0);}

					if(model.hasLost()) {
						Platform.runLater(() -> {
							endCalcul();
							switchToEndgame(event);
						});
					}

					if (model.getWTWords() == 100) {
                        System.out.println("Le niveau augmente !");
						model.resetWTWords();
                        model.incrLevel();
						timerMethod(event, 2500);
					}

					synchronized(model.getListeDeMots()) { model.getListeDeMots().add(model.getNextWord()); }

					Platform.runLater(() -> {
						textarea.clear();
						synchronized(model.getListeDeMots()){
							for (Word w : model.getListeDeMots()) {
								if(w.getBonus()) {
									textarea.append(w.toString(), "blue");
								}
								else {
									textarea.append(w.toString(), "grey");
								}
								textarea.append("   ", "");
							}
						}
					});
				}
				else {
					// if the list of words wasn't filled we add a word at the end of the list and update the textarea
					synchronized(model.getListeDeMots()) { model.getListeDeMots().add(model.getNextWord()); }
					Word w = model.getListeDeMots().get(model.getListeDeMots().size() - 1);
					Platform.runLater(() -> {
						if(w.getBonus()) {
							textarea.append(w.toString(), "blue");
						}
						else {
							textarea.append(w.toString(), "grey");
						}
						textarea.append("   ", "");
					});
				}
			}
			
		}, delay, lvlTime());
	}

	/**
	 * This function handles every KeyEvent from the textarea
	 * @param event
	 */
	@FXML
	public void inputFromKeyboard(KeyEvent event) {

		ListIterator<Letter> it = model.getListeDeMots().get(0).getIterator();
		model.incrNbKeyPressed();

		switch(event.getCode()) {
			case SPACE:
				if (firstType) {
					// we decided to remove one life per mistake in the word
					model.loseLives(model.getListeDeMots().get(0).getMTletters()); 
					nbLivesLbl.setText("Nombre de vies : " + String.valueOf(model.getLives()));
					
					if(model.getListeDeMots().get(0).WTWord()) {
						model.addWellTypedLetters(model.getListeDeMots().get(0));
						model.getListeDeMots().get(0).consecutiveArriveTime();
						List<Double> auxTime = model.getListeDeMots().get(0).getConsecutiveLetterTime();
						auxTime.forEach(n -> model.getTotalConsecutiveTime().add(n));
						
						if(model.getListeDeMots().get(0).getBonus() && !model.getListeDeMots().get(0).getBackspace()) {
							model.gainLives(model.getListeDeMots().get(0).size());
							nbLivesLbl.setText("Nombre de vies : " + String.valueOf(model.getLives()));
						}

						model.incrWTWords();
					}
					
					synchronized(model.getListeDeMots()) { model.getListeDeMots().remove(0); }

					if(model.getLives() <= 0) {
						endCalcul();
						switchToEndgame(event);
					}

					if (model.getWTWords() == 100) {
                        System.out.println("Le niveau augmente !");
						model.resetWTWords();
                        model.incrLevel();
						timerMethod(event, 2500);
					}

					// here we add a word if the list wasn't filled enough
					if (model.getListeDeMots().size() <= 5) {
						synchronized(model.getListeDeMots()) { model.getListeDeMots().add(model.getNextWord()); }
					}

					textarea.clear();
					synchronized(model.getListeDeMots()) {
						for (Word w : model.getListeDeMots()) {
							if(w.getBonus()) {
								textarea.append(w.toString(), "blue");
							}
							else {
								textarea.append(w.toString(), "grey");
							}
							textarea.append("   ", "");
						}
					}
				}
				break;
			case BACK_SPACE:
				if (firstType) {
					model.getListeDeMots().get(0).setBackspace(true);
					if(it.hasPrevious()) {
						int ind = it.previousIndex();
						Letter l = it.previous();
						l.setState(LetterState.NOT_TYPED);
						l.resetArriveTime();
						l.setErased(true);
						textarea.setStyleClass(ind, ind+1, l.getLetterColor());
					}
				}
				break;
			default:
				if (!firstType) {
					timer.start();
					firstType = true;
					timerMethod(event, lvlTime());
				}
				String s = event.getCode().getChar().toLowerCase();
				if(it.hasNext()) {
					int ind = it.nextIndex();
					Letter l = it.next();
					if(s.equals(l.toString())) {
						l.setState(LetterState.WELLTYPED);

						l.setArriveTime(((double)timer.getTime()/1000));
					} else {
						l.setState(LetterState.MISTYPED);
					}

					textarea.setStyleClass(ind,ind+1,l.getLetterColor());
				}
				break;
		}	
	}
}
