package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt;

import de.likewhat.customheads.utils.reflection.nbt.NBTType;

/**
 * These are basically Proxies for the NBT Objects to interact with them a bit easier
 */
public interface NBTBaseWrapper {

    /**
     * @return The NBTType of this Instance
     */
    NBTType getType();

    /**
     * @return The Raw NBT Instance of this Instance
     */
    Object getNBTObject();

    boolean isCompound();

    boolean isList();

    /**
     * @return Whether this Instance is a Generic/Primitive Type
     */
    boolean isGeneric();

    /**
     * @throws de.likewhat.customheads.utils.reflection.nbt.errors.NBTException when the Instance if not a Compound
     * @return The Instance as {@link NBTTagCompoundWrapper}
     */
    default NBTTagCompoundWrapper asCompound() {
        return new NBTTagCompoundWrapper(this.getNBTObject());
    }

    /**
     * @throws de.likewhat.customheads.utils.reflection.nbt.errors.NBTException when the Instance is not a List
     * @return The Instance as {@link NBTTagListWrapper}
     */
    default NBTTagListWrapper asList() {
        return new NBTTagListWrapper(this.getNBTObject());
    }

    /**
     * @return The Instance as {@link NBTTagCompoundWrapper}
     */
    default NBTGenericWrapper asGeneric() {
        return new NBTGenericWrapper(this.getNBTObject());
    }

}
