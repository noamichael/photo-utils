package org.noamichael.photoutils.scramblr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.noamichael.photoutils.exception.PhotoUtilsException;

/**
 * A class which performs a scrambes a JPG image.
 *
 * @author micha_000
 */
public class Scramblr {

    public Scramblr() {
    }

    /**
     * Loads the file found at the given path into a buffer.
     *
     * @param base
     * @return
     */
    public ScrambleStream loadFile(final String base) {
        try {
            File file = new File(base);
            BufferedImage image = ImageIO.read(file);
            return new ScrambleStreamImpl(image);
        } catch (FileNotFoundException ex) {
            throw new PhotoUtilsException(PhotoUtilsException.FILE_NOT_FOUND, ex);
        } catch (IOException ex) {
            throw new PhotoUtilsException("Unexpected IOException", ex);
        }
    }

    /**
     * Represents the result of a scramble operation
     */
    static class ScrambleStreamImpl implements ScrambleStream {

        private final BufferedImage image;
        private final ScrambleContext context;

        public ScrambleStreamImpl(BufferedImage image) {
            this.image = image;
            context = new ScrambleContextImpl(image, this);
        }

        /**
         * Writes the resulting scramble stream to a file with the given path.
         *
         * @param base
         * @return
         */
        @Override
        public File thenWriteTo(final String base) {
            try {
                File file = new File(base);
                ImageIO.write(image, "jpg", file);
                return file;
            } catch (IOException ex) {
                throw new PhotoUtilsException("Unexpected IOException", ex);
            }
        }

        @Override
        public ScrambleStream performScramble(ScramblrStrategy strategy) {
            strategy.scramble(context);
            return this;
        }

    }

    static class ScrambleContextImpl implements ScrambleContext {

        BufferedImage image;
        ScrambleStream scrambleStream;

        public ScrambleContextImpl(BufferedImage image, ScrambleStream scrambleStream) {
            this.image = image;
            this.scrambleStream = scrambleStream;
        }

        @Override
        public BufferedImage getImage() {
            return image;
        }

        @Override
        public ScrambleStream getStream() {
            return scrambleStream;
        }

        @Override
        public void sequentialScramble(Consumer<SequentialScramble> consumer) {
            int width = image.getWidth();
            int height = image.getHeight();
            WritableRaster raster = image.getRaster();
            iteration:
            {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int[] pixels = raster.getPixel(x, y, (int[]) null);
                        Color color = new Color(pixels[0], pixels[1], pixels[2]);
                        SequentialScrambleImpl sequentialScramble = new SequentialScrambleImpl(x, y, height, width, color, raster);
                        consumer.accept(sequentialScramble);
                        if (sequentialScramble.isFinished()) {
                            break iteration;
                        }
                    }
                }
            }
        }

    }

    static class SequentialScrambleImpl implements ScrambleContext.SequentialScramble {

        private final int x;
        private final int y;
        private final int height;
        private final int width;
        private final Color color;
        private final WritableRaster writableRaster;
        private boolean finished;

        public SequentialScrambleImpl(int x, int y, int height, int width, Color color, WritableRaster writableRaster) {
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
        @Override
        public int getX() {
            return x;
        }

        /**
         * @return the y
         */
        @Override
        public int getY() {
            return y;
        }

        /**
         * @return the color
         */
        @Override
        public Color getColor() {
            return color;
        }

        /**
         * @return the writableRaster
         */
        @Override
        public WritableRaster getWritableRaster() {
            return writableRaster;
        }

        /**
         * @return the height
         */
        @Override
        public int getHeight() {
            return height;
        }

        /**
         * @return the width
         */
        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public void finish() {
            finished = true;
        }

        /**
         * @return the finished
         */
        boolean isFinished() {
            return finished;
        }

    }
}
