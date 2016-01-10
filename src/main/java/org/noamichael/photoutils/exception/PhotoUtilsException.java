package org.noamichael.photoutils.exception;

/**
 *
 * @author micha_000
 */
public class PhotoUtilsException extends RuntimeException {

    public static final String FILE_NOT_FOUND = "File for scrabling not found.";

    public PhotoUtilsException() {
    }

    public PhotoUtilsException(String message) {
        super(message);
    }

    public PhotoUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhotoUtilsException(Throwable cause) {
        super(cause);
    }

    public PhotoUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
