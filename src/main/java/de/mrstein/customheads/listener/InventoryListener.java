package de.mrstein.customheads.listener;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHeadsPlayer;
import de.mrstein.customheads.category.BaseCategory;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.SubCategory;
import de.mrstein.customheads.headwriter.HeadFontType;
import de.mrstein.customheads.reflection.TagEditor;
import de.mrstein.customheads.stuff.CHSearchQuery;
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

import java.util.*;
import java.util.stream.Collectors;

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
                return;
            }
            CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
            ItemStack[] inventoryContent = event.getInventory().getContents();
            for (int i = 0; i < inventoryContent.length; i++) {
                if (inventoryContent[i] == null) continue;
                ItemStack contentItem = inventoryContent[i];
                if (CustomHeads.getTagEditor().getTags(contentItem).contains("openCategory")) {
                    String[] categoryArgs = CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openCategory") + 1).split("#>");
                    if (categoryArgs[0].equals("category")) {
                        Category category = CustomHeads.getCategoryLoader().getCategory(categoryArgs[1]);
                        ItemStack nextIcon = category.nextIcon();
                        boolean unlocked = customHeadsPlayer.getUnlockedCategories(false).stream().map(BaseCategory::getId).collect(Collectors.toList()).contains(category.getId());
                        boolean bought = customHeadsPlayer.getUnlockedCategories(true).stream().map(BaseCategory::getId).collect(Collectors.toList()).contains(category.getId());
                        nextIcon = new ItemEditor(nextIcon)
                                .setDisplayName(hasPermission(player, category.getPermission()) || unlocked ? "§a" + nextIcon.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(nextIcon.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED)
                                .addLoreLine(CustomHeads.hasEconomy() ? bought ? CustomHeads.getLanguageManager().ECONOMY_BOUGHT : Utils.getPriceFormatted(category, true) + "\n" + CustomHeads.getLanguageManager().ECONOMY_BUY_CATEGORY_PROMPT : null)
                                .addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList("§8>===-------", "§7§oPermission: " + category.getPermission()) : null)
                                .getItem();
                        if (CustomHeads.hasEconomy()) {
                            if (!bought) {
                                nextIcon = CustomHeads.getTagEditor().addTags(nextIcon, "buyCategory", category.getId());
                            }
                        }
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
                        contentItem = new ItemEditor(contentItem).setDisplayName(hasPermission(player, "heads.use.more") ? contentItem.getItemMeta().getDisplayName().replace("{SIZE}", "(" + customHeadsPlayer.getSavedHeads().size() + ")") : "§7" + ChatColor.stripColor(contentItem.getItemMeta().getDisplayName().replace("{SIZE}", "") + " " + CustomHeads.getLanguageManager().LOCKED)).addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList("§8>===-------", "§7§oPermission: heads.use.more") : null).getItem();
                    }
                    if (CustomHeads.getTagEditor().getTags(contentItem).contains("openMenu")) {
                        contentItem = CustomHeads.getTagEditor().addTags(contentItem, "needsPermission", CustomHeads.getLooks().getMenuInfo(CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openMenu") + 1).toLowerCase())[1]);
                    }
                    if (CustomHeads.getTagEditor().getTags(contentItem).contains("needsPermission")) {
                        contentItem = new ItemEditor(contentItem)
                                .setDisplayName(hasPermission(player, CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "needsPermission") + 1)) ? "§a" + contentItem.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(contentItem.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED)
                                .addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList("§8>===-------", "§7Permission: " + CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "needsPermission") + 1)) : null)
                                .getItem();
                    }
                }
                inventoryContent[i] = CustomHeads.getTagEditor().addTags(contentItem, "menuID", CustomHeads.getLooks().getIDbyTitle(event.getInventory().getTitle()));
            }
            event.getInventory().setContents(inventoryContent);
        }
        player.updateInventory();
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory() == null || event.getRawSlot() > event.getInventory().getSize() || event.getInventory().getType() != InventoryType.CHEST || event.getCurrentItem() == null || !hasPermission(player, "heads.use"))
            return;

        player.sendMessage("§7[CHTags Tags] §r" + CustomHeads.getTagEditor().getTags(event.getCurrentItem()));

        if (event.getInventory().getTitle().equals(CustomHeads.getLanguageManager().LOADING)) {
            event.setCancelled(true);
        }
        CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
        List<String> itemTags = CustomHeads.getTagEditor().getTags(event.getCurrentItem());

        // Delete Head
        if (event.getInventory().getTitle().equals(CustomHeads.getLanguageManager().SAVED_HEADS_TITLE.replace("{PLAYER}", player.getName()))) {
            if (event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                String name = toConfigString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
                if (customHeadsPlayer.hasHead(name)) {
                    player.openInventory(getDialog(CustomHeads.getLanguageManager().REMOVE_CONFIRMATION.replace("{HEAD}", name), new String[]{"confirmDelete", "head#>" + name, "chItem", "saved_heads", "menuID", InventoryListener.getLastMenu(player, false)}, new String[]{""}, new String[]{"chItem", "saved_heads", "menuID", getLastMenu(player, true)}, null, customHeadsPlayer.getHead(name)));
                }
                return;
            }
        }

        // History Inventory
        if (event.getInventory().getTitle().equalsIgnoreCase(CustomHeads.getLanguageManager().HISTORY_INV_TITLE.replace("{PLAYER}", player.getName()))) {
            event.setCancelled(true);
            if (itemTags.contains("history")) {
                String[] args = itemTags.get(itemTags.indexOf("history") + 1).split("#>");
                switch (args[0]) {
                    case "search":
                        List<Category> categories = CustomHeads.getCategoryLoader().getCategoryList();
                        categories.removeAll(CustomHeads.getApi().wrapPlayer(player).getUnlockedCategories(false));
                        new CHSearchQuery(args[1]).setRecordHistory(false).excludeCategories(categories).viewTo(player, "willClose");
                        break;
                    case "get":
                        player.setItemOnCursor(new ItemEditor(event.getCurrentItem()).setLore("").getItem());
                        break;
                    case "open":
                        if (args[1].equals("get")) {
                            player.openInventory(customHeadsPlayer.getGetHistory().getInventory());
                        } else if (args[1].equals("search")) {
                            player.openInventory(customHeadsPlayer.getSearchHistory().getInventory());
                        }
                }
            }
        }

        if (!CustomHeads.getTagEditor().hasMyTags(event.getCurrentItem())) {
            return;
        }

        ScrollableInventory currentScrollInventory = null;
        if (itemTags.contains("scInvID")) {
            currentScrollInventory = ScrollableInventory.getInventoryByID(itemTags.get(itemTags.indexOf("scInvID") + 1));
        }

        String menuID = null;
        if (itemTags.contains("menuID")) {
            menuID = itemTags.get(itemTags.indexOf("menuID") + 1);
        }

        if (itemTags.contains("blockMoving")) {
            event.setCancelled(true);
        }

        if (itemTags.contains("needsPermission")) {
            if (!hasPermission(player, itemTags.get(itemTags.indexOf("needsPermission") + 1))) {
                event.setCancelled(true);
                return;
            }
        }

        if (itemTags.contains("editFont")) {
            event.setCancelled(true);
            String action = itemTags.get(itemTags.indexOf("editFont") + 1);
            switch (action) {
                case "addCharacter": {
                    List<String> extraTags = Arrays.asList(currentScrollInventory.getExtraTags());
                    HeadFontType font = new HeadFontType(extraTags.get(extraTags.indexOf("fontName") + 1)).enableCache();
                    Inventory adder = Bukkit.createInventory(player, 27, CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_TITLE.replace("{FONT}", font.getFontName()));
                    adder.setItem(4, CustomHeads.getTagEditor().addTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_TOGGLE.replace("{MODE}", CustomHeads.getLanguageManager().NO)).getItem(), "editFont", "adderToggle"));
                    adder.setItem(13, CustomHeads.getTagEditor().addTags(new ItemEditor(Material.HOPPER).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_DROPPER_NAME).setLore(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_DROPPER_LORE).getItem(), "editFont", "adder", "fontCacheID", font.getCacheID()));
                    adder.setItem(18, Utils.getBackButton("openScInv", currentScrollInventory.getUid()));
                    adder.setItem(26, CustomHeads.getTagEditor().addTags(new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==").setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_SAVENEXIT).getItem(), "editFont", "saveAndExit", "fontCacheID", font.getCacheID(), "openScInv", currentScrollInventory.getUid()));
                    player.openInventory(adder);
                    break;
                }
                case "removeCharacter": {
                    ItemStack item;
                    if (itemTags.contains("active")) {
                        item = CustomHeads.getTagEditor().removeTags(new ItemEditor(event.getCurrentItem()).removeEnchantment(Enchantment.SILK_TOUCH).hideAllFlags().getItem(), "active");
                        if (event.getClick() == ClickType.SHIFT_LEFT) {
                            List<ItemStack> selected = getSelectedCharacters(event.getInventory());
                            if (!selected.isEmpty()) {
                                // Dont look a it... It works so idc
                                // This was written at like 5AM >_>
                                String ech = "";
                                for (ItemStack ahhhh : selected) {
                                    ech += CustomHeads.getTagEditor().getTags(ahhhh).get(itemTags.indexOf("character") + 1) + ", ";
                                }
                                List<String> splitter = new ArrayList<>(Arrays.asList(ech.split(", ")));
                                List<String> loreInfo = new ArrayList<>();
                                int lines = (int) Math.ceil(splitter.size() / 8.0);
                                for (int line = 0; line < lines; line++) {
                                    int start = line * 8;
                                    int end = (line + 1) * 8;
                                    String listString = splitter.subList(start, splitter.size() > end ? end : splitter.size()).toString();
                                    listString = listString.substring(1, listString.length() - 1);
                                    loreInfo.add("&7" + listString);
                                }
                                player.openInventory(getDialog(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_CONFIRM, new String[]{"confirmDelete", "characters#>" + CustomHeads.getTagEditor().getTags(selected.get(0)).get(itemTags.indexOf("fontName") + 1) + "#>" + ech.substring(0, ech.length() - 2), "invAction", "willClose"}, new String[]{"§cThis cannot be undone!"}, new String[]{"invAction", "willClose"}, null, new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19").setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_PREINFO).setLore(loreInfo).getItem()));
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
                    if (CustomHeads.getTagEditor().getTags(event.getInventory().getItem(50)).contains("active")) {
                        ItemStack item;
                        if (itemTags.contains("charSelected")) {
                            item = getInventory(event.getCurrentItem()).getContent(true).get(event.getSlot());
                        } else {
                            item = CustomHeads.getTagEditor().addTags(new ItemEditor(event.getCurrentItem()).getItem(), "charSelected");
                            String character = CustomHeads.getTagEditor().getTags(item).get(itemTags.indexOf("character") + 1);
                            item = CustomHeads.getTagEditor().copyTags(item, new ItemEditor(Material.SKULL_ITEM, (short) 3).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_SELECTED.replace("{CHARACTER}", character.equals(" ") ? "BLANK" : character)).getItem());
                        }
                        event.setCurrentItem(item);
                    }
                    break;
                }
                case "adder":
                    if (event.getCursor() != null && event.getCursor().hasItemMeta() && event.getCursor().getItemMeta().hasDisplayName()) {
                        String toAdd = event.getCursor().getItemMeta().getDisplayName();
                        if (Utils.hasCustomTexture(event.getCursor())) {
                            HeadFontType font = HeadFontType.getCachedFont(itemTags.get(itemTags.indexOf("fontCacheID") + 1));
                            //  §\_(?)_/§
                            if (!font.exists()) {
                                player.sendMessage("For whatever Reason this Font no longer exists");
                                player.closeInventory();
                                break;
                            }
                            boolean forceReplace = CustomHeads.getTagEditor().getTags(event.getInventory().getItem(4)).contains("toggleActive");
                            if (toAdd.length() == 1 || toAdd.equalsIgnoreCase("BLANK")) {
                                player.sendMessage(font.addCharacter((toAdd.equalsIgnoreCase("BLANK") ? ' ' : toAdd.toLowerCase().charAt(0)), CustomHeads.getHeadUtil().getSkullTexture(event.getCursor()), forceReplace) ? CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_SUCCESSFUL.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd.toLowerCase()) : CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_FAILED.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd.toLowerCase()));
                                break;
                            }
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_FAILED.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd));
                    }
                    break;
                case "saveAndExit":
                    HeadFontType font = HeadFontType.getCachedFont(itemTags.get(itemTags.indexOf("fontCacheID") + 1));
                    if (font == null) {
                        player.sendMessage("§cInvalid Session");
                        break;
                    }
                    if (itemTags.contains("openScInv")) {
                        ScrollableInventory inv = ScrollableInventory.getInventoryByID(itemTags.get(itemTags.indexOf("openScInv") + 1));
                        List<ItemStack> characterList = new ArrayList<>();
                        characterList.sort(Comparator.comparing(itemStack -> itemStack.getItemMeta().getDisplayName()));
                        font.getCharacters().forEach((character, customHead) -> characterList.add(CustomHeads.getTagEditor().addTags(new ItemEditor(customHead).setDisplayName("§b" + (character.equals(' ') ? "BLANK" : character)).getItem(), "fontName", font.getFontName(), "character", "" + character, "editFont", "select")));
                        inv.setContent(characterList);
                        inv.refreshCurrentPage();
                    }
                    font.save();
                    player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_SAVE_SUCCESSFUL.replace("{FONT}", font.getFontName()));
                    break;
                case "adderToggle":
                    if (itemTags.contains("toggleActive")) {
                        event.setCurrentItem(CustomHeads.getTagEditor().removeTags(new ItemEditor(event.getCurrentItem()).removeEnchantment(Enchantment.SILK_TOUCH).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_TOGGLE.replace("{MODE}", CustomHeads.getLanguageManager().NO)).hideAllFlags().getItem(), "toggleActive"));
                    } else {
                        event.setCurrentItem(CustomHeads.getTagEditor().addTags(new ItemEditor(event.getCurrentItem()).addEnchantment(Enchantment.SILK_TOUCH, 1).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADD_TOGGLE.replace("{MODE}", CustomHeads.getLanguageManager().YES)).hideAllFlags().getItem(), "toggleActive"));
                    }
                    break;
            }
        }

        if (itemTags.contains("executeCommand")) {
            player.performCommand(itemTags.get(itemTags.indexOf("executeCommand") + 1));
        }

        if (itemTags.contains("openScInv")) {
            player.openInventory(ScrollableInventory.getInventoryByID(itemTags.get(itemTags.indexOf("openScInv") + 1)).getAsInventory());
        }

        if (itemTags.contains("openCategory")) {
            event.setCancelled(true);
            String[] args = itemTags.get(itemTags.indexOf("openCategory") + 1).split("#>");
            if (!event.getClick().isRightClick()) {
                if (menuID != null) {
                    if (args[0].equalsIgnoreCase("category")) {
                        Category category = CustomHeads.getCategoryLoader().getCategory(args[1]);
                        if (category != null) {
                            if (customHeadsPlayer.getUnlockedCategories(false).stream().map(BaseCategory::getId).collect(Collectors.toList()).contains(category.getId())) {
                                if (category.hasSubCategories()) {
                                    player.openInventory(CustomHeads.getLooks().subCategoryLooks.get(Integer.parseInt(category.getId())));
                                } else {
                                    openPreloader(player);
                                    List<ItemStack> heads = new ArrayList<>();
                                    category.getHeads().forEach(wearable -> heads.add(CustomHeads.getTagEditor().setTags(wearable, "wearable")));
                                    ScrollableInventory inventory = new ScrollableInventory(category.getName(), heads).setContentsClonable(true);
                                    inventory.setBarItem(1, Utils.getBackButton("openMenu", itemTags.get(itemTags.indexOf("menuID") + 1).toLowerCase()));
                                    inventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFO_LORE).getItem(), "dec", "info-item", "blockMoving"));
                                    player.openInventory(inventory.getAsInventory());
                                }
                            }
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("subCategory")) {
                SubCategory subCategory = CustomHeads.getCategoryLoader().getSubCategory(args[1]);
                if (subCategory != null && hasPermission(player, subCategory.getOriginCategory().getPermission())) {
                    openPreloader(player);
                    List<ItemStack> heads = new ArrayList<>(subCategory.getHeads());
                    player.openInventory(new ScrollableInventory(subCategory.getName(), heads).setBarItem(1, Utils.getBackButton("invAction", "goBack#>category#>" + subCategory.getOriginCategory().getId())).setContentsClonable(true).getAsInventory());
                }
            }
        }

        if (itemTags.contains("buyCategory")) {
            event.setCancelled(true);
            if (event.getClick().isRightClick()) {
                Category buyCategory = CustomHeads.getCategoryLoader().getCategory(itemTags.get(itemTags.indexOf("buyCategory") + 1));
                if (buyCategory != null && menuID != null) {
                    if (buyCategory.isFree()) {
                        customHeadsPlayer.unlockCategory(buyCategory);
                        openPreloader(player);
                        List<ItemStack> heads = new ArrayList<>();
                        buyCategory.getHeads().forEach(wearable -> heads.add(CustomHeads.getTagEditor().setTags(wearable, "wearable")));
                        ScrollableInventory inventory = new ScrollableInventory(buyCategory.getName(), heads).setContentsClonable(true);
                        inventory.setBarItem(1, Utils.getBackButton("openMenu", menuID));
                        inventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFO_LORE).getItem(), "dec", "info-item", "blockMoving"));
                        player.openInventory(inventory.getAsInventory());
                        player.sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_SUCCESSFUL.replace("{CATEGORY}", buyCategory.getPlainName()));
                    } else {
                        player.openInventory(getDialog(CustomHeads.getLanguageManager().ECONOMY_BUY_CATEGORY.replace("{CATEGORY}", buyCategory.getPlainName()).replace("{PRICE}", getPriceFormatted(buyCategory, false)), new String[]{"confirmBuy", buyCategory.getId() + "#>" + menuID}, null, new String[]{"openMenu", menuID}, null, buyCategory.getCategoryIcon()));
                    }
                }
            }
        }

        if (itemTags.contains("confirmBuy")) {
            event.setCancelled(true);
            if (CustomHeads.hasEconomy()) {
                String[] args = itemTags.get(itemTags.indexOf("confirmBuy") + 1).split("#>");
                CustomHeads.getEconomyManager().buyCategory(customHeadsPlayer, CustomHeads.getCategoryLoader().getCategory(args[0]), args[1]);
            }
        }

        if (itemTags.contains("openMenu")) {
            event.setCancelled(true);
            if (!hasPermission(player, CustomHeads.getLooks().getMenuInfo(itemTags.get(itemTags.indexOf("openMenu") + 1).toLowerCase())[1])) {
                return;
            }
            Inventory menu = CustomHeads.getLooks().getMenu(itemTags.get(itemTags.indexOf("openMenu") + 1).toLowerCase());
            if (menu != null)
                player.openInventory(menu);
        }

        // delet confirm
        if (itemTags.contains("confirmDelete")) {
            String[] args = itemTags.get(itemTags.indexOf("confirmDelete") + 1).split("#>");
            if (args[0].equalsIgnoreCase("head")) {
                if (customHeadsPlayer.deleteHead(args[1])) {
                    player.sendMessage(CustomHeads.getLanguageManager().REMOVE_SUCCESSFUL.replace("{NAME}", args[1]));
                } else {
                    player.sendMessage(CustomHeads.getLanguageManager().REMOVE_FAILED.replace("{NAME}", args[1]));
                }
            }
            if (args[0].equalsIgnoreCase("characters")) {
                HeadFontType fontType = new HeadFontType(args[1]);
                List<String> characters = new ArrayList<>(Arrays.asList(args[2].split(", ")));
                for (String character : characters) {
                    if (!fontType.removeCharacter(character.charAt(0))) {
                        player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_FAILED.replace("{CHARACTER}", character.substring(0, 1)));
                    }
                }
                fontType.save();
                player.sendMessage(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_SUCCESSFUL);
            }
        }

        if (itemTags.contains("invAction")) {
            event.setCancelled(true);

            String[] args = itemTags.get(itemTags.indexOf("invAction") + 1).split("#>");
            if (args[0].equalsIgnoreCase("willClose")) {
                player.closeInventory();
            } else if (args[0].equalsIgnoreCase("goBack")) {
                if (args[1].equalsIgnoreCase("menu")) {
                    Inventory menu = CustomHeads.getLooks().getCreatedMenus().get(args[2]);
                    if (menu != null)
                        player.openInventory(cloneInventory(menu, player));
                } else if (args[1].equalsIgnoreCase("category")) {
                    Category originCategory = CustomHeads.getCategoryLoader().getCategory(args[2]);
                    if (originCategory != null && hasPermission(player, originCategory.getPermission()))
                        player.openInventory(CustomHeads.getLooks().subCategoryLooks.get(Integer.parseInt(originCategory.getId())));
                }
            } else if (args[0].equalsIgnoreCase("retrySearch")) {
                openSearchGUI(player, args[1], event.getInventory().getItem(18) == null ? new String[]{"willClose"} : CustomHeads.getTagEditor().getTags(event.getInventory().getItem(18)).toArray(new String[0]));
            }
        }

        if (itemTags.contains("chItem")) {
            if (itemTags.contains("saved_heads")) {
                event.setCancelled(true);
                if (!hasPermission(player, "heads.use") || !hasPermission(player, "heads.use.more")) return;
                ScrollableInventory savedHeads = new ScrollableInventory(CustomHeads.getLanguageManager().SAVED_HEADS_TITLE.replace("{PLAYER}", player.getName()), customHeadsPlayer.getSavedHeads()).setContentsClonable(true);
                lastMenu.put(player, menuID);
                savedHeads.setBarItem(1, getBackButton("openMenu", menuID));
                savedHeads.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFO_LORE).getItem(), "dec", "info-item", "blockMoving"));
                player.openInventory(savedHeads.getAsInventory());
            }

            if (itemTags.contains("help")) {
                event.setCancelled(true);
                if (!hasPermission(player, "heads.use") || !hasPermission(player, "heads.view.help")) return;
                player.openInventory(getHelpMenu(player, "openMenu", menuID.toLowerCase()));
            }

            if (itemTags.contains("search")) {
                event.setCancelled(true);
                Utils.openSearchGUI(player, CustomHeads.getLanguageManager().CHANGE_SEARCH_STRING, "openMenu", menuID.toLowerCase());
            }

            if (itemTags.contains("find_more")) {
                event.setCancelled(true);
                player.closeInventory();
                sendJSONMessage("[\"\",{\"text\":\"" + CustomHeads.getLanguageManager().LINK + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://heads.freshcoal.com\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"§bheads.freshcoal.com\"}]}}}]", player);
            }
        }

        // scrollInv
        if (itemTags.contains("scrollInv")) {
            event.setCancelled(true);
            String uid = itemTags.get(itemTags.indexOf("scInvID") + 1);
            String[] args = itemTags.get(itemTags.indexOf("scrollInv") + 1).split("#>");
            switch (args[0]) {
                case "willClose":
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
                        String name = itemTags.get(itemTags.indexOf("originalName") + 1) + ScrollableInventory.sortName.get(nextArrangement);
                        event.setCurrentItem(renameStack(event.getCurrentItem(), 1, name, false));
                    }
                    break;
                }
            }
        }

        if (itemTags.contains("wearable")) {
            if (event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                player.sendMessage(CustomHeads.getLanguageManager().PUT_ON_HEAD.replace("{NAME}", event.getCurrentItem().getItemMeta().getDisplayName()));
                player.getInventory().setHelmet(CustomHeads.getTagEditor().setTags(event.getCurrentItem(), ""));
                player.setItemOnCursor(null);
                player.updateInventory();
                return;
            }
        }

        if (itemTags.contains("suggestCommand")) {
            event.setCancelled(true);
            player.closeInventory();
            sendJSONMessage("[\"\",{\"text\":\"" + CustomHeads.getLanguageManager().HELP_GET_COMMAND + "\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + itemTags.get(itemTags.indexOf("suggestCommand") + 1) + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + itemTags.get(itemTags.indexOf("suggestCommand") + 1) + "\",\"color\":\"aqua\"}]}}}]", player);
            return;
        }

        if (itemTags.contains("clonable")) {
            event.setCancelled(true);
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                player.getInventory().addItem(CustomHeads.getTagEditor().setTags(event.getCurrentItem(), ""));
            } else {
                player.setItemOnCursor(CustomHeads.getTagEditor().setTags(event.getCurrentItem(), ""));
            }
        }

        // No lookin at my Tags
        if (itemTags.contains("tempTags")) {
            event.setCurrentItem(TagEditor.clearTags(event.getCurrentItem()));
        }
    }

}
