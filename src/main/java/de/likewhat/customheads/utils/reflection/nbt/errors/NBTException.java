package de.likewhat.customheads.utils.reflection.nbt.errors;

/**
 * Describes an Exception when handling NBT
 * Cause may be another Exception
 */
public class NBTException extends Exception {

    public NBTException(String message) {
        super(message);
    }

    public NBTException(String message, Throwable cause) {
        super(message, cause);
    }

    public NBTException(Throwable cause) {
        super(cause);
    }
}
