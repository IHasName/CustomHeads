package de.likewhat.customheads.utils.reflection.helpers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.FieldWrappers;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.MethodWrappers;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ReflectionUtils {

    private static final Map<String, Class<?>> CACHED_CLASSES = new ConcurrentHashMap<>();

    /**
     * Sets the given Field Name to <b>newValue</b>
     * @param objectInstance The Object Instance
     * @param fieldName The Field Name to modify
     * @param newValue The changed Value
     * @return whether the Action was successful or not
     */
    public static boolean setField(Object objectInstance, String fieldName, Object newValue) throws NoSuchFieldException {
        return setField(objectInstance, getField(objectInstance.getClass(), fieldName), newValue);
    }

    /**
     * Sets a known Field to <b>newValue</b>
     * @param instance The Object Instance
     * @param field The Field to modify
     * @param newValue The Value to set
     * @return whether the Action was successful or not
     */
    public static boolean setField(Object instance, Field field, Object newValue) {
        boolean wasAccessible = field.isAccessible();
        try {
            if(!wasAccessible) {
                field.setAccessible(true);
            }
            field.set(instance, newValue);
            return true;
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to set Field", e);
            return false;
        } finally {
            if(!wasAccessible) {
                field.setAccessible(false);
            }
        }
    }

    /**
     * Tries to get a Field by its Name
     * @param clazz The Class that has the Field
     * @param fieldName The Field Name to get
     * @return Field from the given Class
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field result = null;
        try {
            result = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if(superClass != null) {
                result = getField(superClass, fieldName);
            }
        }
        if(result == null) {
            throw new NoSuchFieldException("Couldn't find any Field for " + fieldName);
        }
        return result;
    }

    /**
     * Gets the Field Value by the given Field Name and Object Instance
     * @param fieldName The Field Name
     * @param objectInstance The Object Instance
     * @return The Field Value from the given Object
     */
    public static Object getFieldValue(String fieldName, Object objectInstance) {
        try {
            return getFieldValue(getField(objectInstance.getClass(), fieldName), objectInstance);
        } catch(NoSuchFieldException e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to get Field Value", e);
        }
        return null;
    }

    /**
     * Gets the Field Value by the given Field and Object Instance
     * @param field The Field
     * @param objectInstance The Object Instance
     * @return The Field Value from the given Object
     */
    public static Object getFieldValue(Field field, Object objectInstance) {
        boolean accessible = field.isAccessible();
        try {
            if(!accessible) {
                field.setAccessible(true);
            }
            return field.get(objectInstance);
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to get Field Value", e);
            return null;
        } finally {
            if(!accessible) {
                field.setAccessible(false);
            }
        }
    }

    private static final Map<String, Field> FIELD_BY_VALUE_CACHE = new ConcurrentHashMap<>();

    // Yeah, this seems like a bad Idea...
    // But since the Fields always get changed in each Update it's kinda my last resort... sort of?
    // This only gets used to help update the Plugin for newer Versions
    /**
     * <p>Gets a Field by the Field Type Class</p>
     * <b>WARNING!</b> This Method should only be used when you're 100% sure the Field Type Class only appears once in
     * the Field Class
     * @param fieldClass The Class where the Field is located
     * @param typeClass The Field Type
     * @return The Field
     *
     * @throws java.lang.NoSuchFieldException When Field couldn't be found
     * @throws java.lang.IllegalStateException When the Field Type exists more than once
     */
    public static Field getFieldByValueType(Class<?> fieldClass, Class<?> typeClass) throws NoSuchFieldException {
        String cacheName = fieldClass.getCanonicalName() + ":" + typeClass.getCanonicalName();
        if(FIELD_BY_VALUE_CACHE.containsKey(cacheName)) {
            return FIELD_BY_VALUE_CACHE.get(cacheName);
        }

        Field resultField = null;
        for(Field field : fieldClass.getDeclaredFields()) {
            if(field.getType().equals(typeClass)) {
                // Throw an Error if the Field is already set since we can't be sure we got the right one
                if(resultField != null) {
                    throw new IllegalStateException("Refusing to return any Field for Class " + fieldClass.getCanonicalName() + " and Field Type " + typeClass.getCanonicalName() + " since there is more than one Result");
                }
                resultField = field;
            }
        }

        if(resultField == null) {
            throw new NoSuchFieldException("Couldn't find any Fields in " + fieldClass.getCanonicalName() + " with Field Type " + typeClass.getCanonicalName());
        }

        FIELD_BY_VALUE_CACHE.put(cacheName, resultField);
        return resultField;
    }

    /**
     * Tries to get a Constructor by its Name
     * @param clazz The Class that has the Constructor
     * @param paramTypes The Constructor Parameters
     * @return Constructor from the given Class and Parameter Types
     *
     * @throws java.lang.NoSuchMethodException When the Constructor couldn't be found
     */
    public static Constructor<?> getConstructorDynamic(Class<?> clazz, Class<?>... paramTypes) throws NoSuchMethodException {
        Constructor<?> result;
        try {
            result = clazz.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException noConstructor) {
            throw new NoSuchMethodException("Failed to find Constructor for " + LoggingUtils.methodLikeString(clazz.getCanonicalName(), paramTypes) + " in either declared or non-declared State");
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
            getMethod(methodName, clazz, params);
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
        return clazz.getMethod(methodName, params);
    }

    public static Method getDeclaredMethod(String methodName, Class<?> clazz, Class<?>... params) throws NoSuchMethodException {
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(clazz);
        return clazz.getDeclaredMethod(methodName, params);
    }

    /**
     * @param method The Method to invoke
     * @param object The Object on which the Method is to be invoked on
     * @param params Optional Method Parameters
     * @return The Methods return Value (may be null on <b>void</b> Methods)
     */
    public static Object invokeMethod(Method method, Object object, Object... params) throws InvocationTargetException, IllegalAccessException {
        Objects.requireNonNull(method);
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
     * @param enumName The Enum Name
     * @return Enum Constant by given Name
     */
    public static Enum<?> getEnumConstant(Class<?> clazz, String enumName) {
        try {
            for (Object eenum : clazz.getEnumConstants()) {
                if (clazz.getMethod("name").invoke(eenum).equals(enumName)) {
                    return (Enum<?>) eenum;
                }
            }
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to get Enum Instance of " + enumName, e);
        }
        return null;
    }

    public static void sendPacket(Object packet, Player player) throws Exception {
        Object playerHandle = ReflectionUtils.getPlayerHandle(player);
        Object connection = FieldWrappers.PLAYER_CONNECTION.getInstance(playerHandle);
        MethodWrappers.PLAYER_SEND_PACKET.invokeOn(connection, packet);
    }

    public static Class<?> getMCServerClassByName(String className) {
        return getMCServerClassByName(className, null);
    }

    public static Class<?> getMCServerClassByName(String className, String alternativePrefix) {
        if (className.equals("ChatSerializer") && (Version.getCurrentVersion() != Version.V1_8_R1 || Version.getCurrentVersion().isNewerThan(Version.V1_16_R3))) {
            className = "IChatBaseComponent$ChatSerializer";
        }
        String classPath;
        if(Version.getCurrentVersion().isNewerThan(Version.V1_16_R3)) {
            String altPrefix = "";
            if(alternativePrefix != null) {
                altPrefix = alternativePrefix + ".";
            }
            classPath = "net.minecraft." + altPrefix + className;
        } else {
            classPath = "net.minecraft.server." + Version.getCurrentVersionRaw() + "." + className;
        }
        return getClassByName(classPath);
    }

    public static Class<?> getClassByName(String className) {
        if(CACHED_CLASSES.containsKey(className)) {
            return CACHED_CLASSES.get(className);
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                CACHED_CLASSES.put(className, clazz);
                return clazz;
            } catch(Exception e) {
                CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to get Class for Cache", e);
            }
        }
        return null;
    }

    // Since 1.20.5 Craft-Bukkit Packages also dropped the Revisions so we just use the Package Name
    public static Class<?> getCraftBukkitClass(String className) {
        return getClassByName("org.bukkit.craftbukkit." + (Version.getCurrentVersion().isOlderThan(Version.V1_20_5) ? (Version.getCurrentVersionRaw() + ".") : "") + className);
    }

    public static Object getPlayerHandle(Player player) {
        try {
            return MethodWrappers.CRAFTBUKKIT_CRAFTPLAYER_GET_HANDLE.invokeOn(player);
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to get Player Handle", e);
        }
        return null;
    }

    public static Object getWorldHandle(World world) {
        try {
            return MethodWrappers.CRAFTBUKKIT_CRAFTWORLD_GET_HANDLE.invokeOn(world);
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to get World Handle", e);
        }
        return null;
    }

    // Generic Types don't like to be called with null Parameters
    public static <T> T callWrapperAndGetOrNull(Object instance, MethodWrapper<T> wrapper, Object... params) {
        try {
            return wrapper.invokeOn(instance, params);
        } catch(InvocationTargetException | IllegalAccessException e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to call Wrapper for " + wrapper, e);
        }
        return null;
    }

    public static <T> T callWrapperAndGetOrDefault(Object instance, MethodWrapper<T> wrapper, T defaultValue, Object... params) {
        try {
            return wrapper.invokeOn(instance, params);
        } catch(InvocationTargetException | IllegalAccessException e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to call Wrapper for " + wrapper, e);
        }
        return defaultValue;
    }


}
