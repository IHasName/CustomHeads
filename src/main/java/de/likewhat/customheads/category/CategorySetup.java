package de.likewhat.customheads.category;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.ItemEditor;
import de.likewhat.customheads.utils.Utils;
import de.likewhat.customheads.utils.reflection.AnvilGUI;
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

import java.util.Arrays;
import java.util.HashMap;

/*
 *  Project: CustomHeads in CategorySetup
 *     by LikeWhat
 *
 *  created on 23.09.2019 at 10:33
 */
public class CategorySetup {

    private Category category;
    private Inventory setupInventory;

    private ItemStack changeNameItem;
    private ItemStack changeIconItem;
    private ItemStack changePriceItem;
    private ItemStack changeDescriptionItem;

    private int price;
    private String name;
    private ItemStack icon;
    private String description;

    public static final HashMap<Player, CategorySetup> OPENED_SETUPS = new HashMap<>();

    public static CategorySetup createCategory(String name) {
        return new CategorySetup(CustomHeads.getCategoryManager().createCategory(name));
    }

    private CategorySetup(Category category) {
        this.category = category;
    }

    private void registerListeners() {
        if(setupInventory != null)
            return;

        setupInventory = Bukkit.createInventory(null, 9*4, "Category Setup " + category.getPlainName());
        ItemStack createItem = new ItemEditor(Material.SKULL_ITEM, 3).setDisplayName("§aCreate").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMwZjQ1MzdkMjE0ZDM4NjY2ZTYzMDRlOWM4NTFjZDZmN2U0MWEwZWI3YzI1MDQ5YzlkMjJjOGM1ZjY1NDVkZiJ9fX0=").getItem();
        ItemStack cancelItem = new ItemEditor(Material.SKULL_ITEM, 3).setDisplayName("§cCancel").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2Nzg3YmEzMjU2NGU3YzJmM2EwY2U2NDQ5OGVjYmIyM2I4OTg0NWU1YTY2YjVjZWM3NzM2ZjcyOWVkMzcifX19").getItem();
        setupInventory.setItem(27, cancelItem);
        setupInventory.setItem(35, createItem);

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if(!event.getClickedInventory().equals(setupInventory)) {
                    return;
                }
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                ItemStack clicked = event.getCurrentItem();
                if(clicked.equals(changeIconItem)) {
                    if(event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                        changeIcon(event.getCursor());
                        player.setItemOnCursor(null);
                        player.updateInventory();
                    }
                }
                if(clicked.equals(changePriceItem)) {
                    AnvilGUI anvilGUI = new AnvilGUI(player, "Category Setup", new AnvilGUI.AnvilClickEventHandler() {
                        public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilClickEvent) {
                            event.setCancelled(true);

                            if(!Utils.isNumber(anvilClickEvent.getName())) {
                                return;
                            }
                            changePrice(Integer.parseInt(anvilClickEvent.getName()));
                            openInventory(player);
                        }

                        public void onAnvilClose() {

                        }
                    });
                    anvilGUI.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName(String.valueOf(price)).getItem());
                    anvilGUI.open();
                    player.updateInventory();
                }
                if(clicked.equals(changeNameItem)) {
                    AnvilGUI anvilGUI = new AnvilGUI(player, "Category Setup", new AnvilGUI.AnvilClickEventHandler() {
                        public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilClickEvent) {
                            event.setCancelled(true);
                            if(anvilClickEvent.getName().length() > 16 || anvilClickEvent.getName().isEmpty()) {
                                return;
                            }
                            changeName(anvilClickEvent.getName());
                            openInventory(player);
                        }

                        public void onAnvilClose() {

                        }
                    });
                    anvilGUI.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName(name).getItem());
                    anvilGUI.open();
                    player.updateInventory();
                }
                if(clicked.equals(changeDescriptionItem)) {
                    AnvilGUI anvilGUI = new AnvilGUI(player, "Category Setup", new AnvilGUI.AnvilClickEventHandler() {
                        public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilClickEvent) {
                            event.setCancelled(true);
                            if(anvilClickEvent.getName().length() > 32 || anvilClickEvent.getName().isEmpty()) {
                                return;
                            }
                            changeDescription(anvilClickEvent.getName());
                            openInventory(player);
                        }

                        public void onAnvilClose() {

                        }
                    });
                    anvilGUI.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName(description).getItem());
                    anvilGUI.open();
                    player.updateInventory();
                }
                if(clicked.equals(cancelItem)) {
                    HandlerList.unregisterAll(this);
                    player.closeInventory();
                }
                if(clicked.equals(createItem)) {
                    category.setName(name);
                    category.setPrice(price);
                    category.setIcons(Arrays.asList(new ItemEditor(icon).setDisplayName(name).setLore(description).getItem()));
                    CustomHeads.getCategoryManager().updateCategory(category, CustomHeads.getLanguageManager().getCurrentLanguage());
                    CustomHeads.reload(null);
                    player.closeInventory();
                    player.sendMessage("Successfully created Category");
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if(!event.getInventory().equals(setupInventory)) {
                    return;
                }
                OPENED_SETUPS.remove(event.getPlayer());
                HandlerList.unregisterAll(this);
            }
        }, CustomHeads.getInstance());
    }

    private void changeIcon(ItemStack newIcon) {
        setupInventory.setItem(10, changeIconItem = icon = new ItemEditor(newIcon).setDisplayName("§aChange Icon").setLore("§7Drag your Icon here\n§7to change it").getItem());
    }

    private void changeName(String newName) {
        setupInventory.setItem(12, changeNameItem = new ItemEditor(Material.PAPER).setDisplayName("§aChange Name").setLore("§7Current:\n§e" + (name = newName)).getItem());
    }

    private void changePrice(int newPrice) {
        setupInventory.setItem(14, changePriceItem = new ItemEditor(Material.EMERALD).setDisplayName("§aChange Price").setLore("§7Current:\n§e" + (price = newPrice)).getItem());
    }

    private void changeDescription(String newDescription) {
        setupInventory.setItem(16, changeDescriptionItem = new ItemEditor(Material.PAPER).setDisplayName("§aChange Description").setLore("§7Current:\n§e" + (description = newDescription)).getItem());
    }

    public void openInventory(Player player) {
        registerListeners();
        changeIcon(new ItemStack(Material.BARRIER));
        changeName(category.getName());
        changePrice(category.getPrice());
        changeDescription("Default Description");

        OPENED_SETUPS.put(player, this);
        player.openInventory(setupInventory);
    }
}

