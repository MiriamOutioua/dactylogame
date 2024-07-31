/**
 * This class represents a word with the help of the class Letter
 */

package org.project;

import java.util.*;
import java.util.stream.Collectors;

public class Word {
    private List<Letter> word = new ArrayList<>();
    private int welltyped_letters = 0;
    // a list that will stock every time between two welltyped letters
    private List<Double> consecutiveLetterTime = new ArrayList<>();
    private ListIterator<Letter> iterator;
    private int mistyped_letters = 0;
    // in game mode this word will give lives to the player if they didn't make any mistake 
    // in multiplayer mode this word will get into the opposite player's list of words
    private boolean bonus = false;
    // this boolean will help us know if the player wrote the word in one go and without erasing any letter
    private boolean backspace = false;
	
	public Word(String word) {
		this.word = word.chars().mapToObj(c -> new Letter((char)c)).collect(Collectors.toList());
        this.iterator = this.word.listIterator();
	}

    public ListIterator<Letter> getIterator() {
        return iterator;
    }

    public void countWTletters() {
        this.word.forEach(letter -> {
            if(!letter.getErased()) {
                if(letter.getstate() == LetterState.WELLTYPED) {
                    welltyped_letters++;
                }
            }
        });
    }

	public int getWTletters() {
        return welltyped_letters;
    }

    /**
     * This method adds the time between two welltyped letters into our list consecutiveLetterTime
     */
    public void consecutiveArriveTime() {
        for (int i = 0; i < word.size() - 1; i++) {
            if(!word.get(i).getErased() && !word.get(i+1).getErased()) {
                consecutiveLetterTime.add(word.get(i+1).getArriveTime() - word.get(i).getArriveTime());
            }
        }
    }

    public List<Double> getConsecutiveLetterTime() {
        return consecutiveLetterTime;
    }

    public void countMTletters() {
        this.word.forEach(letter -> {
            if(letter.getstate() == LetterState.MISTYPED || letter.getstate() == LetterState.NOT_TYPED) {
                mistyped_letters++;
            }
        });
    }

    public int getMTletters() {
        countMTletters();
        return this.mistyped_letters;
    }

    public boolean WTWord() {
        for (Letter letter : word) {
            if (letter.getstate() == LetterState.MISTYPED || letter.getstate() == LetterState.NOT_TYPED) {
                return false;
            }
        }
        return true;
    }

    public boolean getBonus() {
        return bonus;
    }

    public void setBonus() {
        bonus = true;
    }

    public int size() {
        return word.size();
    }

    public boolean getBackspace() {
        return backspace;
    }

    public void setBackspace(boolean b) {
        backspace = b;
    }

    /**
     * 
     * @return the word converted into a String
     */
    public String normalize() {
        String r = "";
        for(Letter l : this.word) {
            r += l.toString();
        }

        return r;
    }

    public String toString() {
        return this.normalize();
    }
}
