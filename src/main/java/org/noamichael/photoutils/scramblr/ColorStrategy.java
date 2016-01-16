package org.noamichael.photoutils.scramblr;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author micha_000
 */
public class ColorStrategy implements ScramblrStrategy {

    private final Random RANDOM = new Random();
    private final int STRENGTH;

    public ColorStrategy(int STRENGTH) {
        this.STRENGTH = STRENGTH;
    }

    @Override
    public void scramble(ScrambleContext scrambleContext) {
        scrambleContext.sequentialScramble((ctx) -> {
            boolean[] states = {RANDOM.nextBoolean(), RANDOM.nextBoolean(), RANDOM.nextBoolean()};
            Color color = ctx.getColor();
            int[] pixles = {
                plusOrMinus(color.getRed(), states[0]), plusOrMinus(color.getGreen(), states[1]), plusOrMinus(color.getBlue(), states[2])
            };
            ctx.getWritableRaster().setPixel(ctx.getX(), ctx.getY(), pixles);
        });

    }

    int plusOrMinus(int color, boolean plus) {
        return Math.abs(plus ? color + RANDOM.nextInt(STRENGTH) : color - RANDOM.nextInt(STRENGTH));
    }
}
