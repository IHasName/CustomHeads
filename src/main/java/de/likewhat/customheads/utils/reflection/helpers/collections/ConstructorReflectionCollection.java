package de.likewhat.customheads.utils.reflection.helpers.collections;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.ConstructorWrapper;

import java.util.UUID;

public class ConstructorReflectionCollection {

    private static final ConstructorWrapper<?> PACKET_PLAYOUT_CHAT_V1161 = new ConstructorWrapper<>(Version.V1_16_R1, Version.V1_19_R1, ClassReflectionCollection.PACKET_PLAYOUT_CHAT, ClassReflectionCollection.ICHAT_BASE_COMPONENT.resolve(), ClassReflectionCollection.CHAT_MESSAGE_TYPE.resolve(), UUID.class);
    public static final ConstructorWrapper<?> PACKET_PLAYOUT_CHAT = new ConstructorWrapper<>(null, Version.V1_15_R1, ClassReflectionCollection.PACKET_PLAYOUT_CHAT, PACKET_PLAYOUT_CHAT_V1161, ClassReflectionCollection.ICHAT_BASE_COMPONENT);

}
