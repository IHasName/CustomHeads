package de.likewhat.customheads.utils.reflection;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.Utils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.ClassWrappers;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.FieldWrappers;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.MethodWrappers;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * @author chasechocolate (Main Class), PhillipsNonstrum (Reflection)
 * @version 1.4
 * Modified for some extra Stuff and 1.14+ Support
 */
public class AnvilGUI {

    private HashMap<AnvilSlot, ItemStack> items = new HashMap<>();
    private AnvilClickEventHandler handler;
    private ItemStack currentItem = null;
    private Inventory inventory;
    private Listener listener;
    @Getter
    private Player player;
    private final String title;

    public AnvilGUI(final Player player, String title, final AnvilClickEventHandler anvilHandler) {
        this.player = player;
        this.title = title;
        this.handler = anvilHandler;
        this.listener = new Listener() {
            @EventHandler
            public void anvilClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {
                    if (event.getClickedInventory() == null) return;
                    if (event.getClickedInventory().equals(inventory)) {
                        ItemStack item = event.getCurrentItem();
                        int slot = event.getRawSlot();
                        String name = "";
                        if (item != null) {
                            currentItem = event.getCurrentItem();
                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta.hasDisplayName()) {
                                    name = meta.getDisplayName();
                                }
                            }
                        }
                        AnvilClickEvent anvilClickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);
                        handler.onAnvilClick(anvilClickEvent);
                        event.setCancelled(!anvilClickEvent.canTakeOut());
                        if (anvilClickEvent.willClose()) {
                            event.getWhoClicked().closeInventory();
                        }
                        if (anvilClickEvent.isWillDestroy()) {
                            destroy();
                        }
                    }
                }
            }

            @EventHandler
            public void anvilClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inventory = event.getInventory();
                    if (inventory.equals(AnvilGUI.this.inventory)) {
                        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                            player.setLevel(player.getLevel() - 1);
                        }
                        handler.onAnvilClose();
                        inventory.clear();
                        destroy();
                    }
                }
            }

            @EventHandler
            public void playerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(getPlayer())) {
                    if (player.getOpenInventory() != null) {
                        if (player.getOpenInventory().getTopInventory().equals(AnvilGUI.this.inventory)) {
                            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                                player.setLevel(player.getLevel() - 1);
                            }
                            handler.onAnvilClose();
                            destroy();
                        }
                    }
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, CustomHeads.getInstance());
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    public void open() {
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            player.setLevel(player.getLevel() + 1);
        }
        try {
            Version currentVersion = Version.getCurrentVersion();
            Object playerHandle = ReflectionUtils.getPlayerHandle(player);
            Class<?> playerHandleClass = playerHandle.getClass();
            int nextContainerId = (int) playerHandleClass.getMethod("nextContainerCounter").invoke(playerHandle);

            Location location = player.getLocation();
            Object titleObject = MethodWrappers.CHAT_COMPONENT_SERIALIZE.invokeOn(null, String.format(Utils.TEXT_JSON_FORMAT, title));
            Class<?> playerInventoryClass = ReflectionUtils.getMCServerClassByName("PlayerInventory", "world.entity.player");

            Field playerInventoryField = FieldWrappers.PLAYER_INVENTORY.resolve();
            Field activeContainerField = FieldWrappers.PLAYER_ACTIVE_CONTAINER.resolve();
            Field windowIdField = FieldWrappers.CONTAINER_WINDOW_ID.resolve();

            Object playerWorld = FieldWrappers.ENTITY_WORLD.getInstance(playerHandle);
            Object containerAnvil;
            Object packet;
            switch (currentVersion) {
                case V1_8_R1:
                case V1_8_R2:
                case V1_8_R3:
                case V1_9_R1:
                case V1_9_R2:
                case V1_10_R1:
                case V1_11_R1:
                case V1_12_R1:
                case V1_13_R1:
                case V1_13_R2:
                    containerAnvil = ClassWrappers.CONTAINER_ANVIL.resolve().getConstructor(playerInventoryClass, ReflectionUtils.getMCServerClassByName("World"), ReflectionUtils.getMCServerClassByName("BlockPosition"), ReflectionUtils.getMCServerClassByName("EntityHuman")).newInstance(playerHandleClass.getField("inventory").get(playerHandle), playerWorld, ReflectionUtils.getMCServerClassByName("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ()), playerHandle);
                    packet = ClassWrappers.PACKET_PLAY_OUT_OPEN_WINDOW.resolve().getConstructor(int.class, String.class, ReflectionUtils.getMCServerClassByName("IChatBaseComponent"), int.class).newInstance(nextContainerId, "minecraft:anvil", titleObject, 0);
                    break;
                default:
                case LATEST:
                    LoggingUtils.logOnce(Level.WARNING, "Current MC Version (" + Version.getCurrentVersionRaw() + ") may not work with AnvilGUI");
                case V1_14_R1:
                case V1_15_R1:
                case V1_16_R1:
                case V1_16_R2:
                case V1_16_R3:
                case V1_17_R1:
                case V1_18_R1:
                case V1_18_R2:
                case V1_19_R1:
                case V1_19_R2:
                case V1_19_R3:
                case V1_20_R1: {
                    Class<?> containersClass = ReflectionUtils.getMCServerClassByName("Containers", "world.inventory");
                    Class<?> containerAccessClass = ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory");
                    containerAnvil = ClassWrappers.CONTAINER_ANVIL.resolve().getConstructor(int.class, playerInventoryClass, containerAccessClass).newInstance(nextContainerId, ReflectionUtils.getFieldValue(playerInventoryField, playerHandle), MethodWrappers.CONTAINER_ACCESS_AT.invokeOn(containerAccessClass, playerWorld, ReflectionUtils.getMCServerClassByName("BlockPosition", "core").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
                    packet = ClassWrappers.PACKET_PLAY_OUT_OPEN_WINDOW.resolve().getConstructor(int.class, containersClass, ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat")).newInstance(nextContainerId, FieldWrappers.CONTAINERS_ANVIL.getInstance(containersClass), titleObject);
                    break;
                }
            }
            Object bukkitView = containerAnvil.getClass().getMethod("getBukkitView").invoke(containerAnvil);
            inventory = (Inventory) bukkitView.getClass().getMethod("getTopInventory").invoke(bukkitView);
            for (AnvilSlot slot : items.keySet()) {
                inventory.setItem(slot.getSlot(), items.get(slot));
            }
            ReflectionUtils.setField(containerAnvil, "checkReachable", false);
            ReflectionUtils.sendPacket(packet, player);

            ReflectionUtils.setField(playerHandle, activeContainerField, containerAnvil);
            ReflectionUtils.setField(activeContainerField.get(playerHandle), windowIdField, nextContainerId);

            // Thanks to JordanPlayz for helping me find this Issue with the GUI
            if(currentVersion.isOlderThan(Version.V1_17_R1)) {
                ClassWrappers.CONTAINER.resolve().getMethod("addSlotListener", ReflectionUtils.getMCServerClassByName("ICrafting")).invoke(containerAnvil, playerHandle);
            } else if (currentVersion.isOlderThan(Version.V1_18_R1)) {
                playerHandle.getClass().getMethod("initMenu", ClassWrappers.CONTAINER.resolve()).invoke(playerHandle, containerAnvil);
            } else {
                playerHandle.getClass().getMethod("a", ClassWrappers.CONTAINER.resolve()).invoke(playerHandle, containerAnvil);
            }
        } catch (Exception exception) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to open AnvilGUI", exception);
        }
    }

    private void destroy() {
        player = null;
        handler = null;
        items = null;
        HandlerList.unregisterAll(listener);
        listener = null;
    }

    @Getter
    public enum AnvilSlot {
        INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);
        private final int slot;

        AnvilSlot(int slot) {
            this.slot = slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }
            return null;
        }

    }

    public interface AnvilClickEventHandler {
        void onAnvilClick(AnvilClickEvent event);
        void onAnvilClose();
    }

    @Getter
    public class AnvilClickEvent {
        private final AnvilSlot slot;
        private final String name;
        @Setter
        private boolean willDestroy = false;
        private boolean takeout = false;
        @Setter
        private boolean willClose = false;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;
        }

        public void setCanTakeOut(boolean takeout) {
            this.takeout = !takeout;
        }

        public boolean canTakeOut() {
            return takeout;
        }

        public boolean willClose() {
            return this.willClose;
        }

        public void update() {
            player.updateInventory();
        }

        public Player getPlayer() {
            return player;
        }

        public ItemStack getItem() {
            return currentItem;
        }

        public ItemStack getItem(AnvilSlot slot) {
            return inventory.getItem(slot.getSlot());
        }

        public void setCancelled(boolean cancel) {
            this.willClose = !cancel;
            this.willDestroy = !cancel;
            this.takeout = !cancel;
        }
    }
}