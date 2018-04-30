package de.mrstein.customheads.listener;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHead;
import de.mrstein.customheads.api.CustomHeadsAPI;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.SubCategory;
import de.mrstein.customheads.headwriter.HeadFontType;
import de.mrstein.customheads.stuff.CHSearchQuery;
import de.mrstein.customheads.stuff.History;
import de.mrstein.customheads.utils.HeadSaver;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.ScrollableInventory;
import de.mrstein.customheads.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static de.mrstein.customheads.utils.Utils.*;

public class InventoryListener implements Listener {

    private static HashMap<Player, String> lastMenu = new HashMap<>();

    public static String getLastMenu(Player player, boolean remove) {
        String last = lastMenu.get(player);
        if (remove)
            lastMenu.remove(player);
        return last;
    }

    @EventHandler
    public void onInvOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (CustomHeads.getLooks().getMenuTitles().contains(event.getInventory().getTitle())) {
            if (!hasPermission(player, CustomHeads.getLooks().getMenuInfo(CustomHeads.getLooks().getIDbyTitle(event.getInventory().getTitle()))[1])) {
                player.closeInventory();
                event.setCancelled(true);
            }
            ItemStack[] inventoryContent = event.getInventory().getContents();
            for (int i = 0; i < inventoryContent.length; i++) {
                if (inventoryContent[i] == null) continue;
                ItemStack contentItem = inventoryContent[i];
                if (CustomHeads.getTagEditor().getTags(contentItem).contains("openCategory")) {
                    String[] categoryArgs = CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openCategory") + 1).split("#>");
                    if (categoryArgs[0].equals("category")) {
                        Category category = CustomHeads.getCategoryImporter().getCategory(categoryArgs[1]);
                        ItemStack nextIcon = category.nextIcon();
                        nextIcon = new ItemEditor(nextIcon).setDisplayName(hasPermission(player, category.getPermission()) ? "§a" + nextIcon.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(nextIcon.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED).addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList("§8>===-------", "§7§oPermission: " + category.getPermission()) : null).getItem();
                        contentItem = nextIcon;
                    }
                }
                if (CustomHeads.getTagEditor().getTags(contentItem).contains("chItem")) {
                    contentItem = CustomHeads.getLooks().getItems().get(CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "chItem") + 1));
                    if (CustomHeads.getTagEditor().getTags(contentItem).contains("your_head")) {
                        SkullMeta meta = (SkullMeta) contentItem.getItemMeta();
                        meta.setDisplayName(CustomHeads.getLanguageManager().ITEMS_YOUR_HEAD.replace("{PLAYER}", player.getName()));
                        meta.setOwner(player.getName());
                        contentItem.setItemMeta(meta);
                    } else if (CustomHeads.getTagEditor().getTags(contentItem).contains("saved_heads")) {
                        contentItem = new ItemEditor(contentItem).setDisplayName(hasPermission(player, "heads.use.more") ? contentItem.getItemMeta().getDisplayName().replace("{SIZE}", "(" + new HeadSaver(player).getHeads().size() + ")") : "§7" + ChatColor.stripColor(contentItem.getItemMeta().getDisplayName().replace("{SIZE}", "") + " " + CustomHeads.getLanguageManager().LOCKED)).addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList("§8>===-------", "§7§oPermission: heads.use.more") : null).getItem();
                    }
                    if (CustomHeads.getTagEditor().getTags(contentItem).contains("openMenu")) {
                        contentItem = CustomHeads.getTagEditor().addTags(contentItem, "needsPermission", CustomHeads.getLooks().getMenuInfo(CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openMenu") + 1).toLowerCase())[1]);
                    }
                    if (CustomHeads.getTagEditor().getTags(contentItem).contains("needsPermission")) {
                        contentItem = new ItemEditor(contentItem).setDisplayName(hasPermission(player, CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "needsPermission") + 1)) ? "§a" + contentItem.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(contentItem.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED).addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList("§8>===-------", "§7Permission: " + CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "needsPermission") + 1)) : null).getItem();
                    }
                }
                inventoryContent[i] = CustomHeads.getTagEditor().addTags(contentItem, "menuID", CustomHeads.getLooks().getIDbyTitle(event.getInventory().getTitle()));
            }
            event.getInventory().setContents(inventoryContent);
        }

    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory() == null || event.getRawSlot() > event.getInventory().getSize() || event.getInventory().getType() != InventoryType.CHEST || event.getCurrentItem() == null || !hasPermission(player, "heads.use"))
            return;

//        player.sendMessage("§7[CHTags Tags] §r" + CustomHeads.getTagEditor().getTags(event.getCurrentItem()));

        if (event.getInventory().getTitle().equals(CustomHeads.getLanguageManager().LOADING)) {
            event.setCancelled(true);
        }

        // Delete Head
        if (event.getInventory().getTitle().equals(CustomHeads.getLanguageManager().SAVED_HEADS_TITLE.replace("{PLAYER}", player.getName()))) {
            if (event.getClick() == ClickType.SHIFT_RIGHT) {
                HeadSaver saver = new HeadSaver(player);
                event.setCancelled(true);
                if (saver.hasHead(toConfigString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())))) {
                    saver.openDeleteDialog(toConfigString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())));
                }
                return;
            }
        }

        // History Inventory
        if (event.getInventory().getTitle().equalsIgnoreCase(CustomHeads.getLanguageManager().HISTORY_INV_TITLE.replace("{PLAYER}", player.getName()))) {
            event.setCancelled(true);
            if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("history")) {
                String[] args = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "history") + 1).split("#>");
                switch (args[0]) {
                    case "search":
                        new CHSearchQuery(args[1]).setRecordHistory(false).viewTo(player, "close");
                        break;
                    case "get":
                        player.setItemOnCursor(new ItemEditor(event.getCurrentItem()).setLore(null).getItem());
                        break;
                    case "open":
                        if (args[1].equals("get")) {
                            player.openInventory(new History(player).getGetHistory().getAsInventory());
                        } else if (args[1].equals("search")) {
                            player.openInventory(new History(player).getSearchHistory().getAsInventory());
                        }
                }
            }
        }

        if (!CustomHeads.getTagEditor().hasMyTags(event.getCurrentItem())) {
            return;
        }

        ScrollableInventory currentScrollInventory = null;
        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("scInvID")) {
            currentScrollInventory = ScrollableInventory.getInventoryByID(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "scInvID") + 1));
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("blockMoving")) {
            event.setCancelled(true);
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("needsPermission")) {
            if (!hasPermission(player, CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "needsPermission") + 1))) {
                event.setCancelled(true);
                return;
            }
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("editFont")) {
            event.setCancelled(true);
            String action = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "editFont") + 1);
            switch (action) {
                case "addCharacter": {
                    List<String> extraTags = Arrays.asList(currentScrollInventory.getExtraTags());
                    HeadFontType font = new HeadFontType(extraTags.get(extraTags.indexOf("fontName") + 1)).enableCache();
                    Inventory adder = Bukkit.createInventory(player, 27, CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_TITLE.replace("{FONT}", font.getName()));
                    adder.setItem(4, CustomHeads.getTagEditor().addTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_TOGGLE.replace("{MODE}", CustomHeads.getLanguageManager().NO)).getItem(), "editFont", "adderToggle"));
                    adder.setItem(13, CustomHeads.getTagEditor().addTags(new ItemEditor(Material.HOPPER).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_DROPPER_NAME).setLore(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_DROPPER_LORE).getItem(), "editFont", "adder", "fontCacheID", font.getCacheID()));
                    adder.setItem(18, Utils.getBackButton("openScInv", currentScrollInventory.getUid()));
                    adder.setItem(26, CustomHeads.getTagEditor().addTags(new CustomHead("http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b").setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_SAVENEXIT).toItem(), "editFont", "saveAndExit", "fontCacheID", font.getCacheID(), "openScInv", currentScrollInventory.getUid()));
                    player.openInventory(adder);
                    break;
                }
                case "removeCharacter": {
                    ItemStack item;
                    if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("active")) {
                        item = CustomHeads.getTagEditor().removeTags(new ItemEditor(event.getCurrentItem()).removeEnchantment(Enchantment.SILK_TOUCH).hideAllFlags().getItem(), "active");
                        if(event.getClick() == ClickType.SHIFT_LEFT) {
                            List<ItemStack> selected = getSelectedCharacters(event.getInventory());
                            if(!selected.isEmpty()) {
                                // Dont look a it... It works so idc
                                // This was written at like 5AM >_>
                                String ech = "";
                                for(ItemStack ahhhh : selected) {
                                    ech += CustomHeads.getTagEditor().getTags(ahhhh).get(CustomHeads.getTagEditor().indexOf(ahhhh, "character") + 1) + ", ";
                                }
                                List<String> splitter = new ArrayList<>(Arrays.asList(ech.split(", ")));
                                List<String> loreInfo = new ArrayList<>();
                                int lines = (int) Math.ceil(splitter.size() / 8.0);
                                for(int line = 0; line < lines; line++) {
                                    int start = line * 8;
                                    int end = (line + 1) * 8;
                                    System.out.println(start + " " + end);
                                    String listString = splitter.subList(start, splitter.size() > end ? end : splitter.size()).toString();
                                    listString = listString.substring(1, listString.length() - 1);
                                    loreInfo.add("&7" + listString);
                                }
                                player.openInventory(getDeleteDialog(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_CONFIRM, new String[]{"confirmDelete", "characters#>" + CustomHeads.getTagEditor().getTags(selected.get(0)).get(CustomHeads.getTagEditor().indexOf(selected.get(0), "fontName") + 1) + "#>" + ech.substring(0, ech.length() - 2), "invAction", "close"}, new String[]{"invAction", "close"}, new CustomHead("http://textures.minecraft.net/texture/badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a").setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_PREINFO).setLore(loreInfo).toItem()));
                            }
                        }
                        getInventory(item).refreshCurrentPage();
                    } else {
                        item = CustomHeads.getTagEditor().addTags(new ItemEditor(event.getCurrentItem()).addEnchantment(Enchantment.SILK_TOUCH, 1).hideAllFlags().getItem(), "active");
                    }
                    event.setCurrentItem(item);
                    break;
                }
                case "select": {
                    if(CustomHeads.getTagEditor().getTags(event.getInventory().getItem(50)).contains("active")) {
                        ItemStack item;
                        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("charSelected")) {
                            item = getInventory(event.getCurrentItem()).getContent(true).get(event.getSlot());
                        } else {
                            item = CustomHeads.getTagEditor().addTags(new ItemEditor(event.getCurrentItem()).getItem(), "charSelected");
                            String character = CustomHeads.getTagEditor().getTags(item).get(CustomHeads.getTagEditor().indexOf(item, "character") + 1);
                            item = CustomHeads.getTagEditor().copyOf(item, new ItemEditor(Material.SKULL_ITEM).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_SELECTED.replace("{CHARACTER}", character.equals(" ") ? "BLANK" : character)).getItem());
                        }
                        event.setCurrentItem(item);
                    }
                    break;
                }
                case "adder":
                    if(event.getCursor() != null && event.getCursor().hasItemMeta() && event.getCursor().getItemMeta().hasDisplayName()) {
                        String toAdd = event.getCursor().getItemMeta().getDisplayName();
                        if (Utils.hasCustomTexture(event.getCursor())) {
                            HeadFontType font = HeadFontType.getCachedFont(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "fontCacheID") + 1));
                            //  ¯\_(ツ)_/¯
                            if (!font.exists()) {
                                player.sendMessage("For whatever Reason this Font no longer exists");
                                player.closeInventory();
                                break;
                            }
                            boolean forceReplace = CustomHeads.getTagEditor().getTags(event.getInventory().getItem(4)).contains("toggleActive");
                            if (toAdd.length() == 1 || toAdd.equalsIgnoreCase("BLANK")) {
                                player.sendMessage(font.addCharacter((toAdd.equalsIgnoreCase("BLANK") ? ' ' : toAdd.charAt(0)), CustomHeadsAPI.getSkullTexture(event.getCursor()), forceReplace) ? CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_SUCCESSFUL.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd.toLowerCase()) : CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_FAILED.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd.toLowerCase()));
                                break;
                            }
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_FAILED.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd));
                    }
                    break;
                case "saveAndExit":
                    HeadFontType font = HeadFontType.getCachedFont(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "fontCacheID") + 1));
                    if(font == null) {
                        player.sendMessage("§cInvalid Session");
                        break;
                    }
                    if(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("openScInv")) {
                        ScrollableInventory inv = ScrollableInventory.getInventoryByID(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "openScInv") + 1));
                        inv.setContent(Utils.getHeadsAsItemStacks(new ArrayList<>(font.getCharacters().values())));
                        inv.refreshCurrentPage();
                    }
                    font.save();
                    player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_SAVE_SUCCESSFUL.replace("{FONT}", font.getName()));
                    break;
                case "adderToggle":
                    if(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("toggleActive")) {
                        event.setCurrentItem(CustomHeads.getTagEditor().removeTags(new ItemEditor(event.getCurrentItem()).removeEnchantment(Enchantment.SILK_TOUCH).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_TOGGLE.replace("{MODE}", CustomHeads.getLanguageManager().NO)).hideAllFlags().getItem(), "toggleActive"));
                    } else {
                        event.setCurrentItem(CustomHeads.getTagEditor().addTags(new ItemEditor(event.getCurrentItem()).addEnchantment(Enchantment.SILK_TOUCH, 1).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_TOGGLE.replace("{MODE}", CustomHeads.getLanguageManager().YES)).hideAllFlags().getItem(), "toggleActive"));
                    }
                    break;
            }
        }

        if(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("executeCommand")) {
            player.performCommand(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "executeCommand") + 1));
        }

        if(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("openScInv")) {
            player.openInventory(ScrollableInventory.getInventoryByID(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "openScInv") + 1)).getAsInventory());
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("openCategory")) {
            event.setCancelled(true);
            String[] args = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "openCategory") + 1).split("#>");
            if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("menuID")) {
                if (args[0].equalsIgnoreCase("category")) {
                    Category category = CustomHeads.getCategoryImporter().getCategory(args[1]);
                    if (category != null && hasPermission(player, category.getPermission())) {
                        if (category.hasSubCategories()) {
                            player.openInventory(CustomHeads.getLooks().subCategoryLooks.get(Integer.parseInt(category.getId())));
                        } else {
                            openPreloader(player);
                            List<ItemStack> heads = new ArrayList<>();
                            category.getHeads().forEach(customHead -> heads.add(CustomHeads.getTagEditor().setTags(customHead.toItem(), "wearable")));
                            ScrollableInventory inventory = new ScrollableInventory(category.getName(), heads).setContentsClonable(true);
                            inventory.setBarItem(1, Utils.getBackButton("openMenu", CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "menuID") + 1).toLowerCase()));
                            inventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFOLORE).getItem(), "dec", "info-item", "blockMoving"));
                            player.openInventory(inventory.getAsInventory());
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("subCategory")) {
                SubCategory subCategory = CustomHeads.getCategoryImporter().getSubCategory(args[1]);
                if (subCategory != null && hasPermission(player, subCategory.getOriginCategory().getPermission())) {
                    openPreloader(player);
                    List<ItemStack> heads = new ArrayList<>();
                    subCategory.getHeads().forEach(customHead -> heads.add(customHead.toItem()));
                    player.openInventory(new ScrollableInventory(subCategory.getName(), heads).setBarItem(1, Utils.getBackButton("invAction", "goBack#>category#>" + subCategory.getOriginCategory().getId())).setContentsClonable(true).getAsInventory());
                }
            }
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("openMenu")) {
            event.setCancelled(true);
            if (!hasPermission(player, CustomHeads.getLooks().getMenuInfo(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "openMenu") + 1).toLowerCase())[1])) {
                return;
            }
            Inventory menu = CustomHeads.getLooks().getMenu(CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "openMenu") + 1).toLowerCase());
            if (menu != null)
                player.openInventory(menu);
        }

        // delet confirm
        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("confirmDelete")) {
            String[] args = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "confirmDelete") + 1).split("#>");
            if (args[0].equalsIgnoreCase("head")) {
                if (new HeadSaver(player).removeHead(args[1])) {
                    player.sendMessage(CustomHeads.getLanguageManager().REMOVE_SUCCESSFUL.replace("{NAME}", args[1]));
                } else {
                    player.sendMessage(CustomHeads.getLanguageManager().REMOVE_FAILED.replace("{NAME}", args[1]));
                }
            }
            if(args[0].equalsIgnoreCase("characters")) {
                HeadFontType fontType = new HeadFontType(args[1]);
                List<String> characters = new ArrayList<>(Arrays.asList(args[2].split(", ")));
                for(String character : characters) {
                    if(!fontType.removeCharacter(character.charAt(0))) {
                        player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_FAILED.replace("{CHARACTER}", character.substring(0, 1)));
                    }
                }
                fontType.save();
                player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_SUCCESSFUL);
            }
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("invAction")) {
            event.setCancelled(true);

            String[] args = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "invAction") + 1).split("#>");
            if (args[0].equalsIgnoreCase("close")) {
                player.closeInventory();
            } else if (args[0].equalsIgnoreCase("goBack")) {
                if (args[1].equalsIgnoreCase("menu")) {
                    Inventory menu = CustomHeads.getLooks().getCreatedMenus().get(args[2]);
                    if (menu != null)
                        player.openInventory(cloneInventory(menu, player));
                } else if (args[1].equalsIgnoreCase("category")) {
                    Category originCategory = CustomHeads.getCategoryImporter().getCategory(args[2]);
                    if (originCategory != null && hasPermission(player, originCategory.getPermission()))
                        player.openInventory(CustomHeads.getLooks().subCategoryLooks.get(Integer.parseInt(originCategory.getId())));
                }
            } else if(args[0].equalsIgnoreCase("retrySearch")) {
                openSearchGUI(player, args[1], event.getInventory().getItem(18) == null ? "close" : CustomHeads.getTagEditor().getTags(event.getInventory().getItem(18)).get(CustomHeads.getTagEditor().indexOf(event.getInventory().getItem(18), "")));
            }
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("chItem")) {
            if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("saved_heads")) {
                event.setCancelled(true);
                if (!hasPermission(player, "heads.use") || !hasPermission(player, "heads.use.more")) return;
                ScrollableInventory savedHeads = new ScrollableInventory(CustomHeads.getLanguageManager().SAVED_HEADS_TITLE.replace("{PLAYER}", player.getName()), new HeadSaver(player).getHeads()).setContentsClonable(true);
                String menuID = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "menuID") + 1).toLowerCase();
                lastMenu.put(player, menuID);
                savedHeads.setBarItem(1, getBackButton("openMenu", menuID));
                savedHeads.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFOLORE).getItem(), "dec", "info-item", "blockMoving"));
                player.openInventory(savedHeads.getAsInventory());
            }

            if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("help")) {
                event.setCancelled(true);
                if (!hasPermission(player, "heads.use") || !hasPermission(player, "heads.view.help")) return;
                player.openInventory(getHelpMenu(player, "openMenu", CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "menuID") + 1).toLowerCase()));
            }

            if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("search")) {
                event.setCancelled(true);
                Utils.openSearchGUI(player, CustomHeads.getLanguageManager().CHANGE_SEARCH_STRING, "openMenu", CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "menuID") + 1).toLowerCase());
            }

            if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("find_more")) {
                event.setCancelled(true);
                player.closeInventory();
                sendJSONMessage("[\"\",{\"text\":\"" + CustomHeads.getLanguageManager().LINK + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://heads.freshcoal.com\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"§bheads.freshcoal.com\"}]}}}]", player);
            }
        }

        // scrollInv
        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("scrollInv")) {
            event.setCancelled(true);
            String uid = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "scInvID") + 1);
            String[] args = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "scrollInv") + 1).split("#>");
            switch (args[0]) {
                case "close":
                    player.closeInventory();
                    break;
                    // slide it
                case "slidePage": {
                    ScrollableInventory inventory = ScrollableInventory.getInventories().get(uid);
                    if (inventory != null) {
                        if (args[1].equals("next")) {
                            inventory.nextPage();
                        } else if (args[1].equals("previous")) {
                            inventory.previousPage();
                        }
                    }
                    break;
                }
                case "reArrange": {
                    ScrollableInventory inventory = ScrollableInventory.getInventories().get(uid);
                    if (inventory != null) {
                        int nextArrangement;
                        if (args[1].equals("next")) {
                            nextArrangement = inventory.nextArrangement();
                        } else {
                            throw new UnsupportedOperationException("Arrangement " + args[1] + " not supported");
                        }
                        String name = CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "originalName") + 1) + ScrollableInventory.sortName.get(nextArrangement);
                        event.setCurrentItem(renameStack(event.getCurrentItem(), 1, name, false));
                    }
                    break;
                }
            }
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("wearable")) {
            if (event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                player.sendMessage(CustomHeads.getLanguageManager().PUT_ON_HEAD.replace("{NAME}", event.getCurrentItem().getItemMeta().getDisplayName()));
                player.getInventory().setHelmet(CustomHeads.getTagEditor().setTags(event.getCurrentItem(), ""));
                player.setItemOnCursor(null);
                player.updateInventory();
                return;
            }
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("suggestCommand")) {
            event.setCancelled(true);
            player.closeInventory();
            sendJSONMessage("[\"\",{\"text\":\"" + CustomHeads.getLanguageManager().HELP_GET_COMMAND + "\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "suggestCommand") + 1) + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + CustomHeads.getTagEditor().getTags(event.getCurrentItem()).get(CustomHeads.getTagEditor().indexOf(event.getCurrentItem(), "suggestCommand") + 1) + "\",\"color\":\"aqua\"}]}}}]", player);
            return;
        }

        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("clonable")) {
            event.setCancelled(true);
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                player.getInventory().addItem(CustomHeads.getTagEditor().setTags(event.getCurrentItem(), ""));
            } else {
                player.setItemOnCursor(CustomHeads.getTagEditor().setTags(event.getCurrentItem(), ""));
            }
        }

        // No lookin at my Tags
        if (CustomHeads.getTagEditor().getTags(event.getCurrentItem()).contains("tempTags")) {
            event.setCurrentItem(CustomHeads.getTagEditor().removeAll(event.getCurrentItem()));
        }
    }

}
