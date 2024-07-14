package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.ConstructorWrapper;

import java.util.UUID;

public class ConstructorWrappers {

    private static final ConstructorWrapper<?> PACKET_PLAYOUT_CHAT_V1161 = new ConstructorWrapper<>(Version.V1_16_R1, Version.V1_19_R1, ClassWrappers.PACKET_PLAYOUT_CHAT, ClassWrappers.ICHAT_BASE_COMPONENT.resolve(), ClassWrappers.CHAT_MESSAGE_TYPE.resolve(), UUID.class);
    public static final ConstructorWrapper<?> PACKET_PLAYOUT_CHAT = new ConstructorWrapper<>(null, Version.V1_15_R1, ClassWrappers.PACKET_PLAYOUT_CHAT, PACKET_PLAYOUT_CHAT_V1161, ClassWrappers.ICHAT_BASE_COMPONENT);

    public static final ConstructorWrapper<?> MINECRAFT_BLOCK_POSITION = new ConstructorWrapper<>(null, null, ClassWrappers.BLOCK_POSITION, int.class, int.class, int.class);
}
