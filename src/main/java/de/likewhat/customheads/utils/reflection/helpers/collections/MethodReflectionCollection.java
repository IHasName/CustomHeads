package de.likewhat.customheads.utils.reflection.helpers.collections;

import com.mojang.authlib.GameProfile;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;
import de.likewhat.customheads.utils.reflection.nbt.NBTTagUtils;

public class MethodReflectionCollection {

    // NBTTagCompound
    private static final MethodWrapper<Object> NBT_TAGCOMPOUND_GET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GET = new MethodWrapper<>(null, Version.V1_17_R1, "get", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_GET_V1181, String.class);

    private static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETCOMPOUND_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "p", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETCOMPOUND = new MethodWrapper<>(null, Version.V1_17_R1, "getCompound", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_GETCOMPOUND_V1181, String.class);

    private static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETLIST_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class, int.class);
    public static final MethodWrapper<Object> NBT_TAGCOMPOUND_GETLIST = new MethodWrapper<>(null, Version.V1_17_R1, "getList", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_GETLIST_V1181, String.class, int.class);

    private static final MethodWrapper<Void> NBT_TAGCOMPOUND_SET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class, ClassReflectionCollection.NBTBASE_CLASS.resolve());
    public static final MethodWrapper<Void> NBT_TAGCOMPOUND_SET = new MethodWrapper<>(null, Version.V1_17_R1, "set", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_SET_V1181, String.class, ClassReflectionCollection.NBTBASE_CLASS.resolve());

    private static final MethodWrapper<Boolean> NBT_TAGCOMPOUND_HASKEY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "e", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<Boolean> NBT_TAGCOMPOUND_HASKEY = new MethodWrapper<>(null, Version.V1_17_R1, "hasKey", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_HASKEY_V1181, String.class);

    private static final MethodWrapper<String> NBT_TAGCOMPOUND_GETSTRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "l", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), String.class);
    public static final MethodWrapper<String> NBT_TAGCOMPOUND_GETSTRING = new MethodWrapper<>(null, Version.V1_17_R1, "getString", NBTTagUtils.NBTType.COMPOUND.getNBTClass(), NBT_TAGCOMPOUND_GETSTRING_V1181, String.class);

    // NBTTagList
    private static final MethodWrapper<Boolean> NBT_TAGLIST_ADD_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "b", NBTTagUtils.NBTType.LIST.getNBTClass(), int.class, ClassReflectionCollection.NBTBASE_CLASS.resolve());
    private static final MethodWrapper<Void> NBT_TAGLIST_ADD_V1141 = new MethodWrapper<>(Version.V1_14_R1, Version.V1_17_R1, "add", NBTTagUtils.NBTType.LIST.getNBTClass(), NBT_TAGLIST_ADD_V1181, int.class, ClassReflectionCollection.NBTBASE_CLASS.resolve());
    public static final MethodWrapper<Void> NBT_TAGLIST_ADD = new MethodWrapper<>(null, Version.V1_13_R2, "add", NBTTagUtils.NBTType.LIST.getNBTClass(), NBT_TAGLIST_ADD_V1141, ClassReflectionCollection.NBTBASE_CLASS.resolve());

    public static final MethodWrapper<Integer> NBT_TAGLIST_SIZE = new MethodWrapper<>(null, null, "size", NBTTagUtils.NBTType.LIST.getNBTClass());

    private static final MethodWrapper<String> NBT_TAGLIST_GETSTRING_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "j", NBTTagUtils.NBTType.LIST.getNBTClass(), int.class);
    public static final MethodWrapper<String> NBT_TAGLIST_GETSTRING = new MethodWrapper<>(null, Version.V1_17_R1, "getString", NBTTagUtils.NBTType.LIST.getNBTClass(), NBT_TAGLIST_GETSTRING_V1181, int.class);

    private static final MethodWrapper<Object> NBT_TAGLIST_GET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "k", NBTTagUtils.NBTType.LIST.getNBTClass(), int.class);
    public static final MethodWrapper<Object> NBT_TAGLIST_GET = new MethodWrapper<>(null, Version.V1_17_R1, "get", NBTTagUtils.NBTType.LIST.getNBTClass(), NBT_TAGLIST_GET_V1181, int.class);

    // ItemStack
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1201 = new MethodWrapper<>(Version.V1_20_R1, null, "u", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve());
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1191 = new MethodWrapper<>(Version.V1_19_R1, Version.V1_19_R3, "t", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_HASTAG_V1201);
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1182 = new MethodWrapper<>(Version.V1_18_R2, Version.V1_18_R2, "s", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_HASTAG_V1191);
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_18_R1, "r", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_HASTAG_V1182);
    public static final MethodWrapper<Boolean> ITEMSTACK_HASTAG = new MethodWrapper<>(null, Version.V1_17_R1, "hasTag", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_HASTAG_V1181);

    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1201 = new MethodWrapper<>(Version.V1_20_R1, null, "v", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve());
    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1191 = new MethodWrapper<>(Version.V1_19_R1, Version.V1_19_R3, "u", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_GETTAG_V1201);
    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1182 = new MethodWrapper<>(Version.V1_18_R2, Version.V1_18_R2, "t", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_GETTAG_V1191);
    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_18_R1, "s", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_GETTAG_V1182);
    public static final MethodWrapper<Object> ITEMSTACK_GETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "getTag", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_GETTAG_V1181);

    private static final MethodWrapper<Void> ITEMSTACK_SETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), NBTTagUtils.NBTType.COMPOUND.getNBTClass());
    public static final MethodWrapper<Void> ITEMSTACK_SETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "setTag", ClassReflectionCollection.MINECRAFT_ITEMSTACK_CLASS.resolve(), ITEMSTACK_SETTAG_V1181, NBTTagUtils.NBTType.COMPOUND.getNBTClass());

    // GameProfile
    public static final MethodWrapper<Object> GAMEPROFILE_SERIALIZE = new MethodWrapper<>(Version.V1_18_R1, null, "a", ReflectionUtils.getMCServerClassByName("GameProfileSerializer", "nbt"), NBTTagUtils.NBTType.COMPOUND.getNBTClass(), GameProfile.class);

    // Player Packet Handling
    private static final MethodWrapper<Void> PLAYER_SEND_PACKET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ReflectionUtils.getMCServerClassByName("PlayerConnection", "server.network"), ReflectionUtils.getMCServerClassByName("Packet", "network.protocol"));
    public static final MethodWrapper<Void> PLAYER_SEND_PACKET = new MethodWrapper<>(null, Version.V1_17_R1, "sendPacket", ReflectionUtils.getMCServerClassByName("PlayerConnection", "server.network"), PLAYER_SEND_PACKET_V1181, ReflectionUtils.getMCServerClassByName("Packet", "network.protocol"));

    private static final MethodWrapper<Object> CONTAINER_ACCESS_AT_V1182 = new MethodWrapper<>(Version.V1_18_R2, null, "a", ClassReflectionCollection.CONTAINER_ACCESS, ClassReflectionCollection.WORLD, ClassReflectionCollection.BLOCK_POSITION);
    public static final MethodWrapper<Object> CONTAINER_ACCESS_AT = new MethodWrapper<>(null, Version.V1_18_R1, "at", ClassReflectionCollection.CONTAINER_ACCESS, CONTAINER_ACCESS_AT_V1182, ClassReflectionCollection.WORLD, ClassReflectionCollection.BLOCK_POSITION);

    // Other
    public static final MethodWrapper<Object> CHAT_COMPONENT_SERIALIZE = new MethodWrapper<>(null, null, "a", ClassReflectionCollection.CHAT_SERIALIZER.resolve(), String.class);

    // World
    private static final MethodWrapper<Boolean> WORLD_SET_TYPE_AND_DATA_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassReflectionCollection.WORLD.resolve(), ClassReflectionCollection.BLOCK_POSITION.resolve(), ClassReflectionCollection.I_BLOCK_DATA.resolve(), int.class);
    public static final MethodWrapper<Boolean> WORLD_SET_TYPE_AND_DATA = new MethodWrapper<>(null, Version.V1_17_R1, "setTypeAndData", ClassReflectionCollection.WORLD.resolve(), WORLD_SET_TYPE_AND_DATA_V1181, ClassReflectionCollection.BLOCK_POSITION.resolve(), ClassReflectionCollection.I_BLOCK_DATA.resolve(), int.class);

    private static final MethodWrapper<Void> WORLD_SET_TILE_ENTITY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassReflectionCollection.WORLD, ClassReflectionCollection.TILE_ENTITY);
    public static final MethodWrapper<Void> WORLD_SET_TILE_ENTITY = new MethodWrapper<>(null, Version.V1_17_R1, "setTileEntity", ClassReflectionCollection.WORLD, WORLD_SET_TILE_ENTITY_V1181, ClassReflectionCollection.TILE_ENTITY);

    private static final MethodWrapper<Void> WORLD_GET_TILE_ENTITY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c_", ClassReflectionCollection.WORLD, ClassReflectionCollection.BLOCK_POSITION);
    public static final MethodWrapper<Void> WORLD_GET_TILE_ENTITY = new MethodWrapper<>(null, Version.V1_17_R1, "getTileEntity", ClassReflectionCollection.WORLD, WORLD_GET_TILE_ENTITY_V1181, ClassReflectionCollection.BLOCK_POSITION);

    // TileEntity
    private static final MethodWrapper<Void> TILE_ENTITY_SET_GAME_PROFILE_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassReflectionCollection.TILE_ENTITY_SKULL.resolve(), GameProfile.class);
    public static final MethodWrapper<Void> TILE_ENTITY_SET_GAME_PROFILE = new MethodWrapper<>(null, Version.V1_17_R1, "setGameProfile", ClassReflectionCollection.TILE_ENTITY_SKULL.resolve(), TILE_ENTITY_SET_GAME_PROFILE_V1181, GameProfile.class);
}
