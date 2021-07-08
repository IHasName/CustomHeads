package de.likewhat.customheads.utils.reflection;

/*
 *  Project: CustomHeads in NBTTagCreator
 *     by LikeWhat
 *
 *  created on 17.12.2019 at 23:29
 */

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.Utils;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;

public class NBTTagUtils {

    public static final int MC_VERSION;
    public static final String[] ALLOWED_CLASSES = new String[] {"NBTBase", "NBTTagEnd", "NBTTagByte", "NBTTagShort", "NBTTagInt", "NBTTagLong", "NBTTagFloat", "NBTTagDouble", "NBTTagByteArray", "NBTTagString", "NBTTagList", "NBTTagCompound", "NBTTagIntArray"};

    static {
        MC_VERSION = Integer.parseInt(CustomHeads.version.split("_")[1]);
    }

    public static void addObjectToNBTList(Object list, Object objectToAdd) {
        try {
            switch (MC_VERSION) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    list.getClass().getMethod("add", getNBTClass("NBTBase")).invoke(list, objectToAdd);
                    break;
                default:
                    Bukkit.getLogger().log(Level.WARNING, "Falling back to newest Method since the current Version isn't tested yet... (This may not work so here goes)");
                case 14:
                case 15:
                case 16:
                case 17:
                    list.getClass().getMethod("add", int.class, getNBTClass("NBTBase")).invoke(list, list.getClass().getMethod("size").invoke(list), objectToAdd);
                    break;
            }
        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNBTClass(String className) {
        try {
            if(!Arrays.asList(ALLOWED_CLASSES).contains(className)) {
                throw new IllegalArgumentException("Class " + className + " is not allowed!");
            }
            Class<?> clazz;
            switch (MC_VERSION) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                    clazz = Utils.getMCServerClassByName(className);
                    break;
                default:
                    Bukkit.getLogger().log(Level.WARNING, "Falling back to newest Method since the current Version isn't tested yet... (This may not work so here goes)");
                case 17:

                    clazz = Utils.getClassByName("net.minecraft.nbt." + className);
                    break;
            }
            return clazz;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object createInstance(String className) {
        try {
            Class<?> clazz = getNBTClass(className);
            if(clazz == null) {
                return null;
            }
            return clazz.newInstance();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Object createInstance(String className, T newInstanceObject) {
        try {
            Class<?> clazz = getNBTClass(className);
            if(clazz == null) {
                return null;
            }
            Object instance = null;
            // Just return an new empty Instance if the Object is null
            if(newInstanceObject == null) {
                return clazz.newInstance();
            }
            try {
                Constructor<?> constructor = clazz.getConstructor(newInstanceObject.getClass());
                instance = constructor.newInstance(newInstanceObject);
            } catch(NoSuchMethodException e) {
                try {
                    // Newer Versions use the a Method to initialize an Instance
                    instance = clazz.getMethod("a", newInstanceObject.getClass()).invoke(null, newInstanceObject);
                } catch(NoSuchMethodException e2) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to initialize Instance of " + className);
                }
            }
            return instance;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
