package de.likewhat.customheads.utils.reflection.helpers.collections;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.ConstructorWrapper;

import java.util.UUID;

public class ReflectionConstructorCollection {

    public static final ConstructorWrapper<?> PACKET_PLAYOUT_CHAT_V1161 = new ConstructorWrapper<>(Version.V1_16_R1, null, ReflectionClassCollection.PACKET_PLAYOUT_CHAT, ReflectionClassCollection.ICHAT_BASE_COMPONENT, ReflectionClassCollection.CHAT_MESSAGE_TYPE, UUID.class);
    public static final ConstructorWrapper<?> PACKET_PLAYOUT_CHAT = new ConstructorWrapper<>(null, Version.V1_15_R1, ReflectionClassCollection.PACKET_PLAYOUT_CHAT, PACKET_PLAYOUT_CHAT_V1161, ReflectionClassCollection.ICHAT_BASE_COMPONENT);

}
