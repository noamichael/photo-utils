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

    public static int toInt(byte one, byte two) {
        return ((one & 0xff) << 8) | (two & 0xff);
    }

    public static String[] bytesToHex(byte[] bs) {
        String[] hex = new String[bs.length];
        for (int i = 0; i < bs.length; i++) {
            hex[i] = byteToHex(bs[i]);
        }
        return hex;
    }
}
