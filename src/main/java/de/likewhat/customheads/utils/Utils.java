package de.likewhat.customheads.utils;

import com.google.common.io.Files;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.category.BaseCategory;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.category.SubCategory;
import de.likewhat.customheads.utils.reflection.AnvilGUI;
import de.likewhat.customheads.utils.reflection.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.TagEditor;
import de.likewhat.customheads.utils.updaters.AsyncFileDownloader;
import de.likewhat.customheads.utils.updaters.FetchResult;
import de.likewhat.customheads.utils.updaters.GitHubDownloader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
 *  Project: CustomHeads in Utils
 *     by LikeWhat
 */

public class Utils {

    public static final Gson GSON_PRETTY = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static final Random RANDOM = new Random();

    private static HashMap<Character, ItemStack> alphabet;
    private static HashMap<String, String[]> subCommands;
    private static HashMap<String, String[]> perms;

    private static long lastRedownload = 0;

    private static final String TEXTURE_LINK = "http://textures.minecraft.net/texture/%s";
    private static final Pattern KEY_PATTERN = Pattern.compile("[a-z0-9]\\w{63}");
    private static final String JSON_SKIN = "{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";

    public static String getTextureFromProfile(GameProfile profile) {
        String value = "";
        for (Property prop : profile.getProperties().get("textures")) {
            value = prop.getValue();
        }
        return value;
    }

    public static ItemStack getBackButton(String... action) {
        return CustomHeads.getTagEditor().addTags(new ItemEditor(Material.SKULL_ITEM, 3).setDisplayName(CustomHeads.getLanguageManager().BACK_GENERAL).setLore(CustomHeads.getLanguageManager().BACK_TO_PREVIOUS).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==").getItem(), action);
    }

    public static HashMap<String, String[]> getPermissions() {
        if (perms == null) {
            perms = new HashMap<>();
            perms.put("heads.view.help", new String[]{"help"});
            perms.put("heads.use.more", new String[]{"remove", "add"});
            perms.put("heads.use.more.firework", new String[]{"firework"});
            perms.put("heads.use.more.get", new String[]{"get"});
            perms.put("heads.admin", new String[]{"reload", "categories", "language"});
            perms.put("heads.view.history", new String[]{"history"});
            perms.put("heads.use.more.search", new String[]{"search"});
            perms.put("heads.use.more.write", new String[]{"write", "undo", "fonts"});
        }
        return perms;
    }

    public static List<String> getPermissions(Player player) {
        return getPermissions().keySet().stream().filter(permission -> hasPermission(player, permission) || permission.equals("")).collect(Collectors.toList());
    }

    public static void openSearchGUI(Player player, String itemName, String... action) {
        AnvilGUI gui = new AnvilGUI(player, "Search", new AnvilGUI.AnvilClickEventHandler() {
            public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
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
                    List<Category> categories = CustomHeads.getCategoryManager().getCategoryList();
                    categories.removeAll(CustomHeads.getApi().wrapPlayer(player).getUnlockedCategories(false));
                    query.excludeCategories(categories);
                    if (query.resultsReturned() == 0) {
                        Inventory noRes = Bukkit.createInventory(event.getPlayer(), 9 * 3, CustomHeads.getLanguageManager().NO_RESULTS);
                        noRes.setItem(13, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.BARRIER).setDisplayName(CustomHeads.getLanguageManager().NO_RESULTS).getItem(), "blockMoving"));
                        if (action.length > 0) {
                            noRes.setItem(18, Utils.getBackButton(action));
                        } else {
                            noRes.setItem(18, Utils.getBackButton("invAction", "willClose#>"));
                        }
                        noRes.setItem(26, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(CustomHeads.getLanguageManager().NO_RESULTS_TRY_AGAIN).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==").getItem(), "invAction", "retrySearch#>" + event.getName()));
                        player.closeInventory();
                        new BukkitRunnable() {
                            public void run() {
                                player.openInventory(noRes);
                            }
                        }.runTaskLater(CustomHeads.getInstance(), 20);
                        return;
                    }
                    query.viewTo(event.getPlayer(), "willClose");
                }
            }

            public void onAnvilClose() {

            }
        });
        gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName(itemName).setLore(CustomHeads.getLanguageManager().SEARCH_LORE).getItem());
        gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, new ItemEditor(Material.PAPER).setDisplayName(itemName).setLore(CustomHeads.getLanguageManager().SEARCH_LORE).getItem());
        gui.open();
        player.updateInventory();
    }

    public static void openGetGUI(Player player) {
        AnvilGUI gui = new AnvilGUI(player, "Enter Player Name", new AnvilGUI.AnvilClickEventHandler() {
            @Override
            public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
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
                        CustomHeads.getApi().wrapPlayer(player).getGetHistory().addEntry(event.getName());
                        event.getPlayer().setItemOnCursor(new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(CustomHeads.getLanguageManager().GET_HEAD_NAME.replace("{PLAYER}", event.getName())).setOwner(event.getName()).getItem());
                    }
                } else if (event.getSlot() == AnvilGUI.AnvilSlot.INPUT_LEFT || event.getSlot() == AnvilGUI.AnvilSlot.INPUT_RIGHT) {
                    event.setCancelled(true);
                }
            }

            @Override
            public void onAnvilClose() {

            }
        });
        gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().CHANGE_SEARCH_STRING).setLore(CustomHeads.getLanguageManager().SEARCH_LORE).getItem());
        gui.open();
    }

    // The good ol Pre-Loader that you latterly only see for a fraction of a second
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
        Inventory helpMenu = Bukkit.createInventory(null, 9 * (Math.min(2 + lines, 6)), CustomHeads.getLanguageManager().ITEMS_HELP);
        int itemCount = items.size();
        int offset = 10;
        int itemCounter = 0;
        while (itemCount >= 0) {
            int counter = 0;
            for (int lineIndex : line.get((Math.min(itemCount, 7)) - 1)) {
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
            subCommands.put("firework", new String[]{"heads.use.more.firework", "§eStart some Fireworks\n{PERMISSION}", "/heads firework", "§7Permission: §eheads.use.more.firework\n"});
            subCommands.put("get", new String[]{"heads.use.more.get", "§eGives you an Head from an Player\n{PERMISSION}", "/heads get <playername>", "§7Permission: §eheads.use.more.get\n"});
            subCommands.put("reload", new String[]{"heads.admin", "§eReloads the Plugin\n{PERMISSION}§eGood to know: §7heads.admin acts like heads.*", "/heads reload", "§7Permission: §eheads.admin\n"});
            subCommands.put("categories", new String[]{"heads.admin", "§eShows you a List of all Categories\n{PERMISSION}§eGood to know: §7heads.admin acts like heads.*", "/heads categories [remove, delete, import]", "§7Permission: §eheads.admin\n"});
            subCommands.put("language", new String[]{"heads.admin", "§7/heads language change <language>\n§eChanges the current Language\n§eIf you think the Language Config is broken you can\n§eredownload everything with\n§7/heads language redownlaod\n{PERMISSION}§eGood to know: §7heads.admin acts like heads.*", "/heads language <change, redownload>", "§7Permission: §eheads.admin\n"});
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
            alphabet.put('a', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("a").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0=").getItem());
            alphabet.put('b', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("b").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBjMWI1ODRmMTM5ODdiNDY2MTM5Mjg1YjJmM2YyOGRmNjc4NzEyM2QwYjMyMjgzZDg3OTRlMzM3NGUyMyJ9fX0=").getItem());
            alphabet.put('c', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("c").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJlOTgzZWM0NzgwMjRlYzZmZDA0NmZjZGZhNDg0MjY3NjkzOTU1MWI0NzM1MDQ0N2M3N2MxM2FmMThlNmYifX19").getItem());
            alphabet.put('d', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("d").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5M2RjMGQ0YzVlODBmZjlhOGEwNWQyZmNmZTI2OTUzOWNiMzkyNzE5MGJhYzE5ZGEyZmNlNjFkNzEifX19").getItem());
            alphabet.put('e', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("e").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJiMjczN2VjYmY5MTBlZmUzYjI2N2RiN2Q0YjMyN2YzNjBhYmM3MzJjNzdiZDBlNGVmZjFkNTEwY2RlZiJ9fX0=").getItem());
            alphabet.put('f', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("f").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE4M2JhYjUwYTMyMjQwMjQ4ODZmMjUyNTFkMjRiNmRiOTNkNzNjMjQzMjU1OWZmNDllNDU5YjRjZDZhIn19fQ==").getItem());
            alphabet.put('g', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("g").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNhM2YzMjRiZWVlZmI2YTBlMmM1YjNjNDZhYmM5MWNhOTFjMTRlYmE0MTlmYTQ3NjhhYzMwMjNkYmI0YjIifX19").getItem());
            alphabet.put('h', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("h").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFmMzQ2MmE0NzM1NDlmMTQ2OWY4OTdmODRhOGQ0MTE5YmM3MWQ0YTVkODUyZTg1YzI2YjU4OGE1YzBjNzJmIn19fQ==").getItem());
            alphabet.put('i', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("i").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxNzhhZDUxZmQ1MmIxOWQwYTM4ODg3MTBiZDkyMDY4ZTkzMzI1MmFhYzZiMTNjNzZlN2U2ZWE1ZDMyMjYifX19").getItem());
            alphabet.put('j', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("j").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E3OWRiOTkyMzg2N2U2OWMxZGJmMTcxNTFlNmY0YWQ5MmNlNjgxYmNlZGQzOTc3ZWViYmM0NGMyMDZmNDkifX19").getItem());
            alphabet.put('k', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("k").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ2MWIzOGM4ZTQ1NzgyYWRhNTlkMTYxMzJhNDIyMmMxOTM3NzhlN2Q3MGM0NTQyYzk1MzYzNzZmMzdiZTQyIn19fQ==").getItem());
            alphabet.put('l', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("l").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5ZjUwYjQzMmQ4NjhhZTM1OGUxNmY2MmVjMjZmMzU0MzdhZWI5NDkyYmNlMTM1NmM5YWE2YmIxOWEzODYifX19").getItem());
            alphabet.put('m', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("m").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljNDVhMjRhYWFiZjQ5ZTIxN2MxNTQ4MzIwNDg0OGE3MzU4MmFiYTdmYWUxMGVlMmM1N2JkYjc2NDgyZiJ9fX0= ").getItem());
            alphabet.put('n', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("n").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViOGIzZDhjNzdkZmI4ZmJkMjQ5NWM4NDJlYWM5NGZmZmE2ZjU5M2JmMTVhMjU3NGQ4NTRkZmYzOTI4In19fQ==").getItem());
            alphabet.put('o', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("o").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDExZGUxY2FkYjJhZGU2MTE0OWU1ZGVkMWJkODg1ZWRmMGRmNjI1OTI1NWIzM2I1ODdhOTZmOTgzYjJhMSJ9fX0=").getItem());
            alphabet.put('p', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("p").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBhNzk4OWI1ZDZlNjIxYTEyMWVlZGFlNmY0NzZkMzUxOTNjOTdjMWE3Y2I4ZWNkNDM2MjJhNDg1ZGMyZTkxMiJ9fX0=").getItem());
            alphabet.put('q', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("q").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM2MDlmMWZhZjgxZWQ0OWM1ODk0YWMxNGM5NGJhNTI5ODlmZGE0ZTFkMmE1MmZkOTQ1YTU1ZWQ3MTllZDQifX19").getItem());
            alphabet.put('r', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("r").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVjZWQ5OTMxYWNlMjNhZmMzNTEzNzEzNzliZjA1YzYzNWFkMTg2OTQzYmMxMzY0NzRlNGU1MTU2YzRjMzcifX19").getItem());
            alphabet.put('s', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("s").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0MWM2MDU3MmM1MzNlOTNjYTQyMTIyODkyOWU1NGQ2Yzg1NjUyOTQ1OTI0OWMyNWMzMmJhMzNhMWIxNTE3In19fQ==").getItem());
            alphabet.put('t', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("t").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU2MmU4YzFkNjZiMjFlNDU5YmU5YTI0ZTVjMDI3YTM0ZDI2OWJkY2U0ZmJlZTJmNzY3OGQyZDNlZTQ3MTgifX19").getItem());
            alphabet.put('u', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("u").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA3ZmJjMzM5ZmYyNDFhYzNkNjYxOWJjYjY4MjUzZGZjM2M5ODc4MmJhZjNmMWY0ZWZkYjk1NGY5YzI2In19fQ==").getItem());
            alphabet.put('v', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("v").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5YTEzODYzOGZlZGI1MzRkNzk5Mjg4NzZiYWJhMjYxYzdhNjRiYTc5YzQyNGRjYmFmY2M5YmFjNzAxMGI4In19fQ==").getItem());
            alphabet.put('w', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("w").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY5YWQxYTg4ZWQyYjA3NGUxMzAzYTEyOWY5NGU0YjcxMGNmM2U1YjRkOTk1MTYzNTY3ZjY4NzE5YzNkOTc5MiJ9fX0=").getItem());
            alphabet.put('x', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("x").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2Nzg3YmEzMjU2NGU3YzJmM2EwY2U2NDQ5OGVjYmIyM2I4OTg0NWU1YTY2YjVjZWM3NzM2ZjcyOWVkMzcifX19").getItem());
            alphabet.put('y', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("y").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyZmIzODhlMzMyMTJhMjQ3OGI1ZTE1YTk2ZjI3YWNhNmM2MmFjNzE5ZTFlNWY4N2ExY2YwZGU3YjE1ZTkxOCJ9fX0=").getItem());
            alphabet.put('z', new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("z").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA1ODJiOWI1ZDk3OTc0YjExNDYxZDYzZWNlZDg1ZjQzOGEzZWVmNWRjMzI3OWY5YzQ3ZTFlMzhlYTU0YWU4ZCJ9fX0=").getItem());
        }
        return alphabet.get(character);
    }

    public static ScrollableInventory getInventory(ItemStack item) {
        return ScrollableInventory.getInventoryByID(CustomHeads.getTagEditor().getTags(item).get(CustomHeads.getTagEditor().indexOf(item, "scInvID") + 1));
    }

    public static List<ItemStack> getSelectedCharacters(Inventory inventory) {
        return Arrays.stream(inventory.getContents()).filter(itemStack -> itemStack != null && CustomHeads.getTagEditor().getTags(itemStack).contains("charSelected")).collect(Collectors.toList());
    }

    public static Inventory getDialog(String title, String[] yesAction, String[] yesActionLore, String[] noAction, String[] noActionLore, ItemStack middleItem) {
        //System.out.println("title: " + title + "\nyesAction: " + Utils.GSON.toJson(yesAction) + " noAction: " + Utils.GSON.toJson(noAction));
        Inventory dialog = Bukkit.createInventory(null, 9 * 3, title.length() > 32 ? title.substring(0, 29) + "..." : title);
        dialog.setItem(11, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYxZTViMzMzYzJhMzg2OGJiNmE1OGI2Njc0YTI2MzkzMjM4MTU3MzhlNzdlMDUzOTc3NDE5YWYzZjc3In19fQ==").setDisplayName("§a" + CustomHeads.getLanguageManager().YES).setLore(yesActionLore).getItem(), yesAction));
        dialog.setItem(13, CustomHeads.getTagEditor().setTags(middleItem, "blockMoving"));
        dialog.setItem(15, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGJhYzc3NTIwYjllZWU2NTA2OGVmMWNkOGFiZWFkYjAxM2I0ZGUzOTUzZmQyOWFjNjhlOTBlNDg2NjIyNyJ9fX0=").setDisplayName("§c" + CustomHeads.getLanguageManager().NO).setLore(noActionLore).getItem(), noAction));
        return dialog;
    }

    /**
     *
     * @param player Player who will see the Inventory
     * @param title Title of the Dialog
     * @param callback true when Yes is selected
     * @param yesLore Lore of the Yes Button
     * @param noLore Lore of the No Button
     * @param middleItem Item in the Middle of the Dialog
     */
    public static void showInteractiveDialog(Player player, String title, SimpleCallback<Boolean> callback, String[] yesLore, String[] noLore, ItemStack middleItem) {
        new InteractiveDialog(title, callback, yesLore, noLore, middleItem).showTo(player);
    }

    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission) || player.hasPermission("heads.admin") || player.hasPermission("heads.*") || player.isOp();
    }

    public static UUID parseUUID(String uuidStr) {
        return UUID.fromString(uuidStr.substring(0, 8) + "-" + uuidStr.substring(8, 12) + "-" + uuidStr.substring(12, 16) + "-" + uuidStr.substring(16, 20) + "-" + uuidStr.substring(20));
    }

    public static Inventory cloneInventory(Inventory inventory, String title, Player newOwner) {
        Inventory inv = Bukkit.createInventory(newOwner, inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        return inv;
    }

    public static String randomAlphabetic(int length) {
        return randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray(), length);
    }

    public static String randomString(char[] chars, int length) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < RANDOM.nextInt(length); i++) {
            builder.append(chars[RANDOM.nextInt(chars.length)]);
        }
        return builder.toString();
    }

    public static boolean charArrayContains(char[] charArray, char containsChar) {
        for (char c : charArray) {
            if (c == containsChar) {
                return true;
            }
        }
        return false;
    }

    public static void openCategory(BaseCategory baseCategory, Player player, String[] backAction) {
        CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
        List<CustomHead> categoryHeads;
        if (baseCategory.isSubCategory()) {
            SubCategory subCategory = baseCategory.getAsSubCategory();
            if (!customHeadsPlayer.getUnlockedCategories(CustomHeads.hasEconomy() && !CustomHeads.keepCategoryPermissions()).contains(subCategory.getOriginCategory())) {
                return;
            }
            categoryHeads = subCategory.getHeads();
        } else {
            Category category = baseCategory.getAsCategory();
            if (!customHeadsPlayer.getUnlockedCategories(CustomHeads.hasEconomy() && !CustomHeads.keepCategoryPermissions()).contains(category)) {
                return;
            }
            if (category.hasSubCategories()) {
                player.openInventory(CustomHeads.getLooks().subCategoryLooks.get(Integer.parseInt(category.getId())));
                return;
            } else {
                openPreloader(player);
                categoryHeads = category.getHeads();
            }
        }
        openPreloader(player);

        runAsync(new BukkitRunnable() {
            public void run() {
                ScrollableInventory inventory = new ScrollableInventory(baseCategory.getName());
                inventory.setContent(categoryHeads.stream().map(customHead -> {
                    boolean bought = customHeadsPlayer.getUnlockedHeads().contains(customHead);
                    ItemEditor itemEditor = new ItemEditor(customHead);
                    return CustomHeads.hasEconomy() && CustomHeads.headsBuyable() ? (bought || hasPermission(player, "heads.unlockAllHeads." + baseCategory.getPermission().replaceFirst("heads.viewCategory.", ""))) ? CustomHeads.getTagEditor().addTags(itemEditor.addLoreLine(CustomHeads.getLanguageManager().ECONOMY_BOUGHT).getItem(), "wearable", "clonable") : CustomHeads.getTagEditor().addTags(itemEditor.addLoreLine(formatPrice(customHead.getPrice(), true)).getItem(), "buyHead", "buyHead#>" + customHead.getOriginCategory().getId() + ":" + customHead.getId()) : CustomHeads.getTagEditor().addTags(itemEditor.getItem(), "wearable", "clonable");
                }).collect(Collectors.toList()));
                inventory.setBarItem(1, Utils.getBackButton(backAction));
                inventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFO_LORE).getItem(), "dec", "info-item", "blockMoving"));
                runSyncedLater(new BukkitRunnable() {
                    public void run() {
                        player.openInventory(inventory.getAsInventory());
                    }
                }, 10);
            }
        });
    }

    public static boolean hasCustomTexture(ItemStack itemStack) {
        if (itemStack == null) return false;
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

    public static void runSynced(BukkitRunnable runnable) {
        runnable.runTask(CustomHeads.getInstance());
    }

    public static void runSyncedLater(BukkitRunnable runnable, long delay) {
        runnable.runTaskLater(CustomHeads.getInstance(), delay);
    }

    public static void runSyncedTimer(BukkitRunnable runnable, long delay, long time) {
        runnable.runTaskTimer(CustomHeads.getInstance(), delay, time);
    }

    public static void runAsync(BukkitRunnable runnable) {
        runnable.runTaskAsynchronously(CustomHeads.getInstance());
    }

    public static void runAsyncLater(BukkitRunnable runnable, long delay) {
        runnable.runTaskLaterAsynchronously(CustomHeads.getInstance(), delay);
    }

    public static void runAsyncTimer(BukkitRunnable runnable, long delay, long time) {
        runnable.runTaskTimerAsynchronously(CustomHeads.getInstance(), delay, time);
    }

    // I created this Code at Random just to Split Text
    public static String[] splitEvery(String string, String regex, int index) {
        if (index <= 0)
            throw new IllegalArgumentException("Index must be higher than 0");
        String[] splitted = string.split(regex);
        if (splitted.length < index)
            throw new IllegalArgumentException("Index cannot be higher than splitted String");
        String[] result = new String[splitted.length / index];
        int nextIndex = 0;
        int cIndex = 0;
        StringBuilder builder = new StringBuilder();
        for (String split : splitted) {
            cIndex++;
            builder.append(split).append(" ");
            if (cIndex == index) {
                cIndex = 0;
                result[nextIndex] = builder.substring(0, builder.length() - 1);
                builder = new StringBuilder();
                nextIndex++;
            }
        }
        return result;
    }

    public static void getAvailableLanguages(FetchResult<List<String>> fetchResult) {
        GitHubDownloader.getRelease(CustomHeads.getInstance().getDescription().getVersion(), "IHasName", "CustomHeads", new FetchResult<JsonObject>() {
            public void success(JsonObject release) {
                JsonArray releaseAssets = release.getAsJsonArray("assets");
                List<String> languages = new ArrayList<>();
                for (JsonElement assetElement : releaseAssets) {
                    JsonObject asset = assetElement.getAsJsonObject();
                    String assetName = asset.get("name").getAsString();
                    if (assetName.matches("^[a-z]*_[A-Z]*.zip$")) {
                        languages.add(assetName);
                    }
                }
                fetchResult.success(languages);
            }

            public void error(Exception exception) {
                fetchResult.error(exception);
            }
        });
    }

    public static BaseCategory getBaseCategory(String id) {
        return CustomHeads.getCategoryManager().getAllCategories().stream().filter(category -> category.getId().equals(id)).findFirst().orElse(null);
    }

    public static boolean isNumber(String string) {
        return string.matches("[0-9]*");
    }

    public static void getUUID(String name, Consumer<String> consumer) {
        if (CustomHeads.uuidCache.containsKey(name)) {
            consumer.accept(CustomHeads.uuidCache.get(name));
            return;
        }
        Utils.runAsync(new BukkitRunnable() {
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name)).openConnection();
                    connection.setReadTimeout(5000);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                        Bukkit.getLogger().warning("Service is unaviable a this moment: " + connection.getResponseMessage());
                        consumer.accept(null);
                        return;
                    }
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream stream = connection.getInputStream();

                        String json = new BufferedReader(new InputStreamReader(stream)).readLine();
                        if (json.equals("")) {
                            consumer.accept(null);
                            return;
                        }
                        Utils.runSynced(new BukkitRunnable() {
                            public void run() {
                                consumer.accept(new JsonParser().parse(json).getAsJsonObject().get("id").getAsString());
                            }
                        });
                    }
                } catch (Exception whoops) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't get UUID from " + name, whoops);
                    Utils.runSynced(new BukkitRunnable() {
                        public void run() {
                            consumer.accept(null);
                        }
                    });
                }
            }
        });
    }

    public static void unzipFile(File zipFile, File outputDir) {
        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            if (!outputDir.exists())
                Files.createParentDirs(new File(outputDir, "a.temp"));
            ZipEntry zipEntry;
            System.out.println("[FileZipper] Unzipping " + zipFile.getName());
            while ((zipEntry = inputStream.getNextEntry()) != null) {
                File zipEntryFile = new File(outputDir, zipEntry.getName());
                if (getExtension(zipEntry.getName()).length() > 0) {
                    if (!zipEntryFile.exists())
                        Files.createParentDirs(zipEntryFile);
                    ReadableByteChannel byteChannel = Channels.newChannel(inputStream);
                    FileOutputStream outputStream = new FileOutputStream(zipEntryFile);
                    outputStream.getChannel().transferFrom(byteChannel, 0, Integer.MAX_VALUE);
                    outputStream.flush();
                    outputStream.close();
                }
            }
            inputStream.closeEntry();
            inputStream.close();
            System.out.println("[FileZipper] Unzip finished");
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.WARNING, "[FileZipper] Failed to unzip " + zipFile.getName(), exception);
        }
    }

    public static boolean redownloadLanguageFiles(CommandSender sender) {
        boolean console = sender instanceof ConsoleCommandSender;

        if (System.currentTimeMillis() - lastRedownload < TimeUnit.MINUTES.toMillis(2)) {
            sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_FAILED_WAIT);
            return false;
        }
        lastRedownload = System.currentTimeMillis();

        // Backing up jic >_>
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_BACKING_UP);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(System.currentTimeMillis());

            File backUpRoot = new File("plugins/CustomHeads/language-backups/" + format.format(date));
            Files.createParentDirs(backUpRoot);
            int c = 0;
            while (backUpRoot.exists()) {
                backUpRoot = new File(backUpRoot + "_" + ++c);
            }
            File langRoot = new File("plugins/CustomHeads/language");

            for (File langDir : langRoot.listFiles()) {
                if(!langDir.renameTo(new File(backUpRoot + "/" + langDir.getName()))) {
                    Bukkit.getLogger().warning("Failed to move Directory");
                }
                // FileUtils.moveDirectory(langDir, new File(backUpRoot + "/" + langDir.getName()));
            }

        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to move Directory", e);
            sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_BACKUP_FAILED);
            return false;
        }

        sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_DOWNLOADING);
        GitHubDownloader gitGetter = new GitHubDownloader("IHasName", "CustomHeads").enableAutoUnzipping();
        gitGetter.download(CustomHeads.getInstance().getDescription().getVersion(), "en_EN.zip", new File("plugins/CustomHeads/language"), (AsyncFileDownloader.AfterTask) () -> {
            CustomHeads.silentReload();
            sender.sendMessage((console ? CustomHeads.chPrefix : "") + CustomHeads.getLanguageManager().LANGUAGE_REDOWNLOAD_DONE);
        });
        return true;
    }

    public static File saveInternalFile(String filename, String... sub) {
        try {
            String subfolder = sub.length > 0 ? sub[0] : "";
            File outFile = new File(subfolder, filename);
            Files.createParentDirs(outFile);
            if (!outFile.exists()) {
                try (InputStream fileInputStream = CustomHeads.getInstance().getResource(filename); FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
                    fileOutputStream.getChannel().transferFrom(Channels.newChannel(fileInputStream), 0, Integer.MAX_VALUE);
                } catch (FileNotFoundException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to create File " + filename, e);
                }
            }
            return outFile;
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to create File " + filename, e);
        }
        return null;
    }

    public static String getSkinValue(String key) {
        if(!KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("Key doesn't match Pattern : " + key);
        }
        return Base64.getEncoder().encodeToString(String.format(JSON_SKIN, String.format(TEXTURE_LINK, key)).getBytes());
    }

    public static String getCategoryPriceFormatted(Category forCategory, boolean prefix) {
        return formatPrice(forCategory.getPrice(), prefix);
    }

    public static String getHeadPriceFormatted(ItemStack forItem, boolean prefix) {
        return formatPrice(getHeadPriceRaw(forItem), prefix);
    }

    public static String formatPrice(int price, boolean prefix) {
        return (prefix ? CustomHeads.getLanguageManager().ECONOMY_PRICE : "{PRICE}").replace("{PRICE}", price == 0 ? CustomHeads.getLanguageManager().ECONOMY_FREE : CustomHeads.getLanguageManager().ECONOMY_PRICE_FORMAT.replace("{PRICE}", String.valueOf(price)).replace("{CURRENCY}", CustomHeads.hasEconomy() ? price == 1 ? CustomHeads.getEconomyManager().getEconomyPlugin().currencyNameSingular() : CustomHeads.getEconomyManager().getEconomyPlugin().currencyNamePlural() : ""));
    }

    public static int getHeadPriceRaw(ItemStack itemStack) {
        List<String> tags = CustomHeads.getTagEditor().getTags(itemStack);
        if (!tags.contains("price"))
            return -1;
        return Integer.parseInt(tags.get(tags.indexOf("price") + 1));
    }

    public static CustomHead getHeadFromItem(ItemStack itemStack) {
        List<String> tags = CustomHeads.getTagEditor().getTags(itemStack);
        if (!tags.contains("headID"))
            return null;
        String[] id = tags.get(tags.indexOf("headID") + 1).split(":");
        return CustomHeads.getApi().getHead(CustomHeads.getCategoryManager().getCategory(id[0]), Integer.parseInt(id[1]));
    }

    public static String toConfigString(String string) {
        string = string.replace("ä", "{ae}").replace("Ä", "{AE}").replace("ö", "{oe}").replace("Ö", "{OE}").replace("ü", "{ue}").replace("Ü", "{UE}").replace("ß", "[sz]");
        string = string.replace('§', '&');
        return string;
    }

    public static void sendJSONMessage(String json, Player player) {
        try {
            Object chat = ReflectionUtils.getMCServerClassByName("ChatSerializer", "network.chat").getMethod("a", String.class).invoke(null, json);
            Object packet;
            if(ReflectionUtils.MC_VERSION >= 16) {
                Class<?> chatMessageTypeClass = ReflectionUtils.getMCServerClassByName("ChatMessageType", "network.chat");
                packet = ReflectionUtils.getMCServerClassByName("PacketPlayOutChat", "network.protocol.game").getConstructor(ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat"), chatMessageTypeClass, UUID.class).newInstance(chat, ReflectionUtils.getEnumConstant(chatMessageTypeClass, "CHAT"), null);
            } else {
                packet = ReflectionUtils.getMCServerClassByName("PacketPlayOutChat", "network.protocol.game").getConstructor(ReflectionUtils.getMCServerClassByName("IChatBaseComponent", "network.chat")).newInstance(chat);
            }
            ReflectionUtils.sendPacket(packet, player);
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Could not send JSON-Message to Player", e);
        }
    }

    public static List<String> removeColor(List<String> in) {
        in.replaceAll(ChatColor::stripColor);
        return in;
    }

    public static String getExtension(String filename) {
        filename = filename.replace("\\", "/");
        if(filename.contains("/")) {
            filename = filename.substring(filename.lastIndexOf("/")+1);
        }
        if(filename.lastIndexOf(".") > 0 && filename.lastIndexOf(".") < filename.length()) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
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

    public static int[] pushArray(int[] array, int with) {
        int[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = with;
        return newArray;
    }

    public static <T> T[] pushArray(T[] array, T with) {
        T[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = with;
        return newArray;
    }

}
