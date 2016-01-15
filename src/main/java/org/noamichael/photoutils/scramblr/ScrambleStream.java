package org.noamichael.photoutils.scramblr;

import java.io.File;

/**
 *
 * @author micha_000
 */
public interface ScrambleStream {
   
    ScrambleStream performScramble(JPEGScramblrStrategy strategy);
    
    File thenWriteTo(final String base);
}
