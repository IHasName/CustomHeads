package de.likewhat.customheads.utils.reflection;

import de.likewhat.customheads.CustomHeads;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Level;

public class ReflectionUtils {

    public static final int MC_VERSION;
    private static HashMap<String, Class<?>> cachedClasses = new HashMap<>();

    static {
        MC_VERSION = Integer.parseInt(CustomHeads.version.split("_")[1]);
    }

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

    public static Class<?> getMCServerClassByName(String className, String... alternativePrefix) {
        if (className.equals("ChatSerializer") && !CustomHeads.version.equals("v1_8_R1"))
            className = "IChatBaseComponent$ChatSerializer";
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
