package de.likewhat.customheads.utils.reflection.helpers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.collections.ReflectionMethodCollection;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

public class ReflectionUtils {

    public static final int MC_VERSION;
    private static final HashMap<String, Class<?>> CACHED_CLASSES = new HashMap<>();

    static {
        MC_VERSION = Integer.parseInt(Version.getRawVersion().split("_")[1]);
    }

    /**
     * Sets a given Field to <b>newValue</b>
     * @param objectInstance The Object Instance
     * @param fieldName The Field Name to modify
     * @param newValue The changed Value
     * @return true when the Field was modified successfully, false if not
     */
    public static boolean setField(Object objectInstance, String fieldName, Object newValue) {
        try {
            Class<?> sourceClass = objectInstance.getClass();
            Field fieldToModify;
            try {
                fieldToModify = sourceClass.getField(fieldName);
            } catch(NoSuchFieldException e) {
                fieldToModify = sourceClass.getDeclaredField(fieldName);
            }
            return setField(objectInstance, fieldToModify, newValue);
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setField(Object objectInstance, Field field, Object newValue) {
        boolean wasAccessible = field.isAccessible();
        try {
            if(!wasAccessible) {
                field.setAccessible(true);
            }
            field.set(objectInstance, newValue);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(!wasAccessible) {
                field.setAccessible(false);
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
     * Tries to get a Constructor by its Name (tries Normal Constructors first and then Declared Constructors)
     * @param clazz The Class that has the Constructor
     * @param paramTypes The Constructor Parameters
     * @return Constructor from the given Class and Parameter Types
     */
    public static Constructor<?> getConstructorDynamic(Class<?> clazz, Class<?>... paramTypes) throws NoSuchMethodException {
        Constructor<?> result;
        try {
            result = clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException noConstructor) {
            try {
                result = clazz.getDeclaredConstructor(paramTypes);
            } catch(NoSuchMethodException noDeclaredConstructor) {
                throw new NoSuchMethodException("Failed to find Constructor for " + LoggingUtils.methodLikeString(clazz.getCanonicalName(), paramTypes) + " in either declared or non-declared State");
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
            throw new NoSuchMethodException("Failed to get Method from Class " + clazz.getCanonicalName());
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
        boolean wasAccessible = method.isAccessible();
        if(!wasAccessible) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(object, params);
        } finally {
            method.setAccessible(wasAccessible);
        }
    }

    /**
     * Returns an Enum Constant by its Name
     * @param clazz The Class where the Enum is located
     * @param enumName The Enmum Name
     * @return Enum Constant by given Name
     */
    public static Enum<?> getEnumConstant(Class<?> clazz, String enumName) {
//        enumName = enumName.toUpperCase();
        try {
            for (Object eenum : clazz.getEnumConstants()) {
                if (clazz.getMethod("name").invoke(eenum).equals(enumName)) {
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
        ReflectionMethodCollection.PLAYER_SEND_PACKET.invokeOn(connection, packet);
//        connection.getClass().getMethod("sendPacket", ReflectionUtils.getMCServerClassByName("Packet", "network.protocol")).invoke(connection, packet);
    }

    public static Class<?> getMCServerClassByName(String className) {
        return getMCServerClassByName(className, null);
    }

    public static Class<?> getMCServerClassByName(String className, String alternativePrefix) {
        if (className.equals("ChatSerializer") && (Version.getCurrentVersion() == Version.V1_8_R1 || Version.getCurrentVersion().isNewerThan(Version.V1_17_R1))) {
            className = "IChatBaseComponent$ChatSerializer";
        }
        String classPath;
        if(MC_VERSION >= 17) {
            String altPrefix = "";
            if(alternativePrefix != null) {
                altPrefix = alternativePrefix + ".";
            }
            classPath = "net.minecraft." + altPrefix + className;
        } else {
            classPath = "net.minecraft.server." + Version.getRawVersion() + "." + className;
        }
        return checkCachedClassname(classPath);
    }

    public static Class<?> getClassByName(String className) {
        return checkCachedClassname(className);
    }

    public static Class<?> getCBClass(String className) {
        return checkCachedClassname("org.bukkit.craftbukkit." + Version.getRawVersion() + "." + className);
    }

    private static Class<?> checkCachedClassname(String className) {
        if(CACHED_CLASSES.containsKey(className)) {
            return CACHED_CLASSES.get(className);
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                CACHED_CLASSES.put(className, clazz);
                return clazz;
            } catch(Exception e) {
                CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to cache Class", e);
            }
        }
        return null;
    }

}
