package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances;

import com.mojang.authlib.GameProfile;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.ClassWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MethodWrapper;
import de.likewhat.customheads.utils.reflection.nbt.NBTType;
import org.bukkit.inventory.ItemStack;

public class MethodWrappers {

    // ItemStack
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1201 = new MethodWrapper<>(Version.V1_20_R1, null, "u", ClassWrappers.MINECRAFT_ITEMSTACK.resolve());
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1191 = new MethodWrapper<>(Version.V1_19_R1, Version.V1_19_R3, "t", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_HASTAG_V1201);
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1182 = new MethodWrapper<>(Version.V1_18_R2, Version.V1_18_R2, "s", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_HASTAG_V1191);
    private static final MethodWrapper<Boolean> ITEMSTACK_HASTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_18_R1, "r", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_HASTAG_V1182);
    public static final MethodWrapper<Boolean> ITEMSTACK_HASTAG = new MethodWrapper<>(null, Version.V1_17_R1, "hasTag", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_HASTAG_V1181);

    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1201 = new MethodWrapper<>(Version.V1_20_R1, null, "v", ClassWrappers.MINECRAFT_ITEMSTACK.resolve());
    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1191 = new MethodWrapper<>(Version.V1_19_R1, Version.V1_19_R3, "u", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_GETTAG_V1201);
    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1182 = new MethodWrapper<>(Version.V1_18_R2, Version.V1_18_R2, "t", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_GETTAG_V1191);
    private static final MethodWrapper<Object> ITEMSTACK_GETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, Version.V1_18_R1, "s", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_GETTAG_V1182);
    public static final MethodWrapper<Object> ITEMSTACK_GETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "getTag", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_GETTAG_V1181);

    private static final MethodWrapper<Void> ITEMSTACK_SETTAG_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), NBTType.COMPOUND.getNBTClass());
    public static final MethodWrapper<Void> ITEMSTACK_SETTAG = new MethodWrapper<>(null, Version.V1_17_R1, "setTag", ClassWrappers.MINECRAFT_ITEMSTACK.resolve(), ITEMSTACK_SETTAG_V1181, NBTType.COMPOUND.getNBTClass());

    // GameProfile
    public static final MethodWrapper<Object> GAMEPROFILE_SERIALIZE = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassWrappers.GAMEPROFILE_SERIALIZER.resolve(), NBTType.COMPOUND.getNBTClass(), GameProfile.class);

    // Player Packet Handling
    private static final MethodWrapper<Void> PLAYER_SEND_PACKET_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassWrappers.PLAYER_CONNECTION.resolve(), ClassWrappers.PACKET.resolve());
    public static final MethodWrapper<Void> PLAYER_SEND_PACKET = new MethodWrapper<>(null, Version.V1_17_R1, "sendPacket", ClassWrappers.PLAYER_CONNECTION.resolve(), PLAYER_SEND_PACKET_V1181, ClassWrappers.PACKET.resolve());

    private static final MethodWrapper<Object> CONTAINER_ACCESS_AT_V1182 = new MethodWrapper<>(Version.V1_18_R2, null, "a", ClassWrappers.CONTAINER_ACCESS, ClassWrappers.WORLD, ClassWrappers.BLOCK_POSITION);
    public static final MethodWrapper<Object> CONTAINER_ACCESS_AT = new MethodWrapper<>(null, Version.V1_18_R1, "at", ClassWrappers.CONTAINER_ACCESS, CONTAINER_ACCESS_AT_V1182, ClassWrappers.WORLD, ClassWrappers.BLOCK_POSITION);

    // Other
    public static final MethodWrapper<Object> CHAT_COMPONENT_SERIALIZE = new MethodWrapper<>(null, null, "a", ClassWrappers.CHAT_SERIALIZER.resolve(), String.class);

    // World
    private static final MethodWrapper<Boolean> WORLD_SET_TYPE_AND_DATA_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassWrappers.WORLD.resolve(), ClassWrappers.BLOCK_POSITION.resolve(), ClassWrappers.BLOCK_DATA_INTERFACE.resolve(), int.class);
    public static final MethodWrapper<Boolean> WORLD_SET_TYPE_AND_DATA = new MethodWrapper<>(null, Version.V1_17_R1, "setTypeAndData", ClassWrappers.WORLD.resolve(), WORLD_SET_TYPE_AND_DATA_V1181, ClassWrappers.BLOCK_POSITION.resolve(), ClassWrappers.BLOCK_DATA_INTERFACE.resolve(), int.class);

    private static final MethodWrapper<Void> WORLD_SET_TILE_ENTITY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassWrappers.WORLD, ClassWrappers.TILE_ENTITY);
    public static final MethodWrapper<Void> WORLD_SET_TILE_ENTITY = new MethodWrapper<>(null, Version.V1_17_R1, "setTileEntity", ClassWrappers.WORLD, WORLD_SET_TILE_ENTITY_V1181, ClassWrappers.TILE_ENTITY);

    private static final MethodWrapper<Void> WORLD_GET_TILE_ENTITY_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "c_", ClassWrappers.WORLD, ClassWrappers.BLOCK_POSITION);
    public static final MethodWrapper<Void> WORLD_GET_TILE_ENTITY = new MethodWrapper<>(null, Version.V1_17_R1, "getTileEntity", ClassWrappers.WORLD, WORLD_GET_TILE_ENTITY_V1181, ClassWrappers.BLOCK_POSITION);

    // TileEntity
    private static final MethodWrapper<Void> TILE_ENTITY_SET_GAME_PROFILE_V1181 = new MethodWrapper<>(Version.V1_18_R1, null, "a", ClassWrappers.TILE_ENTITY_SKULL.resolve(), GameProfile.class);
    public static final MethodWrapper<Void> TILE_ENTITY_SET_GAME_PROFILE = new MethodWrapper<>(null, Version.V1_17_R1, "setGameProfile", ClassWrappers.TILE_ENTITY_SKULL.resolve(), TILE_ENTITY_SET_GAME_PROFILE_V1181, GameProfile.class);

    // CraftBukkit
    public static final MethodWrapper<ItemStack> CRAFTBUKKIT_ITEMSTACK_AS_BUKKIT_COPY = new MethodWrapper<>(null, null, "asBukkitCopy", ClassWrappers.CRAFTBUKKIT_ITEMSTACK, ClassWrappers.MINECRAFT_ITEMSTACK);
    public static final MethodWrapper<Object> CRAFTBUKKIT_ITEMSTACK_AS_NMS_COPY = new MethodWrapper<>(null, null, "asNMSCopy", ClassWrappers.CRAFTBUKKIT_ITEMSTACK, ClassWrapper.from(ItemStack.class));
    public static final MethodWrapper<Object> CRAFTBUKKIT_CRAFTPLAYER_GET_HANDLE = new MethodWrapper<>(null, null, "getHandle", ClassWrappers.CRAFTBUKKIT_CRAFT_PLAYER);
    public static final MethodWrapper<Object> CRAFTBUKKIT_CRAFTWORLD_GET_HANDLE = new MethodWrapper<>(null, null, "getHandle", ClassWrappers.CRAFTBUKKIT_CRAFT_WORLD);
}
