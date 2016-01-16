package org.noamichael.photoutils.scramblr;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.noamichael.photoutils.scramblr.ScramblrStrategy.StrategeySettingDescriptor.SettingValueType;

/**
 *
 * @author micha_000
 */
public class RemovePixles implements ScramblrStrategy {

    public static final int DEFAULT_WIDTH = 1;
    public static final int DEFAULT_SPACE = 2;
    public static final Direction DEFAULT_DIRECTION = Direction.X;

    private final Direction direction;
    private final int width;
    private final int spaceBetween;

    public RemovePixles() {
        this(DEFAULT_DIRECTION, DEFAULT_WIDTH, DEFAULT_SPACE);
    }

    public RemovePixles(Direction direction) {
        this(direction, DEFAULT_WIDTH, DEFAULT_SPACE);
    }

    public RemovePixles(Direction direction, int width, int spaceBetween) {
        this.direction = Optional.ofNullable(direction).orElse(DEFAULT_DIRECTION);
        this.width = Optional.ofNullable(width).orElse(DEFAULT_WIDTH);
        this.spaceBetween = Optional.ofNullable(spaceBetween).orElse(DEFAULT_SPACE);
    }

    @Override
    public void scramble(ScrambleContext scrambleContext) {
        if (direction == Direction.Y) {
            deleteEveryOtherY(scrambleContext);
        } else {
            deleteEveryOtherX(scrambleContext);
        }
    }

    private void deleteEveryOtherX(ScrambleContext scrambleContext) {
        IterationData data = new IterationData();
        data.nextY = 1;
        data.remainingWidth = width + 1;
        scrambleContext.sequentialScramble((ss) -> {
            int nextYToLock = data.nextY;
            if (ss.getY() == nextYToLock) {
                ss.getWritableRaster().setPixel(ss.getX(), ss.getY(), new int[]{0, 0, 0});
                if (ss.getX() == ss.getWidth() - 1) { // If you reached the end.
                    if (--data.remainingWidth > 0) {
                        data.nextY = ss.getY() + 1;
                    } else {
                        data.nextY = ss.getY() + spaceBetween;
                    }
                }
            }
        });
    }

    private void deleteEveryOtherY(ScrambleContext scrambleContext) {
        boolean[] odd = {false};
        scrambleContext.sequentialScramble((ss) -> {
            if (ss.getX() == ss.getWidth() - 1) {
                odd[0] = false;
            }
            boolean isOdd = odd[0];
            if (isOdd) {
                ss.getWritableRaster().setPixel(ss.getX(), ss.getY(), new int[]{0, 0, 0});
            }
            odd[0] = !odd[0];
        });
    }

    @Override
    public List<StrategeySettingDescriptor> getSettingDescriptors() {
        return Arrays.asList(
                new StrategeySettingDescriptor("direction", SettingValueType.ENUM),
                new StrategeySettingDescriptor("width", SettingValueType.NUMBER)
        );
    }

    public enum Direction {
        X, Y;
    }

    class IterationData {

        private int nextY;
        private boolean odd;
        private int remainingWidth;
        private int spaceBetween;
    }

}
