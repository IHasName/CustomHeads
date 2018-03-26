package de.mrstein.customheads;

import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.headwriter.HeadFontType;
import de.mrstein.customheads.langLoader.CategoryImporter;
import de.mrstein.customheads.langLoader.Language;
import de.mrstein.customheads.langLoader.Looks;
import de.mrstein.customheads.listener.InventoryListener;
import de.mrstein.customheads.listener.OtherListeners;
import de.mrstein.customheads.reflection.TagEditor;
import de.mrstein.customheads.stuff.CHCommand;
import de.mrstein.customheads.stuff.CHTabCompleter;
import de.mrstein.customheads.stuff.History;
import de.mrstein.customheads.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CustomHeads extends JavaPlugin {

    public static int hisOverflow = 18;

    public static HashMap<String, String> uuidChache = new HashMap<>();

    private List<String> versions = Arrays.asList("v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R1");

    public static Configs heads;
    public static Configs his;
    private static Configs categoryLoaderConfig;

    public static boolean usetextures = true;
    public static boolean hisSeeOwn = false;
    public static boolean hisE = false;
    private boolean isInit = false;

    public static History.HistoryMode hisMode;
    private static CategoryImporter categoryImporter;
    private static TagEditor tagEditor;
    private static Language language;
    private static Plugin instance;
    private static Looks looks;

    public static final String chPrefix = "§7[§eCustomHeads§7] ";
    public static final String chWarning = chPrefix + "§6Warning §7: §6";
    public static final String chError = chPrefix + "§cError §7: §c";
    private static String packet = Bukkit.getServer().getClass().getPackage().getName();
    public static String version = packet.substring(packet.lastIndexOf('.') + 1);
    private String bukkitVersion = Bukkit.getVersion().substring(Bukkit.getVersion().lastIndexOf("("));

    public static void reloadHistoryData() {
        hisE = heads.get().getBoolean("history.enabled");
        hisMode = History.HistoryMode.valueOf(heads.get().getString("history.historyMode"));
        hisSeeOwn = heads.get().getBoolean("history.seeown");
        hisOverflow = heads.get().getInt("history.overflow") > 27 ? 27 : heads.get().getInt("history.overflow");
        if (hisE && hisMode == History.HistoryMode.FILE) his = new Configs(instance, "history.yml", true);
    }

    public static boolean reloadTranslations(String language) {
        CustomHeads.language = new Language(language);
        categoryImporter = new CategoryImporter(language);
        looks = new Looks(language);
        return Language.isLoaded() && CategoryImporter.isLoaded() && Looks.isLoaded();
    }

    public static CategoryImporter getCategoryImporter() {
        return categoryImporter;
    }

    public static Looks getLooks() {
        return looks;
    }

    public static Language getLanguageManager() {
        return language;
    }

    public static TagEditor getTagEditor() {
        return tagEditor;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static Configs getCategoryLoaderConfig() {
        return categoryLoaderConfig;
    }

    public void onEnable() {
        instance = this;
        heads = new Configs(instance, "heads.yml", true);
        tagEditor = new TagEditor("chTags");

        boolean firstBoot = heads.get().getString("langFile").equals("none");

        if (firstBoot) {
            heads.get().set("langFile", Locale.getDefault().toString());
            heads.save();
            heads.reload();
        }

        if (!new File("plugins/CustomHeads/language/" + heads.get().getString("langFile")).exists()) {
            getServer().getConsoleSender().sendMessage(chWarning + "Could not find language/" + heads.get().getString("langFile") + ". Using default instead");
            heads.get().set("langFile", "en_EN");
            heads.save();
            heads.reload();
            if (!new File("plugins/CustomHeads/language/en_EN").exists() && firstBoot) {
                new BukkitRunnable() {
                    public void run() {
                        getServer().getConsoleSender().sendMessage(chWarning + "I wasn't able to find the Default Languge File on your Server...");
                        getServer().getConsoleSender().sendMessage("§7Make sure to download the latest language.zip from my GitHub Page =]");
                        getServer().getConsoleSender().sendMessage("§7  -> https://github.com/MrSteinMC/CustomHeads/releases/latest");
                    }
                }.runTaskAsynchronously(instance);
                return;
            }
        }

        categoryLoaderConfig = new Configs(CustomHeads.getInstance(), "loadedCategories.yml", true);

        if (!reloadTranslations(heads.get().getString("langFile"))) {
            getServer().getConsoleSender().sendMessage(chError + "Unable to load Language from language/" + heads.get().getString("langFile"));
            Bukkit.getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        // Register Inventory Listener
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new OtherListeners(), this);

        reloadHistoryData();
        getCommand("heads").setExecutor(new CHCommand());
        getCommand("heads").setTabCompleter(new CHTabCompleter());

        new BukkitRunnable() {
            public void run() {
                Object[] update = Updater.getLastUpdate();
                if (update.length == 5) {
                    CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(chPrefix + "§bNew Update for CustomHeads found! v" + update[0] + " (Running on v" + getDescription().getVersion() + ") - You can Download it here https://www.spigotmc.org/resources/29057");
                }
                if (!versions.contains(version)) {
                    getServer().getConsoleSender().sendMessage(chWarning + "Uh oh. Seems like your Server Version " + bukkitVersion + " is not compatable with CustomHeads");
                    getServer().getConsoleSender().sendMessage(chWarning + "I'll disable Custom Textures from Skulls to prevent any Bugs but don't worry only Effects /heads add");
                    usetextures = false;
                }
            }
        }.runTaskAsynchronously(instance);

        new BukkitRunnable() {
            public void run() {
                GameProfileBuilder.cache.clear();
                uuidChache.clear();
                ScrollableInventory.clearCache();
                HeadFontType.clearCache();
            }
        }.runTaskTimer(instance, 300000, 300000);

        new BukkitRunnable() {
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.getOpenInventory() != null && player.getOpenInventory().getType() == InventoryType.CHEST) {
                        if (CustomHeads.getLooks().getMenuTitles().contains(player.getOpenInventory().getTitle())) {
                            ItemStack[] inventoryContent = player.getOpenInventory().getTopInventory().getContents();
                            for (int i = 0; i < inventoryContent.length; i++) {
                                if(inventoryContent[i] == null) continue;
                                ItemStack contentItem = inventoryContent[i];
                                if (CustomHeads.getTagEditor().getTags(contentItem).contains("openCategory") && CustomHeads.getTagEditor().getTags(contentItem).contains("icon-loop")) {
                                    String[] categoryArgs = CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openCategory") + 1).split("#>");
                                    if (categoryArgs[0].equals("category")) {
                                        Category category = CustomHeads.getCategoryImporter().getCategory(categoryArgs[1]);
                                        ItemStack nextIcon = category.nextIcon();
                                        nextIcon = new ItemEditor(nextIcon).setDisplayName(Utils.hasPermission(player, category.getPermission()) ? "§a" + nextIcon.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(nextIcon.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED).addLoreLines(Utils.hasPermission(player, "heads.view.permissions") ? Arrays.asList("§8>===-------", "§7§oPermission: " + category.getPermission()) : null).getItem();
                                        contentItem = nextIcon;
                                    }
                                }
                                inventoryContent[i] = CustomHeads.getTagEditor().addTags(contentItem, "menuID", CustomHeads.getLooks().getIDbyTitle(player.getOpenInventory().getTopInventory().getTitle()));
                            }
                            player.getOpenInventory().getTopInventory().setContents(inventoryContent);
                        }
                    }
                }
            }
        }.runTaskTimer(instance, 0, 20);

        isInit = true;
    }

    public void onDisable() {
        if (isInit) OtherListeners.saveLoc.values().forEach(loc -> loc.getBlock().setType(Material.AIR));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Pssht. ᶦᵗˢ ᵃ ˢᵉᶜʳᵉᵗ");
        return true;
    }

}
