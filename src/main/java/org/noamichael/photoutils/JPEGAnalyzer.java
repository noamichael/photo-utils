package org.noamichael.photoutils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.noamichael.photoutils.HexUtils.*;
import org.noamichael.photoutils.exception.PhotoUtilsException;

/**
 * Analyzes the content of a JPEG file.
 *
 * <p>
 * Many of the comments and discoveries that helped build this class are
 * from:</p>
 * <ul>
 * <li>http://dev.exiv2.org/projects/exiv2/wiki/The_Metadata_in_JPEG_files</li>
 * <li>http://www.onicos.com/staff/iz/formats/jpeg.html</li>
 * <li>https://en.wikipedia.org/wiki/JPEG</li>
 *
 * </ul>
 *
 * @author micha_000
 */
public class JPEGAnalyzer {

    /**
     * The start of the image file
     */
    public static final String START_OF_IMAGE = "SOI";
    /**
     * Indicates that this is a baseline DCT-based JPEG, and specifies the
     * width, height, number of components, and component subsampling
     */
    public static final String START_OF_FRAME0 = "SOF0";
    /**
     * Indicates that this is a progressive DCT-based JPEG, and specifies the
     * width, height, number of components, and component subsampling
     */
    public static final String START_OF_FRAME2 = "SOF2";
    /**
     * Define Huffman Table(s)
     */
    public static final String HUFFMAN_TABLE = "DHT";
    /**
     * Define Quantization Table(s)
     */
    public static final String QUANTIZATION_TABLE = "DQT";
    /**
     * Specifies the interval between RSTn markers, in macroblocks. This marker
     * is followed by two bytes indicating the fixed size so it can be treated
     * like any other variable size segment.
     */
    public static final String RESTART_INTERVAL = "DRI";
    /**
     * Begins a top-to-bottom scan of the image. In baseline DCT JPEG images,
     * there is generally a single scan. Progressive DCT JPEG images usually
     * contain multiple scans. This marker specifies which slice of data it will
     * contain, and is immediately followed by entropy-coded data.
     */
    public static final String START_OF_SCAN = "SOS";
    /**
     * Inserted every r macroblocks, where r is the restart interval set by a
     * DRI marker. Not used if there was no DRI marker. The low 3 bits of the
     * marker code cycle in value from 0 to 7.
     */
    public static final String RESTART = "RST";
    /**
     * For example, an Exif JPEG file uses an APP1 marker to store metadata,
     * laid out in a structure based closely on TIFF.
     */
    public static final String APPLICATION_SPECIFIC = "APP";
    /**
     * Comment
     */
    public static final String COMMENT = "COM";
    /**
     * End Of Image
     */
    public static final String END_OF_IMAGE = "EOI";

    private static final ByteSet[] BYTE_SETS = {
        new ByteSet(START_OF_IMAGE, "ff", "d8", PayloadType.VARIABLE_LENGTH),
        new ByteSet(START_OF_FRAME0, "ff", "c0", PayloadType.VARIABLE_LENGTH),
        new ByteSet(START_OF_FRAME2, "ff", "c2", PayloadType.VARIABLE_LENGTH),
        new ByteSet(HUFFMAN_TABLE, "ff", "c4", PayloadType.VARIABLE_LENGTH),
        new ByteSet(QUANTIZATION_TABLE, "ff", "db", PayloadType.VARIABLE_LENGTH),
        new ByteSet(RESTART_INTERVAL, "ff", "d", PayloadType.TWO_BYTES),
        new ByteSet(START_OF_SCAN, "ff", "da", PayloadType.VARIABLE_LENGTH),
        new ByteSet(RESTART, "ff", "d", 7),
        new ByteSet(APPLICATION_SPECIFIC, "ff", "e1", PayloadType.VARIABLE_LENGTH),
        new ByteSet(COMMENT, "ff", "fe", PayloadType.VARIABLE_LENGTH),
        new ByteSet(END_OF_IMAGE, "ff", "d9", PayloadType.NONE)
    };

    public static final Map<String, ByteSet> BYTE_SET_MAPPINGS = new HashMap<>();

    static {
        for (ByteSet b : BYTE_SETS) {
            BYTE_SET_MAPPINGS.put(b.name, b);
        }
    }

    public static Map<String, Integer> findMetadataIndexes(byte[] copy) {
        return findByteSets(copy, BYTE_SETS);
    }

    public static JPEGMetadata getMetadata(byte[] src) {
        JPEGMetadata metadata = new JPEGMetadata();
        Map<String, Integer> foundByteSets = findByteSets(src, BYTE_SETS);
        metadata.startOfFrame1 = fetchByteSetContent(src, BYTE_SET_MAPPINGS.get(START_OF_FRAME0), foundByteSets.get(START_OF_FRAME0), false);
        metadata.endOfImage = fetchByteSetContent(src, BYTE_SET_MAPPINGS.get(END_OF_IMAGE), foundByteSets.get(END_OF_IMAGE), false);
        return metadata;
    }

    private static ByteSetPayload fetchByteSetContent(byte[] copy, ByteSet byteSet, int start, boolean read) {
        PayloadType payloadType = byteSet.payloadType;
        ByteSetPayload byteSetPayload = new ByteSetPayload();
        byteSetPayload.owner = byteSet;
        byteSetPayload.start = start;

        switch (payloadType) {
            case TWO_BYTES: {
                if (start + 1 > copy.length) {
                    throw new PhotoUtilsException(
                            String.format("Attempted to read byteset %s. However, it was found that the payload index was out of range", byteSet.name)
                    );
                }
                byteSetPayload.end = start + 1;
                if (read) {
                    byteSetPayload.payload = new byte[]{copy[start], copy[start + 1]};
                }
            }
            case VARIABLE_LENGTH: {
                byte[] length = {copy[start], copy[start + 1]};
                int numberOfBytesToRead = HexUtils.toInt(length[0], length[1]);
                byteSetPayload.start = start + 2;
                byteSetPayload.end = start + numberOfBytesToRead - 1;
                if (read) {
                    byte[] payload = new byte[numberOfBytesToRead - 2];
                    int payloadIndex = 0;
                    for (int i = start + 2; i < start + numberOfBytesToRead; i++) {
                        payload[payloadIndex] = copy[i];
                        payloadIndex++;
                    }
                    System.out.println("Payload:" + Arrays.toString(HexUtils.bytesToHex(payload)));
                    byteSetPayload.payload = payload;
                }

            }
            case NONE: {
                byteSetPayload.end = start;
            }
        }
        return byteSetPayload;
    }

    private static Map<String, Integer> findByteSets(byte[] copy, ByteSet... byteSets) {
        Map<String, Integer> foundSets = new HashMap<>();
        for (int j = 0; j < copy.length; j++) {
            if (j + 1 >= copy.length) {
                break;
            }
            byte b0 = copy[j];
            byte b1 = copy[j + 1];
            for (ByteSet s : byteSets) {
                String[] possible1Bytes;
                //if the byte set has an n variable, collect all possible 
                //byte value
                if (s.n != null) {
                    possible1Bytes = new String[s.n + 1];
                    for (int i = 0; i < s.n; i++) {
                        possible1Bytes[i] = s.byte1.concat(String.valueOf(i));
                    }
                } else {
                    possible1Bytes = new String[]{s.byte1};
                }
                for (String byte1 : possible1Bytes) {
                    if (byteToHex(b0).equals(s.byte0) && byteToHex(b1).equals(byte1)) {
                        foundSets.put(
                                s.n == null ? s.name : s.name.concat(String.valueOf(s.n)),
                                j + 2//the starting index of the length bytes
                        );
                    }
                }
            }
        }
        return foundSets;
    }

    enum PayloadType {
        VARIABLE_LENGTH, TWO_BYTES, NONE;
    }

    static class ByteSet {

        String name;
        String byte0;
        String byte1;
        Integer n = null;
        PayloadType payloadType;

        public ByteSet(String name, String byte0, String byte1, PayloadType payloadType) {
            this.name = name;
            this.byte0 = byte0;
            this.byte1 = byte1;
            this.payloadType = payloadType;
        }

        public ByteSet(String name, String byte0, String byte1, Integer n) {
            this.name = name;
            this.byte0 = byte0;
            this.byte1 = byte1;
            this.n = n;
            this.payloadType = PayloadType.NONE;
        }

    }

    public static class JPEGMetadata {

        private ByteSetPayload startOfFrame1;
        private ByteSetPayload endOfImage;

        public int getStartOfFrame1StartIndex() {
            return startOfFrame1.start;
        }

        public int getStartOfFrameEndIndex() {
            return startOfFrame1.end;
        }

        public int getEndOfImageStartIndex() {
            return endOfImage.start;
        }

        public int getEndOfImageEndIndex() {
            return endOfImage.end;
        }
    }

    static class ByteSetPayload {

        int start;
        int end;
        ByteSet owner;
        byte[] payload;
    }
}
