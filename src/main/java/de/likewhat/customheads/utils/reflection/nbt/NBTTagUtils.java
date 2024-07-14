package de.likewhat.customheads.utils.reflection.nbt;

/*
 *  Project: CustomHeads in NBTTagCreator
 *     by LikeWhat
 *
 *  created on 17.12.2019 at 23:29
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTTagCompoundWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTTagListWrapper;
import de.likewhat.customheads.utils.reflection.nbt.errors.NBTException;

import java.util.Map;

public class NBTTagUtils {

    // Imperfect Json to NBT Converter
    public static Object jsonToNBT(JsonObject jsonObject) throws NBTException {
        return serializeJsonToNBT(jsonObject);
    }

    private static Object serializeJsonToNBT(JsonElement element) throws NBTException {
        if(element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            NBTTagCompoundWrapper nbtCompound = new NBTTagCompoundWrapper();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                Object serialized = checkSerialized(serializeJsonToNBT(entry.getValue()));
                if(serialized == NBTType.INVALID) {
                    continue;
                }
                nbtCompound.set(entry.getKey(), serialized);
            }
            return nbtCompound.getNBTObject();
        } else if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            NBTTagListWrapper nbtList = new NBTTagListWrapper();
            for (JsonElement jsonElement : jsonArray) {
                Object serialized = checkSerialized(serializeJsonToNBT(jsonElement));
                if(serialized == NBTType.INVALID) {
                    continue;
                }
                nbtList.addObject(serialized);
            }
            return nbtList.getNBTObject();
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if(jsonPrimitive.isString()) {
                return NBTType.STRING.createInstance(jsonPrimitive.getAsString());
            } else if (jsonPrimitive.isBoolean()) {
                return NBTType.BYTE.createInstance((byte) (jsonPrimitive.getAsBoolean() ?  1 : 0));
            } else if (jsonPrimitive.isNumber()) {
                Number number = jsonPrimitive.getAsNumber();
                if(number instanceof Integer) {
                    return NBTType.INT.createInstance(number.intValue());
                } else if (number instanceof Long) {
                    return NBTType.LONG.createInstance(number.longValue());
                } else if (number instanceof Float) {
                    return NBTType.FLOAT.createInstance(number.floatValue());
                } else if (number instanceof Double) {
                    return NBTType.DOUBLE.createInstance(number.doubleValue());
                } else if (number instanceof Byte) {
                    return NBTType.BYTE.createInstance(number.byteValue());
                } else if (number instanceof Short) {
                    return NBTType.SHORT.createInstance(number.shortValue());
                }
            }
        } else if (element.isJsonNull()) {
            return NBTType.NULL;
        }
        return NBTType.INVALID;
    }

    private static Object checkSerialized(Object serializedObject) throws NBTException {
        if (serializedObject == NBTType.NULL) {
            return null; // Implicit null return
        } else if (serializedObject == null) {
            throw new NBTException("Failed to encode Json Element");
        }
        return serializedObject;
    }

    /**
     *
     * @param nbt The NBT Object to deserialize
     * @param assumeByteBoolean Assume that every Key Value byte between 0 and 1 (excluding ByteList) is a Boolean
     * @return a Json Element representing the deserialized NBT
     */
    public static JsonElement nbtToJsonObject(Object nbt, boolean assumeByteBoolean) {
        return deserializeNBTToJson(nbt, assumeByteBoolean);
    }

    private static JsonElement deserializeNBTToJson(Object nbt, boolean assumeByteBoolean) {
        // TODO Implement this
        return null;
    }

}
