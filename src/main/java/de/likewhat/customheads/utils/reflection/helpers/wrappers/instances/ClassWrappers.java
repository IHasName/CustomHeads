package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.CraftBukkitClassWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MinecraftServerClassWrapper;

public class ClassWrappers {

    public static final MinecraftServerClassWrapper CLIENTBOUND_PLAYER_CHAT_PACKET = new MinecraftServerClassWrapper(Version.V1_19_R1, null, "ClientboundPlayerChatPacket", "network.protocol.game");
    public static final MinecraftServerClassWrapper PACKET_PLAYOUT_CHAT = new MinecraftServerClassWrapper(null, Version.V1_18_R2, "PacketPlayOutChat", "network.protocol.game", CLIENTBOUND_PLAYER_CHAT_PACKET);
    public static final MinecraftServerClassWrapper PACKET = new MinecraftServerClassWrapper("Packet", "network.protocol");

    public static final MinecraftServerClassWrapper ICHAT_BASE_COMPONENT = new MinecraftServerClassWrapper("IChatBaseComponent", "network.chat");
    public static final MinecraftServerClassWrapper CHAT_SERIALIZER = new MinecraftServerClassWrapper("ChatSerializer", "network.chat");
    public static final MinecraftServerClassWrapper CHAT_MESSAGE_TYPE = new MinecraftServerClassWrapper("ChatMessageType", "network.chat");
    public static final MinecraftServerClassWrapper MINECRAFT_ITEMSTACK = new MinecraftServerClassWrapper("ItemStack", "world.item");

    private static final MinecraftServerClassWrapper TAG = new MinecraftServerClassWrapper(Version.V1_20_5, null, "Tag", "nbt");
    public static final MinecraftServerClassWrapper NBT_BASE = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "NBTBase", "nbt", TAG);

    public static final MinecraftServerClassWrapper GAMEPROFILE_SERIALIZER = new MinecraftServerClassWrapper(Version.V1_18_R1, Version.V1_20_R3, "GameProfileSerializer", "nbt");

    public static final MinecraftServerClassWrapper CONTAINER_ANVIL = new MinecraftServerClassWrapper("ContainerAnvil", "world.inventory");
    public static final MinecraftServerClassWrapper PACKET_PLAY_OUT_OPEN_WINDOW = new MinecraftServerClassWrapper("PacketPlayOutOpenWindow", "network.protocol.game");
    public static final MinecraftServerClassWrapper CONTAINER = new MinecraftServerClassWrapper("Container", "world.inventory");
    public static final MinecraftServerClassWrapper CONTAINERS = new MinecraftServerClassWrapper(Version.V1_14_R1, null, "Containers", "world.inventory");

    private static final MinecraftServerClassWrapper CONTAINER_LEVEL_ACCESS = new MinecraftServerClassWrapper(Version.V1_20_5, null, "ContainerLevelAccess", "world.inventory");
    public static final MinecraftServerClassWrapper CONTAINER_ACCESS = new MinecraftServerClassWrapper(Version.V1_16_R3, Version.V1_20_R3, "ContainerAccess", "world.inventory", CONTAINER_LEVEL_ACCESS);

    public static final MinecraftServerClassWrapper LEVEL = new MinecraftServerClassWrapper(Version.V1_20_5, null, "World", "world.level");
    public static final MinecraftServerClassWrapper WORLD = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "World", "world.level", LEVEL);

    private static final MinecraftServerClassWrapper BLOCK_POS = new MinecraftServerClassWrapper("BlockPosition", "core");
    public static final MinecraftServerClassWrapper BLOCK_POSITION = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "BlockPosition", "core", BLOCK_POS);

    public static final MinecraftServerClassWrapper BLOCK_STATE = new MinecraftServerClassWrapper("BlockState", "world.level.block.state");
    public static final MinecraftServerClassWrapper I_BLOCK_DATA = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "IBlockData", "world.level.block.state", BLOCK_STATE);

    private static final MinecraftServerClassWrapper BLOCK_ENTITY = new MinecraftServerClassWrapper("BlockEntity", "world.level.block.entity");
    public static final MinecraftServerClassWrapper TILE_ENTITY = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "TileEntity", "world.level.block.entity", BLOCK_ENTITY);

    private static final MinecraftServerClassWrapper SKULL_BLOCK_ENTITY = new MinecraftServerClassWrapper(Version.V1_20_5, null, "SkullBlockEntity", "world.level.block.entity");
    public static final MinecraftServerClassWrapper TILE_ENTITY_SKULL = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "TileEntitySkull", "world.level.block.entity", SKULL_BLOCK_ENTITY);

    public static final MinecraftServerClassWrapper SERVER_PLAYER = new MinecraftServerClassWrapper(Version.V1_20_5, null, "ServerPlayer", "server.level");
    public static final MinecraftServerClassWrapper ENTITY_PLAYER = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "EntityPlayer", "server.level", SERVER_PLAYER);

    public static final MinecraftServerClassWrapper PLAYER = new MinecraftServerClassWrapper(Version.V1_20_5, null, "Player", "world.entity.player");
    public static final MinecraftServerClassWrapper ENTITY_HUMAN = new MinecraftServerClassWrapper(null, Version.V1_20_R3, "EntityHuman", "world.entity.player", PLAYER);

    public static final MinecraftServerClassWrapper ENTITY = new MinecraftServerClassWrapper("Entity", "world.entity");
    
    public static final MinecraftServerClassWrapper PLAYER_INVENTORY = new MinecraftServerClassWrapper("PlayerInventory", "world.entity.player");
    public static final MinecraftServerClassWrapper PLAYER_CONNECTION = new MinecraftServerClassWrapper("PlayerConnection", "server.network");

    public static final MinecraftServerClassWrapper RESOURCE_KEY = new MinecraftServerClassWrapper("ResourceKey", "resources");

    // DataComponents
    public static final MinecraftServerClassWrapper DATA_COMPONENT_TYPE = new MinecraftServerClassWrapper(Version.V1_20_5, null, "DataComponentType", "core.component");

    public static final MinecraftServerClassWrapper DATA_COMPONENTS = new MinecraftServerClassWrapper(Version.V1_20_5, null, "DataComponents", "core.component");

    public static final MinecraftServerClassWrapper DATA_COMPONENT_MAP = new MinecraftServerClassWrapper(Version.V1_20_5, null, "DataComponentMap", "core.component");

    // Item Components
    public static final MinecraftServerClassWrapper COMPONENT_CUSTOM_DATA = new MinecraftServerClassWrapper(Version.V1_20_5, null, "CustomData", "world.item.component");

    // CraftBukkit
    public static final CraftBukkitClassWrapper CRAFTBUKKIT_ITEMSTACK = new CraftBukkitClassWrapper("inventory.CraftItemStack");
    public static final CraftBukkitClassWrapper CRAFTBUKKIT_CRAFT_PLAYER = new CraftBukkitClassWrapper("entity.CraftPlayer");
    public static final CraftBukkitClassWrapper CRAFTBUKKIT_CRAFT_WORLD = new CraftBukkitClassWrapper("CraftWorld");
    public static final CraftBukkitClassWrapper CRAFTBUKKIT_CRAFT_BLOCK_DATA = new CraftBukkitClassWrapper("block.data.CraftBlockData");


}