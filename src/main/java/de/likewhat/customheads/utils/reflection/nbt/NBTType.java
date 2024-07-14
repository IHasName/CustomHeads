package de.likewhat.customheads.utils.reflection.nbt;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.logging.Level;

public enum NBTType {
    BASE(-1, "NBTBase", "Tag", false),
    END(0, "NBTTagEnd", "EndTag", false),
    BYTE(1, "NBTTagByte", "ByteTag", true),
    SHORT(2, "NBTTagShort", "ShortTag", true),
    INT(3, "NBTTagInt", "IntTag", true),
    LONG(4, "NBTTagLong", "LongTag", true),
    FLOAT(5, "NBTTagFloat", "FloatTag", true),
    DOUBLE(6, "NBTTagDouble", "DoubleTag", true),
    BYTE_ARRAY(7, "NBTTagByteArray", "ByteArrayTag", true),
    STRING(8, "NBTTagString", "StringTag", true),
    LIST(9, "NBTTagList", "ListTag", true),
    COMPOUND(10, "NBTTagCompound", "CompoundTag", true),
    INTEGER_ARRAY(11, "NBTTagIntArray", "IntArrayTag", true),
    LONG_ARRAY(12, "NBTTagLongArray", "LongArrayTag", true),
    ANY_NUMERIC(99, "NBTNumber", "NumericTag", false),

    INVALID(-1),
    NULL(-1);

    @Getter
    private final int id;
    private final String className;
    private final String altClassName;
    @Getter
    private final boolean instantiable;

    NBTType(int id) {
        this(id, null, null, false);
    }

    NBTType(int id, String className, String altClassName, boolean instantiable) {
        this.id = id;
        this.className = className;
        this.altClassName = altClassName;
        this.instantiable = instantiable;
    }

    /**
     * @return The NBT Class represented by this Enum
     */
    public Class<?> getNBTClass() {
        try {
            return ReflectionUtils.getMCServerClassByName(getClassName(), "nbt");
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to get NBT Class", e);
        }
        return null;
    }

    public static Object createInstance(NBTType nbtType) {
        if (!nbtType.isInstantiable()) {
            throw new IllegalStateException(nbtType.name() + " is not instantiable");
        } else {
            try {
                Class<?> clazz = nbtType.getNBTClass();
                if (clazz == null) {
                    return null;
                }
                return clazz.newInstance();
            } catch (Exception e) {
                CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to create Instance of " + nbtType.name() + " (" + nbtType.getClassName() + ")", e);
            }
        }
        return null;
    }

    // Don't ask what I did here. It works for what I need it to do and it didn't break anything... yet
    public static <T> Object createInstance(NBTType nbtType, T newInstanceObject) {
        try {
            if (!nbtType.isInstantiable()) {
                throw new IllegalStateException("Cannot create Instance of Type " + nbtType.name());
            } else {
                Class<?> clazz = nbtType.getNBTClass();
                if (clazz == null) {
                    return null;
                }
                Object instance = null;
                // Just return a new empty Instance if the Object is null
                if (newInstanceObject == null) {
                    Constructor<?> baseConstructor = clazz.getConstructor();
                    boolean wasAccessible = baseConstructor.isAccessible();
                    baseConstructor.setAccessible(true);
                    try {
                        return baseConstructor.newInstance();
                    } finally {
                        baseConstructor.setAccessible(wasAccessible);
                    }
                }
                try {
                    Class<?> instanceClass = newInstanceObject.getClass();
                    Constructor<?> constructor;
                    if (newInstanceObject instanceof Number) {
                        Class<?> numberType = (Class<?>) ReflectionUtils.getFieldValue("TYPE", newInstanceObject);
                        constructor = clazz.getDeclaredConstructor(numberType);
                    } else {
                        constructor = clazz.getDeclaredConstructor(instanceClass);
                    }
                    boolean wasAccessible = constructor.isAccessible();
                    constructor.setAccessible(true);
                    try {
                        instance = constructor.newInstance(newInstanceObject);
                    } finally {
                        constructor.setAccessible(wasAccessible);
                    }
                } catch (NoSuchMethodException e) {
                    try {
                        // Newer Versions use a Method to initialize an Instance
                        instance = clazz.getMethod("a", newInstanceObject.getClass()).invoke(null, newInstanceObject);
                    } catch (NoSuchMethodException e2) {
                        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to initialize Instance of " + clazz.getCanonicalName(), e);
                    }
                }
                return instance;
            }
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to create Instance of " + nbtType.name() + " (" + nbtType.getClassName() + ")", e);
        }
        return null;
    }

    public boolean isInstance(Object object) {
        return this.getNBTClass().isInstance(object);
    }

    public boolean isNumber() {
        switch(this) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Creates an Empty Instance of this Enum as NBT Object
     * @return The created NBT Object
     */
    public Object createInstance() {
        return createInstance(this);
    }

    public <T> Object createInstance(T newInstanceObject) {
        return createInstance(this, newInstanceObject);
    }

    /**
     * Gets the NBT Class Name (Note that on newer Versions these might change)
     * @return The Class Name of the NBT Class
     */
    public String getClassName() {
        if(this.className == null || this.altClassName == null) {
            throw new IllegalStateException(this.name() + " doesn't have an assigned Class");
        }
        return Version.getCurrentVersion().isNewerThan(Version.V1_20_R3) ? this.altClassName : this.className;
    }

    public static NBTType getById(int id) {
        return Arrays.stream(values()).filter(nbtType -> nbtType.id == id).findFirst().orElse(null);
    }

    public static NBTType getByClassName(String className) {
        return Arrays.stream(values()).filter(nbtType -> nbtType.className != null && nbtType.getClassName().equals(className)).findFirst().orElse(INVALID);
    }

    public static NBTType getByClass(Class<?> clazz) {
        return Arrays.stream(values()).filter(nbtType -> nbtType.className != null && nbtType.getNBTClass().equals(clazz)).findFirst().orElse(INVALID);
    }

    public static NBTType getNBTTypeFromInstance(Object instance) {
        return getByClass(instance.getClass());
    }
}
