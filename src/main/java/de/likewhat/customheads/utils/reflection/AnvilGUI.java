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

            int nextContainerId = (int) p.getClass().getMethod("nextContainerCounter").invoke(p);

            Location location = player.getLocation();
            Object container = NBTTagUtils.MC_VERSION > 13 ?
                    getClassbyName("ContainerAnvil").getConstructor(int.class, getClassbyName("PlayerInventory"), getClassbyName("ContainerAccess")).newInstance(nextContainerId, p.getClass().getField("inventory").get(p), getClassbyName("ContainerAccess").getMethod("at", getClassbyName("World"), getClassbyName("BlockPosition")).invoke(getClassbyName("ContainerAccess"), p.getClass().getField("world").get(p), getClassbyName("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ()))):
                    getClassbyName("ContainerAnvil").getConstructor(getClassbyName("PlayerInventory"), getClassbyName("World"), getClassbyName("BlockPosition"), getClassbyName("EntityHuman")).newInstance(p.getClass().getField("inventory").get(p), p.getClass().getField("world").get(p), getClassbyName("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ()), p);;

            Object bukkitView = container.getClass().getMethod("getBukkitView").invoke(container);
            inventory = (Inventory) bukkitView.getClass().getMethod("getTopInventory").invoke(bukkitView);
            for (AnvilSlot slot : items.keySet()) {
                inventory.setItem(slot.getSlot(), items.get(slot));
            }

            getClassbyName("Container").getField("checkReachable").set(container, false);
            Constructor<?> chatMessageConstructor = getClassbyName("ChatMessage").getConstructor(String.class, Object[].class);
            Object connection = p.getClass().getField("playerConnection").get(p);
            Object packet = NBTTagUtils.MC_VERSION > 13 ?
                    getClassbyName("PacketPlayOutOpenWindow").getConstructor(int.class, getClassbyName("Containers"), getClassbyName("IChatBaseComponent")).newInstance(nextContainerId, getClassbyName("Containers").getField("ANVIL").get(getClassbyName("Containers")), chatMessageConstructor.newInstance(title, new Object[]{})) :
                    getClassbyName("PacketPlayOutOpenWindow").getConstructor(int.class, String.class, getClassbyName("IChatBaseComponent"), int.class).newInstance(nextContainerId, "minecraft:anvil", chatMessageConstructor.newInstance(title, new Object[]{}), 0);
            connection.getClass().getMethod("sendPacket", getClassbyName("Packet")).invoke(connection, packet);
            Field activeContainerField = getClassbyName("EntityHuman").getDeclaredField("activeContainer");
            activeContainerField.setAccessible(true);
            activeContainerField.set(p, container);
            Field windowIdField = getClassbyName("Container").getField("windowId");
            windowIdField.setAccessible(true);
            windowIdField.set(activeContainerField.get(p), nextContainerId);
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

        public void setSlot(AnvilSlot slot, ItemStack item) {
            inventory.setItem(2, item);
            player.updateInventory();
        }
    }
}