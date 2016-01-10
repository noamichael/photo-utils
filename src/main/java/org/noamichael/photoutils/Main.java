package org.noamichael.photoutils;

/**
 *
 * @author micha_000
 */
public class Main {

    public static final String FILE_PATH = "\\C:\\Users\\micha_000\\Desktop\\";
    public static final String[] FILES = {"hvX5EwX", "PICT0007"};

    public static void main(String[] args) {
        String file = FILES[0];
        new JPEGScramblr()
                .loadFile(FILE_PATH.concat(file).concat(".jpg"))
                .performScramble()
                .thenWriteTo(FILE_PATH.concat(file).concat("_scrambled.jpg"));
    }
}
