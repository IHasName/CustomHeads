package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.ClassWrappers;
import de.likewhat.customheads.utils.reflection.nbt.NBTType;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.AbstractList;

public class NBTTagListWrapper extends AbstractList<NBTGenericWrapper> implements NBTBaseWrapper {

    public static final MethodWrapper<Integer> SIZE = new MethodWrapper<>(null, null, "size", NBTType.LIST.getNBTClass());

    private static final MethodWrapper<Byte> GET_LIST_ELEMENTS_TYPE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getElementType", NBTType.LIST.getNBTClass());
    private static final MethodWrapper<Byte> GET_LIST_ELEMENTS_TYPE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "f", NBTType.LIST.getNBTClass(), GET_LIST_ELEMENTS_TYPE_V1205);
    public static final MethodWrapper<Byte> GET_LIST_ELEMENTS_TYPE = new MethodWrapper<>(null, Version.V1_17_R1, "e", NBTType.LIST.getNBTClass(), GET_LIST_ELEMENTS_TYPE_V1181);

    private static final MethodWrapper<Void> ADD_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "add", NBTType.LIST.getNBTClass(), int.class, ClassWrappers.NBT_BASE.resolve());
    private static final MethodWrapper<Void> ADD_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "c", NBTType.LIST.getNBTClass(), ADD_V1205, int.class, ClassWrappers.NBT_BASE.resolve());
    private static final MethodWrapper<Void> ADD = new MethodWrapper<>(null, Version.V1_17_R1, "add", NBTType.LIST.getNBTClass(), ADD_V1181, int.class, ClassWrappers.NBT_BASE.resolve());

    private static final MethodWrapper<Boolean> ADD_TAG_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "addTag", NBTType.LIST.getNBTClass(), int.class, ClassWrappers.NBT_BASE.resolve());
    private static final MethodWrapper<Boolean> ADD_TAG = new MethodWrapper<>(null, Version.V1_20_R3, "b", NBTType.LIST.getNBTClass(), ADD_TAG_V1205, int.class, ClassWrappers.NBT_BASE.resolve());

    // Returns the replaced Value when set (throws an Error when not the same Type as the List)
    private static final MethodWrapper<Object> SET_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "set", NBTType.LIST.getNBTClass(), int.class, ClassWrappers.NBT_BASE.resolve());
    private static final MethodWrapper<Object> SET_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "d", NBTType.LIST.getNBTClass(), SET_V1205, int.class, ClassWrappers.NBT_BASE.resolve());
    public static final MethodWrapper<Object> SET = new MethodWrapper<>(null, Version.V1_17_R1, "set", NBTType.LIST.getNBTClass(), SET_V1181, int.class, ClassWrappers.NBT_BASE.resolve());

    // Returns if the Value was set or not
    private static final MethodWrapper<Boolean> SET_TAG_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "setTag", NBTType.LIST.getNBTClass(), int.class, ClassWrappers.NBT_BASE.resolve());
    public static final MethodWrapper<Boolean> SET_TAG = new MethodWrapper<>(null, Version.V1_20_R3, "a", NBTType.LIST.getNBTClass(), SET_TAG_V1205, int.class, ClassWrappers.NBT_BASE.resolve());

    // Getters
    private static final MethodWrapper<Object> GET_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "get", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Object> GET_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "k", NBTType.LIST.getNBTClass(), GET_V1205, int.class);
    public static final MethodWrapper<Object> GET = new MethodWrapper<>(null, Version.V1_17_R1, "get", NBTType.LIST.getNBTClass(), GET_V1181, int.class);

    private static final MethodWrapper<Short> GET_SHORT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getShort", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Short> GET_SHORT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "g", NBTType.LIST.getNBTClass(), GET_SHORT_V1205, int.class);
    public static final MethodWrapper<Short> GET_SHORT = new MethodWrapper<>(null, Version.V1_17_R1, "getShort", NBTType.LIST.getNBTClass(), GET_SHORT_V1181, int.class);

    private static final MethodWrapper<Integer> GET_INT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getInt", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Integer> GET_INT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "h", NBTType.LIST.getNBTClass(), GET_INT_V1205, int.class);
    public static final MethodWrapper<Integer> GET_INT = new MethodWrapper<>(null, Version.V1_17_R1, "getInt", NBTType.LIST.getNBTClass(), GET_INT_V1181, int.class);

    private static final MethodWrapper<Long> GET_LONG_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getLong", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Long> GET_LONG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "i", NBTType.LIST.getNBTClass(), GET_LONG_V1205, int.class);
    public static final MethodWrapper<Long> GET_LONG = new MethodWrapper<>(null, Version.V1_17_R1, "getLong", NBTType.LIST.getNBTClass(), GET_LONG_V1181, int.class);

    private static final MethodWrapper<Float> GET_FLOAT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getFloat", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Float> GET_FLOAT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "j", NBTType.LIST.getNBTClass(), GET_FLOAT_V1205, int.class);
    public static final MethodWrapper<Float> GET_FLOAT = new MethodWrapper<>(null, Version.V1_17_R1, "getFloat", NBTType.LIST.getNBTClass(), GET_FLOAT_V1181, int.class);

    private static final MethodWrapper<Double> GET_DOUBLE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getDouble", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Double> GET_DOUBLE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "k", NBTType.LIST.getNBTClass(), GET_DOUBLE_V1205, int.class);
    private static final MethodWrapper<Double> GET_DOUBLE = new MethodWrapper<>(null, Version.V1_17_R1, "getDouble", NBTType.LIST.getNBTClass(), GET_DOUBLE_V1181, int.class);

    private static final MethodWrapper<String> GET_STRING_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getString", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<String> GET_STRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "j", NBTType.LIST.getNBTClass(), GET_STRING_V1205, int.class);
    private static final MethodWrapper<String> GET_STRING = new MethodWrapper<>(null, Version.V1_17_R1, "getString", NBTType.LIST.getNBTClass(), GET_STRING_V1181, int.class);

    private static final MethodWrapper<Integer[]> GET_INT_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getIntArray", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Integer[]> GET_INT_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "n", NBTType.LIST.getNBTClass(), GET_INT_ARRAY_V1205, int.class);
    private static final MethodWrapper<Integer[]> GET_INT_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getIntArray", NBTType.LIST.getNBTClass(), GET_INT_ARRAY_V1181, int.class);

    private static final MethodWrapper<Long[]> GET_LONG_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getLongArray", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Long[]> GET_LONG_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "o", NBTType.LIST.getNBTClass(), GET_LONG_ARRAY_V1205, int.class);
    private static final MethodWrapper<Long[]> GET_LONG_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getLongArray", NBTType.LIST.getNBTClass(), GET_LONG_ARRAY_V1181, int.class);

    private static final MethodWrapper<Object> GET_COMPOUND_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getCompound", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Object> GET_COMPOUND_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "p", NBTType.LIST.getNBTClass(), GET_COMPOUND_V1205, int.class);
    private static final MethodWrapper<Object> GET_COMPOUND = new MethodWrapper<>(null, Version.V1_17_R1, "getCompound", NBTType.LIST.getNBTClass(), GET_COMPOUND_V1181, int.class);

    private static final MethodWrapper<Object> GET_LIST_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getList", NBTType.LIST.getNBTClass(), int.class);
    private static final MethodWrapper<Object> GET_LIST_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "c", NBTType.LIST.getNBTClass(), GET_LIST_V1205, int.class);
    private static final MethodWrapper<Object> GET_LIST = new MethodWrapper<>(null, Version.V1_17_R1, "getList", NBTType.LIST.getNBTClass(), GET_LIST_V1181, int.class);

    private static final NBTType TYPE = NBTType.LIST;

    private final Object nbtObject;

    public NBTTagListWrapper() {
        this.nbtObject = TYPE.createInstance();
    }

    public NBTTagListWrapper(Object nbtObject) {
        if(!TYPE.getNBTClass().isInstance(nbtObject)) {
            throw new IllegalArgumentException("NBT Object has to be an Instance of " + TYPE.getNBTClass().getSimpleName() + " instead got " + nbtObject.getClass().getSimpleName());
        }
        this.nbtObject = nbtObject;
    }

    @Override
    public boolean isCompound() {
        return false;
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public boolean isGeneric() {
        return false;
    }

    public static NBTTagListWrapper of(Object nbtObject) {
        return new NBTTagListWrapper(nbtObject);
    }

    public NBTType getListType() {
        return NBTType.getById(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_LIST_ELEMENTS_TYPE, (byte) -1));
    }

    public int size() {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, SIZE, 0);
    }

    public void add(NBTBaseWrapper wrapper) {
        addObject(wrapper.getNBTObject());
    }

    public void addObject(Object nbtObject) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, ADD, size(), nbtObject);
    }

    public boolean addTag(int index, NBTBaseWrapper wrapper) {
        return addTag(index, wrapper.getNBTObject());
    }

    public boolean addTag(int index, Object nbtObject) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, ADD_TAG, false, index, nbtObject);
    }

    @Nullable
    public NBTBaseWrapper set(int index, NBTBaseWrapper wrapper) {
        Object result = this.setObject(index, wrapper.getNBTObject());
        if(result == null) {
            return null;
        } else {
            return new NBTGenericWrapper(result);
        }
    }

    public Object setObject(int index, Object nbtObject) {
        return ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET, index, nbtObject);
    }

    public boolean setTag(int index, NBTBaseWrapper wrapper) {
        return this.setTag(index, wrapper.getNBTObject());
    }

    public boolean setTag(int index, Object nbtObject) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, SET_TAG, false, index, nbtObject);
    }

    public NBTGenericWrapper get(int index) {
        return new NBTGenericWrapper(ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, GET, index));
    }

    public short getShort(int index) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_SHORT, (short) 0, index);
    }

    public int getInt(int index) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_INT, 0, index);
    }

    public long getLong(int index) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_LONG, 0L, index);
    }

    public float getFloat(int index) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_FLOAT, 0f, index);
    }

    public double getDouble(int index) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_DOUBLE, 0d, index);
    }

    public String getString(int index) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_STRING, "", index);
    }

    public int[] getIntArray(int index) {
        return ArrayUtils.toPrimitive(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_INT_ARRAY, new Integer[0], index));
    }

    public long[] getLongArray(int index) {
        return ArrayUtils.toPrimitive(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_LONG_ARRAY, new Long[0], index));
    }

    public NBTTagCompoundWrapper getCompound(int index) {
        return new NBTTagCompoundWrapper(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_COMPOUND, NBTType.COMPOUND.createInstance(), index));
    }

    public NBTTagListWrapper getList(int index) {
        return new NBTTagListWrapper(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_LIST, NBTType.LIST.createInstance(), index));
    }

    @Override
    public NBTType getType() {
        return TYPE;
    }

    @Override
    public Object getNBTObject() {
        return this.nbtObject;
    }

}
