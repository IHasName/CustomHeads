package de.likewhat.customheads.listener;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.api.events.PlayerClickCustomHeadEvent;
import de.likewhat.customheads.category.BaseCategory;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.category.SubCategory;
import de.likewhat.customheads.headwriter.HeadFontType;
import de.likewhat.customheads.utils.CHSearchQuery;
import de.likewhat.customheads.utils.ItemEditor;
import de.likewhat.customheads.utils.ScrollableInventory;
import de.likewhat.customheads.utils.Utils;
import de.likewhat.customheads.utils.reflection.TagEditor;
import de.likewhat.customheads.utils.updaters.GitHubDownloader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static de.likewhat.customheads.utils.Utils.*;

/*
 *  Project: CustomHeads in InventoryListener
 *     by LikeWhat
 */

public class InventoryListener implements Listener {

    private static final HashMap<UUID, String> LAST_ACTIVE_MENU = new HashMap<>();

    public static String getLastMenu(UUID uuid, boolean remove) {
        String last = LAST_ACTIVE_MENU.get(uuid);
        if (remove) {
            LAST_ACTIVE_MENU.remove(uuid);
        }
        return last;
    }

    @EventHandler
    public void onInvOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (CustomHeads.getLooks().getMenuTitles().contains(event.getView().getTitle())) {
            if (!hasPermission(player, CustomHeads.getLooks().getMenuInfo(CustomHeads.getLooks().getIDbyTitle(event.getView().getTitle()))[1])) {
                player.closeInventory();
                event.setCancelled(true);
                return;
            }
            CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
            List<Category> unlockedCategories = customHeadsPlayer.getUnlockedCategories(CustomHeads.hasEconomy() && !CustomHeads.keepCategoryPermissions());
            ItemStack[] inventoryContent = event.getInventory().getContents();
            for (int i = 0; i < inventoryContent.length; i++) {
                if (inventoryContent[i] == null) continue;
                ItemStack contentItem = inventoryContent[i];
                if (CustomHeads.getTagEditor().getTags(contentItem).contains("openCategory")) {
                    String[] categoryArgs = CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openCategory") + 1).split("#>");
                    if (categoryArgs[0].equals("category")) {
                        Category category = CustomHeads.getCategoryManager().getCategory(categoryArgs[1]);
                        ItemStack nextIcon = category.nextIcon();
                        boolean bought = false;
                        if(CustomHeads.hasEconomy() && CustomHeads.categoriesBuyable()) {
                            bought = customHeadsPlayer.getUnlockedCategories(true).contains(category);
                        }
                        nextIcon = new ItemEditor(nextIcon)
                                .setDisplayName(unlockedCategories.contains(category) ? ("§a" + nextIcon.getItemMeta().getDisplayName()) : ("§7" + ChatColor.stripColor(nextIcon.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED))
                                .addLoreLine(CustomHeads.hasEconomy() && CustomHeads.categoriesBuyable() ? bought || hasPermission(player, category.getPermission()) ? CustomHeads.getLanguageManager().ECONOMY_BOUGHT : Utils.getCategoryPriceFormatted(category, true) + "\n" + CustomHeads.getLanguageManager().ECONOMY_BUY_PROMPT : null)
                                .addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList(" ", "§7§oPermission: " + category.getPermission()) : null)
                                .getItem();
                        if (CustomHeads.hasEconomy() && CustomHeads.categoriesBuyable()) {
                            if(!(CustomHeads.keepCategoryPermissions() && hasPermission(player, category.getPermission()))) {
                                if (!bought) {
                                    nextIcon = CustomHeads.getTagEditor().addTags(nextIcon, "buyCategory", category.getId());
                                }
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
                        contentItem = new ItemEditor(contentItem).setDisplayName(hasPermission(player, "heads.use.more") ? contentItem.getItemMeta().getDisplayName().replace("{SIZE}", "(" + customHeadsPlayer.getSavedHeads().size() + ")") : "§7" + ChatColor.stripColor(contentItem.getItemMeta().getDisplayName().replace("{SIZE}", "") + " " + CustomHeads.getLanguageManager().LOCKED)).addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList(" ", "§7§oPermission: heads.use.more") : null).getItem();
                    }
                    if (CustomHeads.getTagEditor().getTags(contentItem).contains("openMenu")) {
                        contentItem = CustomHeads.getTagEditor().addTags(contentItem, "needsPermission", CustomHeads.getLooks().getMenuInfo(CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openMenu") + 1).toLowerCase())[1]);
                    }
                    if (CustomHeads.getTagEditor().getTags(contentItem).contains("needsPermission")) {
                        contentItem = new ItemEditor(contentItem)
                                .setDisplayName(hasPermission(player, CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "needsPermission") + 1)) ? "§a" + contentItem.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(contentItem.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED)
                                .addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList(" ", "§7Permission: " + CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "needsPermission") + 1)) : null)
                                .getItem();
                    }
                }
                inventoryContent[i] = CustomHeads.getTagEditor().addTags(contentItem, "menuID", CustomHeads.getLooks().getIDbyTitle(event.getView().getTitle()));
            }
            event.getInventory().setContents(inventoryContent);
        }
        player.updateInventory();
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER && event.isShiftClick()) {
            handleInventoryAction(event);
        }
        if (event.getInventory() == null || event.getRawSlot() >= event.getInventory().getSize() || event.getInventory().getType() != InventoryType.CHEST || !hasPermission(player, "heads.use")) {
            return;
        }

//        player.sendMessage("§7[CHTags Tags] §r" + CustomHeads.getTagEditor().getTags(event.getCurrentItem()) + " lastActiveMenu: " + lastActiveMenu.getOrDefault(player.getUniqueId(), "none")); // Yeah debug at its finest

        if (event.getView().getTitle().equals(CustomHeads.getLanguageManager().LOADING)) {
            event.setCancelled(true);
        }
        CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
        List<String> itemTags = CustomHeads.getTagEditor().getTags(event.getCurrentItem());

        if(itemTags.contains("headID")) {
            CustomHead head = Utils.getHeadFromItem(event.getCurrentItem());
            if(head != null) {
                PlayerClickCustomHeadEvent otherClickEvent = new PlayerClickCustomHeadEvent(player, head);
                Bukkit.getPluginManager().callEvent(otherClickEvent);
                if(otherClickEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Delete Head
        if (event.getView().getTitle().equals(CustomHeads.getLanguageManager().SAVED_HEADS_TITLE.replace("{PLAYER}", player.getName()))) {
            if (event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                String name = toConfigString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
                if (customHeadsPlayer.hasHead(name)) {
                    player.openInventory(getDialog(CustomHeads.getLanguageManager().REMOVE_CONFIRMATION.replace("{HEAD}", name), new String[]{"confirmDelete", "head#>" + name, "chItem", "saved_heads", "menuID", getLastMenu(player.getUniqueId(), false)}, new String[]{""}, new String[]{"chItem", "saved_heads", "menuID", getLastMenu(player.getUniqueId(), true)}, null, customHeadsPlayer.getHead(name)));
                }
                return;
            }
        }

        // History Inventory
        if (event.getView().getTitle().startsWith(CustomHeads.getLanguageManager().HISTORY_INV_TITLE.replace("{PLAYER}", ""))) {
            event.setCancelled(true);
            if (itemTags.contains("history")) {
                String[] args = itemTags.get(itemTags.indexOf("history") + 1).split("#>");
                switch (args[0]) {
                    case "search":
                        List<Category> categories = CustomHeads.getCategoryManager().getCategoryList();
                        categories.removeAll(CustomHeads.getApi().wrapPlayer(player).getUnlockedCategories(false));
                        event.setCurrentItem(null);
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

        if(event.getAction().name().contains("PLACE")) {
            handleInventoryAction(event);
        }

        ScrollableInventory currentScrollInventory = null;
        if (itemTags.contains("scInvID")) {
            currentScrollInventory = ScrollableInventory.getInventoryByID(itemTags.get(itemTags.indexOf("scInvID") + 1));
        }

        String menuID = null;
        if (itemTags.contains("menuID")) {
            menuID = itemTags.get(itemTags.indexOf("menuID") + 1).toLowerCase();
            //player.sendMessage("menuID present: " + menuID);
            LAST_ACTIVE_MENU.put(player.getUniqueId(), menuID);
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
                    adder.setItem(26, CustomHeads.getTagEditor().addTags(new ItemEditor(Material.SKULL_ITEM,  3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==").setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_SAVENEXIT).getItem(), "editFont", "saveAndExit", "fontCacheID", font.getCacheID(), "openScInv", currentScrollInventory.getUid()));
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
                                    String listString = splitter.subList(start, Math.min(splitter.size(), end)).toString();
                                    listString = listString.substring(1, listString.length() - 1);
                                    loreInfo.add("&7" + listString);
                                }
                                player.openInventory(getDialog(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_CONFIRM, new String[]{"confirmDelete", "characters#>" + CustomHeads.getTagEditor().getTags(selected.get(0)).get(itemTags.indexOf("fontName") + 1) + "#>" + ech.substring(0, ech.length() - 2), "invAction", "willClose"}, new String[]{"§cThis cannot be undone!"}, new String[]{"invAction", "willClose"}, null, new ItemEditor(Material.SKULL_ITEM,  3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19").setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARCATER_PREINFO).setLore(loreInfo).getItem()));
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
                            item = CustomHeads.getTagEditor().copyTags(item, new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_SELECTED.replace("{CHARACTER}", character.equals(" ") ? "BLANK" : character)).getItem());
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
                            //  ¯\_( ツ)_/¯
                            if (!font.exists()) {
                                player.sendMessage("For whatever Reason this Font no longer exists");
                                player.closeInventory();
                                break;
                            }
                            boolean forceReplace = CustomHeads.getTagEditor().getTags(event.getInventory().getItem(4)).contains("toggleActive");
                            if (toAdd.length() == 1 || toAdd.equalsIgnoreCase("BLANK")) {
                                player.sendMessage(font.addCharacter((toAdd.equalsIgnoreCase("BLANK") ? ' ' : toAdd.toLowerCase().charAt(0)), CustomHeads.getApi().getSkullTexture(event.getCursor()), forceReplace) ? CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_SUCCESSFUL.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd.toLowerCase()) : CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER_FAILED.replace("{CHARACTER}", toAdd.equalsIgnoreCase("blank") ? "BLANK" : toAdd.toLowerCase()));
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
                        font.getCharacterItems().forEach((character, customHead) -> characterList.add(CustomHeads.getTagEditor().addTags(new ItemEditor(customHead).setDisplayName("§b" + (character.equals(' ') ? "BLANK" : character)).getItem(), "fontName", font.getFontName(), "character", "" + character, "editFont", "select")));
                        characterList.sort(Comparator.comparing(itemStack -> itemStack.getItemMeta().getDisplayName()));
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

        if(itemTags.contains("executeCommand")) {
            event.setCancelled(true);
            player.performCommand(itemTags.get(itemTags.indexOf("executeCommand") + 1));
        }

        if (itemTags.contains("openScInv")) {
            player.openInventory(ScrollableInventory.getInventoryByID(itemTags.get(itemTags.indexOf("openScInv") + 1)).getAsInventory());
        }

        if (itemTags.contains("openCategory")) {
            event.setCancelled(true);
            String[] args = itemTags.get(itemTags.indexOf("openCategory") + 1).split("#>");
            if (!event.getClick().isRightClick()) {
                if (args[0].equalsIgnoreCase("category")) {
                    Category category = CustomHeads.getCategoryManager().getCategory(args[1]);
                    if (category != null) {
                        if (menuID != null) {
                            openCategory(category, player, new String[]{"openMenu", menuID});
                        } else {
                            if (category.hasSubCategories()) {
                                openCategory(category, player, new String[] {"openCategory", "subCategory#>" + category.getId()});
                            } else if (LAST_ACTIVE_MENU.containsKey(player.getUniqueId())){
                                openCategory(category, player, new String[]{"openMenu", LAST_ACTIVE_MENU.get(player.getUniqueId())});
                            } else {
                                player.closeInventory();
                            }
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("subCategory")) {
                SubCategory subCategory = CustomHeads.getCategoryManager().getSubCategory(args[1]);
                if (subCategory != null) {
                    openCategory(subCategory, player, new String[]{"openCategory", "category#>" + subCategory.getOriginCategory().getId()});
                }
            }

        }

        if (itemTags.contains("buyCategory")) {
            event.setCancelled(true);
            if (event.getClick().isRightClick()) {
                Category buyCategory = CustomHeads.getCategoryManager().getCategory(itemTags.get(itemTags.indexOf("buyCategory") + 1));
                if (buyCategory != null && menuID != null) {
                    if (buyCategory.isFree()) {
                        customHeadsPlayer.unlockCategory(buyCategory);
                        openCategory(buyCategory, player, new String[]{"openMenu", menuID});
                        player.sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_SUCCESSFUL.replace("{ITEM}", buyCategory.getPlainName()));
                    } else {
                        player.openInventory(getDialog(CustomHeads.getLanguageManager().ECONOMY_BUY_CONFIRM.replace("{ITEM}", buyCategory.getPlainName()).replace("{PRICE}", getCategoryPriceFormatted(buyCategory, false)), new String[]{"confirmBuy", "category#>" + buyCategory.getId() + "#>" + menuID}, null, new String[]{"openMenu", menuID}, null, buyCategory.getCategoryIcon()));
                    }
                }
            }
        }

        if (itemTags.contains("buyHead")) {
            event.setCancelled(true);
            if (event.getClick().isRightClick()) {
                String[] idParts = itemTags.get(itemTags.indexOf("buyHead") + 1).split("#>")[1].split(":");
                CustomHead buyHead = CustomHeads.getApi().getHead(CustomHeads.getCategoryManager().getCategory(idParts[0]), Integer.parseInt(idParts[1]));
                if (buyHead != null) {
                    if (buyHead.isFree()) {
                        if (CustomHeads.headsPermanentBuy()) {
                            buyHead.unlockFor(customHeadsPlayer);
                            currentScrollInventory.setItemOnCurrentPage(event.getSlot(), CustomHeads.getTagEditor().addTags(new ItemEditor(buyHead).addLoreLine(CustomHeads.getLanguageManager().ECONOMY_BOUGHT).getItem(), "wearable", "clonable"));
                        } else {
                            player.setItemOnCursor(buyHead.getPlainItem());
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_SUCCESSFUL.replace("{ITEM}", ChatColor.stripColor(buyHead.getItemMeta().getDisplayName())));
                        currentScrollInventory.refreshCurrentPage();
                    } else {
                        Category category = buyHead.getOriginCategory();
                        player.openInventory(getDialog(CustomHeads.getLanguageManager().ECONOMY_BUY_CONFIRM.replace("{ITEM}", ChatColor.stripColor(buyHead.getItemMeta().getDisplayName())).replace("{PRICE}", getHeadPriceFormatted(buyHead, false)),
                                new String[]{"confirmBuy", "head#>" + idParts[0] + ":" + idParts[1] + "#>" + category.getId(), /*"menuID", getLastMenu(player, false),*/ "openCategory", "category#>" + category.getId()}, null,
                                new String[]{"openCategory", "category#>" + category.getId(), /*"menuID", getLastMenu(player, false)*/}, null, buyHead.getPlainItem()));
                    }
                }
            }
        }

        if (itemTags.contains("confirmBuy")) {
            event.setCancelled(true);
            if (CustomHeads.hasEconomy()) {
                String[] args = itemTags.get(itemTags.indexOf("confirmBuy") + 1).split("#>");
                if (args[0].equals("category")) {
                    CustomHeads.getEconomyManager().buyCategory(customHeadsPlayer, CustomHeads.getCategoryManager().getCategory(args[1]), args[2]);
                } else if (args[0].equals("head")) {
                    String[] idParts = args[1].split(":");
                    CustomHeads.getEconomyManager().buyHead(customHeadsPlayer, CustomHeads.getCategoryManager().getCategory(idParts[0]), Integer.parseInt(idParts[1]), CustomHeads.headsPermanentBuy());
                }
            }
        }

        if (itemTags.contains("openMenu")) {
            event.setCancelled(true);
            if (!hasPermission(player, CustomHeads.getLooks().getMenuInfo(itemTags.get(itemTags.indexOf("openMenu") + 1).toLowerCase())[1])) {
                return;
            }
            String mId = itemTags.get(itemTags.indexOf("openMenu") + 1).toLowerCase();
            Inventory menu = CustomHeads.getLooks().getMenu(mId);
            LAST_ACTIVE_MENU.put(player.getUniqueId(), mId);
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
                        player.openInventory(cloneInventory(menu, CustomHeads.getLooks().getCreatedMenuTitles().get(args[2]), player));
                } else if (args[1].equalsIgnoreCase("category")) {
                    Category originCategory = CustomHeads.getCategoryManager().getCategory(args[2]);
                    if (originCategory != null && customHeadsPlayer.getUnlockedCategories(CustomHeads.hasEconomy() && !CustomHeads.keepCategoryPermissions()).contains(originCategory))
                        player.openInventory(CustomHeads.getLooks().subCategoryLooks.get(Integer.parseInt(originCategory.getId())));
                }
            } else if (args[0].equalsIgnoreCase("retrySearch")) {
                openSearchGUI(player, args[1], event.getInventory().getItem(18) == null ? new String[]{"willClose"} : CustomHeads.getTagEditor().getTags(event.getInventory().getItem(18)).toArray(new String[0]));
            } else if (args[0].equalsIgnoreCase("downloadLanguage")) {
                player.closeInventory();
                GitHubDownloader downloader = new GitHubDownloader("IHasName", "CustomHeads").enableAutoUnzipping();
                downloader.download(CustomHeads.getInstance().getDescription().getVersion(), args[1], new File(CustomHeads.getInstance().getDataFolder(), "language"), () -> player.sendMessage(CustomHeads.getLanguageManager().LANGUAGE_DOWNLOAD_SUCCESSFUL.replace("{LANGUAGE}", args[1])));
            }
        }

        if (itemTags.contains("chItem")) {
            if (itemTags.contains("saved_heads")) {
                event.setCancelled(true);
                if (!hasPermission(player, "heads.use") || !hasPermission(player, "heads.use.more")) return;
                ScrollableInventory savedHeads = new ScrollableInventory(CustomHeads.getLanguageManager().SAVED_HEADS_TITLE.replace("{PLAYER}", player.getName()), customHeadsPlayer.getSavedHeads()).setContentsClonable(true);
                LAST_ACTIVE_MENU.put(player.getUniqueId(), menuID);
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
                        event.setCurrentItem(new ItemEditor(event.getCurrentItem()).setDisplayName(itemTags.get(itemTags.indexOf("originalName") + 1) + ScrollableInventory.sortName.get(nextArrangement)).getItem());
                    }
                    break;
                }
            }
        }

        if (itemTags.contains("wearable")) {
            if (event.getClick() == ClickType.SHIFT_RIGHT && !itemTags.contains("buyHead")) {
                event.setCancelled(true);
                PlayerInventory playerInventory = player.getInventory();
                if(playerInventory.getHelmet() != null) {
                    ItemStack helmetItem = playerInventory.getHelmet().clone();
                    if (Utils.getNextFreeInventorySlot(playerInventory, helmetItem) < 0) {
                        return; // We can't add the Helmet to the Inventory. So we don't do anything
                    }
                    playerInventory.setHelmet(null);
                    playerInventory.addItem(helmetItem);
                }
                player.sendMessage(CustomHeads.getLanguageManager().PUT_ON_HEAD.replace("{NAME}", event.getCurrentItem().getItemMeta().getDisplayName()));
                playerInventory.setHelmet(TagEditor.clearTags(event.getCurrentItem()));
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
            Category category = null;
            if(itemTags.contains("headID")) {
                String[] id = itemTags.get(itemTags.indexOf("headID")+1).split(":");
                category = CustomHeads.getCategoryManager().getCategory(id[0]);
            }
            if (getHeadFromItem(event.getCurrentItem()) == null || (category != null && hasPermission(player, category.getPermission() + ".allheads")) || customHeadsPlayer.getUnlockedHeads().contains(getHeadFromItem(event.getCurrentItem())) || getHeadFromItem(event.getCurrentItem()).isFree()) {
                ItemEditor itemEditor = new ItemEditor(event.getCurrentItem());
                if (!CustomHeads.shouldGrabHeadToCursor() || event.getClick() == ClickType.SHIFT_LEFT) {
                    player.getInventory().addItem(TagEditor.clearTags(itemEditor.removeLoreLine(itemEditor.hasLore() ? itemEditor.getLore().size() - 1 : 0).getItem()));
                } else {
                    player.setItemOnCursor(TagEditor.clearTags(itemEditor.removeLoreLine(itemEditor.hasLore() ? itemEditor.getLore().size() - 1 : 0).getItem()));
                }
            }
        }

        // No lookin at my Tags
        if (itemTags.contains("tempTags") && player.getItemOnCursor() != null) {
            player.setItemOnCursor(TagEditor.clearTags(event.getCurrentItem()));
        }
    }

    @EventHandler
    public void onEvent(InventoryDragEvent event) {
        if (event.getInventory() == null || event.getRawSlots().stream().max(Integer::compare).orElse(0) >= event.getInventory().getSize() || event.getInventory().getType() != InventoryType.CHEST || !hasPermission((Player)event.getWhoClicked(), "heads.use")) {
            return;
        }

        handleInventoryAction(event);
    }

    private void handleInventoryAction(InventoryInteractEvent event) {
        Player player = (Player)event.getWhoClicked();

        for(String name : CustomHeads.getCategoryManager().getAllCategories().stream().map(BaseCategory::getName).collect(Collectors.toList())) {
            if(event.getView().getTitle().equals(name)) {
                event.setCancelled(true);
                player.updateInventory();
                break;
            }
        }

        for(String name : CustomHeads.getLooks().getCreatedMenuTitles().values()) {
            if(event.getView().getTitle().equals(name)) {
                event.setCancelled(true);
                player.updateInventory();
                break;
            }
        }

        for (ItemStack item : event.getInventory().getContents()) {
            if (CustomHeads.getTagEditor().hasMyTags(item)) {
                event.setCancelled(true);
                player.updateInventory();
                break;
            }
        }
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(LAST_ACTIVE_MENU.containsKey(player)) {
//            player.sendMessage("Clearing Last Menu");
            LAST_ACTIVE_MENU.remove(player);
        }
    }

}
