package de.likewhat.customheads.utils;

/*
 *  Project: CustomHeads in InteractiveDialog
 *     by LikeWhat
 *
 *  created on 06.02.2020 at 23:24
 */

import de.likewhat.customheads.CustomHeads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InteractiveDialog {

    private Inventory dialog;
    private ItemStack yesItem;
    private ItemStack noItem;

    private Listener listener;

    InteractiveDialog(String title, Consumer<Boolean> callback, String[] yesLore, String[] noLore, ItemStack middleItem) {
        dialog = Bukkit.createInventory(null, 9 * 3, title);
        yesItem = new ItemEditor(Material.SKULL_ITEM,  3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYxZTViMzMzYzJhMzg2OGJiNmE1OGI2Njc0YTI2MzkzMjM4MTU3MzhlNzdlMDUzOTc3NDE5YWYzZjc3In19fQ==").setDisplayName("§a" + CustomHeads.getLanguageManager().YES).setLore(yesLore).getItem();
        noItem = new ItemEditor(Material.SKULL_ITEM,  3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGJhYzc3NTIwYjllZWU2NTA2OGVmMWNkOGFiZWFkYjAxM2I0ZGUzOTUzZmQyOWFjNjhlOTBlNDg2NjIyNyJ9fX0=").setDisplayName("§c" + CustomHeads.getLanguageManager().NO).setLore(noLore).getItem();
        dialog.setItem(11, yesItem);
        dialog.setItem(13, middleItem);
        dialog.setItem(15, noItem);

        listener = new Listener() {
            @EventHandler
            public void onEvent(InventoryClickEvent event) {
                if(event.getView().getTopInventory() == dialog) {
                    ItemStack currentItem = event.getCurrentItem();
                    if (yesItem.equals(currentItem)) {
                        callback.accept(true);
                    } else if (noItem.equals(currentItem)) {
                        callback.accept(false);
                    }
                }
            }

            @EventHandler
            public void onEvent(InventoryCloseEvent event) {
                if(event.getView().getTopInventory() == dialog) {
                    destroy();
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, CustomHeads.getInstance());
    }

    private void destroy() {
        dialog = null;
        yesItem = null;
        noItem = null;
        HandlerList.unregisterAll(listener);
        listener = null;
    }

    public void showTo(Player player) {
        player.openInventory(dialog);
    }

}
