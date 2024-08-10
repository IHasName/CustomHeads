package de.likewhat.customheads.utils;

/*
 *  Project: CustomHeads in InteractiveDialog
 *     by LikeWhat
 *
 *  created on 06.02.2020 at 23:24
 */

import de.likewhat.customheads.CustomHeads;
import org.bukkit.Bukkit;
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

    private static final ItemEditor YES_ITEM_BASE = Utils.createPlayerHeadItemEditor().setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYxZTViMzMzYzJhMzg2OGJiNmE1OGI2Njc0YTI2MzkzMjM4MTU3MzhlNzdlMDUzOTc3NDE5YWYzZjc3In19fQ==");
    private static final ItemEditor NO_ITEM_BASE = Utils.createPlayerHeadItemEditor().setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGJhYzc3NTIwYjllZWU2NTA2OGVmMWNkOGFiZWFkYjAxM2I0ZGUzOTUzZmQyOWFjNjhlOTBlNDg2NjIyNyJ9fX0=");

    private Inventory dialog;
    private ItemStack yesItem;
    private ItemStack noItem;

    private Listener listener;

    InteractiveDialog(String title, Consumer<Boolean> callback, String[] yesLore, String[] noLore, ItemStack middleItem) {
        this.dialog = Bukkit.createInventory(new CustomHeadsInventoryHolder.BaseHolder("interactive_dialog", null), 9 * 3, title);

        this.yesItem = new ItemEditor(YES_ITEM_BASE.getItem()).setDisplayName("§a" + CustomHeads.getLanguageManager().YES).setLore(yesLore).getItem();
        this.noItem = new ItemEditor(NO_ITEM_BASE.getItem()).setDisplayName("§c" + CustomHeads.getLanguageManager().NO).setLore(noLore).getItem();
        this.dialog.setItem(11, this.yesItem);
        this.dialog.setItem(13, middleItem);
        this.dialog.setItem(15, this.noItem);

        this.listener = new Listener() {
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
        Bukkit.getPluginManager().registerEvents(this.listener, CustomHeads.getInstance());
    }

    private void destroy() {
        this.dialog = null;
        this.yesItem = null;
        this.noItem = null;
        HandlerList.unregisterAll(this.listener);
        this.listener = null;
    }

    public void showTo(Player player) {
        player.openInventory(this.dialog);
    }

}
