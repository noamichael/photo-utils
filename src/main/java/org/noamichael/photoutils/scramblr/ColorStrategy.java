package org.noamichael.photoutils.scramblr;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author micha_000
 */
public class ColorStrategy implements JPEGScramblrStrategy {

    @Override
    public void scramble(ScrambleContext scrambleContext) {
        Random random = new Random();
        scrambleContext.sequentialScramble((ctx) -> {
            Color color = ctx.getColor();
            int[] pixles = {
                random.nextInt(), color.getGreen() / 2, color.getBlue() + random.nextInt()
            };
            ctx.getWritableRaster().setPixel(ctx.getX(), ctx.getY(), pixles);
        });
    }

}
