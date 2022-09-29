package de.likewhat.customheads.command;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CategorySetup;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.category.SubCategory;
import de.likewhat.customheads.headwriter.HeadFontType;
import de.likewhat.customheads.headwriter.HeadWriter;
import de.likewhat.customheads.utils.*;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.updaters.FetchResult;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static de.likewhat.customheads.utils.Utils.*;

/*
 *  Project: CustomHeads in CHCommand
 *     by LikeWhat
 */

public class CustomHeadsCommand implements CommandExecutor {

    public static final List<Location> CACHED_FIREWORKS = new ArrayList<>();

    private static final Comparator<Category> CATEGORY_COMPARATOR = Comparator.comparing(category -> Integer.parseInt(category.getId()));
    private static final List<Player> ACTIVE_FIREWORKS = new ArrayList<>();
    private static final String[] RANDOM_ANSWERS = {"CustomHeads says: Hmm", "CustomHeads says: Does the Console have an Inventory?", "CustomHeads says: That tickels!", "CustomHeads says: No", "CustomHeads says: Im lost", "CustomHeads says: I don't think this is what you are searching for", "CustomHeads says: Hold on... Nevermind", "CustomHeads says: Sorry", "CustomHeads says: Spoilers... There will be a new Command soon =]"};
    private static final Random RANDOM = new Random();

    private final HashMap<Player, String[]> haltedCommands = new HashMap<>();
    private final FireworkEffect.Type[] fxTypes = {FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR};
    private final BlockFace[] faces = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_NORTH_EAST};

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                    CustomHeads.reload(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("info")) {
                    CustomHeads.getSpigetFetcher().getVersions(resourceUpdates -> sender.sendMessage("§7Version: §e" + CustomHeads.getInstance().getDescription().getVersion() + " §7Update: §e" + resourceUpdates.size()));
                    return true;
                }
                if (args[0].equalsIgnoreCase("redownload")) {
                    redownloadLanguageFiles(sender);
                    return true;
                }
                // Cache Cleaner
//                if (args[0].equalsIgnoreCase("clear")) {
//                    HeadFontType.clearCache();
//                    GitHubDownloader.clearCache();
//                    GameProfileBuilder.cache.clear();
//                    ScrollableInventory.clearCache();
//                    return true;
//                }
            }
            Random rdm = new Random();
            sender.sendMessage(RANDOM_ANSWERS[rdm.nextInt(RANDOM_ANSWERS.length)]);
            return true;
        }
        Player player = (Player) sender;
        if (hasPermission(player, "heads.use")) {
            Configs headsConfig = CustomHeads.getHeadsConfig();
            if (args.length == 0) {
                Inventory menu = CustomHeads.getLooks().getCreatedMenus().get(headsConfig.get().getString("mainMenu"));
                if (menu == null) {
                    return false;
                }
                player.openInventory(cloneInventory(menu, CustomHeads.getLooks().getCreatedMenuTitles().get(headsConfig.get().getString("mainMenu")), player));
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                if (hasPermission(player, "heads.view.help")) {
                    player.openInventory(getHelpMenu(player));
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                player.sendMessage("§7CustomHeads Version: §e" + CustomHeads.getInstance().getDescription().getVersion());
                return true;
            }
            if (args[0].equalsIgnoreCase("changelog")) {
                if (hasPermission(player, "heads.admin")) {
                    CustomHeads.getSpigetFetcher().getLatestUpdate(resourceUpdate -> player.sendMessage("§6- Recent Update Changelog -\n \n§e" + resourceUpdate.getTitle() + "\n \n§7You can read the full Update here:\nhttps://www.spigotmc.org/resources/29057/update?update=" + resourceUpdate.getReleaseId()));
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }

            if (args[0].equalsIgnoreCase("language")) {
                if (hasPermission(player, "heads.admin")) {
                    if (args.length < 2) {
                        player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads language <change, redownload>"));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("change")) {
                        if (args.length < 3) {
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads language change <language>"));
                            return true;
                        }
                        if (CustomHeads.getLanguageManager().getCurrentLanguage().equalsIgnoreCase(args[2])) {
                            player.sendMessage(CustomHeads.getLanguageManager().LANGUAGE_CHANGE_ALREADY_USED);
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().LANGUAGE_CHANGE_CHANGING.replace("{LANGUAGE}", args[2]));
                        boolean success = false;
                        try {
                            success = CustomHeads.reloadTranslations(args[2]);
                        } catch(NullPointerException e) {
                            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to load Language", e);
                        }
                        if (success) {
                            headsConfig.get().set("langFile", args[2]);
                            headsConfig.save();
                            player.sendMessage(CustomHeads.getLanguageManager().LANGUAGE_CHANGE_SUCCESSFUL.replace("{LANGUAGE}", CustomHeads.getLanguageManager().DEFINITION));
                        } else {
                            CustomHeads.reloadTranslations(headsConfig.get().getString("langFile"));
                            player.sendMessage(CustomHeads.getLanguageManager().LANGUAGE_CHANGE_FAILED);
                        }
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("redownload")) {
                        redownloadLanguageFiles(sender);
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("download")) {
                        Inventory inventory = Bukkit.createInventory(player, 9 * 4, CustomHeads.getLanguageManager().LANGUAGE_DOWNLOAD_TITLE);
                        inventory.setItem(13, new ItemEditor(Material.SKULL_ITEM, (byte) 3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMxNmMxNmIxYWM0NzBkMmMxMTQ0MzRmZjg3MzBmMTgxNTcwOTM4M2RiNmYzY2Y3MjBjMzliNmRjZTIxMTYifX19").setDisplayName(CustomHeads.getLanguageManager().LANGUAGE_DOWNLOAD_FETCHING).getItem());
                        player.openInventory(inventory);
                        BukkitTask animationTask = new BukkitRunnable() {
                            boolean orange = false;
                            public void run() {
                                // Fancy Animations =P
                                inventory.setItem(13, new ItemEditor(Material.SKULL_ITEM, (byte) 3).setTexture((orange = !orange) ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQ5MzBlZTIxYWQzYmMzOWRkZmRkZmI0YWE2MjA5MDU2ZTJkOWMxMTVmMTM3ZDc2YWQzYmY2MTI3YzNkMiJ9fX0=" : "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMxNmMxNmIxYWM0NzBkMmMxMTQ0MzRmZjg3MzBmMTgxNTcwOTM4M2RiNmYzY2Y3MjBjMzliNmRjZTIxMTYifX19").setDisplayName(CustomHeads.getLanguageManager().LANGUAGE_DOWNLOAD_FETCHING).getItem());
                            }
                        }.runTaskTimer(CustomHeads.getInstance(), 20, 20);

                        Utils.getAvailableLanguages(new FetchResult<List<String>>() {
                            public void success(List<String> languages) {
                                animationTask.cancel();
                                inventory.setItem(13, null);
                                for (String language : languages) {
                                    inventory.addItem(CustomHeads.getTagEditor().addTags(new ItemEditor(Material.PAPER).setDisplayName("§e" + language).getItem(), "blockMoving", "invAction", "downloadLanguage#>" + language));
                                }
                            }

                            public void error(Exception exception) {
                                animationTask.cancel();
                                inventory.setItem(13, new ItemEditor(Material.SKULL_ITEM, (byte) 3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU0MmY2ODJlNDMwYjU1YjYxMjA0YTZmOGI3NmQ1MjI3ZDI3OGVkOWVjNGQ5OGJkYTRhN2E0ODMwYTRiNiJ9fX0=").setDisplayName(CustomHeads.getLanguageManager().LANGUAGE_DOWNLOAD_FETCHFAILED).setLore(Utils.splitEvery(exception.getMessage(), " ", 4)).getItem());
                            }
                        });
                        return true;
                    }
                    player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads language <change, redownload, download>"));
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }

            /* Test Command plez Ignore
            if (args[0].equalsIgnoreCase("test")) {
                try {
                    Configs tempcon = new Configs(CustomHeads.getInstance(), "test.yml", false, "testing");
                    if(args[1].equalsIgnoreCase("nbt")) {
                        Object result = ItemNBTUtils.getTagFromItem(player.getItemInHand(), true);
                        player.sendMessage(result == null ? "null" : result.toString());
                    }
                    if (args[1].equalsIgnoreCase("gettags")) {
                        player.sendMessage("[Tags] " + CustomHeads.getTagEditor().getTags(player.getItemInHand()));
                    }
                    if(args[1].equalsIgnoreCase("getid")) {
                        CustomHead customHead = CustomHeads.getApi().getHead(CustomHeads.getCategoryManager().getCategory(args[2]), Integer.parseInt(args[3]));
                        player.sendMessage("[" + customHead
                                .getId() + "] Name: " + customHead
                                .getItemMeta()
                                .getDisplayName() + "§f Category: " + customHead
                                .getOriginCategory()
                                .getPlainName());
                    }
                } catch(Throwable into_the_trash) {
                    into_the_trash.printStackTrace();
                }
                return true;
            }
            */
            if (args[0].equalsIgnoreCase("categories")) {
                if (hasPermission(player, "heads.admin")) {
                    if (args.length < 2) {
                        player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_BASECOMMAND_HEADER);

                        List<Category> categories = CustomHeads.getCategoryManager().getCategoryList();
                        categories.sort(CATEGORY_COMPARATOR);

                        for (int i = 0; i < categories.size(); i++) {
                            Category cCategory = categories.get(i);
                            StringBuilder hoverInfoCategoryBuilder = new StringBuilder("{\"text\":\"");
                            for (String hoverInfoCategory : CustomHeads.getLanguageManager().CATEGORIES_BASECOMMAND_HOVERINFO_CATEGORY) {
                                hoverInfoCategoryBuilder.append(hoverInfoCategory).append("\n");
                            }
                            hoverInfoCategoryBuilder = new StringBuilder(hoverInfoCategoryBuilder.toString().replace("{ID}", "" + cCategory.getId()).replace("{CATEGORY}", cCategory.getName()).replace("{PERMISSION}", cCategory.getPermission()).replace("{USED}", cCategory.isUsed() ? CustomHeads.getLanguageManager().YES : CustomHeads.getLanguageManager().NO));
                            hoverInfoCategoryBuilder = new StringBuilder(hoverInfoCategoryBuilder.substring(0, hoverInfoCategoryBuilder.length() - 1));
                            hoverInfoCategoryBuilder.append("\"}");

                            Utils.sendJSONMessage("[\"\",{\"text\":\" §e" + (i == (categories.size() - 1) ? "┗╸" : "┣╸") + " \"},{\"text\":\"" + (cCategory.isUsed() ? "§a" : "§7") + ChatColor.stripColor(cCategory.getName()) + "\"" +/*(CustomHeads.hasCategoryEditor() ? ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cedit " + cCategory.getId() + "\"}" : "") +*/",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + hoverInfoCategoryBuilder +/*",{\"text\":\"\n" + (CustomHeads.hasCategoryEditor() ? "§eClick to open in Editor" : "§eGet CategoryCreator to\n§eopen an Editor") + "\"}" +*/"]}}}]", player);
                            if (cCategory.hasSubCategories()) {
                                for (int j = 0; j < cCategory.getSubCategories().size(); j++) {
                                    SubCategory cSubCategory = cCategory.getSubCategories().get(j);

                                    StringBuilder hoverInfoSubCategoryBuilder = new StringBuilder("{\"text\":\"");
                                    for (String hoverInfoSubCategory : CustomHeads.getLanguageManager().CATEGORIES_BASECOMMAND_HOVERINFO_SUBCATEGORY) {
                                        hoverInfoSubCategoryBuilder.append(hoverInfoSubCategory).append("\n");
                                    }
                                    hoverInfoSubCategoryBuilder = new StringBuilder(hoverInfoSubCategoryBuilder.toString().replace("{ID}", "" + cCategory.getId()).replace("{CATEGORY}", cCategory.getName()).replace("{USED}", cSubCategory.isUsed() ? CustomHeads.getLanguageManager().YES : CustomHeads.getLanguageManager().NO));
                                    hoverInfoSubCategoryBuilder = new StringBuilder(hoverInfoSubCategoryBuilder.substring(0, hoverInfoSubCategoryBuilder.length() - 1));
                                    hoverInfoSubCategoryBuilder.append("\"}");

                                    Utils.sendJSONMessage("[\"\",{\"text\":\" §e" + (i == (categories.size() - 1) ? " " : "┃ ") + "  " + (j == (cCategory.getSubCategories().size() - 1) ? "┗╸" : "┣╸") + " \"},{\"text\":\"" + (cCategory.isUsed() ? "§a" : "§7") + ChatColor.stripColor(cSubCategory.getName()) + "\"" +/*(CustomHeads.hasCategoryEditor() ? ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cedit " + cSubCategory.getId() + "\"}" : "") +*/",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + hoverInfoSubCategoryBuilder +/*",{\"text\":\"\n" + (CustomHeads.hasCategoryEditor() ? "§eClick to open in Editor" : "§eGet CategoryCreator to\n§eopen an Editor") + "\"}" +*/"]}}}]", player);
                                }
                            }
                        }
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        if (args.length < 3) {
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories remove <id>"));
                            return true;
                        }
                        Category category = CustomHeads.getCategoryManager().getCategory(args[2]);
                        if (category == null) {
                            player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_REMOVE_NOTFOUND.replace("{ID}", args[2]));
                            return false;
                        }
                        if (category.isUsed()) {
                            player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_REMOVE_INUSE, category));
                            return true;
                        }
                        if (CustomHeads.getCategoryManager().removeCategory(category)) {
                            player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_REMOVE_SUCCESSFUL, category));
                            return true;
                        }
                        player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_REMOVE_FAILED, category));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("delete")) {
                        if (args.length < 3) {
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories delete <category, subcategory> <id>"));
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("category")) {
                            Category category = CustomHeads.getCategoryManager().getCategory(args[3]);
                            if (category == null) {
                                player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_DELETE_CATEGORY_NOTFOUND.replace("{ID}", args[3]));
                                return false;
                            }
                            if (category.isUsed()) {
                                player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_DELETE_CATEGORY_INUSE, category));
                                return true;
                            }
                            player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_DELETE_CATEGORY_CONFIRM, category));
                            haltedCommands.put(player, args);
                            new BukkitRunnable() {
                                public void run() {
                                    if (haltedCommands.containsKey(player) && Arrays.equals(haltedCommands.get(player), args)) {
                                        haltedCommands.remove(player);
                                    }
                                }
                            }.runTaskLater(CustomHeads.getInstance(), 300);
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("subcategory")) {
                            SubCategory subCategory = CustomHeads.getCategoryManager().getSubCategory(args[3]);
                            if (subCategory == null) {
                                player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_DELETE_SUBCATEGORY_NOTFOUND.replace("{ID}", args[3]));
                                return false;
                            }
                            if (subCategory.isUsed()) {
                                player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_DELETE_SUBCATEGORY_INUSE, subCategory));
                                return true;
                            }
                            player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_DELETE_SUBCATEGORY_CONFIRM, subCategory));
                            haltedCommands.put(player, args);
                            new BukkitRunnable() {
                                public void run() {
                                    if (haltedCommands.containsKey(player) && haltedCommands.get(player).equals(args)) {
                                        haltedCommands.remove(player);
                                    }
                                }
                            }.runTaskLater(CustomHeads.getInstance(), 300);
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories delete <category, subcategory> <id>"));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("import")) {

                        if (CustomHeads.getCategoryManager().getLangRootDir().listFiles() == null) {
                            player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_NOCATEGORYFOLDER);
                        }
                        List<File> fileList = Arrays.stream(CustomHeads.getCategoryManager().getLangRootDir().listFiles()).filter(file -> file.getName().endsWith(".json") && !CustomHeads.getHeadsConfig().get().getList("categories").contains(file.getName().substring(0, file.getName().lastIndexOf(".")))).collect(Collectors.toList());

                        List<File> files = new ArrayList<>();
                        for (File file : fileList) {
                            try {
                                Category.getConverter().fromJson(new JsonFile(file).getJsonAsString(), Category.class);
                                files.add(file);
                            } catch (Exception e) {
                            }
                        }

                        if (args.length < 3) {
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories import <filename>"));
                            if (files.isEmpty()) {
                                player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_BASECOMMAND_NOFILES);
                            } else {
                                player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_BASECOMMAND_LIST);
                                files.forEach(file -> Utils.sendJSONMessage("[\"\",{\"text\":\"" + replace(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_BASECOMMAND_LISTFORMAT + " - Size: {SIZE}", file) + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cheads categories import " + file.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + CustomHeads.getLanguageManager().CATEGORIES_IMPORT_BASECOMMAND_CLICKTOIMPORT + "\"}]}}}]", player));
                            }
                            return true;
                        }

                        StringBuilder fileName = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            fileName.append(args[i]).append(" ");
                        }
                        fileName = new StringBuilder(fileName.substring(0, fileName.length() - 1));

                        File categoryFile = new File(CustomHeads.getCategoryManager().getLangRootDir(), fileName.toString());
                        if (fileList.contains(categoryFile)) {
                            int result = CustomHeads.getCategoryManager().importSingle(categoryFile);
                            Category category = null;
                            if (result < 3) {
                                category = Category.getConverter().fromJson(new JsonFile(categoryFile).getJsonAsString(), Category.class);
                            }
                            player.sendMessage(
                                    result == 0 ? replace(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_SUCCESSFUL, categoryFile) :
                                            result == 1 ? replace(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_DUBLICATE_CATEGORY, category) :
                                                    result == 2 ? replace(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_DUBLICATE_SUBCATEGORY, categoryFile) :
                                                            result == 3 ? replace(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_INVALIDFILE, categoryFile) :
                                                                    result == 4 ? replace(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_ERROR, categoryFile) : "Uhm... this isn't supposed to happen...");
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_IMPORT_FILENOTFOUND.replace("{FILE}", args[2]));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("create")) {
                        if(args.length < 3) {
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories create <name>"));
                            return true;
                        }
                        CategorySetup.createCategory(args[2]).openInventory(player);
                        return true;
                    }
                    player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories <remove, delete, import>"));
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("confirm")) {
                if (haltedCommands.containsKey(player)) {
                    String[] haltedArgs = haltedCommands.get(player);
                    if (haltedArgs[1].equalsIgnoreCase("delete")) {
                        if (haltedArgs[2].equalsIgnoreCase("category")) {
                            Category deleteCategory = CustomHeads.getCategoryManager().getCategory(haltedArgs[3]);
                            if (CustomHeads.getCategoryManager().deleteCategory(deleteCategory)) {
                                player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_DELETE_CATEGORY_SUCCESSFUL, deleteCategory));
                                return true;
                            }
                            player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_DELETE_CATEGORY_FAILED);
                            return false;
                        }
                        if (haltedArgs[2].equalsIgnoreCase("subcategory")) {
                            SubCategory deleteSubCategory = CustomHeads.getCategoryManager().getSubCategory(haltedArgs[3]);
                            if (CustomHeads.getCategoryManager().deleteSubCategory(deleteSubCategory)) {
                                player.sendMessage(replace(CustomHeads.getLanguageManager().CATEGORIES_DELETE_SUBCATEGORY_SUCCESSFUL, deleteSubCategory));
                                return true;
                            }
                            player.sendMessage(CustomHeads.getLanguageManager().CATEGORIES_DELETE_SUBCATEGORY_FAILED);
                            return false;
                        }
                    }
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().CONFIRM_NOTHINGFOUND);
                return true;
            }
            if (args[0].equalsIgnoreCase("history")) {
                if (hasPermission(player, "heads.view.history")) {
                    if (CustomHeads.hasHistoryEnabled()) {
                        if (args.length > 1) {
                            getUUID(args[1], uuid -> {
                                if (uuid != null) {
                                    OfflinePlayer phis = Bukkit.getOfflinePlayer(Utils.parseUUID(uuid));
                                    if (phis.hasPlayedBefore() || phis.isOnline()) {
                                        if (hasPermission(player, "heads.view.history." + phis.getName()) || hasPermission(player, "heads.view.history.*") || (phis == player && CustomHeads.canSeeOwnHistory())) {
                                            player.openInventory(CustomHeads.getApi().wrapPlayer(phis).getSearchHistory().getInventory());
                                            return;
                                        }
                                        player.sendMessage(CustomHeads.getLanguageManager().HISTORY_NO_VIEW_PERMISSION);
                                        return;
                                    }
                                }
                                player.sendMessage(CustomHeads.getLanguageManager().HISTORY_INVALID_PLAYER.replace("{PLAYER}", args[1]));
                            });
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads history <Player>"));
                        return true;
                    }
                    player.sendMessage(CustomHeads.getLanguageManager().HISTORY_DISABLED);
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("firework")) {
                if (hasPermission(player, "heads.use.more.firework")) {
                    if (player.getLocation().getBlock().getType() != Material.AIR) {
                        player.sendMessage(CustomHeads.getLanguageManager().CANNOT_PLACE_IN_BLOCK);
                        return true;
                    }
                    if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                        player.sendMessage(CustomHeads.getLanguageManager().CANNOT_PLACE_IN_AIR);
                        return true;
                    }
                    if (ACTIVE_FIREWORKS.contains(player)) {
                        player.sendMessage(CustomHeads.getLanguageManager().ALREADY_IN_USE);
                        return true;
                    }
                    player.sendMessage(CustomHeads.getLanguageManager().STARTING);
                    ACTIVE_FIREWORKS.add(player);
                    Location startLocation = player.getLocation().getBlock().getLocation().add(.5, .5, .5);
                    CACHED_FIREWORKS.add(startLocation);
                    CustomHeads.getApi().createFireworkBattery(startLocation, 10, 20, new FireworksBatteryHandler() {
                        final Location location = startLocation.clone();
                        final Random random = new Random();

                        public void onStart() {
                            CustomHeads.getApi().setSkull(location.getBlock(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGEzZDVkNWIyY2YzMzEyOTllNjNkNzYxOGExNDI2NmU4Y2NjNjE1OGU5ZTMxZmNiMGJkOTExZTEyZmY3NzM2In19fQ==", faces[RANDOM.nextInt(faces.length)]);
                        }

                        public void onNext() {
                            World world = location.getWorld();
                            Firework f = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                            FireworkMeta fm = f.getFireworkMeta();
                            FireworkEffect.Builder fx = FireworkEffect.builder();
                            fx.flicker(random.nextBoolean()).trail(random.nextBoolean()).with(fxTypes[random.nextInt(fxTypes.length)]);
                            int c = random.nextInt(2) + 2;
                            for (int i = 0; i < c; i++) {
                                fx.withColor(Color.fromRGB(random.nextInt(200) + 50, random.nextInt(200) + 50, random.nextInt(200) + 50));
                                if (random.nextBoolean()) {
                                    fx.withFade(Color.fromRGB(random.nextInt(200) + 50, random.nextInt(200) + 50, random.nextInt(200) + 50));
                                }
                            }
                            fm.addEffect(fx.build());
                            fm.setPower(random.nextInt(2) + 1);
                            f.setFireworkMeta(fm);
                            f.setVelocity(new Vector(random.nextDouble() * (random.nextBoolean() ? .01 : -.01), .2, random.nextDouble() * (random.nextBoolean() ? .01 : -.01)));
                            if (ReflectionUtils.MC_VERSION > 13) {
                                try {
                                    Class<?> particleClass = ReflectionUtils.getClassByName("org.bukkit.Particle");
                                    World.class.getMethod("spawnParticle", particleClass, Location.class, int.class).invoke(world, ReflectionUtils.getEnumConstant(particleClass, "LAVA"), location, 6);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                for (int i = 0; i < 6; i++) {
                                    world.playEffect(location, Effect.LAVA_POP, 0);
                                }
                            }
                        }

                        public void onEnd() {
                            location.getWorld().playEffect(location.getBlock().getLocation(), Effect.STEP_SOUND, 17);
                            location.getBlock().setType(Material.AIR);
                        }
                    });
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("search")) {
                if (hasPermission(player, "heads.use.more.search")) {
                    if (args.length == 1) {
                        openSearchGUI(player, CustomHeads.getLanguageManager().CHANGE_SEARCH_STRING);
                        player.updateInventory();
                        return true;
                    }
                    CHSearchQuery query = new CHSearchQuery(args[1]);
                    List<Category> categories = CustomHeads.getCategoryManager().getCategoryList();
                    CustomHeadsPlayer wrappedPlayer = CustomHeads.getApi().wrapPlayer(player);
                    wrappedPlayer.getSearchHistory().addEntry(args[1]);
                    categories.removeAll(wrappedPlayer.getUnlockedCategories(false));
                    query.excludeCategories(categories);
                    if (query.resultsReturned() == 0) {
                        Inventory noRes = Bukkit.createInventory(player, 9 * 3, CustomHeads.getLanguageManager().NO_RESULTS);
                        noRes.setItem(13, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.BARRIER).setDisplayName(CustomHeads.getLanguageManager().NO_RESULTS).getItem(), "blockMoving"));
                        noRes.setItem(26, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(CustomHeads.getLanguageManager().NO_RESULTS_TRY_AGAIN).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==").getItem(), "invAction", "retrySearch#>" + args[1]));
                        player.openInventory(noRes);
                        return true;
                    }
                    Utils.openPreloader(player);
                    query.viewTo(player, "willClose");
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                if (hasPermission(player, "heads.admin")) {
                    CustomHeads.reload(sender);
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if(args[0].equalsIgnoreCase("editor")) {
                if(hasPermission(player, "heads.admin")) {
                    CategoryEditor.openCategoryOverview(player);
                }
            }
            if (args[0].equalsIgnoreCase("undo")) {
                if (hasPermission(player, "heads.use.more.write")) {
                    if (args.length == 1) {
                        HeadWriter.undoWriting(player, 1, player);
                        return true;
                    }
                    try {
                        HeadWriter.undoWriting(player, Integer.parseInt(args[1]), player);
                    } catch (Exception e) {
                        player.sendMessage(CustomHeads.getLanguageManager().UNDO_INVALID_INPUT.replace("{TIMES}", args[1]));
                    }
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("get")) {
                if (hasPermission(player, "heads.use.more.get") || hasPermission(player, "heads.use.more.get.all")) {
                    if (args.length <= 1) {
                        openGetGUI(player);
                        player.updateInventory();
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("-all")) {
                        if(!hasPermission(player, "heads.use.more.get.all")) {
                            player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                            return true;
                        }
                        ScrollableInventory scrollableInventory = new ScrollableInventory(" ", Bukkit.getOnlinePlayers().stream().map(p -> new ItemEditor(Material.SKULL_ITEM, 3).setOwner(p.getName()).setDisplayName("§a" + p.getName()).getItem()).collect(Collectors.toList()));
                        scrollableInventory.setContentsClonable(true).setContentMovable(false);
                        player.openInventory(scrollableInventory.getAsInventory());
                        return true;
                    }
                    if (args[1].length() > 16 || args[1].length() < 3) {
                        player.sendMessage(CustomHeads.getLanguageManager().GET_INVALID.replace("{PLAYER}", args[1]));
                        return true;
                    }
                    ItemStack playerHead = new ItemEditor(Material.SKULL_ITEM, 3).setDisplayName(CustomHeads.getLanguageManager().GET_HEAD_NAME.replace("{PLAYER}", args[1])).setOwner(args[1]).getItem();

                    if(CustomHeads.getGetCommandPrice() > 0 && CustomHeads.hasEconomy()) {
                        Utils.showInteractiveDialog(player, CustomHeads.getLanguageManager().ECONOMY_BUY_CONFIRM.replace("{ITEM}", args[1]).replace("{PRICE}", Utils.formatPrice(CustomHeads.getGetCommandPrice(), false)), yesPressed -> {
                            if(yesPressed) {
                                CustomHeads.getEconomyManager().buyItem(player, CustomHeads.getGetCommandPrice(), playerHead.getItemMeta().getDisplayName(), successful -> {
                                    if(successful) {
                                        player.getInventory().addItem(playerHead);
                                    }
                                });
                            } else {
                                player.closeInventory();
                            }
                        }, null, null, playerHead);
                        return true;
                    }
                    CustomHeads.getApi()
                            .wrapPlayer(player)
                            .getGetHistory()
                            .addEntry(args[1]);
                    player.getInventory().addItem(playerHead);
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("fonts")) {
                if (hasPermission(player, "heads.use.more.write")) {
                    // heads fonts <create, remove, edit> <fontname>
                    if (hasPermission(player, "heads.admin") && args.length <= 2) {
                        player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads fonts <create, remove, edit> <fontname>"));
                    }
                    if (args.length <= 2 || !hasPermission(player, "heads.admin")) {
                        File fontRoot = new File("plugins/CustomHeads/fonts");
                        List<File> files;
                        if (fontRoot.exists() && fontRoot.listFiles() != null && (files = Arrays.stream(fontRoot.listFiles()).filter(file -> file.getName().endsWith(".yml")).collect(Collectors.toList())).size() > 0) {
                            player.sendMessage(CustomHeads.getLanguageManager().FONTS_LIST_HEADER);
                            for (File file : files) {
                                Configs fontFile = new Configs(CustomHeads.getInstance(), file.getName(), false, "fonts");
                                player.sendMessage(replace(CustomHeads.getLanguageManager().FONTS_LIST, file).replace(".yml", "").replace("{CHARACTERS}", (fontFile.get().isConfigurationSection("characters") ? fontFile.get().getConfigurationSection("characters").getKeys(false).size() + "" : "0")));
                            }
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().FONTS_NOFONTS);
                        return true;
                    }
                    HeadFontType font = new HeadFontType(args[2]);
                    if (args[1].equalsIgnoreCase("create")) {
                        if (font.exists()) {
                            player.sendMessage(CustomHeads.getLanguageManager().FONTS_CREATE_ALREADYEXISTS.replace("{FONT}", args[2]));
                            return true;
                        }
                        font.save();
                        player.sendMessage(CustomHeads.getLanguageManager().FONTS_CREATE_SUCCESSFUL.replace("{FONT}", args[2]));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        if (!font.exists()) {
                            player.sendMessage(CustomHeads.getLanguageManager().FONTS_GENERAL_NOTFOUND.replace("{FONT}", font.getFontName()));
                            return true;
                        }
                        String name = font.getFontName();
                        player.sendMessage(font.delete() ? CustomHeads.getLanguageManager().FONTS_REMOVE_SUCCESSFUL.replace("{FONT}", name) : CustomHeads.getLanguageManager().FONTS_REMOVE_FAILED.replace("{FONT}", name));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("edit")) {
                        if (!font.exists()) {
                            player.sendMessage(CustomHeads.getLanguageManager().FONTS_GENERAL_NOTFOUND);
                            return true;
                        }
                        // Edit {FONTNAME}
                        HashMap<Character, ItemStack> characters = font.getCharacterItems();
                        List<ItemStack> characterList = new ArrayList<>();
                        characters.forEach((character, customHead) -> characterList.add(CustomHeads.getTagEditor().addTags(new ItemEditor(customHead).setDisplayName("§b" + (character.equals(' ') ? "BLANK" : character)).getItem(), "fontName", font.getFontName(), "character", "" + character, "editFont", "select")));
                        characterList.sort(Comparator.comparing(itemStack -> itemStack.getItemMeta().getDisplayName()));
                        ScrollableInventory editFont = new ScrollableInventory(CustomHeads.getLanguageManager().FONTS_EDIT_TITLE.replace("{FONT}", font.getFontName()), characterList);
                        editFont.setExtraTags("fontName", font.getFontName());
                        /* |-|-|0|1|2|3|4|-|-|
                         * 0: -         :
                         * 1: add       : EMERALD --> open DropInv
                         * 2: -         :
                         * 3: remove    : BARRIER --> activate Remove (add Enchantment or something)
                         * 4: -         :
                         */
                        editFont.setBarItem(1, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.EMERALD).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_ADDCHARACTER).getItem(), "editFont", "addCharacter"));
                        editFont.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.BARRIER).setDisplayName(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARACTER).setLore(CustomHeads.getLanguageManager().FONTS_EDIT_REMOVECHARACTER_INFO).getItem(), "editFont", "removeCharacter"));
                        editFont.setContentMovable(false);
                        player.openInventory(editFont.getAsInventory());
                        return true;
                    }
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if(args[0].equalsIgnoreCase("random")) {
                List<CustomHead> heads = CustomHeads.getCategoryManager().getAllHeads();
                CustomHead randomHead = heads.get(Utils.RANDOM.nextInt(heads.size()));
                if(randomHead.isFree()) {
                    player.getInventory().addItem(randomHead);
                } else {
                    Utils.showInteractiveDialog(player, CustomHeads.getLanguageManager().ECONOMY_BUY_CONFIRM.replace("{ITEM}", ChatColor.stripColor(randomHead.getItemMeta().getDisplayName())).replace("{PRICE}", getHeadPriceFormatted(randomHead, false)), success ->  {
                        CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
                        if(success) {
                            CustomHeads.getEconomyManager().buyHead(customHeadsPlayer, randomHead.getOriginCategory(), randomHead.getId(), CustomHeads.headsPermanentBuy());
                            customHeadsPlayer.unwrap().getInventory().addItem(randomHead.getPlainItem());
                        } else {
                            customHeadsPlayer.unwrap().closeInventory();
                        }
                    }, null, null, randomHead.getPlainItem());
                }
            }
            if (args.length > 1) {
                CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("save")) {
                    if (hasPermission(player, "heads.use.more")) {
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("-t")) {
                                if (hasPermission(player, "heads.use.saveastexture")) {
                                    if (!customHeadsPlayer.hasHead(args[1])) {
                                        player.sendMessage(CustomHeads.getLanguageManager().SAVE_GET_TEXTURE);
                                        GameProfileBuilder.fetch(player.getUniqueId(), gameProfile -> {
                                            try {
                                                args[1] = toConfigString(args[1]);
                                                customHeadsPlayer.saveHead(args[1], Utils.getTextureFromProfile(gameProfile));
                                                player.sendMessage(CustomHeads.getLanguageManager().SAVE_OWN_SUCCESSFUL.replace("{NAME}", format(args[1])));
                                            } catch (Exception e) {
                                                sender.sendMessage(CustomHeads.getLanguageManager().SAVE_OWN_FAILED);
                                                CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get Playertexture", e);
                                            }
                                        });
                                        return true;
                                    }
                                    player.sendMessage(CustomHeads.getLanguageManager().SAVE_ALREADY_EXIST.replace("{NAME}", format(args[1])));
                                    return true;
                                }
                            }
                        }

                        if (player.getItemInHand().getData().toString().contains("SKULL_ITEM(3)")) {
                            if (!customHeadsPlayer.hasHead(args[1])) {
                                ItemEditor itemStack = new ItemEditor(player.getItemInHand());
                                args[1] = toConfigString(args[1]);
                                if (itemStack.getOwner() == null) {

//                                    if (!CustomHeads.USE_TEXTURES) {
//                                        player.sendMessage(CustomHeads.getLanguageManager().SAVE_UNAVAILABLE);
//                                        return true;
//                                    }
                                    String nbtstring = itemStack.getTexture();
                                    if (nbtstring == null || nbtstring.equals("")) {
                                        player.sendMessage(CustomHeads.getLanguageManager().SAVE_NOT_CUSTOM_TEXTURE);
                                        return true;
                                    }
                                    customHeadsPlayer.saveHead(args[1], nbtstring);
                                    player.sendMessage(CustomHeads.getLanguageManager().SAVE_SUCCESSFUL.replace("{NAME}", format(args[1])));
                                    return true;
                                }
                                customHeadsPlayer.saveHead(args[1], itemStack.getOwner());
                                player.sendMessage(CustomHeads.getLanguageManager().SAVE_SUCCESSFUL.replace("{NAME}", format(args[1])));
                                return true;
                            }
                            player.sendMessage(CustomHeads.getLanguageManager().SAVE_ALREADY_EXIST.replace("{NAME}", format(args[1])));
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().SAVE_NOT_SKULL);
                        return true;
                    }
                    player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                    return true;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    if (hasPermission(player, "heads.use.more")) {
                        if (customHeadsPlayer.deleteHead(args[1])) {
                            player.sendMessage(CustomHeads.getLanguageManager().REMOVE_SUCCESSFUL.replace("{NAME}", format(args[1])));
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().REMOVE_FAILED.replace("{NAME}", format(args[1])));
                        return true;
                    }
                    player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                    return true;
                }
                if (args[0].equalsIgnoreCase("write")) {
                    if (hasPermission(player, "heads.use.more.write")) {
                        if (args.length < 3) {
                            player.sendMessage("§cPlease Note that this Comamnd is still an Beta-Command and may cause Issues!");
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "§c/heads write <fontName> <text>"));
                            return true;
                        }
                        HeadFontType fontType = new HeadFontType(args[1]);
                        if (!fontType.exists()) {
                            player.sendMessage(CustomHeads.getLanguageManager().WRITE_NOFONT);
                            return true;
                        }
                        StringBuilder text = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            text.append(args[i]).append(" ");
                        }
                        text = new StringBuilder(text.substring(0, text.length() - 1));
                        HeadWriter writer = new HeadWriter(fontType, text.toString().toLowerCase(), player);
                        writer.writeAt(player.getLocation());
                        return true;
                    }
                    player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                    return true;
                }
                StringBuilder builder = new StringBuilder();
                for (String permission : Utils.getPermissions(player)) {
                    for (String subCommand : Utils.getPermissions().get(permission)) {
                        builder.append(subCommand).append(", ");
                    }
                }
                if (builder.length() != 0)
                    builder.setLength(builder.length() - 2);
                player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads " + (builder.length() == 0 ? "" : "<" + builder + ">")));
                return true;
            }
            StringBuilder builder = new StringBuilder();
            for (String permission : Utils.getPermissions(player)) {
                for (String subCommand : Utils.getPermissions().get(permission)) {
                    builder.append(subCommand).append(", ");
                }
            }
            if (builder.length() != 0)
                builder.setLength(builder.length() - 2);
            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads " + (builder.length() == 0 ? "" : "<" + builder + ">")));
            return true;
        }
        player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
        return true;
    }

    private String replace(String string, Category category) {
        return string.replace("{ID}", "" + category.getId()).replace("{CATEGORY}", category.getPlainName()).replace("{PERMISSION}", category.getPermission()).replace("{USED}", category.isUsed() ? CustomHeads.getLanguageManager().YES : CustomHeads.getLanguageManager().NO);
    }

    private String replace(String string, SubCategory subCategory) {
        return string.replace("{ID}", "" + subCategory.getId()).replace("{CATEGORY}", subCategory.getPlainName()).replace("{USED}", subCategory.isUsed() ? CustomHeads.getLanguageManager().YES : CustomHeads.getLanguageManager().NO);
    }

    private String replace(String string, File file) {
        return string.replace("{FILE}", file.getName()).replace("{SIZE}", (file.length() > 1000 ? file.length() > 1000000 ? Math.round((double) file.length() / 1000000.0) + "MB" : Math.round((double) file.length() / 1000.0) + "KB" : file.length() + "B")).replace("{PATH}", file.getPath()).replace("{PARENT}", file.getParent());
    }

}
