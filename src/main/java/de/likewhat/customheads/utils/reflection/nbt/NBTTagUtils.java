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
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.MethodWrappers;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTBaseWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTGenericWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTTagCompoundWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTTagListWrapper;
import de.likewhat.customheads.utils.reflection.nbt.errors.NBTException;

import java.lang.reflect.InvocationTargetException;
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
        return deserializeNBTToJson(new NBTGenericWrapper(nbt), assumeByteBoolean);
    }

    private static JsonElement deserializeNBTToJson(NBTBaseWrapper nbt, boolean assumeByteBoolean) {
        if(nbt.isCompound()) {
            NBTTagCompoundWrapper compound = nbt.asCompound();
            JsonObject jsonObject = new JsonObject();
            compound.entrySet().forEach(entry -> {
                JsonElement element = deserializeNBTToJson(entry.getValue(), assumeByteBoolean);
                if(element == null) {
                    return;
                }
                jsonObject.add(entry.getKey(), element);
            });
            return jsonObject;
        } else if(nbt.isList()) {
            NBTTagListWrapper list = nbt.asList();
            JsonArray jsonArray = new JsonArray();
            list.forEach(item -> {
                JsonElement element = deserializeNBTToJson(item, assumeByteBoolean);
                if(element == null) {
                    return;
                }
                jsonArray.add(element);
            });
            return jsonArray;
        } else if(nbt.isGeneric()) {
            NBTGenericWrapper generic = nbt.asGeneric();
            JsonElement result;
            switch(generic.getType()) {
                case BYTE:
                    byte byts = generic.asByte();
                    if(assumeByteBoolean && byts == 0 || byts == 1) {
                        result = new JsonPrimitive(byts == 1);
                    } else {
                        result = new JsonPrimitive(byts);
                    }
                    break;
                case SHORT:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                    result = new JsonPrimitive(generic.asNumber());
                    break;
                case STRING:
                    result = new JsonPrimitive(generic.asString());
                    break;
                case BYTE_ARRAY:
                    byte[] bytes = generic.asByteArray();
                    // I'm going to assume that no one tries to save an Array of Booleans like this
                    result = new JsonArray();
                    for (byte b : bytes) {
                        ((JsonArray) result).add(new JsonPrimitive(b));
                    }
                    break;
                case INTEGER_ARRAY:
                    int[] ints = generic.asIntegerArray();
                    result = new JsonArray();
                    for (int i : ints) {
                        ((JsonArray) result).add(new JsonPrimitive(i));
                    }
                    break;
                case LONG_ARRAY:
                    long[] longs = generic.asLongArray();
                    result = new JsonArray();
                    for (long l : longs) {
                        ((JsonArray) result).add(new JsonPrimitive(l));
                    }
                    break;
                default:
                    CustomHeads.getPluginLogger().warning("Invalid Tag Type encountered while deserializing NBT: " + generic.getType());
                    result = null;
                    break;
            }
            return result;
        }
        return null;
    }

    public static Object nbtToCustomData(Object nbt) throws InvocationTargetException, IllegalAccessException {
        return MethodWrappers.COMPONENT_CUSTOM_DATA_OF.invokeOn(null, nbt);
    }

}
