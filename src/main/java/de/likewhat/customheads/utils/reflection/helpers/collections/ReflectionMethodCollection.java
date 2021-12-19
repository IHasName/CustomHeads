package de.likewhat.customheads.utils.reflection.helpers.collections;

import de.likewhat.customheads.utils.reflection.helpers.ItemNBTUtils;
import de.likewhat.customheads.utils.reflection.helpers.NBTTagUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;

public class ReflectionMethodCollection {

    // NBTTagCompound
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), String.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GET = new MethodWrapper<>(null, Version.V1_17_R1, "get", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), NBT_TAGCOMPOUND_GET_V1181, String.class);

    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETCOMPOUND_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "p", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), String.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETCOMPOUND = new MethodWrapper<>(null, Version.V1_17_R1, "getCompound", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), NBT_TAGCOMPOUND_GETCOMPOUND_V1181, String.class);

    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETLIST_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), String.class, int.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETLIST = new MethodWrapper<>(null, Version.V1_17_R1, "getList", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), NBT_TAGCOMPOUND_GETLIST_V1181, String.class, int.class);

    public static final MethodWrapper<Void> NBT_TAGCOMPOUND_SET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), String.class, NBTTagUtils.NBTBASE_CLASS);
    public static final MethodWrapper<Void> NBT_TAGCOMPOUND_SET = new MethodWrapper<>(null, Version.V1_17_R1, "set", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), NBT_TAGCOMPOUND_SET_V1181, String.class, NBTTagUtils.NBTBASE_CLASS);

    public static final MethodWrapper<Boolean> NBT_TAGCOMPOUND_HASKEY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "e", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), String.class);
    public static final MethodWrapper<Boolean> NBT_TAGCOMPOUND_HASKEY = new MethodWrapper<>(null, Version.V1_17_R1, "hasKey", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND), NBT_TAGCOMPOUND_HASKEY_V1181, String.class);

    // NBTTagList
    public static final MethodWrapper<Void> NBT_TAGLIST_ADD_V1181 = new MethodWrapper<>(Version.V1_8_R1, null, "b", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.LIST), int.class, NBTTagUtils.NBTBASE_CLASS);
    public static final MethodWrapper<Void> NBT_TAGLIST_ADD_V1141 = new MethodWrapper<>(Version.V1_14_R1, Version.V1_17_R1, "add", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.LIST), NBT_TAGLIST_ADD_V1181, int.class, NBTTagUtils.NBTBASE_CLASS);
    public static final MethodWrapper<Void> NBT_TAGLIST_ADD = new MethodWrapper<>(null, Version.V1_13_R1, "add", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.LIST), NBT_TAGLIST_ADD_V1141, NBTTagUtils.NBTBASE_CLASS);

    public static final MethodWrapper<Integer> NBT_TAGLIST_SIZE = new MethodWrapper<>(null, null, "size", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.LIST));

    public static final MethodWrapper<String> NBT_TAGLIST_GETSTRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "j", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.LIST), int.class);
    public static final MethodWrapper<String> NBT_TAGLIST_GETSTRING = new MethodWrapper<>(null, Version.V1_17_R1, "getString", NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.LIST), NBT_TAGLIST_GETSTRING_V1181, int.class);

    // ItemStack
    public static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "r", ItemNBTUtils.MINECRAFT_ITEMSTACK_CLASS);
    public static final MethodWrapper<Boolean> ITEMSTACK_HASTAG = new MethodWrapper<>(null, Version.V1_17_R1, "hasTag", ItemNBTUtils.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_HASTAG_V1181);

    public static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "t", ItemNBTUtils.MINECRAFT_ITEMSTACK_CLASS);
    public static final MethodWrapper<Object> ITEMSTACK_GETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "getTag", ItemNBTUtils.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_GETTAG_V1181);

    public static final MethodWrapper<Void> ITEMSTACK_SETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", ItemNBTUtils.MINECRAFT_ITEMSTACK_CLASS, NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND));
    public static final MethodWrapper<Void> ITEMSTACK_SETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "setTag", ItemNBTUtils.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_SETTAG_V1181, NBTTagUtils.getNBTClass(NBTTagUtils.NBTType.COMPOUND));

}
