package org.noamichael.photoutils.scramblr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.function.Consumer;

/**
 *
 * @author micha_000
 */
public interface ScrambleContext {

    BufferedImage getImage();

    ScrambleStream getStream();

    void sequentialScramble(Consumer<SequentialScramble> consumer);

    interface SequentialScramble {

        /**
         * @return the x
         */
        int getX();

        /**
         * @return the y
         */
        int getY();

        /**
         * @return the color
         */
        Color getColor();

        /**
         * @return the writableRaster
         */
        WritableRaster getWritableRaster();

        /**
         * @return the height
         */
        int getHeight();

        /**
         * @return the width
         */
        int getWidth();

        void finish();

    }
}
