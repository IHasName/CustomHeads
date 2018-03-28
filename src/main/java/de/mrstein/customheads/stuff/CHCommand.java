package de.mrstein.customheads.stuff;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.Updater;
import de.mrstein.customheads.api.CustomHead;
import de.mrstein.customheads.api.CustomHeadsAPI;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.SubCategory;
import de.mrstein.customheads.headwriter.HeadFontType;
import de.mrstein.customheads.headwriter.HeadWriter;
import de.mrstein.customheads.langLoader.Language;
import de.mrstein.customheads.reflection.AnvilGUI;
import de.mrstein.customheads.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static de.mrstein.customheads.listener.OtherListeners.saveLoc;
import static de.mrstein.customheads.utils.Utils.*;

public class CHCommand implements CommandExecutor {

    private static Language lg = CustomHeads.getLanguageManager();
    public HashMap<Player, String[]> haltedCommands = new HashMap<>();
    private Random ran = new Random();
    private BlockFace[] faces = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_NORTH_EAST};
    private FireworkEffect.Type[] fxtypes = {FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR};
    private String[] rdmans = {"CustomHeads says: Hmm", "CustomHeads says: Does the Console have an Inventory?", "CustomHeads says: That tickels!", "CustomHeads says: No", "CustomHeads says: Im lost", "CustomHeads says: I don't think this is what you are searching for", "CustomHeads says: Hold on... Nevermind", "CustomHeads says: Sorry"};

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            if(args.length > 0) {
                if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                    return reload(sender);
                }
                if (args[0].equalsIgnoreCase("info")) {
                    new BukkitRunnable() {
                        public void run() {
                            Object[] up = Updater.getLastUpdate(true);
                            sender.sendMessage("§7Version: §e" + CustomHeads.getInstance().getDescription().getVersion() + " §7Update: §e" + (up.length == 5 ? up[4] : -1));
                        }
                    }.runTaskLaterAsynchronously(CustomHeads.getInstance(), 10);
                }
            }
            Random rdm = new Random();
            sender.sendMessage(rdmans[rdm.nextInt(rdmans.length)]);
            return true;
        }
        Player player = (Player) sender;
        if (hasPermission(player, "heads.use")) {
            if (args.length == 0) {
                Inventory menu = CustomHeads.getLooks().getCreatedMenus().get(CustomHeads.heads.get().getString("mainMenu"));
                if (menu == null) {
                    return true;
                }
                player.openInventory(cloneInventory(menu, player));
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                if (hasPermission(player, "heads.view.help")) {
                    player.openInventory(getHelpMenu(player));
                    return true;
                }
                player.sendMessage(lg.NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                player.sendMessage("§7CustomHeads Version: §e" + CustomHeads.getInstance().getDescription().getVersion());
                return true;
            }
            if (args[0].equalsIgnoreCase("changelog")) {
                if (hasPermission(player, "heads.admin")) {
                    new BukkitRunnable() {
                        public void run() {
                            Object[] changeLog = Updater.getChangeLog();
                            player.sendMessage("§6- Recent Update Changelog -\n \n§e" + changeLog[0] + "\n \n§7You can read the full Update here:\nhttps://www.spigotmc.org/resources/29057/update?update=" + changeLog[1]);
                        }
                    }.runTaskAsynchronously(CustomHeads.getInstance());
                    return true;
                }
                player.sendMessage(lg.NO_PERMISSION);
                return true;
            }
            /* Test Command plez Ignore
            if (args[0].equalsIgnoreCase("test")) {
                Configs tempcon = new Configs(CustomHeads.getInstance(), "test.yml", false, "testing");
                return false;
            }
            */
            if (args[0].equalsIgnoreCase("categories")) {
                if (hasPermission(player, "heads.admin")) {
                    if (args.length < 2) {
                        player.sendMessage(lg.CATEGORIES_BASECOMMAND_HEADER);

                        List<Category> sortedCategories = new ArrayList<>();
                        HashMap<Integer, Category> tempMap = new HashMap<>();

                        List<Category> categories = CustomHeads.getCategoryImporter().getCategoryList();
                        for (Category category : categories) {
                            if (StringUtils.isNumeric(category.getId())) {
                                tempMap.put(Integer.parseInt(category.getId()), category);
                            }
                        }
                        categories.removeAll(tempMap.values());

                        List<Integer> sorty = new ArrayList<>(tempMap.keySet());
                        Collections.sort(sorty);
                        sorty.forEach(s -> sortedCategories.add(tempMap.get(s)));

                        categories.addAll(0, sortedCategories);

                        for (int i = 0; i < sortedCategories.size(); i++) {
                            Category cCategory = categories.get(i);
                            String hoverInfoCategoryBuilder = "{\"text\":\"";
                            for (String hoverInfoCategory : lg.CATEGORIES_BASECOMMAND_HOVERINFO_CATEGORY) {
                                hoverInfoCategoryBuilder += hoverInfoCategory + "\n";
                            }
                            hoverInfoCategoryBuilder = hoverInfoCategoryBuilder.replace("{ID}", "" + cCategory.getId()).replace("{CATEGORY}", cCategory.getName()).replace("{PERMISSION}", cCategory.getPermission()).replace("{USED}", cCategory.isUsed() ? lg.YES : lg.NO);
                            hoverInfoCategoryBuilder = hoverInfoCategoryBuilder.substring(0, hoverInfoCategoryBuilder.length() - 1);
                            hoverInfoCategoryBuilder += "\"}";

                            Utils.sendJSONMessage("[\"\",{\"text\":\" §e" + (i == (categories.size() - 1) ? "┗╸" : "┣╸") + " \"},{\"text\":\"" + (cCategory.isUsed() ? "§a" : "§7") + ChatColor.stripColor(cCategory.getName()) + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + hoverInfoCategoryBuilder + "]}}}]", player);
                            if (cCategory.hasSubCategories()) {
                                for (int j = 0; j < cCategory.getSubCategories().size(); j++) {
                                    SubCategory cSubCategory = cCategory.getSubCategories().get(j);

                                    String hoverInfoSubCategoryBuilder = "{\"text\":\"";
                                    for (String hoverInfoSubCategory : lg.CATEGORIES_BASECOMMAND_HOVERINFO_SUBCATEGORY) {
                                        hoverInfoSubCategoryBuilder += hoverInfoSubCategory + "\n";
                                    }
                                    hoverInfoSubCategoryBuilder = hoverInfoSubCategoryBuilder.replace("{ID}", "" + cCategory.getId()).replace("{CATEGORY}", cCategory.getName()).replace("{USED}", cSubCategory.isUsed() ? lg.YES : lg.NO);
                                    hoverInfoSubCategoryBuilder = hoverInfoSubCategoryBuilder.substring(0, hoverInfoSubCategoryBuilder.length() - 1);
                                    hoverInfoSubCategoryBuilder += "\"}";

                                    Utils.sendJSONMessage("[\"\",{\"text\":\" §e" + (i == (categories.size() - 1) ? " " : "┃ ") + "  " + (j == (cCategory.getSubCategories().size() - 1) ? "┗╸" : "┣╸") + " \"},{\"text\":\"" + (cCategory.isUsed() ? "§a" : "§7") + ChatColor.stripColor(cSubCategory.getName()) + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + hoverInfoSubCategoryBuilder + "]}}}]", player);
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
                        Category category = CustomHeads.getCategoryImporter().getCategory(args[2]);
                        if (category == null) {
                            player.sendMessage(lg.CATEGORIES_REMOVE_NOTFOUND.replace("{ID}", args[2]));
                            return false;
                        }
                        if (category.isUsed()) {
                            player.sendMessage(replace(lg.CATEGORIES_REMOVE_INUSE, category));
                            return true;
                        }
                        if (CustomHeads.getCategoryImporter().removeCategory(category)) {
                            player.sendMessage(replace(lg.CATEGORIES_REMOVE_SUCCESSFUL, category));
                            return true;
                        }
                        player.sendMessage(replace(lg.CATEGORIES_REMOVE_FAILED, category));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("delete")) {
                        if (args.length < 3) {
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories delete <category, subcategory> <id>"));
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("category")) {
                            Category category = CustomHeads.getCategoryImporter().getCategory(args[3]);
                            if (category == null) {
                                player.sendMessage(lg.CATEGORIES_DELETE_CATEGORY_NOTFOUND.replace("{ID}", args[3]));
                                return false;
                            }
                            if (category.isUsed()) {
                                player.sendMessage(replace(lg.CATEGORIES_DELETE_CATEGORY_INUSE, category));
                                return true;
                            }
                            player.sendMessage(replace(lg.CATEGORIES_DELETE_CATEGORY_CONFIRM, category));
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
                        if (args[2].equalsIgnoreCase("subcategory")) {
                            SubCategory subCategory = CustomHeads.getCategoryImporter().getSubCategory(args[3]);
                            if (subCategory == null) {
                                player.sendMessage(lg.CATEGORIES_DELETE_SUBCATEGORY_NOTFOUND.replace("{ID}", args[3]));
                                return false;
                            }
                            if (subCategory.isUsed()) {
                                player.sendMessage(replace(lg.CATEGORIES_DELETE_SUBCATEGORY_INUSE, subCategory));
                                return true;
                            }
                            player.sendMessage(replace(lg.CATEGORIES_DELETE_SUBCATEGORY_CONFIRM, subCategory));
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

                        if (CustomHeads.getCategoryImporter().getLanguageRootDirectory().listFiles() == null) {
                            player.sendMessage(lg.CATEGORIES_IMPORT_NOCATEGORYFOLDER);
                        }
                        List<File> fileList = Arrays.stream(CustomHeads.getCategoryImporter().getLanguageRootDirectory().listFiles()).filter(file -> file.getName().endsWith(".json") && !CustomHeads.getCategoryLoaderConfig().get().getList("categories").contains(file.getName().substring(0, file.getName().lastIndexOf(".")))).collect(Collectors.toList());

                        List<File> files = new ArrayList<>();
                        for (File file : fileList) {
                            try {
                                Category.fromJson(new JsonFile(file).getJsonAsString());
                                files.add(file);
                            } catch (Exception e) {}
                        }

                        if (args.length < 3) {
                            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads categories import <filename>"));
                            if (files.isEmpty()) {
                                player.sendMessage(lg.CATEGORIES_IMPORT_BASECOMMAND_NOFILES);
                            } else {
                                player.sendMessage(lg.CATEGORIES_IMPORT_BASECOMMAND_LIST);
                                files.forEach(file -> Utils.sendJSONMessage("[\"\",{\"text\":\"" + replace(lg.CATEGORIES_IMPORT_BASECOMMAND_LISTFORMAT + " - Size: {SIZE}", file) + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cheads categories import " + file.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + lg.CATEGORIES_IMPORT_BASECOMMAND_CLICKTOIMPORT + "\"}]}}}]", player));
                            }
                            return true;
                        }

                        StringBuilder fileName = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            fileName.append(args[i]).append(" ");
                        }
                        fileName = new StringBuilder(fileName.substring(0, fileName.length() - 1));

                        File categoryFile = new File(CustomHeads.getCategoryImporter().getLanguageRootDirectory(), fileName.toString());
                        if (fileList.contains(categoryFile)) {
                            int result = CustomHeads.getCategoryImporter().importSingle(categoryFile);
                            Category category = null;
                            if (result < 3) {
                                category = Category.fromJson(new JsonFile(categoryFile).getJsonAsString());
                            }
                            player.sendMessage(
                                    result == 0 ? replace(lg.CATEGORIES_IMPORT_SUCCESSFUL, categoryFile) :
                                            result == 1 ? replace(lg.CATEGORIES_IMPORT_DUBLICATE_CATEGORY, category) :
                                                    result == 2 ? replace(lg.CATEGORIES_IMPORT_DUBLICATE_SUBCATEGORY, categoryFile) :
                                                            result == 3 ? replace(lg.CATEGORIES_IMPORT_INVALIDFILE, categoryFile) :
                                                                    result == 4 ? replace(lg.CATEGORIES_IMPORT_ERROR, categoryFile) : "Uhm... this isn't supposed to happen...");
                            return true;
                        }
                        player.sendMessage(lg.CATEGORIES_IMPORT_FILENOTFOUND.replace("{FILE}", args[2]));
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
                            Category deleteCategory = CustomHeads.getCategoryImporter().getCategory(haltedArgs[3]);
                            if (CustomHeads.getCategoryImporter().deleteCategory(deleteCategory)) {
                                player.sendMessage(replace(lg.CATEGORIES_DELETE_CATEGORY_SUCCESSFUL, deleteCategory));
                                return true;
                            }
                            player.sendMessage(lg.CATEGORIES_DELETE_CATEGORY_FAILED);
                            return false;
                        }
                        if (haltedArgs[2].equalsIgnoreCase("subcategory")) {
                            SubCategory deleteSubCategory = CustomHeads.getCategoryImporter().getSubCategory(haltedArgs[3]);
                            if (CustomHeads.getCategoryImporter().deleteSubCategory(deleteSubCategory)) {
                                player.sendMessage(replace(lg.CATEGORIES_DELETE_SUBCATEGORY_SUCCESSFUL, deleteSubCategory));
                                return true;
                            }
                            player.sendMessage(lg.CATEGORIES_DELETE_SUBCATEGORY_FAILED);
                            return false;
                        }
                    }
                    return true;
                }
                player.sendMessage(lg.CONFIRM_NOTHINGFOUND);
                return true;
            }
            if (args[0].equalsIgnoreCase("history")) {
                if (hasPermission(player, "heads.view.history")) {
                    if (CustomHeads.hisE) {
                        if (args.length > 1) {
                            String uuid = getUUID(args[1]);
                            if (!uuid.equals("")) {
                                OfflinePlayer phis = Bukkit.getOfflinePlayer(Utils.parseUUID(uuid));
                                if (phis.hasPlayedBefore() || phis.isOnline()) {
                                    if (hasPermission(player, "heads.view.history." + phis.getName()) || hasPermission(player, "heads.view.history.*") || (phis == player && CustomHeads.hisSeeOwn)) {
                                        player.openInventory(new History(phis).getSearchHistory().getAsInventory());
                                        return true;
                                    }
                                    player.sendMessage(lg.HISTORY_NO_VIEW_PERMISSION);
                                    return true;
                                }
                            }
                            player.sendMessage(lg.HISTORY_INVALID_PLAYER.replace("{PLAYER}", args[1]));
                            return true;
                        }
                        player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads history <Player>"));
                        return true;
                    }
                    player.sendMessage(lg.HISTORY_DISABLED);
                    return true;
                }
                player.sendMessage(lg.NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("firework")) {
                if (hasPermission(player, "heads.use.more.firework")) {
                    if (player.getLocation().getBlock().getType() != Material.AIR) {
                        player.sendMessage(lg.CANNOT_PLACE_IN_BLOCK);
                        return true;
                    }
                    if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                        player.sendMessage(lg.CANNOT_PLACE_IN_AIR);
                        return true;
                    }
                    if (saveLoc.containsKey(player)) {
                        player.sendMessage(lg.ALREADY_IN_USE);
                        return true;
                    }
                    player.sendMessage(lg.STARTING);
                    final Player p2 = player;
                    CustomHeadsAPI.setSkullWithTexture(player.getLocation().getBlock(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGEzZDVkNWIyY2YzMzEyOTllNjNkNzYxOGExNDI2NmU4Y2NjNjE1OGU5ZTMxZmNiMGJkOTExZTEyZmY3NzM2In19fQ==", faces[ran.nextInt(faces.length)]);
                    saveLoc.put(player, player.getLocation().getBlock().getLocation().add(.5, 0, .5));
                    new BukkitRunnable() {
                        int counter = 10;

                        public void run() {
                            if (!saveLoc.containsKey(p2)) {
                                this.cancel();
                                return;
                            }
                            if (counter < 1) {
                                saveLoc.get(p2).getWorld().playEffect(saveLoc.get(p2).getBlock().getLocation(), Effect.STEP_SOUND, 17);
                                saveLoc.get(p2).getBlock().setType(Material.AIR);
                                saveLoc.remove(p2);
                                this.cancel();
                                return;
                            }
                            Firework f = (Firework) saveLoc.get(p2).getWorld().spawnEntity(saveLoc.get(p2), EntityType.FIREWORK);
                            FireworkMeta fm = f.getFireworkMeta();
                            FireworkEffect.Builder fx = FireworkEffect.builder();
                            fx.flicker(ran.nextBoolean()).trail(ran.nextBoolean()).with(fxtypes[ran.nextInt(fxtypes.length)]);
                            int c = ran.nextInt(2) + 2;
                            for (int i = 0; i < c; i++) {
                                fx.withColor(Color.fromRGB(ran.nextInt(256), ran.nextInt(256), ran.nextInt(256)));
                                if (ran.nextBoolean()) {
                                    fx.withFade(Color.fromRGB(ran.nextInt(256), ran.nextInt(256), ran.nextInt(256)));
                                }
                            }
                            fm.addEffect(fx.build());
                            fm.setPower(ran.nextInt(2) + 1);
                            f.setFireworkMeta(fm);
                            f.setVelocity(new Vector(ran.nextDouble() * (ran.nextBoolean() ? .01 : -.01), .2, ran.nextDouble() * (ran.nextBoolean() ? .01 : -.01)));
                            for (int i = 0; i < 6; i++) {
                                saveLoc.get(p2).getWorld().playEffect(saveLoc.get(p2), Effect.LAVA_POP, 11);
                            }
                            counter--;
                        }
                    }.runTaskTimer(CustomHeads.getInstance(), 10, 20);
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("search")) {
                if (hasPermission(player, "heads.use.more.search")) {
                    if (args.length == 1) {
                        openGUI(player, lg.CHANGE_SEARCH_STRING);
                        player.updateInventory();
                        return true;
                    }
                    CHSearchQuery query = new CHSearchQuery(args[1]);
                    List<Category> categories = CustomHeads.getCategoryImporter().getCategoryList();
                    categories.removeAll(getAvailableCategories(player));
                    query.excludeCategories(categories);
                    if (query.getResults().isEmpty()) {
                        Inventory noRes = Bukkit.createInventory(player, 9 * 3, lg.NO_RESULTS);
                        noRes.setItem(13, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.BARRIER, 1, lg.NO_RESULTS, "", (short) 0), "blockMoving"));
//                        noRes.setItem(18, CustomHeads.getTagEditor().setTags(back, "invAction", "close#>"));
                        noRes.setItem(26, CustomHeads.getTagEditor().setTags(Utils.createHead(lg.NO_RESULTS_TRY_AGAIN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), "invAction", "retrySearch#>" + args[1]));
                        player.openInventory(noRes);
                        return true;
                    }
                    query.viewTo(player, "close");
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                if (hasPermission(player, "heads.admin")) {
                    return reload(sender);
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
                        player.sendMessage(lg.UNDO_INVALID_INPUT.replace("{TIMES}", args[1]));
                    }
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("get")) {
                if (hasPermission(player, "heads.use.more.get")) {
                    if (args.length <= 1) {
                        openGetGUI(player);
                        player.updateInventory();
                        return true;
                    }
                    if (args[1].length() > 16 || args[1].length() < 3) {
                        player.sendMessage(lg.GET_INVALID.replace("{PLAYER}", args[1]));
                        return true;
                    }
                    new History(player).getGetHistory().addEntry(args[1]);
                    player.getInventory().addItem(Utils.createPlayerHead(lg.GET_HEAD_NAME.replace("{PLAYER}", args[1]), args[1], ""));
                    return true;
                }
                player.sendMessage(CustomHeads.getLanguageManager().NO_PERMISSION);
                return true;
            }
            if (args[0].equalsIgnoreCase("fonts")) {
                if (hasPermission(player, "heads.use.more.write")) {
                    // heads fonts <create, remove, edit> <fontname>
                    if (hasPermission(player, "heads.admin") && args.length <= 2) {
                        player.sendMessage(lg.COMMAND_USAGE.replace("{COMMAND}", "/heads fonts <create, remove, edit> <fontname>"));
                    }
                    if (args.length <= 2 || !hasPermission(player, "heads.admin")) {
                        File fontRoot = new File("plugins/CustomHeads/fonts");
                        List<File> files;
                        if (fontRoot.exists() && fontRoot.listFiles() != null && (files = Arrays.stream(fontRoot.listFiles()).filter(file -> file.getName().endsWith(".yml")).collect(Collectors.toList())).size() > 0) {
                            player.sendMessage(lg.FONTS_LIST_HEADER);
                            for (File file : files) {
                                Configs fontFile = new Configs(CustomHeads.getInstance(), file.getName(), false, "fonts");
                                player.sendMessage(replace(lg.FONTS_LIST, file).replace(".yml", "").replace("{CHARACTERS}", (fontFile.get().isConfigurationSection("characters") ? fontFile.get().getConfigurationSection("characters").getKeys(false).size() + "" : "0")));
                            }
                            return true;
                        }
                        player.sendMessage(lg.FONTS_NOFONTS);
                        return true;
                    }
                    HeadFontType font = new HeadFontType(args[2]);
                    if (args[1].equalsIgnoreCase("create")) {
                        if (font.exists()) {
                            player.sendMessage(CustomHeads.getLanguageManager().FONTS_CREATE_ALREADYEXISTS);
                            return true;
                        }
                        font.save();
                        player.sendMessage(CustomHeads.getLanguageManager().FONTS_CREATE_SUCCESSFUL);
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        if (!font.exists()) {
                            player.sendMessage(CustomHeads.getLanguageManager().FONTS_GENERAL_NOTFOUND.replace("{FONT}", font.getName()));
                            return true;
                        }
                        String name = font.getName();
                        player.sendMessage(font.delete() ? CustomHeads.getLanguageManager().FONTS_REMOVE_SUCCESSFUL.replace("{FONT}", name) : CustomHeads.getLanguageManager().FONTS_REMOVE_FAILED.replace("{FONT}", name));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("edit")) {
                        if (!font.exists()) {
                            player.sendMessage(CustomHeads.getLanguageManager().FONTS_GENERAL_NOTFOUND);
                            return true;
                        }
                        // Edit {FONTNAME}
                        HashMap<Character, CustomHead> characters = font.getCharacters();
                        List<ItemStack> characterList = new ArrayList<>();
                        characterList.sort(Comparator.comparing(itemStack -> itemStack.getItemMeta().getDisplayName()));
                        characters.forEach((character, customHead) -> characterList.add(CustomHeads.getTagEditor().addTags(customHead.setDisplayName("§b" + (character.equals(' ') ? "BLANK" : character)).toItem(), "fontName", font.getName(), "character", "" + character, "editFont", "select")));
                        ScrollableInventory editFont = new ScrollableInventory(CustomHeads.getLanguageManager().FONTS_EDIT_TITLE.replace("{FONT}", font.getName()), characterList);
                        editFont.setExtraTags("fontName", font.getName());
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
                player.sendMessage(lg.NO_PERMISSION);
                return true;
            }
            if (args.length > 1) {
                if (hasPermission(player, "heads.use.more")) {
                    if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("save")) {
                        HeadSaver saver = new HeadSaver(player);
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("-t")) {
                                if (hasPermission(player, "heads.use.saveastexture")) {
                                    if (!saver.hasHead(args[1])) {
                                        player.sendMessage(lg.SAVE_GET_TEXTURE);
                                        try {
                                            String playertexture = Utils.getTextureFromProfile(GameProfileBuilder.fetch(player.getUniqueId()));
                                            args[1] = toConfigString(args[1]);
                                            saver.saveHead(playertexture, args[1]);
                                            player.sendMessage(lg.SAVE_OWN_SUCCESSFUL.replace("{NAME}", format(args[1])));
                                        } catch (Exception e) {
                                            sender.sendMessage(lg.SAVE_OWN_FAILED);
                                            Bukkit.getLogger().log(Level.WARNING, "Failed to get Playertexture", e);
                                        }
                                        return true;
                                    }
                                    player.sendMessage(lg.SAVE_ALREADY_EXIST.replace("{NAME}", format(args[1])));
                                    return true;
                                }
                            }
                        }
                        if (player.getItemInHand().getData().toString().equalsIgnoreCase("SKULL_ITEM(3)")) {
                            if (!saver.hasHead(args[1])) {
                                ItemStack headstack = player.getItemInHand();
                                if (((SkullMeta) headstack.getItemMeta()).getOwner() == null) {
                                    if (!CustomHeads.usetextures) {
                                        player.sendMessage(lg.SAVE_UNAVIABLE);
                                        return true;
                                    }
                                    String nbtstring = CustomHeadsAPI.getSkullTexture(headstack);
                                    if (nbtstring == null || nbtstring.equals("")) {
                                        player.sendMessage(lg.SAVE_NOT_CUSTOM_TEXTURE);
                                        return true;
                                    }
                                    args[1] = toConfigString(args[1]);
                                    saver.saveHead(nbtstring, args[1]);
                                    player.sendMessage(lg.SAVE_SUCCESSFUL.replace("{NAME}", format(args[1])));
                                    return true;
                                }
                                saver.saveHead(((SkullMeta) headstack.getItemMeta()).getOwner(), args[1]);
                                player.sendMessage(lg.SAVE_SUCCESSFUL.replace("{NAME}", format(args[1])));
                                return true;
                            }
                            player.sendMessage(lg.SAVE_ALREADY_EXIST.replace("{NAME}", format(args[1])));
                            return true;
                        }
                        player.sendMessage(lg.SAVE_NOT_SKULL);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        CustomHeads.heads.save();
                        if (CustomHeads.heads.get().get("heads." + player.getUniqueId() + "." + args[1]) != null) {
                            CustomHeads.heads.get().set("heads." + player.getUniqueId() + "." + args[1], null);
                            player.sendMessage(lg.REMOVE_SUCCESSFUL.replace("{NAME}", format(args[1])));
                            CustomHeads.heads.save();
                            return true;
                        }
                        player.sendMessage(lg.REMOVE_FAILED.replace("{NAME}", format(args[1])));
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
                                player.sendMessage(lg.WRITE_NOFONT);
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
                        player.sendMessage(lg.NO_PERMISSION);
                        return true;
                    }
                    StringBuilder builder = new StringBuilder();
                    for (String permission : Utils.getPermissions(player)) {
                        for (String subCommand : Utils.getPermissions().get(permission)) {
                            builder.append(subCommand + ", ");
                        }
                    }
                    if (builder.length() != 0)
                        builder.setLength(builder.length() - 2);
                    player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads " + (builder.length() == 0 ? "" : "<" + builder.toString() + ">")));
                    return true;
                }
                player.sendMessage(lg.NO_PERMISSION);
                return true;
            }
            StringBuilder builder = new StringBuilder();
            for (String permission : Utils.getPermissions(player)) {
                for (String subCommand : Utils.getPermissions().get(permission)) {
                    builder.append(subCommand + ", ");
                }
            }
            if (builder.length() != 0)
                builder.setLength(builder.length() - 2);
            player.sendMessage(CustomHeads.getLanguageManager().COMMAND_USAGE.replace("{COMMAND}", "/heads " + (builder.length() == 0 ? "" : "<" + builder.toString() + ">")));
            return true;
        }
        player.sendMessage(lg.NO_PERMISSION);
        return true;
    }

    private String replace(String string, Category category) {
        return string.replace("{ID}", "" + category.getId()).replace("{CATEGORY}", category.getPlainName()).replace("{PERMISSION}", category.getPermission()).replace("{USED}", category.isUsed() ? lg.YES : lg.NO);
    }

    private String replace(String string, SubCategory subCategory) {
        return string.replace("{ID}", "" + subCategory.getId()).replace("{CATEGORY}", subCategory.getPlainName()).replace("{USED}", subCategory.isUsed() ? lg.YES : lg.NO);
    }

    private String replace(String string, File file) {
        return string.replace("{FILE}", file.getName()).replace("{SIZE}", (file.length() > 1000 ? file.length() > 1000000 ? Math.round((double) file.length() / 1000000.0) + "MB" : Math.round((double) file.length() / 1000.0) + "KB" : file.length() + "B")).replace("{PATH}", file.getPath()).replace("{PARENT}", file.getParent());
    }

    private boolean reload(CommandSender sender) {
        boolean console = sender instanceof ConsoleCommandSender;
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + lg.RELOAD_CONFIG);
        CustomHeads.heads.reload();
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + lg.RELOAD_HISTORY);
        CustomHeads.reloadHistoryData();
        if (CustomHeads.hisE && CustomHeads.hisMode == History.HistoryMode.FILE) {
            CustomHeads.his.reload();
        }
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + lg.RELOAD_LANGUAGE);
        if (!CustomHeads.reloadTranslations(CustomHeads.heads.get().getString("langFile"))) {
            sender.sendMessage((console ? CustomHeads.chPrefix : "") + lg.RELOAD_FAILED);
            return false;
        }
        lg = CustomHeads.getLanguageManager();
        ScrollableInventory.sortName = new ArrayList<>(Arrays.asList("invalid", lg.CYCLE_ARRANGEMENT_DEFAULT, lg.CYCLE_ARRANGEMENT_ALPHABETICAL, lg.CYCLE_ARRANGEMENT_COLOR));
        sender.sendMessage((console ? CustomHeads.chPrefix : "") + lg.RELOAD_SUCCESSFUL);
        return true;
    }

    public void openGUI(final Player player, String itemname) {
        AnvilGUI gui = new AnvilGUI(player, e -> {
            e.setCancelled(true);
            if (e.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                if (e.getName().equals("")) {
                    return;
                }
                if (e.getName().length() > 16) {
                    player.sendMessage(lg.TO_LONG_INPUT);
                    return;
                }
                player.sendMessage(lg.SEARCHING.replace("{SEARCH}", e.getName()));
                CHSearchQuery query = new CHSearchQuery(e.getName());
                if (query.getResults().isEmpty()) {
                    Inventory noRes = Bukkit.createInventory(player, 9 * 3, lg.NO_RESULTS);
                    noRes.setItem(13, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.BARRIER, 1, lg.NO_RESULTS, "", (short) 0), "blockMoving"));
//                    noRes.setItem(18, CustomHeads.getTagEditor().setTags(back, "invAction", "close#>"));
                    noRes.setItem(26, CustomHeads.getTagEditor().setTags(Utils.createHead(lg.NO_RESULTS_TRY_AGAIN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), "invAction", "retrySearch#>" + e.getName()));
                    player.openInventory(noRes);
                    return;
                }
                query.viewTo(player, "close");
            }
        });
        gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, Utils.createItem(Material.PAPER, 1, itemname, lg.SEARCH_LORE));
        gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, Utils.createItem(Material.PAPER, 1, itemname, lg.SEARCH_LORE));
        gui.open();
    }

    public void openGetGUI(final Player player) {
        AnvilGUI gui = new AnvilGUI(player, e -> {
            e.update();
            if (e.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                if (e.getItem().getType() == Material.PAPER) {
                    e.setCancelled(true);
                    if (e.getName().equals("")) {
                        return;
                    }
                    if (e.getName().length() > 16) {
                        player.sendMessage(lg.TO_LONG_INPUT);
                        return;
                    }
                    new History(e.getPlayer()).getGetHistory().addEntry(e.getName());
                    e.getPlayer().setItemOnCursor(Utils.createPlayerHead(lg.GET_HEAD_NAME.replace("{PLAYER}", e.getName()), e.getName(), ""));
                }
            } else if (e.getSlot() == AnvilGUI.AnvilSlot.INPUT_LEFT || e.getSlot() == AnvilGUI.AnvilSlot.INPUT_RIGHT) {
                e.setCancelled(true);
            }
        });
        gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, Utils.createItem(Material.PAPER, 1, lg.CHANGE_SEARCH_STRING, lg.SEARCH_LORE));
        gui.open();
    }

}
