package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt;

import de.likewhat.customheads.utils.reflection.nbt.NBTType;

/**
 * These are basically Proxies for the NBT Objects to interact with them a bit easier
 */
public interface NBTBaseWrapper {

    NBTType getType();

    Object getNBTObject();

    boolean isCompound();

    boolean isList();

    boolean isGeneric();

    default NBTTagCompoundWrapper asCompound() {
        return new NBTTagCompoundWrapper(this.getNBTObject());
    }

    default NBTTagListWrapper asList() {
        return new NBTTagListWrapper(this.getNBTObject());
    }

    default NBTGenericWrapper asGeneric() {
        return new NBTGenericWrapper(this.getNBTObject());
    }

}
