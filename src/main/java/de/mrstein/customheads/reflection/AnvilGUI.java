package de.mrstein.customheads.reflection;

import de.mrstein.customheads.CustomHeads;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

/**
 * @author chasechocolate (Main Class), PhillipsNonstrum (Reflection)
 * @version 1.2
 * Modified for some extra Stuff
 */
public class AnvilGUI {

    private HashMap<AnvilSlot, ItemStack> items = new HashMap<>();

    private Inventory inv;
    private Player player;
    private ItemStack currentitem = null;

    private AnvilClickEventHandler handler;
    private Listener listener;

    public AnvilGUI(final Player player, final AnvilClickEventHandler anvilhandler) {
        this.player = player;
        handler = anvilhandler;
        this.listener = new Listener() {
            @EventHandler
            public void anvilClick(InventoryClickEvent e) {
                if (e.getWhoClicked() instanceof Player) {
                    if (e.getClickedInventory() == null) return;
                    if (e.getClickedInventory().equals(inv)) {
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
                        if (cE.getWillDestroy()) {
                            destroy();
                        }
                    }
                }
            }

            @EventHandler
            public void anvilClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inv = event.getInventory();
                    if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                        player.setLevel(player.getLevel() - 1);
                    }
                    if (inv.equals(AnvilGUI.this.inv)) {
                        inv.clear();
                        destroy();
                    }
                }
            }

            @EventHandler
            public void playerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(getPlayer())) {
                    if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                        player.setLevel(player.getLevel() - 1);
                    }
                    destroy();
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
            Object container = getClassbyName("ContainerAnvil").getConstructor(getClassbyName("PlayerInventory"), getClassbyName("World"), getClassbyName("BlockPosition"), getClassbyName("EntityHuman")).newInstance(p.getClass().getField("inventory").get(p), p.getClass().getField("world").get(p), getClassbyName("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0), p);
            getClassbyName("Container").getField("checkReachable").set(container, false);
            Object bukkitView = container.getClass().getMethod("getBukkitView").invoke(container);
            inv = (Inventory) bukkitView.getClass().getMethod("getTopInventory").invoke(bukkitView);
            for (AnvilSlot slot : items.keySet()) {
                inv.setItem(slot.getSlot(), items.get(slot));
            }
            int c = (int) p.getClass().getMethod("nextContainerCounter").invoke(p);
            Constructor<?> chatMessageConstructor = getClassbyName("ChatMessage").getConstructor(String.class, Object[].class);
            Object connection = p.getClass().getField("playerConnection").get(p);
            Object packet = getClassbyName("PacketPlayOutOpenWindow").getConstructor(int.class, String.class, getClassbyName("IChatBaseComponent"), int.class).newInstance(c, "minecraft:anvil", chatMessageConstructor.newInstance("Repairing", new Object[]{}), 0);
            connection.getClass().getMethod("sendPacket", getClassbyName("Packet")).invoke(connection, packet);
            Field activeContainerField = getClassbyName("EntityHuman").getDeclaredField("activeContainer");
            activeContainerField.setAccessible(true);
            activeContainerField.set(p, container);
            getClassbyName("Container").getField("windowId").set(activeContainerField.get(p), c);
        } catch (Exception e) {
            e.printStackTrace();
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
    }

    public class AnvilClickEvent {
        private AnvilSlot slot;
        private String name;
        private boolean destroy = false;
        private boolean takeout = false;
        private boolean close = false;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;
        }

        public AnvilSlot getSlot() {
            return slot;
        }

        public String getName() {
            return name;
        }

        public boolean getWillDestroy() {
            return destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }

        public void setCanTakeOut(boolean takeout) {
            this.takeout = !takeout;
        }

        public boolean canTakeOut() {
            return takeout;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean willClose() {
            return this.close;
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
            return inv.getItem(slot.getSlot());
        }

        public void setCancelled(boolean cancel) {
            this.close = !cancel;
            this.destroy = !cancel;
            this.takeout = !cancel;
        }

        public void setSlot(AnvilSlot slot, ItemStack item) {
            inv.setItem(2, item);
            player.updateInventory();
        }
    }
}