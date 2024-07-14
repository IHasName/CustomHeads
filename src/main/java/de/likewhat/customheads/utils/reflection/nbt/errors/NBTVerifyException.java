package de.likewhat.customheads.utils.reflection.nbt.errors;

/**
 * Describes an Error where the NBT failed to be verified
 */
public class NBTVerifyException extends Exception {

    public NBTVerifyException(String message) {
        super(message);
    }

}
