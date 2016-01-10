package org.noamichael.photoutils;

/**
 *
 * @author micha_000
 */
public class HexUtils {

    /**
     * Converts a byte to it's hex string.
     *
     * @param b
     * @return
     */
    public static String byteToHex(byte b) {
        int i = b & 0xFF;
        return Integer.toHexString(i);
    }
}
