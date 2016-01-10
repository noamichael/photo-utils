package org.noamichael.photoutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import static org.noamichael.photoutils.JPEGAnalyzer.END_OF_IMAGE;
import static org.noamichael.photoutils.JPEGAnalyzer.START_OF_IMAGE;
import static org.noamichael.photoutils.JPEGAnalyzer.START_OF_SCAN;
import org.noamichael.photoutils.exception.PhotoUtilsException;

/**
 * A class which performs a scrambes a JPG image.
 *
 * @author micha_000
 */
public class JPEGScramblr {

    /**
     * The original source of the image, unmodified.
     */
    private byte[] src;

    public JPEGScramblr() {
    }

    /**
     * Loads the file found at the given path into a buffer.
     *
     * @param base
     * @return
     */
    public JPEGScramblr loadFile(final String base) {
        try {
            File file = new File(base);
            Path p = Paths.get(file.getAbsolutePath());
            src = Files.readAllBytes(p);
        } catch (FileNotFoundException ex) {
            throw new PhotoUtilsException(PhotoUtilsException.FILE_NOT_FOUND, ex);
        } catch (IOException ex) {
            throw new PhotoUtilsException("Unexpected IOException", ex);
        }
        return this;
    }

    /**
     * Performs the default scramble operation on the image.
     *
     * @return
     */
    public ScrambleStream performScramble() {
        ScrambleStream stream = new ScrambleStream();
        byte[] copy = cloneSrc();
        final Random random = new Random();
        performSaveManipulation(copy, (b) -> {
            //TODO: One functional, this method will allow the developer to
            //randomly modify bytes of the image without destroying needed
            //metadata
            return Boolean.TRUE;
        });
        stream.copy = copy;
        return stream;

    }

    private void performSaveManipulation(byte[] array, Function<SafeRangeEntry, Boolean> forEach) {
        Map<String, Integer> foundByteSets = JPEGAnalyzer.findMetadataIndexes(array);
        int startOfJpeg = Optional.ofNullable(foundByteSets.get(START_OF_IMAGE)).orElseThrow(() -> new PhotoUtilsException("Could not find SOI"));
        int startOfScan = Optional.ofNullable(foundByteSets.get(START_OF_SCAN)).orElseThrow(() -> new PhotoUtilsException("Could not find SOS"));
        int endOfJpeg = Optional.ofNullable(foundByteSets.get(END_OF_IMAGE)).orElseThrow(() -> new PhotoUtilsException("Could not find EOI"));
        for (int i = startOfScan; i < endOfJpeg; i++) {
            Boolean result = forEach.apply(new SafeRangeEntry(array[i], array, i));
            if (result != null && result == true) {
                break;
            }
        }
    }

    /**
     * Copies the given array into a new instance.
     *
     * @return
     * @throws PhotoUtilsException
     */
    private byte[] cloneSrc() throws PhotoUtilsException {
        if (src == null) {
            throw new PhotoUtilsException("Cannot scramble if no file is available.");
        }
        byte[] copy = new byte[src.length];
        System.arraycopy(src, 0, copy, 0, src.length);
        return copy;
    }

    /**
     * Represents the result of a scramble operation
     */
    public class ScrambleStream {

        private byte[] copy;

        /**
         * Writes the resulting scramble stream to a file with the given path.
         *
         * @param base
         * @return
         */
        public File thenWriteTo(final String base) {
            try {
                File file = new File(base);
                if (!file.exists()) {
                    file.createNewFile();
                }
                Path p = Paths.get(file.getAbsolutePath());
                return Files.write(p, copy).toFile();
            } catch (IOException e) {
                throw new PhotoUtilsException("Unexpected IOException", e);
            }

        }
    }

    /**
     * Represents an entry which is safe to randomly modify
     */
    static class SafeRangeEntry {

        byte current;
        byte[] array;
        int index;

        public SafeRangeEntry(byte current, byte[] array, int index) {
            this.current = current;
            this.array = array;
            this.index = index;
        }

    }

}
