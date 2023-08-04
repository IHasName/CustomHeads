package de.likewhat.customheads.utils.reflection.helpers.collections;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MinecraftServerClassWrapper;

public class ClassReflectionCollection {

    public static final MinecraftServerClassWrapper CLIENTBOUND_PLAYER_CHAT_PACKET = new MinecraftServerClassWrapper(Version.V1_19_R1, null, "ClientboundPlayerChatPacket", "network.protocol.game", null);
    public static final MinecraftServerClassWrapper PACKET_PLAYOUT_CHAT = new MinecraftServerClassWrapper(null, Version.V1_18_R2, "PacketPlayOutChat", "network.protocol.game", CLIENTBOUND_PLAYER_CHAT_PACKET);

    public static final MinecraftServerClassWrapper ICHAT_BASE_COMPONENT = new MinecraftServerClassWrapper("IChatBaseComponent", "network.chat");
    public static final MinecraftServerClassWrapper CHAT_SERIALIZER = new MinecraftServerClassWrapper("ChatSerializer", "network.chat");
    public static final MinecraftServerClassWrapper CHAT_MESSAGE_TYPE = new MinecraftServerClassWrapper("ChatMessageType", "network.chat");
    public static final MinecraftServerClassWrapper MINECRAFT_ITEMSTACK_CLASS = new MinecraftServerClassWrapper("ItemStack", "world.item");

    public static final MinecraftServerClassWrapper NBTBASE_CLASS = new MinecraftServerClassWrapper("NBTBase", "nbt");

    public static final MinecraftServerClassWrapper CONTAINER_ANVIL = new MinecraftServerClassWrapper("ContainerAnvil", "world.inventory");
    public static final MinecraftServerClassWrapper PACKET_PLAY_OUT_OPEN_WINDOW = new MinecraftServerClassWrapper("PacketPlayOutOpenWindow", "network.protocol.game");
    public static final MinecraftServerClassWrapper CONTAINER = new MinecraftServerClassWrapper("Container", "world.inventory");
    public static final MinecraftServerClassWrapper CONTAINERS = new MinecraftServerClassWrapper(Version.V1_14_R1, null, "Containers", "world.inventory");
    public static final MinecraftServerClassWrapper CONTAINER_ACCESS = new MinecraftServerClassWrapper(Version.V1_16_R3, null, "ContainerAccess", "world.inventory");

    public static final MinecraftServerClassWrapper WORLD = new MinecraftServerClassWrapper("World", "world.level");
    public static final MinecraftServerClassWrapper BLOCK_POSITION = new MinecraftServerClassWrapper("BlockPosition", "core");
    public static final MinecraftServerClassWrapper I_BLOCK_DATA = new MinecraftServerClassWrapper("IBlockData", "world.level.block.state");
    public static final MinecraftServerClassWrapper TILE_ENTITY = new MinecraftServerClassWrapper("TileEntity", "world.level.block.entity");
    public static final MinecraftServerClassWrapper TILE_ENTITY_SKULL = new MinecraftServerClassWrapper("TileEntitySkull", "world.level.block.entity");

    public static final MinecraftServerClassWrapper ENTITY_PLAYER = new MinecraftServerClassWrapper("EntityPlayer", "server.level");
    public static final MinecraftServerClassWrapper ENTITY_HUMAN = new MinecraftServerClassWrapper("EntityHuman", "world.entity.player");
    public static final MinecraftServerClassWrapper ENTITY = new MinecraftServerClassWrapper("Entity", "world.entity");
    
    public static final MinecraftServerClassWrapper PLAYER_INVENTORY = new MinecraftServerClassWrapper("PlayerInventory", "world.entity.player");

    public static final MinecraftServerClassWrapper RESOURCE_KEY = new MinecraftServerClassWrapper("ResourceKey", "resources");


}