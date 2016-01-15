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

    class SequentialScramble {

        private final int x;
        private final int y;
        private final int height;
        private final int width;
        private final Color color;
        private final WritableRaster writableRaster;

        public SequentialScramble(int x, int y, int height, int width, Color color, WritableRaster writableRaster) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.color = color;
            this.writableRaster = writableRaster;
        }

        /**
         * @return the x
         */
        public int getX() {
            return x;
        }

        /**
         * @return the y
         */
        public int getY() {
            return y;
        }

        /**
         * @return the color
         */
        public Color getColor() {
            return color;
        }

        /**
         * @return the writableRaster
         */
        public WritableRaster getWritableRaster() {
            return writableRaster;
        }

        /**
         * @return the height
         */
        public int getHeight() {
            return height;
        }

        /**
         * @return the width
         */
        public int getWidth() {
            return width;
        }

    }
}
