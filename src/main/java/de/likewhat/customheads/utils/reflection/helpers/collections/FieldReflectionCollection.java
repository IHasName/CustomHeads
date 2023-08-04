package de.likewhat.customheads.utils.reflection.helpers.collections;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.FieldWrapper;

public class FieldReflectionCollection {

    private static final FieldWrapper PLAYER_INVENTORY_V1121 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "cl", Version.V1_20_R1, Version.LATEST_UPDATE);
    private static final FieldWrapper PLAYER_INVENTORY_V1193 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "ck", Version.V1_19_R3, Version.V1_19_R3, PLAYER_INVENTORY_V1121);
    private static final FieldWrapper PLAYER_INVENTORY_V1192 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "co", Version.V1_19_R2, Version.V1_19_R2, PLAYER_INVENTORY_V1193);
    private static final FieldWrapper PLAYER_INVENTORY_V1191 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "cp", Version.V1_19_R1, Version.V1_19_R1, PLAYER_INVENTORY_V1192);
    private static final FieldWrapper PLAYER_INVENTORY_V1182 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "co", Version.V1_18_R2, Version.V1_18_R2, PLAYER_INVENTORY_V1191);
    private static final FieldWrapper PLAYER_INVENTORY_V1181 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "cp", Version.V1_18_R1, Version.V1_18_R1, PLAYER_INVENTORY_V1182);
    private static final FieldWrapper PLAYER_INVENTORY_V1171 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "co", Version.V1_17_R1, Version.V1_17_R1, PLAYER_INVENTORY_V1181);
    public static final FieldWrapper PLAYER_INVENTORY = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "inventory", null, Version.V1_16_R3, PLAYER_INVENTORY_V1171);

    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1121 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "bR", Version.V1_20_R1, Version.LATEST_UPDATE);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1193 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "bP", Version.V1_19_R3, Version.V1_19_R3, PLAYER_ACTIVE_CONTAINER_V1121);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1182 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "bU", Version.V1_18_R2, Version.V1_19_R2, PLAYER_ACTIVE_CONTAINER_V1193);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1181 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "bW", Version.V1_18_R1, Version.V1_18_R1, PLAYER_ACTIVE_CONTAINER_V1182);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1171 = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "bV", Version.V1_17_R1, Version.V1_17_R1, PLAYER_ACTIVE_CONTAINER_V1181);
    public static final FieldWrapper PLAYER_ACTIVE_CONTAINER = new FieldWrapper(ClassReflectionCollection.ENTITY_HUMAN, "activeContainer", null, Version.V1_16_R3, PLAYER_ACTIVE_CONTAINER_V1171);

    private static final FieldWrapper PLAYER_CONNECTION_V1201 = new FieldWrapper(ClassReflectionCollection.ENTITY_PLAYER, "c", Version.V1_20_R1, Version.LATEST_UPDATE);
    private static final FieldWrapper PLAYER_CONNECTION_V1171 = new FieldWrapper(ClassReflectionCollection.ENTITY_PLAYER, "b", Version.V1_17_R1, Version.V1_19_R3, PLAYER_CONNECTION_V1201);
    public static final FieldWrapper PLAYER_CONNECTION = new FieldWrapper(ClassReflectionCollection.ENTITY_PLAYER, "playerConnection", null, Version.V1_16_R3, PLAYER_CONNECTION_V1171);

    private static final FieldWrapper ENTITY_WORLD_V1201 = new FieldWrapper(ClassReflectionCollection.ENTITY, "t", Version.V1_20_R1, Version.LATEST_UPDATE);
    private static final FieldWrapper ENTITY_WORLD_V1193 = new FieldWrapper(ClassReflectionCollection.ENTITY, "H", Version.V1_19_R3, Version.V1_19_R3, ENTITY_WORLD_V1201);
    private static final FieldWrapper ENTITY_WORLD_V1182 = new FieldWrapper(ClassReflectionCollection.ENTITY, "s", Version.V1_18_R2, Version.V1_19_R2, ENTITY_WORLD_V1193);
    private static final FieldWrapper ENTITY_WORLD_V1171 = new FieldWrapper(ClassReflectionCollection.ENTITY, "t", Version.V1_17_R1, Version.V1_18_R1, ENTITY_WORLD_V1182);
    public static final FieldWrapper ENTITY_WORLD = new FieldWrapper(ClassReflectionCollection.ENTITY, "world", null, Version.V1_16_R3, ENTITY_WORLD_V1171);

    private static final FieldWrapper CONTAINER_WINDOW_ID_V1171 = new FieldWrapper(ClassReflectionCollection.CONTAINER, "j", Version.V1_17_R1, Version.LATEST_UPDATE);
    public static final FieldWrapper CONTAINER_WINDOW_ID = new FieldWrapper(ClassReflectionCollection.CONTAINER, "windowId", null, Version.V1_16_R3, CONTAINER_WINDOW_ID_V1171);

    private static final FieldWrapper CONTAINERS_ANVIL_V1201 = new FieldWrapper(ClassReflectionCollection.CONTAINERS, "h", Version.V1_16_R3, Version.LATEST_UPDATE);
    public static final FieldWrapper CONTAINERS_ANVIL = new FieldWrapper(ClassReflectionCollection.CONTAINERS, "ANVIL", Version.V1_14_R1, Version.V1_16_R3, CONTAINERS_ANVIL_V1201);

}
