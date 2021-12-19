package de.likewhat.customheads.utils.reflection.helpers;

import de.likewhat.customheads.CustomHeads;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

public class ReflectionUtils {

    public static final int MC_VERSION;
    private static final HashMap<String, Class<?>> cachedClasses = new HashMap<>();

    static {
        MC_VERSION = Integer.parseInt(CustomHeads.version.split("_")[1]);
    }

    /**
     * Sets a given Field to <b>newValue</b>
     * @param objectInstance The Object Instance
     * @param fieldName The Field Name to modify
     * @param newValue The changed Value
     * @return true when the Field was modified successfully, false if not
     */
    public static boolean setField(Object objectInstance, String fieldName, Object newValue) {
        boolean wasAccessible = true;
        Field fieldToModify = null;
        try {
            Class<?> sourceClass = objectInstance.getClass();
            try {
                fieldToModify = sourceClass.getField(fieldName);
            } catch(NoSuchFieldException e) {
                fieldToModify = sourceClass.getDeclaredField(fieldName);
            }
            wasAccessible = fieldToModify.isAccessible();
            if(!wasAccessible) {
                fieldToModify.setAccessible(true);
            }
            fieldToModify.set(objectInstance, newValue);
            return true;
        } catch(Exception e) {
            return false;
        } finally {
            if(fieldToModify != null && !wasAccessible) {
                fieldToModify.setAccessible(false);
            }
        }
    }

    /**
     * Tries to get a Field by its Name (tries Normal Fields first and then Declared Fields)
     * @param clazz The Class that has the Field
     * @param fieldName The Field Name to get
     * @return Field from the given Class
     */
    public static Field getFieldDynamic(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field result;
        try {
            result = clazz.getField(fieldName);
        } catch(NoSuchFieldException noField) {
            try {
                result = clazz.getDeclaredField(fieldName);
            } catch(NoSuchFieldException noDeclaredField) {
                throw new NoSuchFieldException("Failed to find Field in " + clazz.getCanonicalName() + " named " + fieldName + " in either declared or non-declared State");
            }
        }
        return result;
    }

    /**
     * Check whether the given Method exists or not
     * @param clazz The Class where the Method could be
     * @param methodName The Method Name
     * @param params Optional Method Parameters
     * @return true when the Method exists, false if not
     */
    public static boolean methodExists(Class<?> clazz, String methodName, Class<?>... params) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(methodName);
        try {
            clazz.getMethod(methodName, params);
            return true;
        } catch(NoSuchMethodException e) {
            // ignored since it will still return false
        }
        return false;
    }

    /**
     * @param methodName The Method Name
     * @param clazz The Class where the Method is located
     * @param params The Method Parameters may be null
     * @return The Method from the given Name and Parameters
     */
    public static Method getMethod(String methodName, Class<?> clazz, Class<?>... params) throws NoSuchMethodException {
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(clazz);
        try {
            return clazz.getMethod(methodName, params);
        } catch(NoSuchMethodException e) {
            throw new NoSuchMethodException("Failed to get Method from Class " + clazz.getCanonicalName() + ": " + e.getMessage());
        }
    }

    /**
     * @param method The Method to invoke
     * @param object The Object on which the Method is to be invoked on
     * @param params Optional Method Parameters
     * @return The Methods return Value (may be null on <b>void</b> Methods)
     */
    public static Object invokeMethod(Method method, Object object, Object... params) throws InvocationTargetException, IllegalAccessException {
        Objects.requireNonNull(method);
        Objects.requireNonNull(object);
        return method.invoke(object, params);
    }

    /**
     * Returns an Enum Constant by its Name
     * @param clazz The Class where the Enum is located
     * @param enumName The Enmum Name
     * @return Enum Constant by given Name
     */
    public static Enum<?> getEnumConstant(Class<?> clazz, String enumName) {
        enumName = enumName.toUpperCase();
        try {
            for (Object eenum : clazz.getEnumConstants()) {
                if (eenum.getClass().getMethod("name").invoke(eenum).equals(enumName)) {
                    return (Enum<?>) eenum;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Object packet, Player player) throws Exception {
        Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
        Object connection;
        if(MC_VERSION >= 17) {
            connection = playerHandle.getClass().getField("b").get(playerHandle);
        } else {
            connection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
        }
        connection.getClass().getMethod("sendPacket", ReflectionUtils.getMCServerClassByName("Packet", "network.protocol")).invoke(connection, packet);
    }

    public static Class<?> getMCServerClassByName(String className, boolean useVersionTag) {
        if (className.equals("ChatSerializer") && Version.getCurrentVersion() == Version.V1_8_R1) {
            className = "IChatBaseComponent$ChatSerializer";
        }
        String classPath;
        if(useVersionTag) {
            classPath = "net.minecraft.server." + CustomHeads.version + "." + className;
        } else {
            classPath = "net.minecraft." + className;
        }
        return checkCached(classPath);
    }

    public static Class<?> getMCServerClassByName(String className, String... alternativePrefix) {
        if (className.equals("ChatSerializer") && Version.getCurrentVersion() == Version.V1_8_R1) {
            className = "IChatBaseComponent$ChatSerializer";
        }
        String classPath;
        if(MC_VERSION >= 17) {
            String altPrefix = "";
            if(alternativePrefix != null && alternativePrefix.length > 0) {
                altPrefix = alternativePrefix[0] + ".";
            }
            classPath = "net.minecraft." + altPrefix + className;
        } else {
            classPath = "net.minecraft.server." + CustomHeads.version + "." + className;
        }
        return checkCached(classPath);
    }

    public static Class<?> getClassByName(String className) {
        return checkCached(className);
    }

    public static Class<?> getCBClass(String className) {
        return checkCached("org.bukkit.craftbukkit." + CustomHeads.version + "." + className);
    }

    private static Class<?> checkCached(String className) {
        if(cachedClasses.containsKey(className)) {
            return cachedClasses.get(className);
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                cachedClasses.put(className, clazz);
                return clazz;
            } catch(Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to cache Class", e);
            }
        }
        return null;
    }

}
