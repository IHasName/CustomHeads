package de.likewhat.customheads.utils.reflection.helpers;

/*
 *  Project: CustomHeads in NBTTagCreator
 *     by LikeWhat
 *
 *  created on 17.12.2019 at 23:29
 */

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.collections.ReflectionMethodCollection;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;

public class NBTTagUtils {

    public static void addObjectToNBTList(Object list, Object objectToAdd) {
        try {
            if(Version.getCurrentVersion().isOlderThan(ReflectionMethodCollection.NBT_TAGLIST_ADD_V1141.getFrom())) {
                ReflectionMethodCollection.NBT_TAGLIST_ADD.invokeOn(list, objectToAdd);
            } else {
                ReflectionMethodCollection.NBT_TAGLIST_ADD.invokeOn(list, ReflectionMethodCollection.NBT_TAGLIST_SIZE.invokeOn(list), objectToAdd);
            }
        } catch(IllegalAccessException | InvocationTargetException e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to add Object to NBT List", e);
        }
    }

    public static Object createInstance(NBTType nbtType) {
        try {
            Class<?> clazz = nbtType.getNBTClass();
            if(clazz == null) {
                return null;
            }
            return clazz.newInstance();
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to create Instance of " + nbtType.name() + " ("+ nbtType.getClassName() + ")", e);
        }
        return null;
    }

    // Don't ask what I did here. It works for what I need it to do and it didn't break anything... yet
    public static <T> Object createInstance(NBTType nbtType, T newInstanceObject) {
        try {
            Class<?> clazz = nbtType.getNBTClass();
            if(clazz == null) {
                return null;
            }
            Object instance = null;
            // Just return a new empty Instance if the Object is null
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
                    CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to initialize Instance of " + clazz.getCanonicalName());
                }
            }
            return instance;
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to create Instance of " + nbtType.name() + " ("+ nbtType.getClassName() + ")", e);
        }
        return null;
    }

    @Getter
    public enum NBTType {
        END(0, "NBTTagEnd"),
        BYTE(1, "NBTTagByte"),
        SHORT(2, "NBTTagShort"),
        INT(3, "NBTTagInt"),
        LONG(4, "NBTTagLong"),
        FLOAT(5, "NBTTagFloat"),
        DOUBLE(6, "NBTTagDouble"),
        BYTE_LIST(7, "NBTTagByteArray"),
        STRING(8, "NBTTagString"),
        LIST(9, "NBTTagList"),
        COMPOUND(10, "NBTTagCompound"),
        INTEGER_LIST(11, "NBTTagIntArray");

        private final int id;
        private final String className;

        NBTType(int id, String className) {
            this.id = id;
            this.className = className;
        }

        public Class<?> getNBTClass() {
            try {
                return ReflectionUtils.getMCServerClassByName(this.className, "nbt");
            } catch(Exception e) {
                CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get NBT Class", e);
            }
            return null;
        }

        public static NBTType getById(int id) {
            return Arrays.stream(values()).filter(nbtType -> nbtType.id == id).findFirst().orElse(null);
        }

        public static NBTType getByClassName(String className) {
            return Arrays.stream(values()).filter(nbtType -> nbtType.className.equals(className)).findFirst().orElse(null);
        }
    }

}
