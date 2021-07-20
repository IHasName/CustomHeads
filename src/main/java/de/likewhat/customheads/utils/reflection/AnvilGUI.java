package de.likewhat.customheads.utils.reflection;

import de.likewhat.customheads.CustomHeads;
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

    private static final Class<?> containerAnvilClass;
    private static final Class<?> packetPlayOutOpenWindowClass;
    private static final Class<?> containerClass;

    static {
        containerAnvilClass = ReflectionUtils.getMCServerClassByName("ContainerAnvil", "world.inventory");
        packetPlayOutOpenWindowClass = ReflectionUtils.getMCServerClassByName("PacketPlayOutOpenWindow", "network.protocol.game");
        containerClass = ReflectionUtils.getMCServerClassByName("Container", "world.inventory");
    }

    private HashMap<AnvilSlot, ItemStack> items = new HashMap<>();
    private AnvilClickEventHandler handler;
    private ItemStack currentitem = null;
    private Inventory inventory;
    private Listener listener;
    private Player player;
    private String title;

    public AnvilGUI(final Player player, String title, final AnvilClickEventHandler anvilhandler) {
        this.player = player;
        this.title = title;
        handler = anvilhandler;
        this.listener = new Listener() {
            @EventHandler
            public void anvilClick(InventoryClickEvent e) {
                if (e.getWhoClicked() instanceof Player) {
                    if (e.getClickedInventory() == null) return;
                    if (e.getClickedInventory().equals(inventory)) {
                        ItemStack item = e.getCurrentItem();
                        int slot = e.getRawSlot();
                        String name = "";
                        if (item != null) {
                            currentitem = e.getCurrentItem();
                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta.hasDisplayName()) {
                                    name = meta.getDisplayName();
                                }
                            }
                        }
                        AnvilClickEvent cE = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);
                        handler.onAnvilClick(cE);
                        e.setCancelled(!cE.canTakeOut());
                        if (cE.willClose()) {
                            e.getWhoClicked().closeInventory();
                        }
                        if (cE.isWillDestroy()) {
                            destroy();
                        }
                    }
                }
            }

            @EventHandler
            public void anvilClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inv = event.getInventory();
                    if (inv.equals(AnvilGUI.this.inventory)) {
                        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                            player.setLevel(player.getLevel() - 1);
                        }
                        handler.onAnvilClose();
                        inv.clear();
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

    private static Class<?> getClassbyName(String ClassName) {
        try {
            return Class.forName("net.minecraft.server." + CustomHeads.version + "." + ClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

            Object p = player.getClass().getMethod("getHandle").invoke(player);
            Class<?> playerHandleClass = p.getClass();
            int nextContainerId = (int) playerHandleClass.getMethod("nextContainerCounter").invoke(p);

            Location location = player.getLocation();
            Constructor<?> chatMessageConstructor = ReflectionUtils.getMCServerClassByName("ChatMessage", "network.chat").getConstructor(String.class, Object[].class);
            Object container;
            Object packet;
            Object playerConnection;
            Field windowIdField;
            Field activeContainerField;
            switch (ReflectionUtils.MC_VERSION) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    container = containerAnvilClass.getConstructor(getClassbyName("PlayerInventory"), getClassbyName("World"), getClassbyName("BlockPosition"), getClassbyName("EntityHuman")).newInstance(playerHandleClass.getField("inventory").get(p), playerHandleClass.getField("world").get(p), getClassbyName("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ()), p);
                    packet = packetPlayOutOpenWindowClass.getConstructor(int.class, String.class, getClassbyName("IChatBaseComponent"), int.class).newInstance(nextContainerId, "minecraft:anvil", chatMessageConstructor.newInstance(title, new Object[]{}), 0);
                    playerConnection = playerHandleClass.getField("playerConnection").get(p);
                    windowIdField = containerClass.getField("windowId");
                    activeContainerField = playerHandleClass.getDeclaredField("activeContainer");
                    break;
                case 14:
                case 15:
                case 16:
                    container = containerAnvilClass.getConstructor(int.class, ReflectionUtils.getMCServerClassByName("PlayerInventory", "world.entity.player"), ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory")).newInstance(nextContainerId, playerHandleClass.getField("inventory").get(p), ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory").getMethod("at", ReflectionUtils.getMCServerClassByName("World", "world.level"), ReflectionUtils.getMCServerClassByName("BlockPosition", "core")).invoke(ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory"), playerHandleClass.getField("world").get(p), ReflectionUtils.getMCServerClassByName("BlockPosition", "core").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
                    packet = packetPlayOutOpenWindowClass.getConstructor(int.class, containerClass, ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat")).newInstance(nextContainerId, ReflectionUtils.getMCServerClassByName("Containers", "world.inventory").getField("ANVIL").get(ReflectionUtils.getMCServerClassByName("Containers", "world.inventory")), chatMessageConstructor.newInstance(title, new Object[]{}));
                    playerConnection = playerHandleClass.getField("playerConnection").get(p);
                    windowIdField = containerClass.getField("windowId");
                    activeContainerField = playerHandleClass.getDeclaredField("activeContainer");
                    break;
                case 17:
                    Class<?> containersClass = ReflectionUtils.getMCServerClassByName("Containers", "world.inventory");
                    Class<?> containerAccessClass = ReflectionUtils.getMCServerClassByName("ContainerAccess", "world.inventory");
                    container = containerAnvilClass.getConstructor(int.class, ReflectionUtils.getMCServerClassByName("PlayerInventory", "world.entity.player"), containerAccessClass).newInstance(nextContainerId, playerHandleClass.getMethod("getInventory").invoke(p), containerAccessClass.getMethod("at", ReflectionUtils.getMCServerClassByName("World", "world.level"), ReflectionUtils.getMCServerClassByName("BlockPosition", "core")).invoke(containerAccessClass, playerHandleClass.getField("t").get(p), ReflectionUtils.getMCServerClassByName("BlockPosition", "core").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
                    packet = packetPlayOutOpenWindowClass.getConstructor(int.class, containersClass, ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat")).newInstance(nextContainerId, containersClass.getField("h").get(containersClass), chatMessageConstructor.newInstance(title, new Object[]{}));
                    //playerConnection = playerHandleClass.getField("b").get(p);
                    windowIdField = containerClass.getDeclaredField("j");
                    activeContainerField = playerHandleClass.getField("bV");
                    break;
                default:
                    throw new UnsupportedOperationException("Current MC Version (" + CustomHeads.version + ") isn't supported for AnvilGUI");
            }
            Object bukkitView = container.getClass().getMethod("getBukkitView").invoke(container);
            inventory = (Inventory) bukkitView.getClass().getMethod("getTopInventory").invoke(bukkitView);
            for (AnvilSlot slot : items.keySet()) {
                inventory.setItem(slot.getSlot(), items.get(slot));
            }

            containerClass.getField("checkReachable").set(container, false);
            ReflectionUtils.sendPacket(packet, player);
            //playerConnection.getClass().getMethod("sendPacket", ReflectionUtils.getMCServerClassByName("Packet", "network.protocol")).invoke(playerConnection, packet);
            activeContainerField.setAccessible(true);
            activeContainerField.set(p, container);
            windowIdField.setAccessible(true);
            windowIdField.set(activeContainerField.get(p), nextContainerId);

            // Thanks to JordanPlayz for helping me find this Issue with the GUI
            if(ReflectionUtils.MC_VERSION >= 17) {
                p.getClass().getMethod("initMenu", containerClass).invoke(p, container);
            } else {
                containerClass.getMethod("addSlotListener", p.getClass()).invoke(container, p);
            }
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to open AnvilGUI", exception);
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
        private int slot;

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
        private AnvilSlot slot;
        private String name;
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
            return currentitem;
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