package org.noamichael.photoutils.scramblr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.noamichael.photoutils.exception.PhotoUtilsException;

/**
 * A class which performs a scrambes a JPG image.
 *
 * @author micha_000
 */
public class JPEGScramblr {

    public JPEGScramblr() {
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
        private ScrambleContext context;

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
        public ScrambleStream performScramble(JPEGScramblrStrategy strategy) {
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
            Random random = new Random();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (random.nextBoolean()) {
                        int[] pixels = raster.getPixel(x, y, (int[]) null);
                        Color color = new Color(pixels[0], pixels[1], pixels[2]);
                        consumer.accept(new SequentialScramble(x, y, height, width, color, raster
                        ));
                    }
                }
            }
        }

    }
}
