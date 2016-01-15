package org.noamichael.photoutils.scramblr;

/**
 *
 * @author micha_000
 */
@FunctionalInterface
public interface JPEGScramblrStrategy {

    void scramble(ScrambleContext scrambleContext);

}
