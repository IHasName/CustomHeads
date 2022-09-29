package de.likewhat.customheads.utils.reflection.helpers.collections;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.MinecraftServerClassWrapper;

public class ReflectionClassCollection {

    public static final MinecraftServerClassWrapper CLIENTBOUND_PLAYER_CHAT_PACKET = new MinecraftServerClassWrapper(Version.V1_19_R1, null, "ClientboundPlayerChatPacket", "network.protocol.game", null);
    public static final MinecraftServerClassWrapper PACKET_PLAYOUT_CHAT = new MinecraftServerClassWrapper(null, Version.V1_18_R2, "PacketPlayOutChat", "network.protocol.game", CLIENTBOUND_PLAYER_CHAT_PACKET);

    public static final MinecraftServerClassWrapper ICHAT_BASE_COMPONENT = new MinecraftServerClassWrapper(null, null, "IChatBaseComponent", "network.chat", null);
    public static final MinecraftServerClassWrapper CHAT_MESSAGE_TYPE = new MinecraftServerClassWrapper(null, null, "ChatMessageType", "network.chat", null);
    public static final MinecraftServerClassWrapper MINECRAFT_ITEMSTACK_CLASS = new MinecraftServerClassWrapper(null, null, "ItemStack", "world.item", null);

    public static final MinecraftServerClassWrapper NBTBASE_CLASS = new MinecraftServerClassWrapper(null, null, "NBTBase", "nbt", null);

    public static final MinecraftServerClassWrapper CONTAINER_ANVIL = new MinecraftServerClassWrapper(null, null, "ContainerAnvil", "world.inventory", null);
    public static final MinecraftServerClassWrapper PACKET_PLAY_OUT_OPEN_WINDOW = new MinecraftServerClassWrapper(null, null, "PacketPlayOutOpenWindow", "network.protocol.game", null);
    public static final MinecraftServerClassWrapper CONTAINER = new MinecraftServerClassWrapper(null, null, "Container", "world.inventory", null);
    public static final MinecraftServerClassWrapper CONTAINER_ACCESS = new MinecraftServerClassWrapper(Version.V1_16_R3, null, "ContainerAccess", "world.inventory", null);

    public static final MinecraftServerClassWrapper WORLD = new MinecraftServerClassWrapper(null, null, "World", "world.level", null);
    public static final MinecraftServerClassWrapper BLOCK_POSITION = new MinecraftServerClassWrapper(null, null, "BlockPosition", "core", null);

    public static final MinecraftServerClassWrapper ENTITY_PLAYER = new MinecraftServerClassWrapper(null, null, "EntityPlayer", "server.level", null);

    public static final MinecraftServerClassWrapper RESOURCE_KEY = new MinecraftServerClassWrapper(null, null, "ResourceKey", "resources", null);

}