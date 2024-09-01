package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.ClassWrappers;
import de.likewhat.customheads.utils.reflection.nbt.NBTType;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

/**
 * A Helper Class for NBT Primitive Type wrapping
 */
@Getter
public class NBTGenericWrapper implements NBTBaseWrapper {

    // Maybe I'm gonna remove these Number Conversions all since asNumber exists
    private static final MethodWrapper<Byte> NBT_AS_BYTE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsByte", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Byte> NBT_AS_BYTE_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R3, "i", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_BYTE_V1205);
    private static final MethodWrapper<Byte> NBT_AS_BYTE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "h", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_BYTE_V1201);
    private static final MethodWrapper<Byte> NBT_AS_BYTE = new MethodWrapper<>(null, Version.V1_17_R1, "asByte", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_BYTE_V1181);

    private static final MethodWrapper<Short> NBT_AS_SHORT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsShort", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Short> NBT_AS_SHORT_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R3, "h", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_SHORT_V1205);
    private static final MethodWrapper<Short> NBT_AS_SHORT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "g", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_SHORT_V1201);
    private static final MethodWrapper<Short> NBT_AS_SHORT = new MethodWrapper<>(null, Version.V1_17_R1, "asShort", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_SHORT_V1181);

    private static final MethodWrapper<Integer> NBT_AS_INT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsInt", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Integer> NBT_AS_INT_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R3, "g", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_INT_V1205);
    private static final MethodWrapper<Integer> NBT_AS_INT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "f", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_INT_V1201);
    private static final MethodWrapper<Integer> NBT_AS_INT = new MethodWrapper<>(null, Version.V1_17_R1, "asInt", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_INT_V1181);

    private static final MethodWrapper<Long> NBT_AS_LONG_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsLong", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Long> NBT_AS_LONG_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R3, "f", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_LONG_V1205);
    private static final MethodWrapper<Long> NBT_AS_LONG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "e", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_LONG_V1201);
    private static final MethodWrapper<Long> NBT_AS_LONG = new MethodWrapper<>(null, Version.V1_17_R1, "asLong", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_LONG_V1181);

    private static final MethodWrapper<Float> NBT_AS_FLOAT_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsFloat", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Float> NBT_AS_FLOAT_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R3, "k", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_FLOAT_V1205);
    private static final MethodWrapper<Float> NBT_AS_FLOAT_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "j", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_FLOAT_V1201);
    private static final MethodWrapper<Float> NBT_AS_FLOAT = new MethodWrapper<>(null, Version.V1_17_R1, "asFloat", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_FLOAT_V1181);

    private static final MethodWrapper<Double> NBT_AS_DOUBLE_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsDouble", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Double> NBT_AS_DOUBLE_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R3, "j", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_DOUBLE_V1205);
    private static final MethodWrapper<Double> NBT_AS_DOUBLE_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "i", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_DOUBLE_V1201);
    private static final MethodWrapper<Double> NBT_AS_DOUBLE = new MethodWrapper<>(null, Version.V1_17_R1, "asDouble", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_DOUBLE_V1181);

    private static final MethodWrapper<Number> NBT_AS_NUMBER_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsNumber", NBTType.ANY_NUMERIC.getNBTClass());
    private static final MethodWrapper<Number> NBT_AS_NUMBER_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R3, "l", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_NUMBER_V1205);
    private static final MethodWrapper<Number> NBT_AS_NUMBER_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "k", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_NUMBER_V1201);
    private static final MethodWrapper<Number> NBT_AS_NUMBER = new MethodWrapper<>(null, Version.V1_17_R1, "asNumber", NBTType.ANY_NUMERIC.getNBTClass(), NBT_AS_NUMBER_V1181);
    
    // At this point I could just use toString =/
    private static final MethodWrapper<String> NBT_AS_STRING_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsString", ClassWrappers.NBT_BASE);
    private static final MethodWrapper<String> NBT_AS_STRING_V1203 = new MethodWrapper<>(Version.V1_20_R3, Version.V1_20_R3, "t_", ClassWrappers.NBT_BASE, NBT_AS_STRING_V1205);
    private static final MethodWrapper<String> NBT_AS_STRING_V1202 = new MethodWrapper<>(Version.V1_20_R2, Version.V1_20_R2, "r_", ClassWrappers.NBT_BASE, NBT_AS_STRING_V1203);
    private static final MethodWrapper<String> NBT_AS_STRING_V1201 = new MethodWrapper<>(Version.V1_20_R1, Version.V1_20_R1, "m_", ClassWrappers.NBT_BASE, NBT_AS_STRING_V1202);
    private static final MethodWrapper<String> NBT_AS_STRING_V1192 = new MethodWrapper<>(Version.V1_19_R2, Version.V1_19_R3, "f_", ClassWrappers.NBT_BASE, NBT_AS_STRING_V1201);
    private static final MethodWrapper<String> NBT_AS_STRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "e_", ClassWrappers.NBT_BASE, NBT_AS_STRING_V1192);
    private static final MethodWrapper<String> NBT_AS_STRING = new MethodWrapper<>(null, Version.V1_17_R1, "asString", ClassWrappers.NBT_BASE, NBT_AS_STRING_V1181);

    private static final MethodWrapper<Byte[]> NBT_AS_BYTE_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsByteArray", NBTType.BYTE_ARRAY.getNBTClass());
    private static final MethodWrapper<Byte[]> NBT_AS_BYTE_ARRAY_V1192 = new MethodWrapper<>(Version.V1_19_R2, Version.V1_20_R3, "e", NBTType.BYTE_ARRAY.getNBTClass(), NBT_AS_BYTE_ARRAY_V1205);
    private static final MethodWrapper<Byte[]> NBT_AS_BYTE_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "d", NBTType.BYTE_ARRAY.getNBTClass(), NBT_AS_BYTE_ARRAY_V1192);
    private static final MethodWrapper<Byte[]> NBT_AS_BYTE_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getBytes", NBTType.BYTE_ARRAY.getNBTClass(), NBT_AS_BYTE_ARRAY_V1181);

    private static final MethodWrapper<Integer[]> NBT_AS_INTEGER_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsIntArray", NBTType.INTEGER_ARRAY.getNBTClass());
    private static final MethodWrapper<Integer[]> NBT_AS_INTEGER_ARRAY_V1192 = new MethodWrapper<>(Version.V1_19_R2, Version.V1_20_R3, "g", NBTType.INTEGER_ARRAY.getNBTClass(), NBT_AS_INTEGER_ARRAY_V1205);
    private static final MethodWrapper<Integer[]> NBT_AS_INTEGER_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "f", NBTType.INTEGER_ARRAY.getNBTClass(), NBT_AS_INTEGER_ARRAY_V1192);
    private static final MethodWrapper<Integer[]> NBT_AS_INTEGER_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getInts", NBTType.INTEGER_ARRAY.getNBTClass(), NBT_AS_INTEGER_ARRAY_V1181);

    private static final MethodWrapper<Long[]> NBT_AS_LONG_ARRAY_V1205 = new MethodWrapper<>(Version.V1_20_5, null, "getAsLongArray", NBTType.LONG_ARRAY.getNBTClass());
    private static final MethodWrapper<Long[]> NBT_AS_LONG_ARRAY_V1192 = new MethodWrapper<>(Version.V1_19_R2, Version.V1_20_R3, "g", NBTType.LONG_ARRAY.getNBTClass(), NBT_AS_LONG_ARRAY_V1205);
    private static final MethodWrapper<Long[]> NBT_AS_LONG_ARRAY_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_19_R1, "f", NBTType.LONG_ARRAY.getNBTClass(), NBT_AS_LONG_ARRAY_V1192);
    private static final MethodWrapper<Long[]> NBT_AS_LONG_ARRAY = new MethodWrapper<>(null, Version.V1_17_R1, "getLongs", NBTType.LONG_ARRAY.getNBTClass(), NBT_AS_LONG_ARRAY_V1181);

    protected final Object nbtObject;
    protected final NBTType type;

    protected NBTGenericWrapper(NBTType nbtType) {
        this.nbtObject = nbtType.createInstance();
        this.type = nbtType;
    }

    /**
     * @throws java.lang.IllegalArgumentException When the nbtObject is not an Instance of NBTBase (Tag since 1.20.5)
     * @param nbtObject The NBT Object to be wrapped
     */
    public NBTGenericWrapper(Object nbtObject) {
        if(!ClassWrappers.NBT_BASE.resolve().isInstance(nbtObject)) {
            throw new IllegalArgumentException("Generic Type has to be instance of " + ClassWrappers.NBT_BASE.getFinalClassName() + " got: " + nbtObject);
        }
        this.nbtObject = nbtObject;
        this.type = NBTType.getNBTTypeFromInstance(nbtObject);
    }

    @Override
    public boolean isCompound() {
        return this.type == NBTType.COMPOUND;
    }

    @Override
    public boolean isList() {
        return this.type == NBTType.LIST;
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    /**
     * Static Caller for Instance Creation
     * @param nbtObject The NBT Object to be wrapped
     * @return A new {@link de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTGenericWrapper} Instance of nbtObject
     */
    public static NBTGenericWrapper of(Object nbtObject) {
        return new NBTGenericWrapper(nbtObject);
    }

    @Override
    public Object getNBTObject() {
        return this.nbtObject;
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

    // TODO Throw an Error instead of returning a Value?
    public byte asByte() {
        if(type.isNumber()) {
            return this.asNumber().byteValue();
        }
        return 0;
    }

    public short asShort() {
        if(type.isNumber()) {
            return this.asNumber().shortValue();
        }
        return 0;
    }

    public int asInt() {
        if(type.isNumber()) {
            return this.asNumber().intValue();
        }
        return 0;
    }

    public long asLong() {
        if(type.isNumber()) {
            return this.asNumber().longValue();
        }
        return 0;
    }

    public float asFloat() {
        if(type.isNumber()) {
            return this.asNumber().floatValue();
        }
        return 0;
    }

    public double asDouble() {
        if(type.isNumber()) {
            return this.asNumber().doubleValue();
        }
        return 0;
    }

    public Number asNumber() {
        if(type.isNumber()) {
            return ReflectionUtils.callWrapperAndGetOrDefault(this.nbtObject, NBT_AS_NUMBER, 0);
        }
        return 0;
    }

    public String asString() {
        return ReflectionUtils.callWrapperAndGetOrNull(this.nbtObject, NBT_AS_STRING);
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