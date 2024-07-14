package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt;

import de.likewhat.customheads.utils.reflection.nbt.NBTType;

/**
 * These are basically Proxies for the NBT Objects to interact with them a bit easier
 */
public interface NBTBaseWrapper {

    NBTType getType();

    Object getNBTObject();

    default NBTGenericWrapper asGeneric() {
        return new NBTGenericWrapper(this.getNBTObject());
    }

}
