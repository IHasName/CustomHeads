package de.mrstein.customheads.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHead;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.reflection.AnvilGUI;
import de.mrstein.customheads.reflection.TagEditor;
import de.mrstein.customheads.stuff.CHSearchQuery;
import de.mrstein.customheads.stuff.History;
import de.mrstein.customheads.updaters.AfterTask;
import de.mrstein.customheads.updaters.GitHubDownloader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

    private static HashMap<String, String[]> subCommands;
    private static HashMap<Character, ItemStack> alphabet;
    private static HashMap<String, String[]> perms;

    public static ItemStack createHead(String displayName, String lore, String texture) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        RefSet(headMetaClass, headMeta, "profile", GameProfileBuilder.createProfileWithTexture(texture));
        head.setItemMeta(headMeta);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(Arrays.asList(lore.split("\n")));
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack createHead(String displayName, String texture) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        if (!RefSet(headMetaClass, headMeta, "profile", GameProfileBuilder.createProfileWithTexture(texture))) {
            return null;
        }
        head.setItemMeta(headMeta);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(displayName);
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack createHead(String displayName, GameProfile profile) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        if (!RefSet(headMetaClass, headMeta, "profile", profile)) {
            return null;
        }
        head.setItemMeta(headMeta);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(displayName);
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack createPlayerHead(String displayname, String playername) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(displayname);
        skullMeta.setOwner(playername);
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack createPlayerHead(String displayname, String playername, String lore) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(displayname);
        skullMeta.setOwner(playername);
        if (lore.length() > 0) {
            skullMeta.setLore(Arrays.asList(lore.split("\n")));
        }
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack createPlayerHead(String displayname, String playername, List<String> lore) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(displayname);
        skullMeta.setOwner(playername);
        if (lore != null && !lore.isEmpty()) {
            List<String> temp = new ArrayList<>();
            for (String s : lore) {
                temp.add("§7" + s);
            }
            skullMeta.setLore(temp);
        }
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack createItem(Material material, int amount, String itemname, short... data) {
        ItemStack item = data.length == 0 ? new ItemStack(material, amount) : new ItemStack(material, amount, data[0]);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemname);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, String itemname, String lore, short... data) {
        ItemStack item = data.length == 0 ? new ItemStack(material, amount) : new ItemStack(material, amount, data[0]);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemname);
        if (!lore.isEmpty()) {
            meta.setLore(Arrays.asList(lore.split("\n")));
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, String itemname, List<String> lore, short... data) {
        ItemStack item = data.length == 0 ? new ItemStack(material, amount) : new ItemStack(material, amount, data[0]);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemname);
        if (lore != null && !lore.isEmpty()) {
            List<String> temp = new ArrayList<>();
            for (String s : lore) {
                temp.add("§7" + s);
            }
            meta.setLore(temp);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack renameStack(ItemStack stack, int newamount, String newitename, boolean remlore) {
        ItemStack item = stack.clone();
        item.setAmount(newamount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(newitename);
        if (remlore && meta.hasLore()) {
            meta.setLore(null);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static String getTextureFromProfile(GameProfile profile) {
        String value = "";
        if (profile == null) {
            throw new NullPointerException("Profile is null");
        }
        for (Property prop : profile.getProperties().get("textures")) {
            value = prop.getValue();
        }
        return new CustomHead(getURLFrom(value)).getTextureValue();
    }

    public static ItemStack getBackButton(String... action) {
        return CustomHeads.getTagEditor().addTags(Utils.createHead(CustomHeads.getLanguageManager().BACK_GENERAL, CustomHeads.getLanguageManager().BACK_TO_PREVIOUS, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ=="), action);
    }

    public static HashMap<String, String[]> getPermissions() {
        if (perms == null) {
            perms = new HashMap<>();
            perms.put("heads.view.help", new String[]{"help"});
            perms.put("heads.use.more", new String[]{"remove", "add", "firework"});
            perms.put("heads.use.more.get", new String[]{"get"});
            perms.put("heads.admin", new String[]{"reload", "categories"});
            perms.put("heads.view.history", new String[]{"history"});
            perms.put("heads.use.more.search", new String[]{"search"});
            perms.put("heads.use.more.write", new String[]{"write", "undo", "fonts"});
        }
        return perms;
    }

    public static List<String> getPermissions(Player player) {
        return getPermissions().keySet().stream().filter(permission -> hasPermission(player, permission) || permission.equals("")).collect(Collectors.toList());
    }

    public static List<Category> getAvailableCategories(Player player) {
        return CustomHeads.getCategoryImporter().getCategoryList().stream().filter(category -> hasPermission(player, category.getPermission())).collect(Collectors.toList());
    }

    public static void openSearchGUI(Player player, String itemname, String... action) {
        AnvilGUI gui = new AnvilGUI(player, event -> {
            event.setCancelled(true);
            if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                if (event.getName().equals("")) {
                    return;
                }
                if (event.getName().length() > 16) {
                    event.getPlayer().sendMessage(CustomHeads.getLanguageManager().TO_LONG_INPUT);
                    return;
                }
                event.getPlayer().sendMessage(CustomHeads.getLanguageManager().SEARCHING.replace("{SEARCH}", event.getName()));
                CHSearchQuery query = new CHSearchQuery(event.getName());
                if (query.getResults().isEmpty()) {
                    Inventory noRes = Bukkit.createInventory(event.getPlayer(), 9 * 3, CustomHeads.getLanguageManager().NO_RESULTS);
                    noRes.setItem(13, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.BARRIER, 1, CustomHeads.getLanguageManager().NO_RESULTS, "", (short) 0), "blockMoving"));
                    if (action.length > 0) {
                        noRes.setItem(18, Utils.getBackButton(action));
                    } else {
                        noRes.setItem(18, Utils.getBackButton("invAction", "close#>"));
                    }
                    noRes.setItem(26, CustomHeads.getTagEditor().setTags(Utils.createHead(CustomHeads.getLanguageManager().NO_RESULTS_TRY_AGAIN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), "invAction", "retrySearch#>" + event.getName()));
                    player.closeInventory();
                    new BukkitRunnable() {
                        public void run() {
                            player.openInventory(noRes);
                        }
                    }.runTaskLater(CustomHeads.getInstance(), 20);
                    return;
                }
                query.viewTo(event.getPlayer(), "close");
            }
        });
        gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, Utils.createItem(Material.PAPER, 1, itemname, CustomHeads.getLanguageManager().SEARCH_LORE));
        gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, Utils.createItem(Material.PAPER, 1, itemname, CustomHeads.getLanguageManager().SEARCH_LORE));
        gui.open();
        player.updateInventory();
    }

    public static void openGetGUI(Player player) {
        AnvilGUI gui = new AnvilGUI(player, event -> {
            event.update();
            if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                if (event.getItem().getType() == Material.PAPER) {
                    event.setCancelled(true);
                    if (event.getName().isEmpty()) {
                        return;
                    }
                    if (event.getName().length() > 16) {
                        event.getPlayer().sendMessage(CustomHeads.getLanguageManager().TO_LONG_INPUT);
                        return;
                    }
                    new History(event.getPlayer()).getGetHistory().addEntry(event.getName());
                    event.getPlayer().setItemOnCursor(Utils.createPlayerHead(CustomHeads.getLanguageManager().GET_HEAD_NAME.replace("{PLAYER}", event.getName()), event.getName(), ""));
                }
            } else if (event.getSlot() == AnvilGUI.AnvilSlot.INPUT_LEFT || event.getSlot() == AnvilGUI.AnvilSlot.INPUT_RIGHT) {
                event.setCancelled(true);
            }
        });
        gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, Utils.createItem(Material.PAPER, 1, CustomHeads.getLanguageManager().CHANGE_SEARCH_STRING, CustomHeads.getLanguageManager().SEARCH_LORE));
        gui.open();
    }

    // The good ol preloader that you lituarly only see for a fraction of a second
    public static void openPreloader(Player player) {
        player.closeInventory();
        Inventory preLoader = Bukkit.createInventory(null, 27, CustomHeads.getLanguageManager().LOADING);
        preLoader.setItem(13, CustomHeads.getTagEditor().addTags(new ItemEditor(Material.WATCH).setDisplayName(CustomHeads.getLanguageManager().LOADING).getItem(), "blockMoving"));
        player.openInventory(preLoader);
    }

    // Generating this because im lazy >_>
    public static Inventory getHelpMenu(Player player, String... action) {
        List<int[]> line = Arrays.asList(
                new int[]{0, 0, 0, 1, 0, 0, 0},
                new int[]{0, 0, 1, 0, 1, 0, 0},
                new int[]{0, 0, 1, 1, 1, 0, 0},
                new int[]{0, 1, 1, 0, 1, 1, 0},
                new int[]{0, 1, 1, 1, 1, 1, 0},
                new int[]{1, 1, 1, 0, 1, 1, 1},
                new int[]{1, 1, 1, 1, 1, 1, 1});
        List<ItemStack> items = new ArrayList<>();
        for (String permission : getPermissions(player)) {
            for (String subCommand : getPermissions().get(permission)) {
                items.add(CustomHeads.getTagEditor().setTags(new ItemEditor(getAlphabetCharacter(subCommand.charAt(0))).setDisplayName("§e" + getSubCommands().get(subCommand)[2]).setLore(Arrays.asList(getSubCommands().get(subCommand)[1].replace("{PERMISSION}", hasPermission(player, "heads.view.permissions") ? getSubCommands().get(subCommand)[3] : "").split("\n"))).getItem(), "suggestCommand", getSubCommands().get(subCommand)[2]));
            }
        }
        int lines = (int) Math.ceil(items.size() / 7.0);
        Inventory helpMenu = Bukkit.createInventory(null, 9 * (2 + lines > 6 ? 6 : 2 + lines), CustomHeads.getLanguageManager().ITEMS_HELP);
        int itemCount = items.size();
        int offset = 10;
        int itemCounter = 0;
        while (itemCount >= 0) {
            int counter = 0;
            for (int lineIndex : line.get((itemCount > 7 ? 7 : itemCount) - 1)) {
                if (lineIndex == 1) {
                    helpMenu.setItem(offset + counter, items.get(itemCounter));
                    itemCounter++;
                }
                counter++;
            }
            offset += 9;
            itemCount -= 7;
        }
        if (action.length > 0) {
            helpMenu.setItem(helpMenu.getSize() - 9, getBackButton(action));
        }
        return helpMenu;
    }

    public static HashMap<String, String[]> getSubCommands() {
        if (subCommands == null) {
            subCommands = new HashMap<>();
            subCommands.put("", new String[]{"heads.use", "§eOpens the Main Menu\n{PERMISSION}", "/heads", "§7Permission: §eheads.use\n"});
            subCommands.put("help", new String[]{"heads.view.help", "§eOpens this Menu\n{PERMISSION}", "/heads help", "§7Permission: §eheads.view.help\n"});
            subCommands.put("remove", new String[]{"heads.use.more", "§eRemove an Head from your Collection\n{PERMISSION}§eTip: §7You can Tab to \n§7AutoComplete your Heads", "/heads remove <headname>", "§7Permission: §eheads.use.more\n"});
            subCommands.put("add", new String[]{"heads.use.more", "§eAdd an Head to your Collection\n{PERMISSION}", "/heads add <headname> [-t]", "§7Permission: §eheads.use.more\n§7heads.use.saveastexture for -t\n"});
            subCommands.put("firework", new String[]{"heads.use.more", "§eStart some Fireworks\n{PERMISSION}", "/heads firework", "§7Permission: §eheads.use.more\n"});
            subCommands.put("get", new String[]{"heads.use.more.get", "§eGives you an Head from an Player\n{PERMISSION}", "/heads get <playername>", "§7Permission: §eheads.use.more.get\n"});
            subCommands.put("reload", new String[]{"heads.admin", "§eReloads the Plugin\n{PERMISSION}§eGood to know: §7heads.admin acts like heads.*", "/heads reload", "§7Permission: §eheads.admin\n"});
            subCommands.put("categories", new String[]{"heads.admin", "§eShows you a List of all Categories\n{PERMISSION}§eGood to know: §7heads.admin acts like heads.*", "/heads categories [remove, delete, import]", "§7Permission: §eheads.admin\n"});
            subCommands.put("history", new String[]{"heads.view.history", "§eShows the History of an Player\n{PERMISSION}", "/heads history", "§7Permission: §eheads.view.history.[playername]\n"});
            subCommands.put("search", new String[]{"heads.use.more.search", "§eSearches for an Head from the Categories\n{PERMISSION}", "/heads search <query>", "§7Permission: §eheads.use.more.search\n"});
            subCommands.put("write", new String[]{"heads.use.more.write", "§eWrites an Text in Heads\n§c§lBeta-Command!\n{PERMISSION}", "/heads write <wood, stone> <text>", "§7Permission: §eheads.use.more.write\n"});
            subCommands.put("fonts", new String[]{"heads.use.more.write", "§eShows you a List of Fonts\n§eyou can use with /heads write\n{PERMISSION}", "/heads fonts <[create, remove]> <[fontname]>", "§7Permission: §eheads.use.more.write\n"});
            subCommands.put("undo", new String[]{"heads.use.more.write", "§eUndo Heads written with /heads write\n§c§lBeta-Command!\n{PERMISSION}", "/heads undo [times]", "§7Permission: §eheads.use.more.write\n"});
        }
        return subCommands;
    }

    public static ItemStack getAlphabetCharacter(char character) {
        if (alphabet == null) {
            alphabet = new HashMap<>();
            alphabet.put('a', createHead("a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0="));
            alphabet.put('b', createHead("b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBjMWI1ODRmMTM5ODdiNDY2MTM5Mjg1YjJmM2YyOGRmNjc4NzEyM2QwYjMyMjgzZDg3OTRlMzM3NGUyMyJ9fX0="));
            alphabet.put('c', createHead("c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJlOTgzZWM0NzgwMjRlYzZmZDA0NmZjZGZhNDg0MjY3NjkzOTU1MWI0NzM1MDQ0N2M3N2MxM2FmMThlNmYifX19"));
            alphabet.put('d', createHead("d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5M2RjMGQ0YzVlODBmZjlhOGEwNWQyZmNmZTI2OTUzOWNiMzkyNzE5MGJhYzE5ZGEyZmNlNjFkNzEifX19"));
            alphabet.put('e', createHead("e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJiMjczN2VjYmY5MTBlZmUzYjI2N2RiN2Q0YjMyN2YzNjBhYmM3MzJjNzdiZDBlNGVmZjFkNTEwY2RlZiJ9fX0="));
            alphabet.put('f', createHead("f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE4M2JhYjUwYTMyMjQwMjQ4ODZmMjUyNTFkMjRiNmRiOTNkNzNjMjQzMjU1OWZmNDllNDU5YjRjZDZhIn19fQ=="));
            alphabet.put('g', createHead("g", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNhM2YzMjRiZWVlZmI2YTBlMmM1YjNjNDZhYmM5MWNhOTFjMTRlYmE0MTlmYTQ3NjhhYzMwMjNkYmI0YjIifX19"));
            alphabet.put('h', createHead("h", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFmMzQ2MmE0NzM1NDlmMTQ2OWY4OTdmODRhOGQ0MTE5YmM3MWQ0YTVkODUyZTg1YzI2YjU4OGE1YzBjNzJmIn19fQ=="));
            alphabet.put('i', createHead("i", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxNzhhZDUxZmQ1MmIxOWQwYTM4ODg3MTBiZDkyMDY4ZTkzMzI1MmFhYzZiMTNjNzZlN2U2ZWE1ZDMyMjYifX19"));
            alphabet.put('j', createHead("j", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E3OWRiOTkyMzg2N2U2OWMxZGJmMTcxNTFlNmY0YWQ5MmNlNjgxYmNlZGQzOTc3ZWViYmM0NGMyMDZmNDkifX19"));
            alphabet.put('k', createHead("k", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ2MWIzOGM4ZTQ1NzgyYWRhNTlkMTYxMzJhNDIyMmMxOTM3NzhlN2Q3MGM0NTQyYzk1MzYzNzZmMzdiZTQyIn19fQ=="));
            alphabet.put('l', createHead("l", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5ZjUwYjQzMmQ4NjhhZTM1OGUxNmY2MmVjMjZmMzU0MzdhZWI5NDkyYmNlMTM1NmM5YWE2YmIxOWEzODYifX19"));
            alphabet.put('m', createHead("m", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljNDVhMjRhYWFiZjQ5ZTIxN2MxNTQ4MzIwNDg0OGE3MzU4MmFiYTdmYWUxMGVlMmM1N2JkYjc2NDgyZiJ9fX0= "));
            alphabet.put('n', createHead("n", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViOGIzZDhjNzdkZmI4ZmJkMjQ5NWM4NDJlYWM5NGZmZmE2ZjU5M2JmMTVhMjU3NGQ4NTRkZmYzOTI4In19fQ=="));
            alphabet.put('o', createHead("o", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDExZGUxY2FkYjJhZGU2MTE0OWU1ZGVkMWJkODg1ZWRmMGRmNjI1OTI1NWIzM2I1ODdhOTZmOTgzYjJhMSJ9fX0="));
            alphabet.put('p', createHead("p", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBhNzk4OWI1ZDZlNjIxYTEyMWVlZGFlNmY0NzZkMzUxOTNjOTdjMWE3Y2I4ZWNkNDM2MjJhNDg1ZGMyZTkxMiJ9fX0="));
            alphabet.put('q', createHead("q", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM2MDlmMWZhZjgxZWQ0OWM1ODk0YWMxNGM5NGJhNTI5ODlmZGE0ZTFkMmE1MmZkOTQ1YTU1ZWQ3MTllZDQifX19"));
            alphabet.put('r', createHead("r", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVjZWQ5OTMxYWNlMjNhZmMzNTEzNzEzNzliZjA1YzYzNWFkMTg2OTQzYmMxMzY0NzRlNGU1MTU2YzRjMzcifX19"));
            alphabet.put('s', createHead("s", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0MWM2MDU3MmM1MzNlOTNjYTQyMTIyODkyOWU1NGQ2Yzg1NjUyOTQ1OTI0OWMyNWMzMmJhMzNhMWIxNTE3In19fQ=="));
            alphabet.put('t', createHead("t", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU2MmU4YzFkNjZiMjFlNDU5YmU5YTI0ZTVjMDI3YTM0ZDI2OWJkY2U0ZmJlZTJmNzY3OGQyZDNlZTQ3MTgifX19"));
            alphabet.put('u', createHead("u", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA3ZmJjMzM5ZmYyNDFhYzNkNjYxOWJjYjY4MjUzZGZjM2M5ODc4MmJhZjNmMWY0ZWZkYjk1NGY5YzI2In19fQ=="));
            alphabet.put('v', createHead("v", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5YTEzODYzOGZlZGI1MzRkNzk5Mjg4NzZiYWJhMjYxYzdhNjRiYTc5YzQyNGRjYmFmY2M5YmFjNzAxMGI4In19fQ=="));
            alphabet.put('w', createHead("w", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY5YWQxYTg4ZWQyYjA3NGUxMzAzYTEyOWY5NGU0YjcxMGNmM2U1YjRkOTk1MTYzNTY3ZjY4NzE5YzNkOTc5MiJ9fX0="));
            alphabet.put('x', createHead("x", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2Nzg3YmEzMjU2NGU3YzJmM2EwY2U2NDQ5OGVjYmIyM2I4OTg0NWU1YTY2YjVjZWM3NzM2ZjcyOWVkMzcifX19"));
            alphabet.put('y', createHead("y", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyZmIzODhlMzMyMTJhMjQ3OGI1ZTE1YTk2ZjI3YWNhNmM2MmFjNzE5ZTFlNWY4N2ExY2YwZGU3YjE1ZTkxOCJ9fX0="));
            alphabet.put('z', createHead("z", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA1ODJiOWI1ZDk3OTc0YjExNDYxZDYzZWNlZDg1ZjQzOGEzZWVmNWRjMzI3OWY5YzQ3ZTFlMzhlYTU0YWU4ZCJ9fX0="));
        }
        return alphabet.get(character);
    }

    public static ScrollableInventory getInventory(ItemStack item) {
        return ScrollableInventory.getInventoryByID(CustomHeads.getTagEditor().getTags(item).get(CustomHeads.getTagEditor().indexOf(item, "scInvID") + 1));
    }

    public static List<ItemStack> getSelectedCharacters(Inventory inventory) {
        return Arrays.stream(inventory.getContents()).filter(itemStack -> itemStack != null && CustomHeads.getTagEditor().getTags(itemStack).contains("charSelected")).collect(Collectors.toList());
    }

    public static Inventory getDeleteDialog(String title, String[] yesAction, String[] noAction, ItemStack middleItem) {
        Inventory dialog = Bukkit.createInventory(null, 9 * 3, title);
        dialog.setItem(11, CustomHeads.getTagEditor().setTags(new CustomHead("http://textures.minecraft.net/texture/361e5b333c2a3868bb6a58b6674a2639323815738e77e053977419af3f77").setDisplayName("§a" + CustomHeads.getLanguageManager().YES).setLore("§cThis cannot be undone!").toItem(), yesAction));
        dialog.setItem(13, CustomHeads.getTagEditor().setTags(middleItem, "blockMoving"));
        dialog.setItem(15, CustomHeads.getTagEditor().setTags(new CustomHead("http://textures.minecraft.net/texture/4bac77520b9eee65068ef1cd8abeadb013b4de3953fd29ac68e90e4866227").setDisplayName("§c" + CustomHeads.getLanguageManager().NO).toItem(), noAction));
        return dialog;
    }

    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission) || player.hasPermission("heads.admin") || player.hasPermission("heads.*") || player.isOp();
    }

    public static List<ItemStack> getHeadsAsItemStacks(List<CustomHead> heads) {
        List<ItemStack> items = new ArrayList<>();
        heads.forEach(customHead -> items.add(customHead.toItem()));
        return items;
    }

    public static String getURLFrom(String base64) {
        return new JsonParser().parse(new String(Base64.decodeBase64(base64), Charsets.UTF_8)).getAsJsonObject().get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
    }

    public static UUID parseUUID(String uuidStr) {
        System.out.println(uuidStr);
        return UUID.fromString(uuidStr.substring(0, 8) + "-" + uuidStr.substring(8, 12) + "-" + uuidStr.substring(12, 16) + "-" + uuidStr.substring(16, 20) + "-" + uuidStr.substring(20, uuidStr.length()));
    }

    public static Inventory cloneInventory(Inventory inventory) {
        Inventory inv = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), inventory.getTitle());
        inv.setContents(inventory.getContents());
        return inv;
    }

    public static Inventory cloneInventory(Inventory inventory, Player newOwner) {
        Inventory inv = Bukkit.createInventory(newOwner, inventory.getSize(), inventory.getTitle());
        inv.setContents(inventory.getContents());
        return inv;
    }

    public static boolean RefSet(Class<?> sourceClass, Object instance, String fieldName, Object value) {
        try {
            Field field = sourceClass.getDeclaredField(fieldName);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            int modifiers = modifiersField.getModifiers();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if ((modifiers & Modifier.FINAL) == Modifier.FINAL) {
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
            }
            try {
                field.set(instance, value);
            } finally {
                if ((modifiers & Modifier.FINAL) == Modifier.FINAL) {
                    modifiersField.setInt(field, modifiers | Modifier.FINAL);
                }
                if (!field.isAccessible()) {
                    field.setAccessible(false);
                }
            }
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Unable to inject Gameprofile", e);
        }
        return false;
    }

    public static boolean charArrayContains(char[] charArray, char containsChar) {
        for (char c : charArray) {
            if (c == containsChar) {
                return true;
            }
        }
        return false;
    }

    public static void saveInternalFile(Plugin plugin, String filename, String... sub) {
        InputStream in = plugin.getResource(filename);
        String subfolder = sub.length > 0 ? "plugins/" + plugin.getName() + "/" + sub[0] : "plugins/" + plugin.getName();
        File outFile = new File(subfolder, filename);
        try {
            Files.createParentDirs(outFile);
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] b = new byte[1024];
                int read;
                while ((read = in.read(b)) > 0) {
                    out.write(b, 0, read);
                }
                out.close();
                in.close();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to create File " + filename, e);
        }
    }

    public static boolean hasCustomTexture(ItemStack itemStack) {
        if(itemStack == null) return false;
        try {
            Object nmscopy = TagEditor.getAsMNSCopy(itemStack);
            Object tag = nmscopy.getClass().getMethod("getTag").invoke(nmscopy);
            Object so = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "SkullOwner");
            Object prop = so.getClass().getMethod("getCompound", String.class).invoke(so, "Properties");
            Object tex = prop.getClass().getMethod("getList", String.class, int.class).invoke(prop, "textures", 10);
            Object list = tex.getClass().getMethod("get", int.class).invoke(tex, 0);
            return list.getClass().getMethod("getString", String.class).invoke(list, "Value").toString() != null;
        } catch (Exception whoops) {
        }
        return false;
    }

    public static String getUUID(String name) {
        if (CustomHeads.uuidChache.containsKey(name)) {
            return CustomHeads.uuidChache.get(name);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name)).openConnection();
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                Bukkit.getLogger().warning("Service is unaviable a this moment: " + connection.getResponseMessage());
                return null;
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();

                String json = new BufferedReader(new InputStreamReader(stream)).readLine();
                if (json.equals("")) {
                    return "";
                }
                return new JsonParser().parse(json).getAsJsonObject().get("id").getAsString();
            }
        } catch (Exception whoops) {
            Bukkit.getLogger().log(Level.WARNING, "Couldn't get UUID from " + name, whoops);
        }
        return "";
    }

    public static void unzipFile(File zipFile, File outputDir) {
        try {
            if (!outputDir.exists())
                Files.createParentDirs(outputDir);
            byte[] buffer = new byte[0x1000];
            ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry;
            System.out.println("Unzipping " + zipFile.getName());
            while ((zipEntry = inputStream.getNextEntry()) != null) {
                File zipEntryFile = new File(outputDir, zipEntry.getName());
                if (FilenameUtils.getExtension(zipEntry.getName()).length() > 0) {
                    if (!zipEntryFile.exists())
                        Files.createParentDirs(zipEntryFile);
                    OutputStream streamOut = new FileOutputStream(zipEntryFile);
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        streamOut.write(buffer, 0, len);
                    }
                    streamOut.flush();
                    streamOut.close();
                }
            }
            inputStream.closeEntry();
            inputStream.close();
            System.out.println("Unzip finished");
        } catch (Exception exception) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to unzip " + zipFile.getName(), exception);
        }
    }

    public static boolean reload(CommandSender sender) {
        boolean console = sender instanceof ConsoleCommandSender;
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().RELOAD_CONFIG);
        CustomHeads.heads.reload();
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().RELOAD_HISTORY);
        CustomHeads.reloadHistoryData();
        if (CustomHeads.hisE && CustomHeads.hisMode == History.HistoryMode.FILE) {
            CustomHeads.his.reload();
        }
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().RELOAD_LANGUAGE);
        if (!CustomHeads.reloadTranslations(CustomHeads.heads.get().getString("langFile"))) {
            sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().RELOAD_FAILED);
            return false;
        }
        ScrollableInventory.sortName = new ArrayList<>(Arrays.asList("invalid", CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_DEFAULT, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_ALPHABETICAL, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_COLOR));
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().RELOAD_SUCCESSFUL);
        return true;
    }

    public static boolean reload() {
        CustomHeads.heads.reload();
        CustomHeads.reloadHistoryData();
        if (CustomHeads.hisE && CustomHeads.hisMode == History.HistoryMode.FILE) {
            CustomHeads.his.reload();
        }
        if (!CustomHeads.reloadTranslations(CustomHeads.heads.get().getString("langFile"))) {
            return false;
        }
        ScrollableInventory.sortName = new ArrayList<>(Arrays.asList("invalid", CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_DEFAULT, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_ALPHABETICAL, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_COLOR));
        return true;
    }

    public static boolean redownloadLanguageFiles(CommandSender sender) {
        boolean console = sender instanceof ConsoleCommandSender;

        // Backing up jic >_>
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_BACKING_UP);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(System.currentTimeMillis());

            File backUpRoot = new File("plugins/CustomHeads/language-backups/" + format.format(date));
            int c = 0;
            while(backUpRoot.exists()) {
                backUpRoot = new File(backUpRoot + "_" + ++c);
            }
            File langRoot = new File("plugins/CustomHeads/language");

            for (File langDir : langRoot.listFiles()) {
                FileUtils.moveDirectory(langDir, new File(backUpRoot + "/" + langDir.getName()));
            }

        } catch(Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to move Directory", e);
            sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_BACKUP_FAILED);
            return false;
        }

        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_DOWNLOADING);
        GitHubDownloader gitGetter = new GitHubDownloader("MrSteinMC", "CustomHeads").enableAutoUnzipping();
        gitGetter.download(CustomHeads.getInstance().getDescription().getVersion(), "language.zip", new File("plugins/CustomHeads"), (AfterTask) () -> {
            reload();
            sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_DONE);
        });
        return true;
    }

    public static String toConfigString(String string) {
        string = string.replace("ä", "{ae}").replace("ö", "{oe}").replace("ü", "{ue}").replace("ẞ", "[sz]");
        string = string.replace('§', '&');
        return string;
    }

    public static void sendJSONMessage(String json, Player p) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " " + json);
    }

    public static List<String> removeColor(List<String> in) {
        in.replaceAll(ChatColor::stripColor);
        return in;
    }

    public static Class<?> getClassbyName(String className) {
        try {
            return Class.forName("net.minecraft.server." + CustomHeads.version + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getCBClass(String className) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + CustomHeads.version + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(String uwat) {
        return ChatColor.translateAlternateColorCodes('&', uwat).replace("{ae}", "ä").replace("{oe}", "ö").replace("{ue}", "ü").replace("{AE}", "Ä").replace("{OE}", "Ö").replace("{UE}", "Ü").replace("{nl}", "\n");
    }

    public static List<String> format(List<String> ulist) {
        List<String> replist = new ArrayList<>();
        for (String st : ulist) {
            replist.add(ChatColor.translateAlternateColorCodes('&', st).replace("{ae}", "ä").replace("{oe}", "ö").replace("{ue}", "ü").replace("{AE}", "Ä").replace("{OE}", "Ö").replace("{UE}", "Ü"));
        }
        return replist;
    }

}
