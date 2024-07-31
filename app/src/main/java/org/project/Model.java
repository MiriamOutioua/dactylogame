/**
 * The Model class will manage all of our game's logic (number of lives, level, words to type) and it calculates every statistics of the player.
 */
package org.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Model {
    // the words that the player will have to type
    private final List<Word> listeDeMots = new ArrayList<>();
    // a list that we get our words from (randomly)
    private final List<Word> stock = new ArrayList<>();
    // this list will keep every time between two welltyped letters from every single welltyped word
    private final List<Double> totalConsecutiveTime = new ArrayList<>();
    private Random rand = new Random();
    private int sumOfWellTypedLetters = 0;
    private int motsParMinute = 0;
    private int precision = 0;
    private double ecart_type = 0;
    private double variance = 0;
    private double moyenne = 0;
    private int nbKeyPressed = 0;
    // this attribute will help us know if the player wrote enough words in normal mode
    private int typedWords = 0;
    // this attribute will help us know if we need to level up in game mode (every 100 welltyped words)
    private int WTWords = 0;
    // according to the difficulty chosen by the player
    private double percentageBonus;
    private int lives;
    private int level;
    private String difficulty;

    public Model(ModelBuilder builder) {
        this.difficulty = builder.difficulty;
        this.lives = builder.lives;
        this.percentageBonus = builder.percentageBonus;
        this.level = builder.level;

        wordsFromFile("eng_words.csv");
      
        for(int i = 0; i < 10; i++) {
            listeDeMots.add(getNextWord());
        }
    }

    public Model() {
        wordsFromFile("eng_words.csv");
      
        for(int i = 0; i < 10; i++) {
            listeDeMots.add(getNextWord());
        }
    }

    /*
     * The next 4 methods create a new Model object using our intern class ModelBuilder
     */

    public static Model easyMode() {
        return new Model.ModelBuilder().lives(10).difficulty("easy").percentageBonus(0.2).level(2).build();
    }

    public static Model normalMode() {
        return new Model.ModelBuilder().lives(5).difficulty("normal").percentageBonus(0.1).level(5).build();
    }

    public static Model hardMode() {
        return new Model.ModelBuilder().lives(3).difficulty("hard").percentageBonus(0.04).level(7).build();
    }

    public static Model multiMode() {
        return new Model.ModelBuilder().lives(10).percentageBonus(0.3).build();
    }

    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Gets a word from the stock with a random
     * @return a Word object
     */
    public Word getNextWord() {
        return stock.get(rand.nextInt(stock.size()));
    }

    public int getWellTypedLetters() {
        return sumOfWellTypedLetters;
    }

    public void resetWellTypedLetter() {
        sumOfWellTypedLetters = 0;
    }

    public void addWellTypedLetters(Word word) {
        word.countWTletters();
        sumOfWellTypedLetters += word.getWTletters();
    }

    public int getMotsParMinute() {
        return motsParMinute;
    }

    public void setMotsParMinute(int n) {
        motsParMinute = n;
    }

    public void calculateMotsParMinute(float n) {
        motsParMinute = (int)(sumOfWellTypedLetters/n)/5;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int n) {
        precision = n;
    }

    public void calculatePrecision() {
        // we use a double so that n will not be rounded to 0 if we have a result like 0,2...
        double n = (double)sumOfWellTypedLetters/nbKeyPressed;
        precision = (int)(n*100);
    }

    public void calculateRegularite() {
        // totalConsecutiveTime is empty if the player didn't write any consecutive welltyped letters
        if(totalConsecutiveTime.isEmpty()) {
            ecart_type = -999;
        } else {
            // this calculation uses the formula of the standard deviation (écart-type)
            totalConsecutiveTime.forEach(n -> moyenne += n);
            moyenne = moyenne/totalConsecutiveTime.size();
            List<Double> ecart_eleve = totalConsecutiveTime.stream().map(data -> Math.pow((data-moyenne),2)).collect(Collectors.toList());
            ecart_eleve.forEach(n -> variance += n);
            variance = variance/totalConsecutiveTime.size();
            ecart_type = Math.sqrt(variance);
        }
    }

    public double getRegularite() {
        return ecart_type;
    }

    public void setRegularite(double n) {
        ecart_type = n;
    }

    public List<Double> getTotalConsecutiveTime() {
        return totalConsecutiveTime;
    }

    public int getTypedWords() {
        return typedWords;
    }

    public void incrTypedWords() {
        typedWords++;
    }

    public int getLives() {
        return lives;
    }

    public boolean hasLost() {
        return lives <= 0;
    }

    public int getLevel() {
        return level;
    }

    public void incrLevel() {
        level++;
    }

    public void incrNbKeyPressed() {
        nbKeyPressed++;
    }

    public void gainLives(int n) {
        lives += n;
    }

    public void loseLives(int n) {
        lives -= n;
    }

    public int getWTWords() {
        return WTWords;
    }

    public void incrWTWords() {
        WTWords++;
    }

    public void resetWTWords() {
        WTWords = 0;
    }

    /**
     * This method will read through a file, transform every word into a Word object and add it to the stock.
     * It uses the percentageBonus to decide wether the word will be a bonus or not.
     * @param filename
     */
    public void wordsFromFile(String filename) {
        try {
            URL resource = getClass().getClassLoader().getResource(filename);
            File f = new File(resource.toURI());
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine().toLowerCase();
                Word w = new Word(s);

                if(rand.nextDouble() < percentageBonus) {
                    w.setBonus();
                }
                
                stock.add(w);
            }
            scanner.close();
        } catch(FileNotFoundException e) {e.printStackTrace();}
        catch (URISyntaxException e1) {e1.printStackTrace();}
    }

    public List<Word> getListeDeMots() {
        return listeDeMots;
    }

    /*
     * This class uses the Builder pattern, these attributes are optional because we need them in two game modes : game and multiplayer
     */
    public static class ModelBuilder {
        private int lives;
        private double percentageBonus;
        private int level;
        private String difficulty;

        public ModelBuilder lives(int lives) {
            this.lives = lives;
            return this;
        }

        public ModelBuilder difficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public ModelBuilder percentageBonus(double percentageBonus) {
            this.percentageBonus = percentageBonus;
            return this;
        }

        public ModelBuilder level(int level) {
            this.level = level;
            return this;
        }

        public Model build() {
            Model model = new Model(this);
            return model;
        }
    }
}
