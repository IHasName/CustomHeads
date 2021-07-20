package de.likewhat.customheads.listener;

/*
 *  Project: CustomHeads in CategoryEditorListener
 *     by LikeWhat
 *
 *  created on 27.10.2019 at 15:42
 */

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.category.BaseCategory;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CategorySetup;
import de.likewhat.customheads.category.SubCategory;
import de.likewhat.customheads.utils.CategoryEditor;
import de.likewhat.customheads.utils.ItemEditor;
import de.likewhat.customheads.utils.Utils;
import de.likewhat.customheads.utils.reflection.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static de.likewhat.customheads.utils.Utils.hasPermission;

// Not implemented yet
public class CategoryEditorListener implements Listener {

    private HashMap<UUID, String> cachedPlayers = new HashMap<>();

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory() == null || event.getRawSlot() > event.getInventory().getSize() || event.getCurrentItem() == null || !hasPermission(player, "heads.admin"))
            return;
        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("cEditor")) {
            String[] args = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "cEditor") + 1).split("#>");

            player.sendMessage("CEditor: §7" + CustomHeads.getTagEditor().getTags(event.getCurrentItem()));

            switch (args[0]) {
                case "create":
                    event.setCancelled(true);
                    AnvilGUI anvilGUI = new AnvilGUI(player, "Name your Category", new AnvilGUI.AnvilClickEventHandler() {
                        public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilClickEvent) {
                            event.setCancelled(true);
                            if (anvilClickEvent.getName().length() > 16 || anvilClickEvent.getName().isEmpty()) {
                                return;
                            }
                            CategorySetup.createCategory(anvilClickEvent.getName()).openInventory(player);
                        }

                        public void onAnvilClose() {

                        }
                    });
                    anvilGUI.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName("Category Name").getItem());
                    anvilGUI.open();
                    player.updateInventory();
                    break;
                case "edit":
                    event.setCancelled(true);
                    if (cachedPlayers.containsKey(player.getUniqueId()) && cachedPlayers.get(player.getUniqueId()).contains("selectCategory")) {
                        String[] actionSplitted = cachedPlayers.get(player.getUniqueId()).split("#>");
                        Category category = CustomHeads.getCategoryManager().getCategory(actionSplitted[1]);
                        if (category != null) {
                            CategoryEditor editor = new CategoryEditor(category, player);
                            editor.showCategoryInfos();
                            cachedPlayers.remove(player.getUniqueId());
                        }
                    }
                    break;
                case "delete":
                    event.setCancelled(true);
                    if (args.length < 2) {
                        if (cachedPlayers.containsKey(player.getUniqueId()) && cachedPlayers.get(player.getUniqueId()).contains("selectCategory")) {
                            String[] actionSplitted = cachedPlayers.get(player.getUniqueId()).split("#>");
                            BaseCategory category = Utils.getBaseCategory(actionSplitted[1]);
                            player.openInventory(Utils.getDialog("Delete " + category.getName(), new String[]{"cEditor", "confirmDelete"}, new String[]{"This Action cannot be undone!"}, new String[]{"cEditor", "open#>cOverview"}, null, category.isCategory() ? category.getAsCategory().getCategoryIcon() : category.getAsSubCategory().getCategoryIcon()));
                            // player.openInventory(Utils.getDeleteInventory(category, new String[]{"cEditor", "open#>cOverview"}));
                        }
                    } else {

                        /*
                         * ----------------------------
                         * |0 |1 |2 |3 |4 |5 |6 |7 |8 |
                         * |9 |10|11|12|13|14|15|16|17|
                         * |18|19|20|21|22|23|24|25|26|
                         * ----------------------------
                         * */

                        if (args[1].equals("category")) {
                            Category category = CustomHeads.getCategoryManager().getCategory(args[2]);
                            if (category != null) {
                                player.openInventory(Utils.getDialog("Delete " + category.getName(), new String[]{"cEditor", "confirmDelete"}, new String[]{"This Action cannot be undone!"}, new String[]{"cEditor", "openInfo#>" + category.getId()}, null, category.getCategoryIcon()));
                                //player.openInventory(Utils.getDeleteInventory(category, new String[]{"cEditor", "openInfo#>" + category.getId()}));
                            }
                            return;
                        } else if (args[1].equals("subCategory")) {
                            SubCategory subCategory = CustomHeads.getCategoryManager().getSubCategory(args[2]);
                            if (subCategory != null) {
                                player.openInventory(Utils.getDialog("Delete " + subCategory.getName(), new String[]{"cEditor", "confirmDelete"}, new String[]{"This Action cannot be undone!"}, new String[]{"cEditor", "openInfo#>" + subCategory.getId()}, null, subCategory.getCategoryIcon()));
                                // player.openInventory(Utils.getDeleteInventory(subCategory, new String[]{"cEditor", "openInfo#>" + subCategory.getId()}));
                            }
                            return;
                        }
                    }
                    break;
                case "open":
                    event.setCancelled(true);
                    if (args[1].equals("cOverview")) {
                        CategoryEditor.openCategoryOverview(player);
                    }
                    break;
                case "convert": {
                    /*
                    Setup: Check if Category has Heads: if true -> Ask if Player wants to move them into the Subcategory
                     */
                    Category category = CustomHeads.getCategoryManager().getCategory(args[1]);
                    if (category == null) {
                        throw new NullPointerException("Convert: Given Category wasn't found. Please report this Error to me");
                    }
                    if(args.length > 2) {

                    } else {
                        if (category.hasHeads()) {
                            Utils.getDialog("Move existing Heads into this Subcategory?", new String[]{"cEditor", "convert", "y"}, null, new String[]{"cEditor", "convert", "n"}, new String[]{"The existing Heads", "will be deleted!"}, new ItemEditor(Material.SKULL_ITEM, 3).setDisplayName("Move existing Heads into this Subcategory?").setTexture("").getItem());
                        }
                    }
                    event.setCancelled(true);
                    break;
                }
                case "blockMoving":
                    event.setCancelled(true);
                    break;
                case "openInfo": {
                    event.setCancelled(true);
                    Category category = CustomHeads.getCategoryManager().getCategory(args[1]);
                    if (category != null) {
                        player.sendMessage(args[1]);
                        CategoryEditor editor = new CategoryEditor(category, player);
                        editor.showCategoryInfos();
                    }
                    break;
                }
                case "confirmDelete":
                    // cEditor confirmDelete#>category/subCategory#>id
                    if (args[1].equals("category")) {
                        Category category = CustomHeads.getCategoryManager().getCategory(args[2]);
                        CategoryEditor.openCategoryOverview(player);
                        if (CustomHeads.getCategoryManager().deleteCategory(category)) {

                        } else {

                        }
                        return;
                    } else if (args[1].equals("subCategory")) {
                        SubCategory subCategory = CustomHeads.getCategoryManager().getSubCategory(args[2]);
                        CategoryEditor.openCategoryOverview(player);
                        if (CustomHeads.getCategoryManager().deleteSubCategory(subCategory)) {

                        } else {

                        }
                        return;
                    }
                    break;
                case "selectCategory":
                    String action;
                    if (cachedPlayers.containsKey(player.getUniqueId()) && (action = cachedPlayers.get(player.getUniqueId())).contains("selectCategory#>")) {
                        String[] actionSplitted = action.split("#>");
                        if (actionSplitted[1].equals(args[1])) {
                            event.setCancelled(true);
                            return;
                        }
                        int invIndex = Integer.parseInt(actionSplitted[2]);
                        ItemEditor item = new ItemEditor(event.getInventory().getItem(invIndex));
                        event.getInventory().setItem(invIndex, item.setDisplayName(item.getDisplayName().substring(0, item.getDisplayName().lastIndexOf(":") - 3)).getItem());
                    }
                    ItemEditor item = new ItemEditor(event.getCurrentItem());
                    event.setCurrentItem(item.setDisplayName(item.getDisplayName() + " §7: §aSelected").getItem());
                    cachedPlayers.put(player.getUniqueId(), "selectCategory#>" + args[1] + "#>" + event.getSlot());
                    event.setCancelled(true);
                    break;
                case "rename":
                    if (args[1].equalsIgnoreCase("category")) {
                        Category category = CustomHeads.getCategoryManager().getCategory(args[2]);
                        if (category != null) {
                            AnvilGUI gui = new AnvilGUI(player, "Rename Category", new AnvilGUI.AnvilClickEventHandler() {
                                public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilClickEvent) {
                                    anvilClickEvent.setCancelled(true);
                                    if (anvilClickEvent.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                                        if (anvilClickEvent.getName().equals("")) {
                                            return;
                                        }
                                        if (anvilClickEvent.getName().length() > 16) {
                                            anvilClickEvent.getPlayer().sendMessage("§eCEdit: §7Name cannot be longer than §c16 §7Characters");
                                            return;
                                        }
                                        String newCategoryName = ChatColor.stripColor(Utils.format(anvilClickEvent.getName()));
                                        category.setName(newCategoryName);
                                        category.setPermission(newCategoryName.toLowerCase().replace(" ", "_"));

                                        // Rewrite Category File
                                        File categoryFile = CustomHeads.getCategoryManager().getSourceFile(category);
                                        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(categoryFile), StandardCharsets.UTF_8)) {
                                            writer.write(category.toString());
                                            writer.flush();
                                        } catch (Exception e) {
                                            Bukkit.getLogger().log(Level.SEVERE, "Failed to rewrite " + category.getName(), e);
                                        }
                                        player.sendMessage("§eCEdit: §7Successfully renamed Category to §a" + newCategoryName);
                                    }
                                }

                                public void onAnvilClose() {
                                    CategorySetup.OPENED_SETUPS.get(player).openInventory(player);
                                }
                            });
                            gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, new ItemEditor(Material.PAPER).setDisplayName(category.getPlainName()).setLore("§7Set the Name of this Category").getItem());
                            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName(category.getPlainName()).setLore("§7Set the Name of this Category").getItem());
                            gui.open();
                            player.updateInventory();
                        }
                    } else if (args[1].equalsIgnoreCase("subCategory")) {
                        SubCategory subCategory = CustomHeads.getCategoryManager().getSubCategory(args[2]);
                        if (subCategory != null) {
                            AnvilGUI gui = new AnvilGUI(player, "Rename Subcategory", new AnvilGUI.AnvilClickEventHandler() {
                                public void onAnvilClick(AnvilGUI.AnvilClickEvent anvilClickEvent) {
                                    anvilClickEvent.setCancelled(true);
                                    if (anvilClickEvent.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                                        if (anvilClickEvent.getName().equals("")) {
                                            return;
                                        }
                                        if (anvilClickEvent.getName().length() > 16) {
                                            anvilClickEvent.getPlayer().sendMessage("§eCEdit: §7Name cannot be longer than §c16 §7Characters");
                                            return;
                                        }
                                        Category originCategory = subCategory.getOriginCategory();
                                        List<SubCategory> subCategories = subCategory.getOriginCategory().getSubCategories();
                                        int index = subCategories.indexOf(subCategory);
                                        String newCategoryName = ChatColor.stripColor(Utils.format(anvilClickEvent.getName()));
                                        subCategory.setName(newCategoryName);
                                        subCategory.setPermission(anvilClickEvent.getName().toLowerCase().replace(" ", "_"));
                                        subCategories.set(index, subCategory);
                                        originCategory.setSubCategories(subCategories);

                                        // Rewrite Category File
                                        File categoryFile = CustomHeads.getCategoryManager().getSourceFile(originCategory);
                                        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(categoryFile), StandardCharsets.UTF_8)) {
                                            writer.write(originCategory.toString());
                                            writer.flush();
                                        } catch (Exception e) {
                                            Bukkit.getLogger().log(Level.SEVERE, "Failed to rewrite " + originCategory.getName(), e);
                                        }
                                        player.sendMessage("§eCedit: §7Successfully renamed Subcategory to §a" + newCategoryName);
                                    }
                                }

                                public void onAnvilClose() {
                                    CategorySetup.OPENED_SETUPS.get(player).openInventory(player);
                                }
                            });
                            gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, new ItemEditor(Material.PAPER).setDisplayName(subCategory.getPlainName()).setLore("§7Set the Name of this Category").getItem());
                            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName(subCategory.getPlainName()).setLore("§7Set the Name of this Category").getItem());
                            gui.open();
                            player.updateInventory();
                        }
                    }
            }
        }
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent event) {
        cachedPlayers.remove(event.getPlayer().getUniqueId());
    }

}
