/**
 * LetterState will help us get the color of a letter more easily
 */
package org.project;

public enum LetterState {
    MISTYPED("red"),
    WELLTYPED("green"),
    NOT_TYPED("grey");

    final String couleur;

    LetterState(String color) {
        this.couleur = color;
    }
}
