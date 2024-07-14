package de.likewhat.customheads.utils.reflection.helpers.wrappers.instances;

import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.FieldWrapper;

public class FieldWrappers {

    private static final FieldWrapper PLAYER_INVENTORY_V1123 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "cm", Version.V1_20_R3, Version.V1_20_R3);
    private static final FieldWrapper PLAYER_INVENTORY_V1122 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "cn", Version.V1_20_R2, Version.V1_20_R2, PLAYER_INVENTORY_V1123);
    private static final FieldWrapper PLAYER_INVENTORY_V1121 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "cl", Version.V1_20_R1, Version.V1_20_R1, PLAYER_INVENTORY_V1122);
    private static final FieldWrapper PLAYER_INVENTORY_V1193 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "ck", Version.V1_19_R3, Version.V1_19_R3, PLAYER_INVENTORY_V1121);
    private static final FieldWrapper PLAYER_INVENTORY_V1192 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "co", Version.V1_19_R2, Version.V1_19_R2, PLAYER_INVENTORY_V1193);
    private static final FieldWrapper PLAYER_INVENTORY_V1191 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "cp", Version.V1_19_R1, Version.V1_19_R1, PLAYER_INVENTORY_V1192);
    private static final FieldWrapper PLAYER_INVENTORY_V1182 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "co", Version.V1_18_R2, Version.V1_18_R2, PLAYER_INVENTORY_V1191);
    private static final FieldWrapper PLAYER_INVENTORY_V1181 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "cp", Version.V1_18_R1, Version.V1_18_R1, PLAYER_INVENTORY_V1182);
    private static final FieldWrapper PLAYER_INVENTORY_V1171 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "co", Version.V1_17_R1, Version.V1_17_R1, PLAYER_INVENTORY_V1181);
    public static final FieldWrapper PLAYER_INVENTORY = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "inventory", null, Version.V1_16_R3, PLAYER_INVENTORY_V1171);


    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1202 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "bS", Version.V1_20_R2, Version.V1_20_R3);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1201 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "bR", Version.V1_20_R1, Version.V1_20_R1, PLAYER_ACTIVE_CONTAINER_V1202);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1193 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "bP", Version.V1_19_R3, Version.V1_19_R3, PLAYER_ACTIVE_CONTAINER_V1201);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1182 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "bU", Version.V1_18_R2, Version.V1_19_R2, PLAYER_ACTIVE_CONTAINER_V1193);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1181 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "bW", Version.V1_18_R1, Version.V1_18_R1, PLAYER_ACTIVE_CONTAINER_V1182);
    private static final FieldWrapper PLAYER_ACTIVE_CONTAINER_V1171 = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "bV", Version.V1_17_R1, Version.V1_17_R1, PLAYER_ACTIVE_CONTAINER_V1181);
    public static final FieldWrapper PLAYER_ACTIVE_CONTAINER = new FieldWrapper(ClassWrappers.ENTITY_HUMAN, "activeContainer", null, Version.V1_16_R3, PLAYER_ACTIVE_CONTAINER_V1171);

    private static final FieldWrapper PLAYER_CONNECTION_V1201 = new FieldWrapper(ClassWrappers.ENTITY_PLAYER, "c", Version.V1_20_R1, Version.V1_20_R3);
    private static final FieldWrapper PLAYER_CONNECTION_V1171 = new FieldWrapper(ClassWrappers.ENTITY_PLAYER, "b", Version.V1_17_R1, Version.V1_19_R3, PLAYER_CONNECTION_V1201);
    public static final FieldWrapper PLAYER_CONNECTION = new FieldWrapper(ClassWrappers.ENTITY_PLAYER, "playerConnection", null, Version.V1_16_R3, PLAYER_CONNECTION_V1171);

    private static final FieldWrapper ENTITY_WORLD_V1201 = new FieldWrapper(ClassWrappers.ENTITY, "t", Version.V1_20_R1, Version.V1_20_R3);
    private static final FieldWrapper ENTITY_WORLD_V1193 = new FieldWrapper(ClassWrappers.ENTITY, "H", Version.V1_19_R3, Version.V1_19_R3, ENTITY_WORLD_V1201);
    private static final FieldWrapper ENTITY_WORLD_V1182 = new FieldWrapper(ClassWrappers.ENTITY, "s", Version.V1_18_R2, Version.V1_19_R2, ENTITY_WORLD_V1193);
    private static final FieldWrapper ENTITY_WORLD_V1171 = new FieldWrapper(ClassWrappers.ENTITY, "t", Version.V1_17_R1, Version.V1_18_R1, ENTITY_WORLD_V1182);
    public static final FieldWrapper ENTITY_WORLD = new FieldWrapper(ClassWrappers.ENTITY, "world", null, Version.V1_16_R3, ENTITY_WORLD_V1171);

    private static final FieldWrapper CONTAINER_WINDOW_ID_V1171 = new FieldWrapper(ClassWrappers.CONTAINER, "j", Version.V1_17_R1, Version.V1_20_R3);
    public static final FieldWrapper CONTAINER_WINDOW_ID = new FieldWrapper(ClassWrappers.CONTAINER, "windowId", null, Version.V1_16_R3, CONTAINER_WINDOW_ID_V1171);

    private static final FieldWrapper CONTAINERS_ANVIL_V1204 = new FieldWrapper(ClassWrappers.CONTAINERS, "i", Version.V1_20_R2, Version.V1_20_R3);
    private static final FieldWrapper CONTAINERS_ANVIL_V1201 = new FieldWrapper(ClassWrappers.CONTAINERS, "h", Version.V1_16_R3, Version.V1_20_R2, CONTAINERS_ANVIL_V1204);
    public static final FieldWrapper CONTAINERS_ANVIL = new FieldWrapper(ClassWrappers.CONTAINERS, "ANVIL", Version.V1_14_R1, Version.V1_16_R3, CONTAINERS_ANVIL_V1201);

}
