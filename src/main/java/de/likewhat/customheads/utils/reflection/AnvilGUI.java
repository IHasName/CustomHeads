package de.likewhat.customheads.utils.reflection;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.collections.ReflectionClassCollection;
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

import java.lang.reflect.Constructor;
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

    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    public void open() {
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            player.setLevel(player.getLevel() + 1);
        }
        try {

            Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
            Class<?> playerHandleClass = playerHandle.getClass();
            int nextContainerId = (int) playerHandleClass.getMethod("nextContainerCounter").invoke(playerHandle);

            Location location = player.getLocation();
            Constructor<?> chatMessageConstructor = ReflectionUtils.getMCServerClassByName("ChatMessage", "network.chat").getConstructor(String.class, Object[].class);
            Object container;
            Object packet;
            Field windowIdField;
            Field activeContainerField;
            Class<?> containersClass;
            switch (ReflectionUtils.MC_VERSION) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    container = ReflectionClassCollection.CONTAINER_ANVIL.getConstructor(ReflectionUtils.getMCServerClassByName("PlayerInventory"), ReflectionUtils.getMCServerClassByName("World"), ReflectionUtils.getMCServerClassByName("BlockPosition"), ReflectionUtils.getMCServerClassByName("EntityHuman")).newInstance(playerHandleClass.getField("inventory").get(playerHandle), playerHandleClass.getField("world").get(playerHandle), ReflectionUtils.getMCServerClassByName("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ()), playerHandle);
                    packet = ReflectionClassCollection.PACKET_PLAY_OUT_OPEN_WINDOW.getConstructor(int.class, String.class, ReflectionUtils.getMCServerClassByName("IChatBaseComponent"), int.class).newInstance(nextContainerId, "minecraft:anvil", chatMessageConstructor.newInstance(title, new Object[]{}), 0);
                    windowIdField = ReflectionClassCollection.CONTAINER.getField("windowId");
                    activeContainerField = ReflectionUtils.getFieldDynamic(playerHandleClass, "activeContainer");
                    break;
                case 14:
                case 15:
                case 16:
                    containersClass = ReflectionUtils.getMCServerClassByName("Containers", "world.inventory");
                    container = ReflectionClassCollection.CONTAINER_ANVIL.getConstructor(int.class, ReflectionUtils.getMCServerClassByName("PlayerInventory", "world.entity.player"), ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory")).newInstance(nextContainerId, playerHandleClass.getField("inventory").get(playerHandle), ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory").getMethod("at", ReflectionUtils.getMCServerClassByName("World", "world.level"), ReflectionUtils.getMCServerClassByName("BlockPosition", "core")).invoke(ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory"), playerHandleClass.getField("world").get(playerHandle), ReflectionUtils.getMCServerClassByName("BlockPosition", "core").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
                    packet = ReflectionClassCollection.PACKET_PLAY_OUT_OPEN_WINDOW.getConstructor(int.class, containersClass, ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat")).newInstance(nextContainerId, containersClass.getField("ANVIL").get(containersClass), chatMessageConstructor.newInstance(title, new Object[]{}));
                    windowIdField = ReflectionClassCollection.CONTAINER.getField("windowId");
                    activeContainerField = ReflectionUtils.getFieldDynamic(playerHandleClass, "activeContainer");
                    break;
                default:
                    LoggingUtils.logOnce(Level.WARNING, "Current MC Version (" + Version.getRawVersion() + ") may not work with AnvilGUI");
                case 17:
                //case 18:
                    containersClass = ReflectionUtils.getMCServerClassByName("Containers", "world.inventory");
                    Class<?> containerAccessClass = ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory");
                    container = ReflectionClassCollection.CONTAINER_ANVIL.getConstructor(int.class, ReflectionUtils.getMCServerClassByName("PlayerInventory", "world.entity.player"), containerAccessClass).newInstance(nextContainerId, playerHandleClass.getMethod("getInventory").invoke(playerHandle), containerAccessClass.getMethod("at", ReflectionUtils.getMCServerClassByName("World", "world.level"), ReflectionUtils.getMCServerClassByName("BlockPosition", "core")).invoke(containerAccessClass, playerHandleClass.getField("t").get(playerHandle), ReflectionUtils.getMCServerClassByName("BlockPosition", "core").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
                    packet = ReflectionClassCollection.PACKET_PLAY_OUT_OPEN_WINDOW.getConstructor(int.class, containersClass, ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat")).newInstance(nextContainerId, containersClass.getField("h").get(containersClass), chatMessageConstructor.newInstance(title, new Object[]{}));
                    windowIdField = ReflectionClassCollection.CONTAINER.getDeclaredField("j");
                    activeContainerField = playerHandleClass.getField("bV");
                    break;
            }
            Object bukkitView = container.getClass().getMethod("getBukkitView").invoke(container);
            inventory = (Inventory) bukkitView.getClass().getMethod("getTopInventory").invoke(bukkitView);
            for (AnvilSlot slot : items.keySet()) {
                inventory.setItem(slot.getSlot(), items.get(slot));
            }
            ReflectionUtils.setField(container, "checkReachable", false);
            ReflectionUtils.sendPacket(packet, player);

            ReflectionUtils.setField(playerHandle, activeContainerField, container);
            ReflectionUtils.setField(activeContainerField.get(playerHandle), windowIdField, nextContainerId);

            // Thanks to JordanPlayz for helping me find this Issue with the GUI
            if(ReflectionUtils.MC_VERSION >= 17) {
                playerHandle.getClass().getMethod("initMenu", ReflectionClassCollection.CONTAINER).invoke(playerHandle, container);
            } else {
                ReflectionClassCollection.CONTAINER.getMethod("addSlotListener", ReflectionUtils.getMCServerClassByName("ICrafting")).invoke(container, playerHandle);
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

        public int getSlot() {
            return slot;
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