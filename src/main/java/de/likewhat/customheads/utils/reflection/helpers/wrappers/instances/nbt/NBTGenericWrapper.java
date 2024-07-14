package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;
import de.likewhat.customheads.utils.reflection.nbt.NBTType;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

@Getter
public class NBTGenericWrapper implements NBTBaseWrapper {

    private static final MethodWrapper<Byte> NBT_AS_BYTE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsByte", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Byte> NBT_AS_BYTE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "h", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Byte> NBT_AS_BYTE = new MethodWrapper<>(null, Version.V1_17_R1, "asByte", NBTType.ANY_NUMERIC.getNBTClass());

    private static final MethodWrapper<Short> NBT_AS_SHORT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsShort", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Short> NBT_AS_SHORT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "g", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Short> NBT_AS_SHORT = new MethodWrapper<>(null, Version.V1_17_R1, "asShort", NBTType.ANY_NUMERIC.getNBTClass());

    private static final MethodWrapper<Integer> NBT_AS_INT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsInt", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Integer> NBT_AS_INT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "f", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Integer> NBT_AS_INT = new MethodWrapper<>(null, Version.V1_17_R1, "asInt", NBTType.ANY_NUMERIC.getNBTClass());

    private static final MethodWrapper<Long> NBT_AS_LONG_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsLong", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Long> NBT_AS_LONG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "e", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Long> NBT_AS_LONG = new MethodWrapper<>(null, Version.V1_17_R1, "asLong", NBTType.ANY_NUMERIC.getNBTClass());

    private static final MethodWrapper<Float> NBT_AS_FLOAT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsFloat", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Float> NBT_AS_FLOAT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "j", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Float> NBT_AS_FLOAT = new MethodWrapper<>(null, Version.V1_17_R1, "asFloat", NBTType.ANY_NUMERIC.getNBTClass());

    private static final MethodWrapper<Double> NBT_AS_DOUBLE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsDouble", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Double> NBT_AS_DOUBLE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "i", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Double> NBT_AS_DOUBLE = new MethodWrapper<>(null, Version.V1_17_R1, "asDouble", NBTType.ANY_NUMERIC.getNBTClass());

    private static final MethodWrapper<String> NBT_AS_STRING_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsString", NBTType.BASE.getNBTClass());
    private static final MethodWrapper<String> NBT_AS_STRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "e_", NBTType.BASE.getNBTClass());
    private static final MethodWrapper<String> NBT_AS_STRING = new MethodWrapper<>(null, Version.V1_17_R1, "asString", NBTType.BASE.getNBTClass());

    private static final MethodWrapper<Byte[]> NBT_AS_BYTE_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsByteArray", NBTType.BYTE_ARRAY.getNBTClass());
    private static final MethodWrapper<Byte[]> NBT_AS_BYTE_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "d", NBTType.BYTE_ARRAY.getNBTClass());
    private static final MethodWrapper<Byte[]> NBT_AS_BYTE_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getBytes", NBTType.BYTE_ARRAY.getNBTClass());

    private static final MethodWrapper<Integer[]> NBT_AS_INTEGER_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsIntArray", NBTType.INTEGER_ARRAY.getNBTClass());
    private static final MethodWrapper<Integer[]> NBT_AS_INTEGER_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "f", NBTType.INTEGER_ARRAY.getNBTClass());
    private static final MethodWrapper<Integer[]> NBT_AS_INTEGER_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getInts", NBTType.INTEGER_ARRAY.getNBTClass());

    private static final MethodWrapper<Long[]> NBT_AS_LONG_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsLongArray", NBTType.LONG_ARRAY.getNBTClass());
    private static final MethodWrapper<Long[]> NBT_AS_LONG_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_20_R3, "f", NBTType.LONG_ARRAY.getNBTClass());
    private static final MethodWrapper<Long[]> NBT_AS_LONG_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getLongs", NBTType.LONG_ARRAY.getNBTClass());

    protected final Object nbtObject;
    protected final NBTType type;

    protected NBTGenericWrapper(NBTType nbtType) {
        this.nbtObject = nbtType.createInstance();
        this.type = nbtType;
    }

    public NBTGenericWrapper(Object nbtObject) {
        this.nbtObject = nbtObject;
        this.type = NBTType.getNBTTypeFromInstance(nbtObject);
    }

    @Override
    public Object getNBTObject() {
        return this.nbtObject;
    }

    public boolean isCompound() {
        return this.type == NBTType.COMPOUND;
    }

    public boolean isList() {
        return this.type == NBTType.LIST;
    }

    public boolean isByte() {
        return this.type == NBTType.BYTE;
    }

    public boolean isShort() {
        return this.type == NBTType.SHORT;
    }

    public boolean isInt() {
        return this.type == NBTType.INT;
    }

    public boolean isLong() {
        return this.type == NBTType.LONG;
    }

    public boolean isFloat() {
        return this.type == NBTType.FLOAT;
    }

    public boolean isDouble() {
        return this.type == NBTType.DOUBLE;
    }

    public boolean isString() {
        return this.type == NBTType.STRING;
    }

    public boolean isByteArray() {
        return this.type == NBTType.BYTE_ARRAY;
    }

    public boolean isIntegerArray() {
        return this.type == NBTType.INTEGER_ARRAY;
    }

    public boolean isLongArray() {
        return this.type == NBTType.LONG_ARRAY;
    }

    public NBTTagCompoundWrapper asCompound() {
        return new NBTTagCompoundWrapper(this.nbtObject);
    }

    public NBTTagListWrapper asList() {
        return new NBTTagListWrapper(this.nbtObject);
    }

    // TODO Throw an Error instead of returning a Value?
    public byte asByte() {
        if(type.isNumber()) {
            return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_BYTE, (byte) 0);
        }
        return 0;
    }

    public short asShort() {
        if(type.isNumber()) {
            return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_SHORT, (short) 0);
        }
        return 0;
    }

    public int asInt() {
        if(type.isNumber()) {
            return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_INT, 0);
        }
        return 0;
    }

    public long asLong() {
        if(type.isNumber()) {
            return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_LONG, 0L);
        }
        return 0;
    }

    public float asFloat() {
        if(type.isNumber()) {
            return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_FLOAT, 0f);
        }
        return 0;
    }

    public double asDouble() {
        if(type.isNumber()) {
            return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_DOUBLE, 0d);
        }
        return 0;
    }

    public String asString() {
        return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_STRING, "");
    }

    public byte[] asByteArray() {
        if(isByteArray()) {
            return ArrayUtils.toPrimitive(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_BYTE_ARRAY, new Byte[0]));
        }
        return null;
    }

    public int[] asIntegerArray() {
        if(isIntegerArray()) {
            return ArrayUtils.toPrimitive(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_INTEGER_ARRAY, new Integer[0]));
        }
        return null;
    }

    public long[] asLongArray() {
        if(isLongArray()) {
            return ArrayUtils.toPrimitive(ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_LONG_ARRAY, new Long[0]));
        }
        return null;
    }
}