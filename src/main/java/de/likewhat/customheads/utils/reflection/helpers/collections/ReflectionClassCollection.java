package de.likewhat.customheads.utils.reflection.helpers.collections;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;

public class ReflectionClassCollection {

    public static final Class<?> PACKET_PLAYOUT_CHAT = ReflectionUtils.getMCServerClassByName("PacketPlayOutChat", "network.protocol.game");
    public static final Class<?> ICHAT_BASE_COMPONENT = ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat");
    public static final Class<?> CHAT_MESSAGE_TYPE = ReflectionUtils.getMCServerClassByName("ChatMessageType", "network.chat");
    public static final Class<?> MINECRAFT_ITEMSTACK_CLASS = ReflectionUtils.getMCServerClassByName("ItemStack", "world.item");

    public static final Class<?> NBTBASE_CLASS = ReflectionUtils.getMCServerClassByName("NBTBase", "nbt");

    public static final Class<?> CONTAINER_ANVIL = ReflectionUtils.getMCServerClassByName("ContainerAnvil", "world.inventory");
    public static final Class<?> PACKET_PLAY_OUT_OPEN_WINDOW = ReflectionUtils.getMCServerClassByName("PacketPlayOutOpenWindow", "network.protocol.game");
    public static final Class<?> CONTAINER = ReflectionUtils.getMCServerClassByName("Container", "world.inventory");

}