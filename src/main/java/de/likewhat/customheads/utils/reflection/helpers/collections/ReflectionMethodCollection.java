package de.likewhat.customheads.utils.reflection.helpers.collections;

import com.mojang.authlib.GameProfile;
import de.likewhat.customheads.utils.reflection.helpers.NBTTagUtils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;

public class ReflectionMethodCollection {

    // NBTTagCompound
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GET = new MethodWrapper<>(null, Version.V1_17_R1, "get", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_GET_V1181, String.class);

    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETCOMPOUND_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "p", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETCOMPOUND = new MethodWrapper<>(null, Version.V1_17_R1, "getCompound", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_GETCOMPOUND_V1181, String.class);

    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETLIST_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class, int.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETLIST = new MethodWrapper<>(null, Version.V1_17_R1, "getList", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_GETLIST_V1181, String.class, int.class);

    public static final MethodWrapper<Void> NBT_TAGCOMPOUND_SET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class, ReflectionClassCollection.NBTBASE_CLASS);
    public static final MethodWrapper<Void> NBT_TAGCOMPOUND_SET = new MethodWrapper<>(null, Version.V1_17_R1, "set", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_SET_V1181, String.class, ReflectionClassCollection.NBTBASE_CLASS);

    public static final MethodWrapper<Boolean> NBT_TAGCOMPOUND_HASKEY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "e", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<Boolean> NBT_TAGCOMPOUND_HASKEY = new MethodWrapper<>(null, Version.V1_17_R1, "hasKey", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_HASKEY_V1181, String.class);

    // NBTTagList
    public static final MethodWrapper<Void> NBT_TAGLIST_ADD_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "b", NBTTagUtils.NBTType.LIST.getNBTClass(), int.class, ReflectionClassCollection.NBTBASE_CLASS);
    public static final MethodWrapper<Void> NBT_TAGLIST_ADD_V1141 = new MethodWrapper<>(Version.V1_14_R1, Version.V1_17_R1, "add", NBTTagUtils.NBTType.LIST.getNBTClass(), NBT_TAGLIST_ADD_V1181, int.class, ReflectionClassCollection.NBTBASE_CLASS);
    public static final MethodWrapper<Void> NBT_TAGLIST_ADD = new MethodWrapper<>(null, Version.V1_13_R2, "add", NBTTagUtils.NBTType.LIST.getNBTClass(), NBT_TAGLIST_ADD_V1141, ReflectionClassCollection.NBTBASE_CLASS);

    public static final MethodWrapper<Integer> NBT_TAGLIST_SIZE = new MethodWrapper<>(null, null, "size", NBTTagUtils.NBTType.LIST.getNBTClass());

    public static final MethodWrapper<String> NBT_TAGLIST_GETSTRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "j", NBTTagUtils.NBTType.LIST.getNBTClass(), int.class);
    public static final MethodWrapper<String> NBT_TAGLIST_GETSTRING = new MethodWrapper<>(null, Version.V1_17_R1, "getString", NBTTagUtils.NBTType.LIST.getNBTClass(), NBT_TAGLIST_GETSTRING_V1181, int.class);

    // ItemStack
    public static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1182 = new MethodWrapper<>(Version.V1_18_R2, null, "s", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS);
    public static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_18_R1, "r", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_HASTAG_V1182);
    public static final MethodWrapper<Boolean> ITEMSTACK_HASTAG = new MethodWrapper<>(null, Version.V1_17_R1, "hasTag", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_HASTAG_V1181);

    public static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1182 = new MethodWrapper<>(Version.V1_18_R2, null, "t", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS);
    public static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_18_R1, "s", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_GETTAG_V1182);
    public static final MethodWrapper<Object> ITEMSTACK_GETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "getTag", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_GETTAG_V1181);

    public static final MethodWrapper<Void> ITEMSTACK_SETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS, NBTTagUtils.NBTType.COMPOUND.getNBTClass());
    public static final MethodWrapper<Void> ITEMSTACK_SETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "setTag", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS, ITEMSTACK_SETTAG_V1181, NBTTagUtils.NBTType.COMPOUND.getNBTClass());

    // GameProfile
    public static final MethodWrapper<Object> GAMEPROFILE_SERIALIZE = new MethodWrapper<>(Version.V1_18_R1, null, "a", ReflectionUtils.getMCServerClassByName("GameProfileSerializer", "nbt"), NBTTagUtils.NBTType.COMPOUND.getNBTClass(), GameProfile.class);

    // Player Packet Handling
    public static final MethodWrapper<Void> PLAYER_SEND_PACKET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ReflectionUtils.getMCServerClassByName("PlayerConnection", "server.network"), ReflectionUtils.getMCServerClassByName("Packet", "network.protocol"));
    public static final MethodWrapper<Void> PLAYER_SEND_PACKET = new MethodWrapper<>(null, Version.V1_17_R1, "sendPacket", ReflectionUtils.getMCServerClassByName("PlayerConnection", "server.network"), PLAYER_SEND_PACKET_V1181, ReflectionUtils.getMCServerClassByName("Packet", "network.protocol"));

}
