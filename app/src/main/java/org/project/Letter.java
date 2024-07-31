/**
 * This class represents a letter in a word we will use it for our Word class.
 *  A Letter knows its state (if it was welltyped or not by the player), the time the player wrote it and if it got erased or not.
 */
package org.project;

public class Letter {
    private final char letter;
    private LetterState state = LetterState.NOT_TYPED;
    // the time in seconds the letter was typed (only if it was welltyped)
    private double arriveTime = 0;
    private boolean erased = false;

    public Letter(char letter) {
        this.letter = letter;
    }

    public void setState(LetterState state) {
        if(this.state == LetterState.WELLTYPED || this.state == LetterState.MISTYPED) {
            this.state = LetterState.NOT_TYPED;
        } else {
            this.state = state;
        }
    }

    public LetterState getstate() {
        return this.state;
    }

    public String getLetterColor() {
        return this.state.couleur;
    }

    public char getLetter() {
        return this.letter;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(double n) {
        arriveTime = n;
    }

    public void resetArriveTime() {
        arriveTime = 0;
    }

    public boolean getErased() {
        return erased;
    }

    public void setErased(boolean b) {
        erased = b;
    }

    public String toString() {
        return String.valueOf(this.letter);
    }
}
