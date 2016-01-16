package org.noamichael.photoutils;

import org.noamichael.photoutils.scramblr.Scramblr;
import org.noamichael.photoutils.scramblr.RemovePixles;

/**
 *
 * @author micha_000
 */
public class Main {

    public static final String FILE_PATH = "\\C:\\Users\\micha_000\\Desktop\\";
    public static final String[] FILES = {"hvX5EwX", "PICT0007"};

    public static void main(String[] args) {
        String file = FILES[1];
        new Scramblr()
                .loadFile(FILE_PATH.concat(file).concat(".jpg"))
                //.performScramble(new ColorStrategy(60))
                //.performScramble(new RemovePixles(RemovePixles.Direction.Y))
                .performScramble(new RemovePixles(RemovePixles.Direction.X, 2, 5))
                .thenWriteTo(FILE_PATH.concat(file).concat("_scrambled.jpg"));
    }
}
