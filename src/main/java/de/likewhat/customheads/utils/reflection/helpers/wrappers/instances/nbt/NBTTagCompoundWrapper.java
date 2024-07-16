package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.ClassWrappers;
import de.likewhat.customheads.utils.reflection.nbt.NBTType;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

public class NBTTagCompoundWrapper implements NBTBaseWrapper {

    // Misc
    public static final MethodWrapper<Byte> GET_KEY_TYPE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getKeyType", NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<Byte> GET_KEY_TYPE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "d", NBTType.COMPOUND.getNBTClass(), GET_KEY_TYPE_V1205, String.class);
    public static final MethodWrapper<Byte> GET_KEY_TYPE = new MethodWrapper<>(null, Version.V1_17_R1, "getKeyType", NBTType.COMPOUND.getNBTClass(), GET_KEY_TYPE_V1181, String.class);

    private static final MethodWrapper<Boolean> HAS_KEY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "contains", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Boolean> HAS_KEY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "e", NBTType.COMPOUND.getNBTClass(), HAS_KEY_V1205, String.class);
    public static final MethodWrapper<Boolean> HAS_KEY = new MethodWrapper<>(null, Version.V1_17_R1, "hasKey", NBTType.COMPOUND.getNBTClass(), HAS_KEY_V1181, String.class);

    private static final MethodWrapper<Boolean> HAS_KEY_OF_TYPE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "contains", NBTType.COMPOUND.getNBTClass(), String.class, int.class);
    private static final MethodWrapper<Boolean> HAS_KEY_OF_TYPE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "b", NBTType.COMPOUND.getNBTClass(), HAS_KEY_OF_TYPE_V1205, String.class, int.class);
    public static final MethodWrapper<Boolean> HAS_KEY_OF_TYPE = new MethodWrapper<>(null, Version.V1_17_R1, "hasKeyOfType", NBTType.COMPOUND.getNBTClass(), HAS_KEY_OF_TYPE_V1181, String.class, int.class);

    private static final MethodWrapper<Integer> SIZE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "size", NBTType.COMPOUND.getNBTClass());
    public static final MethodWrapper<Integer> SIZE = new MethodWrapper<>(null, Version.V1_20_R3, "e", NBTType.COMPOUND.getNBTClass(), SIZE_V1205);

    private static final MethodWrapper<Set<String>> GET_KEYS_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAllKeys", NBTType.COMPOUND.getNBTClass());
    private static final MethodWrapper<Set<String>> GET_KEYS_V119R2 = new MethodWrapper<>(Version.V1_19_R2, Version.V1_20_R3, "e", NBTType.COMPOUND.getNBTClass(), GET_KEYS_V1205);
    private static final MethodWrapper<Set<String>> GET_KEYS_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "d", NBTType.COMPOUND.getNBTClass(), GET_KEYS_V119R2);
    public static final MethodWrapper<Set<String>> GET_KEYS = new MethodWrapper<>(null, Version.V1_17_R1, "getKeys", NBTType.COMPOUND.getNBTClass(), GET_KEYS_V1181);

    private static final MethodWrapper<Map.Entry<String, Object>> ENTRY_SET = new MethodWrapper<>(Version.V1_20_5, null, "entrySet", NBTType.COMPOUND.getNBTClass(), GET_KEYS_V1181);
    // This is a protected Method which will be changed to a Map Entry in the Wrapper
    private static final MethodWrapper<Map<String, Object>> UNMODIFIABLE_COPY_V119R2 = new MethodWrapper<>(Version.V1_19_R2, Version.V1_20_R3, "i", NBTType.COMPOUND.getNBTClass(), ENTRY_SET);
    private static final MethodWrapper<Map<String, Object>> UNMODIFIABLE_COPY = new MethodWrapper<>(null, Version.V1_17_R1, "h", NBTType.COMPOUND.getNBTClass(), UNMODIFIABLE_COPY_V119R2);

    // Getters
    private static final MethodWrapper<Object> GET_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "get", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Object> GET_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "c", NBTType.COMPOUND.getNBTClass(), GET_V1205, String.class);
    public static final MethodWrapper<Object> GET = new MethodWrapper<>(null, Version.V1_17_R1, "get", NBTType.COMPOUND.getNBTClass(), GET_V1181, String.class);

    private static final MethodWrapper<Byte> GET_BYTE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getByte", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Byte> GET_BYTE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "f", NBTType.COMPOUND.getNBTClass(), GET_BYTE_V1205, String.class);
    public static final MethodWrapper<Byte> GET_BYTE = new MethodWrapper<>(null, Version.V1_17_R1, "getByte", NBTType.COMPOUND.getNBTClass(), GET_BYTE_V1181, String.class);

    private static final MethodWrapper<Short> GET_SHORT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getShort", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Short> GET_SHORT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "g", NBTType.COMPOUND.getNBTClass(), GET_SHORT_V1205, String.class);
    public static final MethodWrapper<Short> GET_SHORT = new MethodWrapper<>(null, Version.V1_17_R1, "getShort", NBTType.COMPOUND.getNBTClass(), GET_SHORT_V1181, String.class);

    private static final MethodWrapper<Integer> GET_INT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getInt", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Integer> GET_INT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "h", NBTType.COMPOUND.getNBTClass(), GET_INT_V1205, String.class);
    public static final MethodWrapper<Integer> GET_INT = new MethodWrapper<>(null, Version.V1_17_R1, "getInt", NBTType.COMPOUND.getNBTClass(), GET_INT_V1181, String.class);

    private static final MethodWrapper<Long> GET_LONG_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getLong", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Long> GET_LONG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "i", NBTType.COMPOUND.getNBTClass(), GET_LONG_V1205, String.class);
    public static final MethodWrapper<Long> GET_LONG = new MethodWrapper<>(null, Version.V1_17_R1, "getLong", NBTType.COMPOUND.getNBTClass(), GET_LONG_V1181, String.class);

    private static final MethodWrapper<UUID> GET_UUID_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getUUID", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<UUID> GET_UUID_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), GET_UUID_V1205, String.class);
    public static final MethodWrapper<UUID> GET_UUID = new MethodWrapper<>(null, Version.V1_17_R1, "a", NBTType.COMPOUND.getNBTClass(), GET_UUID_V1181, String.class);

    private static final MethodWrapper<Float> GET_FLOAT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getFloat", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Float> GET_FLOAT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "j", NBTType.COMPOUND.getNBTClass(), GET_FLOAT_V1205, String.class);
    public static final MethodWrapper<Float> GET_FLOAT = new MethodWrapper<>(null, Version.V1_17_R1, "getFloat", NBTType.COMPOUND.getNBTClass(), GET_FLOAT_V1181, String.class);

    private static final MethodWrapper<Double> GET_DOUBLE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getDouble", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Double> GET_DOUBLE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "k", NBTType.COMPOUND.getNBTClass(), GET_DOUBLE_V1205, String.class);
    public static final MethodWrapper<Double> GET_DOUBLE = new MethodWrapper<>(null, Version.V1_17_R1, "getDouble", NBTType.COMPOUND.getNBTClass(), GET_DOUBLE_V1181, String.class);

    private static final MethodWrapper<String> GET_STRING_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getString", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<String> GET_STRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "l", NBTType.COMPOUND.getNBTClass(), GET_STRING_V1205, String.class);
    public static final MethodWrapper<String> GET_STRING = new MethodWrapper<>(null, Version.V1_17_R1, "getString", NBTType.COMPOUND.getNBTClass(), GET_STRING_V1181, String.class);

    private static final MethodWrapper<Byte[]> GET_BYTE_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "m", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Byte[]> GET_BYTE_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "m", NBTType.COMPOUND.getNBTClass(), GET_BYTE_ARRAY_V1205, String.class);
    public static final MethodWrapper<Byte[]> GET_BYTE_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getByteArray", NBTType.COMPOUND.getNBTClass(), GET_BYTE_ARRAY_V1181, String.class);

    private static final MethodWrapper<Integer[]> GET_INT_ARRAY_V1205 = new MethodWrapper<>(Version.V1_18_R1, null, "getIntArray", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Integer[]> GET_INT_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "n", NBTType.COMPOUND.getNBTClass(), GET_INT_ARRAY_V1205, String.class);
    public static final MethodWrapper<Integer[]> GET_INT_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getIntArray", NBTType.COMPOUND.getNBTClass(), GET_INT_ARRAY_V1181, String.class);

    private static final MethodWrapper<Long[]> GET_LONG_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getLongArray", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Long[]> GET_LONG_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "o", NBTType.COMPOUND.getNBTClass(), GET_LONG_ARRAY_V1205, String.class);
    public static final MethodWrapper<Long[]> GET_LONG_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getLongArray", NBTType.COMPOUND.getNBTClass(), GET_LONG_ARRAY_V1181, String.class);

    private static final MethodWrapper<Boolean> GET_BOOLEAN_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getBoolean", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Boolean> GET_BOOLEAN_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "q", NBTType.COMPOUND.getNBTClass(), GET_BOOLEAN_V1205, String.class);
    public static final MethodWrapper<Boolean> GET_BOOLEAN = new MethodWrapper<>(null, Version.V1_17_R1, "getBoolean", NBTType.COMPOUND.getNBTClass(), GET_BOOLEAN_V1181, String.class);

    private static final MethodWrapper<Object> GET_COMPOUND_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getCompound", NBTType.COMPOUND.getNBTClass(), String.class);
    private static final MethodWrapper<Object> GET_COMPOUND_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "p", NBTType.COMPOUND.getNBTClass(), GET_COMPOUND_V1205, String.class);
    public static final MethodWrapper<Object> GET_COMPOUND = new MethodWrapper<>(null, Version.V1_17_R1, "getCompound", NBTType.COMPOUND.getNBTClass(), GET_COMPOUND_V1181, String.class);

    private static final MethodWrapper<Object> GET_LIST_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getList", NBTType.COMPOUND.getNBTClass(), String.class, int.class);
    private static final MethodWrapper<Object> GET_LIST_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "c", NBTType.COMPOUND.getNBTClass(), GET_LIST_V1205, String.class, int.class);
    public static final MethodWrapper<Object> GET_LIST = new MethodWrapper<>(null, Version.V1_17_R1, "getList", NBTType.COMPOUND.getNBTClass(), GET_LIST_V1181, String.class, int.class);

    // Setters
    private static final MethodWrapper<Object> SET_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "put", NBTType.COMPOUND.getNBTClass(), String.class, ClassWrappers.NBT_BASE.resolve());
    private static final MethodWrapper<Object> SET_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_V1205, String.class, ClassWrappers.NBT_BASE.resolve());
    public static final MethodWrapper<Object> SET = new MethodWrapper<>(null, Version.V1_17_R1, "set", NBTType.COMPOUND.getNBTClass(), SET_V1181, String.class, ClassWrappers.NBT_BASE.resolve());

    private static final MethodWrapper<Void> SET_BYTE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putByte", NBTType.COMPOUND.getNBTClass(), String.class, byte.class);
    private static final MethodWrapper<Void> SET_BYTE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_BYTE_V1205, String.class, byte.class);
    public static final MethodWrapper<Void> SET_BYTE = new MethodWrapper<>(null, Version.V1_17_R1, "setByte", NBTType.COMPOUND.getNBTClass(), SET_BYTE_V1181, String.class, byte.class);

    private static final MethodWrapper<Void> SET_SHORT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putShort", NBTType.COMPOUND.getNBTClass(), String.class, short.class);
    private static final MethodWrapper<Void> SET_SHORT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_SHORT_V1205, String.class, short.class);
    public static final MethodWrapper<Void> SET_SHORT = new MethodWrapper<>(null, Version.V1_17_R1, "setShort", NBTType.COMPOUND.getNBTClass(), SET_SHORT_V1181, String.class, short.class);

    private static final MethodWrapper<Void> SET_INT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putInt", NBTType.COMPOUND.getNBTClass(), String.class, int.class);
    private static final MethodWrapper<Void> SET_INT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_INT_V1205, String.class, int.class);
    public static final MethodWrapper<Void> SET_INT = new MethodWrapper<>(null, Version.V1_17_R1, "setInt", NBTType.COMPOUND.getNBTClass(), SET_INT_V1181, String.class, int.class);

    private static final MethodWrapper<Void> SET_LONG_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putLong", NBTType.COMPOUND.getNBTClass(), String.class, long.class);
    private static final MethodWrapper<Void> SET_LONG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_LONG_V1205, String.class, long.class);
    public static final MethodWrapper<Void> SET_LONG = new MethodWrapper<>(null, Version.V1_17_R1, "setLong", NBTType.COMPOUND.getNBTClass(), SET_LONG_V1181, String.class, long.class);

    private static final MethodWrapper<Void> SET_UUID_V1205 = new MethodWrapper<>(Version.V1_20_R3, null, "putUUID", NBTType.COMPOUND.getNBTClass(), String.class, UUID.class);
    public static final MethodWrapper<Void> SET_UUID = new MethodWrapper<>(null, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_UUID_V1205, String.class, UUID.class);

    private static final MethodWrapper<Void> SET_FLOAT_V1205 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "putFloat", NBTType.COMPOUND.getNBTClass(), String.class, float.class);
    private static final MethodWrapper<Void> SET_FLOAT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_FLOAT_V1205, String.class, float.class);
    public static final MethodWrapper<Void> SET_FLOAT = new MethodWrapper<>(null, Version.V1_17_R1, "setFloat", NBTType.COMPOUND.getNBTClass(), SET_FLOAT_V1181, String.class, float.class);

    private static final MethodWrapper<Void> SET_DOUBLE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putDouble", NBTType.COMPOUND.getNBTClass(), String.class, double.class);
    private static final MethodWrapper<Void> SET_DOUBLE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_DOUBLE_V1205, String.class, double.class);
    public static final MethodWrapper<Void> SET_DOUBLE = new MethodWrapper<>(null, Version.V1_17_R1, "setDouble", NBTType.COMPOUND.getNBTClass(), SET_DOUBLE_V1181, String.class, double.class);

    private static final MethodWrapper<Void> SET_STRING_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putString", NBTType.COMPOUND.getNBTClass(), String.class, String.class);
    private static final MethodWrapper<Void> SET_STRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_STRING_V1205, String.class, String.class);
    public static final MethodWrapper<Void> SET_STRING = new MethodWrapper<>(null, Version.V1_17_R1, "setString", NBTType.COMPOUND.getNBTClass(), SET_STRING_V1181, String.class, String.class);

    private static final MethodWrapper<Void> SET_BYTE_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putByteArray", NBTType.COMPOUND.getNBTClass(), String.class, byte[].class);
    private static final MethodWrapper<Void> SET_BYTE_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_BYTE_ARRAY_V1205, String.class, byte[].class);
    public static final MethodWrapper<Void> SET_BYTE_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "setByteArray", NBTType.COMPOUND.getNBTClass(), SET_BYTE_ARRAY_V1181, String.class, byte[].class);

    private static final MethodWrapper<Void> SET_BYTE_LIST_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putByteArray", NBTType.COMPOUND.getNBTClass(), String.class, List.class);
    public static final MethodWrapper<Void> SET_BYTE_LIST = new MethodWrapper<>(null, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_BYTE_LIST_V1205, String.class, List.class);

    private static final MethodWrapper<Void> SET_INT_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putIntArray", NBTType.COMPOUND.getNBTClass(), String.class, int[].class);
    private static final MethodWrapper<Void> SET_INT_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_INT_ARRAY_V1205, String.class, int[].class);
    public static final MethodWrapper<Void> SET_INT_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "setIntArray", NBTType.COMPOUND.getNBTClass(), SET_INT_ARRAY_V1181, String.class, int[].class);

    private static final MethodWrapper<Void> SET_INT_LIST_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putIntArray", NBTType.COMPOUND.getNBTClass(), String.class, List.class);
    public static final MethodWrapper<Void> SET_INT_LIST = new MethodWrapper<>(null, Version.V1_20_R3, "b", NBTType.COMPOUND.getNBTClass(), SET_INT_LIST_V1205, String.class, List.class);

    private static final MethodWrapper<Void> SET_LONG_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putLongArray", NBTType.COMPOUND.getNBTClass(), String.class, long[].class);
    public static final MethodWrapper<Void> SET_LONG_ARRAY = new MethodWrapper<>(null, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_LONG_ARRAY_V1205, String.class, long[].class);

    public static final MethodWrapper<Void> SET_LONG_LIST_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putLongArray", NBTType.COMPOUND.getNBTClass(), String.class, List.class);
    public static final MethodWrapper<Void> SET_LONG_LIST_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "c", NBTType.COMPOUND.getNBTClass(), SET_LONG_LIST_V1205, String.class, List.class);
    public static final MethodWrapper<Void> SET_LONG_LIST = new MethodWrapper<>(null, Version.V1_17_R1, "a", NBTType.COMPOUND.getNBTClass(), SET_LONG_LIST_V1181, String.class, List.class);

    private static final MethodWrapper<Void> SET_BOOLEAN_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "putBoolean", NBTType.COMPOUND.getNBTClass(), String.class, boolean.class);
    private static final MethodWrapper<Void> SET_BOOLEAN_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "a", NBTType.COMPOUND.getNBTClass(), SET_BOOLEAN_V1205, String.class, boolean.class);
    public static final MethodWrapper<Void> SET_BOOLEAN = new MethodWrapper<>(null, Version.V1_17_R1, "setBoolean", NBTType.COMPOUND.getNBTClass(), SET_BOOLEAN_V1181, String.class, boolean.class);

    public static final NBTType TYPE = NBTType.COMPOUND;

    private final Object nbtObject;

    public NBTTagCompoundWrapper() {
        this.nbtObject = TYPE.createInstance();
    }

    public NBTTagCompoundWrapper(Object nbtObject) {
        if(!TYPE.getNBTClass().isInstance(nbtObject)) {
            throw new IllegalArgumentException("NBT Object has to be an Instance of " + TYPE.getNBTClass().getSimpleName() + " instead got " + nbtObject.getClass().getSimpleName());
        }
        this.nbtObject = nbtObject;
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public boolean isGeneric() {
        return false;
    }

    public static NBTTagCompoundWrapper of(Object nbtObject) {
        return new NBTTagCompoundWrapper(nbtObject);
    }

    public NBTType getKeyType(String key) {
        return NBTType.getById(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_KEY_TYPE, (byte) -1, key));
    }

    public boolean hasKey(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, HAS_KEY, false, key);
    }

    public boolean hasKeyOfType(String key, NBTType type) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, HAS_KEY_OF_TYPE, false, key, type.getId());
    }

    public int size() {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, SIZE, -1);
    }

    public Set<String> keySet() {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_KEYS, Collections.emptySet());
    }

    public Set<Map.Entry<String, NBTGenericWrapper>> entrySet() {
        Object result = ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, UNMODIFIABLE_COPY);
        if(result == null) {
            return Collections.emptySet();
        }

        Set<Map.Entry<String, Object>> actualResult;
        if(result instanceof Set) {
            actualResult = (Set<Map.Entry<String, Object>>) result;
        } else {
            actualResult = ((Map<String, Object>) result).entrySet();
        }
        Set<Map.Entry<String, NBTGenericWrapper>> genericWrapperResult = new HashSet<>();
        actualResult.forEach(entry -> {
            genericWrapperResult.add(new AbstractMap.SimpleEntry<>(entry.getKey(), NBTGenericWrapper.of(entry.getValue())));
        });
        return genericWrapperResult;
    }

    // Getters
    @Override
    public NBTType getType() {
        return TYPE;
    }

    @Override
    public Object getNBTObject() {
        return this.nbtObject;
    }

    public NBTGenericWrapper get(String key) {
        return new NBTGenericWrapper(ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, GET, key));
    }

    public byte getByte(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_BYTE, (byte) 0, key);
    }

    public short getShort(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_SHORT, (short) 0, key);
    }

    public int getInt(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_INT, 0, key);
    }

    public long getLong(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_LONG, 0L, key);
    }

    public UUID getUUID(String key) {
        return ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, GET_UUID, key);
    }

    public float getFloat(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_FLOAT, 0f, key);
    }

    public double getDouble(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_DOUBLE, 0d, key);
    }

    public String getString(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_STRING, "", key);
    }

    public byte[] getByteArray(String key) {
        byte[] result = new byte[0];
        try {
            result = ArrayUtils.toPrimitive(GET_BYTE_ARRAY.invokeOn(this.nbtObject, key));
        } catch (InvocationTargetException | IllegalAccessException e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to call Wrapper", e);
        }
        return result;
    }

    public int[] getIntArray(String key) {
        int[] result = new int[0];
        try {
            result = ArrayUtils.toPrimitive(GET_INT_ARRAY.invokeOn(this.nbtObject, key));
        } catch (InvocationTargetException | IllegalAccessException e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to call Wrapper", e);
        }
        return result;
    }

    public long[] getLongArray(String key) {
        long[] result = new long[0];
        try {
            result = ArrayUtils.toPrimitive(GET_LONG_ARRAY.invokeOn(this.nbtObject, key));
        } catch (InvocationTargetException | IllegalAccessException e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to call Wrapper", e);
        }
        return result;
    }

    public NBTTagCompoundWrapper getCompound(String key) {
        return new NBTTagCompoundWrapper(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_COMPOUND, NBTType.COMPOUND.createInstance(), key));
    }

    public NBTTagListWrapper getList(String key) {
        return this.get(key).asList();
    }

    public NBTTagListWrapper getList(String key, int type) {
        return new NBTTagListWrapper(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_LIST, NBTType.LIST.createInstance(), key, type));
    }

    public NBTTagListWrapper getList(String key, NBTType type) {
        return getList(key, type.getId());
    }

    public boolean getBoolean(String key) {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, GET_BOOLEAN, false, key);
    }

    // Setters

    public Object set(String key, Object value) {
        return ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET, key, value);
    }

    public Object set(String key, NBTBaseWrapper wrapper) {
        return set(key, wrapper.getNBTObject());
    }

    public void setByte(String key, byte value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_BYTE, key, value);
    }

    public void setShort(String key, short value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_SHORT, key, value);
    }

    public void setInt(String key, int value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_INT, key, value);
    }

    public void setLong(String key, long value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_LONG, key, value);
    }

    public void setUUID(String key, UUID value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_UUID, key, value);
    }

    public void setFloat(String key, float value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_FLOAT, key, value);
    }

    public void setDouble(String key, double value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_DOUBLE, key, value);
    }

    public void setString(String key, String value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_STRING, key, value);
    }

    public void setByteArray(String key, byte[] value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_BYTE_ARRAY, key, value);
    }

    public void setByteArray(String key, List<Byte> value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_BYTE_LIST, key, value);
    }

    public void setIntArray(String key, int[] value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_INT_ARRAY, key, value);
    }

    public void setIntArray(String key, List<Integer> value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_INT_LIST, key, value);
    }

    public void setLongArray(String key, long[] value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_LONG_ARRAY, key, value);
    }

    public void setLongArray(String key, List<Long> value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_LONG_LIST, key, value);
    }

    public void setBoolean(String key, boolean value) {
        ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, SET_BOOLEAN, key, value);
    }


}