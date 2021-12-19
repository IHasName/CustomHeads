package de.likewhat.customheads;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.likewhat.customheads.api.CustomHeadsAPI;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CategoryManager;
import de.likewhat.customheads.command.CustomHeadsCommand;
import de.likewhat.customheads.command.CustomHeadsTabCompleter;
import de.likewhat.customheads.economy.EconomyHandler;
import de.likewhat.customheads.economy.EconomyManager;
import de.likewhat.customheads.headwriter.HeadFontType;
import de.likewhat.customheads.listener.CategoryEditorListener;
import de.likewhat.customheads.listener.InventoryListener;
import de.likewhat.customheads.listener.OtherListeners;
import de.likewhat.customheads.loader.Language;
import de.likewhat.customheads.loader.Looks;
import de.likewhat.customheads.utils.*;
import de.likewhat.customheads.utils.reflection.TagEditor;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.updaters.FetchResult;
import de.likewhat.customheads.utils.updaters.GitHubDownloader;
import de.likewhat.customheads.utils.updaters.SpigetResourceFetcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static de.likewhat.customheads.utils.Utils.hasPermission;

/*
 *  Project: CustomHeads in CustomHeads
 *     by LikeWhat
 */

@Getter
public class CustomHeads extends JavaPlugin {

    private static final boolean DEV_BUILD = false;

    public static final HashMap<String, String> uuidCache = new HashMap<>();
    public static final String chPrefix = "§7[§eCustomHeads§7] ";
    public static final String chError = chPrefix + "§cError §7: §c";
    public static final String chWarning = chPrefix + "§eWarning §7: §e";
    public static int hisOverflow = 18;
    @Getter private static Configs updateFile;
    @Getter private static Configs headsConfig;
    @Getter private static JsonFile playerDataFile;
    @Getter private static Looks looks;
    @Getter private static CustomHeads instance;
    @Getter private static Language languageManager;
    @Getter private static CustomHeadsAPI api;
    @Getter private static TagEditor tagEditor;
    @Getter private static SpigetResourceFetcher spigetFetcher;
    @Getter private static EconomyManager economyManager;
    @Getter private static CategoryManager categoryManager;
    private static List<String> versions = Arrays.asList("v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R1", "v1_13_R1", "v1_13_R2", "v1_14_R1", "v1_15_R1", "v1_16_R1", "v1_17_R1","v1_18_R1");
    private static String packet = Bukkit.getServer().getClass().getPackage().getName();
    public static String version = packet.substring(packet.lastIndexOf('.') + 1);
    @Getter private static int getCommandPrice = 0;

    public static final boolean USE_TEXTURES = versions.contains(version);
    private static boolean keepCategoryPermissions = false;
    private static boolean categoriesBuyable = false;
    private static boolean headsPermanentBuy = false;
    private static boolean canSeeOwnHistory = false;
    private static boolean historyEnabled = false;
    private static boolean reducedDebug = false;
    private static boolean headsBuyable = false;
    private static boolean hasEconomy = false;
    private boolean isInit = false;
    private String bukkitVersion;

    // Search/Get History Loader
    public static void reloadHistoryData() {
        historyEnabled = headsConfig.get().getBoolean("history.enabled");
        canSeeOwnHistory = headsConfig.get().getBoolean("history.seeown");
        hisOverflow = (hisOverflow = headsConfig.get().getInt("history.overflow")) > 27 ? 27 : Math.max(hisOverflow, 1);
    }

    // Language/Looks Loader
    public static boolean reloadTranslations(String language) {
        CustomHeads.languageManager = new Language(language);
        categoryManager = new CategoryManager(language);
        categoryManager.load();
        looks = new Looks(language);
        return Language.isLoaded() && CategoryManager.isLoaded() && Looks.isLoaded();
    }

    public static void reloadEconomy() {
        hasEconomy = false;
        economyManager = null;
        if (headsConfig.get().getBoolean("economy.enable")) {
            Bukkit.getLogger().info("Loading Economy Handler...");
            economyManager = new EconomyManager();
            EconomyHandler handler = economyManager.getActiveEconomyHandler();
            hasEconomy = handler != null && handler.isValid();
            if (hasEconomy) {
                Bukkit.getLogger().info("Successfully loaded Economy Handler: " + handler.getName());
            } else {
                Bukkit.getLogger().warning("Failed to load Economy Handler");
            }
        }
    }

    public static boolean hasHistoryEnabled() {
        return historyEnabled;
    }

    public static boolean canSeeOwnHistory() {
        return canSeeOwnHistory;
    }

    public static boolean hasEconomy() {
        return hasEconomy;
    }

    public static boolean categoriesBuyable() {
        return categoriesBuyable;
    }

    public static boolean keepCategoryPermissions() {
        return keepCategoryPermissions;
    }

    public static boolean headsBuyable() {
        return headsBuyable;
    }

    public static boolean headsPermanentBuy() {
        return headsPermanentBuy;
    }

    public static boolean hasReducedDebug() {
        return reducedDebug;
    }

    public static boolean reload(CommandSender sender) {
        boolean console = sender instanceof ConsoleCommandSender;
        sender.sendMessage((console ? chPrefix : "") + languageManager.RELOAD_CONFIG);
        headsConfig.reload();
        reducedDebug = headsConfig.get().getBoolean("reducedDebug");
        categoriesBuyable = headsConfig.get().getBoolean("economy.category.buyable");
        headsBuyable = headsConfig.get().getBoolean("economy.heads.buyable");
        headsPermanentBuy = headsConfig.get().getBoolean("economy.heads.permanentBuy");
        keepCategoryPermissions = headsConfig.get().getBoolean("economy.category.keepPermissions");
        reloadEconomy();
        sender.sendMessage((console ? chPrefix : "") + languageManager.RELOAD_HISTORY);
        reloadHistoryData();
        sender.sendMessage((console ? chPrefix : "") + languageManager.RELOAD_LANGUAGE);
        PlayerWrapper.clearCache();
        if (!reloadTranslations(headsConfig.get().getString("langFile"))) {
            sender.sendMessage((console ? chPrefix : "") + languageManager.RELOAD_FAILED);
            return false;
        }
        ScrollableInventory.sortName = new ArrayList<>(Arrays.asList("invalid", languageManager.CYCLE_ARRANGEMENT_DEFAULT, languageManager.CYCLE_ARRANGEMENT_ALPHABETICAL, languageManager.CYCLE_ARRANGEMENT_COLOR));
        sender.sendMessage((console ? chPrefix : "") + languageManager.RELOAD_SUCCESSFUL);
        return true;
    }

    public static boolean silentReload() {
        headsConfig.reload();
        reducedDebug = headsConfig.get().getBoolean("reducedDebug");
        categoriesBuyable = headsConfig.get().getBoolean("economy.category.buyable");
        headsBuyable = headsConfig.get().getBoolean("economy.heads.buyable");
        headsPermanentBuy = headsConfig.get().getBoolean("economy.heads.permanentBuy");
        keepCategoryPermissions = headsConfig.get().getBoolean("economy.category.keepPermissions");

        reloadHistoryData();
        reloadEconomy();
        PlayerWrapper.clearCache();
        if (!reloadTranslations(headsConfig.get().getString("langFile"))) {
            return false;
        }
        ScrollableInventory.sortName = new ArrayList<>(Arrays.asList("invalid", languageManager.CYCLE_ARRANGEMENT_DEFAULT, languageManager.CYCLE_ARRANGEMENT_ALPHABETICAL, languageManager.CYCLE_ARRANGEMENT_COLOR));
        return true;
    }

    public void onDisable() {
        if (isInit) {
            CustomHeadsCommand.CACHED_FIREWORKS.forEach(loc -> loc.getBlock().setType(Material.AIR));
            PlayerWrapper.clearCache();
        }
    }

    private void convertOldHeadData() {
        JsonObject rootObject = playerDataFile.getJson().isJsonObject() ? playerDataFile.getJson().getAsJsonObject() : new JsonObject();
        try {
            for (String uuid : headsConfig.get().getConfigurationSection("heads").getKeys(false)) {
                JsonObject uuidObject = rootObject.has(uuid) ? rootObject.getAsJsonObject(uuid) : new JsonObject();
                JsonObject savedHeads = uuidObject.has("savedHeads") ? uuidObject.getAsJsonObject("savedHeads") : new JsonObject();
                for (String key : headsConfig.get().getConfigurationSection("heads." + uuid).getKeys(false)) {
                    savedHeads.addProperty(key, headsConfig.get().getString("heads." + uuid + "." + key + ".texture"));
                }
                uuidObject.add("unlockedCategories", new JsonArray());
                uuidObject.add("savedHeads", savedHeads);
                rootObject.add(uuid, uuidObject);
            }

            playerDataFile.setJson(rootObject);
            playerDataFile.saveJson();
            headsConfig.get().set("heads", null);
            headsConfig.save();
            getServer().getConsoleSender().sendMessage(chPrefix + "Successfully converted old Head Data");
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to convert old Head Data...", e);
        }
    }

    private void initMetrics() {
        Metrics metrics = new Metrics(instance);
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> languageManager.getCurrentLanguage()));
    }

    public void onEnable() {
        instance = this;
        if(DEV_BUILD) {
            getServer().getConsoleSender().sendMessage("[CustomHeads]\n§e=============================================================================================\nThis is a Dev Version of the Plugin! Please update update as soon as a new Version gets released\n=============================================================================================");
        }
        try {
        } catch(Exception e) {
            bukkitVersion = Bukkit.getVersion().substring(Bukkit.getVersion().lastIndexOf("("));
        }
        Version current = Version.getCurrentVersion();
        getLogger().info("Using " + current.name() + " as Version Handler");
        if (current == Version.LATEST) {
            getServer().getConsoleSender().sendMessage(chWarning + "Hrm. Seems like CustomHeads wasn't tested on this Minecraft Version (" + CustomHeads.version + ") yet...");
            getServer().getConsoleSender().sendMessage(chWarning + "Feel free to join my Discord to know when it gets updated");
        }
        File oldHeadFile;
        if ((oldHeadFile = new File("plugins/CustomHeads", "heads.yml")).exists()) {
            oldHeadFile.renameTo(new File("plugins/CustomHeads", "config.yml"));
        }
        headsConfig = new Configs(instance, "config.yml", true);
        updateFile = new Configs(instance, "update.yml", true);

        if (headsConfig.get().getString("langFile").equals("none")) {
            headsConfig.get().set("langFile", Locale.getDefault().toString());
            headsConfig.save();
            headsConfig.reload();
        }

        // Check if Language File exists
        String selectedLanguage = headsConfig.get().getString("langFile");
        if (!new File("plugins/CustomHeads/language/" + selectedLanguage).exists()) {
            getServer().getConsoleSender().sendMessage(chWarning + "Could not find language/" + selectedLanguage + ". Looking up available Languages...");
            Utils.getAvailableLanguages(new FetchResult<List<String>>() {
                public void success(List<String> strings) {
                    if(strings.contains(selectedLanguage + ".zip")) {
                        getServer().getConsoleSender().sendMessage(chPrefix + "§7Downloading " + selectedLanguage + "...");
                        GitHubDownloader gitHubDownloader = new GitHubDownloader("IHasName", "CustomHeads").enableAutoUnzipping();
                        gitHubDownloader.download(getDescription().getVersion(), selectedLanguage + ".zip", new File(getDataFolder(), "language"), () -> {
                            getServer().getConsoleSender().sendMessage(chPrefix + "§7Done downloading! Have fun with the Plugin =D");
                            Utils.runSynced(new BukkitRunnable() {
                                public void run() {
                                    loadRest();
                                }
                            });
                        });
                    } else {
                        getLogger().log(Level.WARNING, "Couldn't find selected Language. Using default instead");
                        downloadDefaultLanguage();
                    }
                }

                public void error(Exception exception) {
                    if(exception instanceof GitHubDownloader.RateLimitExceededException) {
                        GitHubDownloader.RateLimitExceededException rateLimitException = (GitHubDownloader.RateLimitExceededException)exception;
                        getLogger().log(Level.WARNING, "GitHub Rate-Limited the Plugins Requests. Please try again later. (Time until Reset: " + rateLimitException.getTimeUntilReset() + ")");
                        getLogger().log(Level.WARNING, "If this Error continues to occur try downloading it manually from Github (https://github.com/IHasName/CustomHeads/releases/download/" + instance.getDescription().getVersion() + "/en_EN.zip)");
                        getLogger().log(Level.INFO, "A Tutorial on how to do that can be found on the Wiki");
                        Bukkit.getPluginManager().disablePlugin(instance);
                    } else {
                        getLogger().log(Level.WARNING, "Failed to lookup Languages trying to use default instead");
                        downloadDefaultLanguage();
                    }
                }
            });
        } else {
            loadRest();
        }
    }

    private void downloadDefaultLanguage() {
        headsConfig.get().set("langFile", "en_EN");
        headsConfig.save();
        headsConfig.reload();

        // Check if default Language is present if not download it
        if (!new File("plugins/CustomHeads/language/en_EN").exists()) {
            if (new File("plugins/CustomHeads/downloads").listFiles() != null) {
                for (File file : new File("plugins/CustomHeads/downloads").listFiles()) {
                    file.delete();
                }
            }
            getServer().getConsoleSender().sendMessage(chWarning + "I wasn't able to find the Default Language File on your Server...");
            getServer().getConsoleSender().sendMessage(chPrefix + "§7Downloading necessary Files...");
            GitHubDownloader gitHubDownloader = new GitHubDownloader("IHasName", "CustomHeads").enableAutoUnzipping();
            gitHubDownloader.download(getDescription().getVersion(), "en_EN.zip", new File(getDataFolder(), "language"), () -> {
                getServer().getConsoleSender().sendMessage(chPrefix + "§7Done downloading! Have fun with the Plugin =D");
                Utils.runSynced(new BukkitRunnable() {
                    public void run() {
                        loadRest();
                    }
                });
            });
        }
    }

    // Load rest of the Plugin after Language Download
    private void loadRest() {
        reducedDebug = headsConfig.get().getBoolean("reducedDebug");
        keepCategoryPermissions = headsConfig.get().getBoolean("economy.category.keepPermissions");
        categoriesBuyable = headsConfig.get().getBoolean("economy.category.buyable");
        headsBuyable = headsConfig.get().getBoolean("economy.heads.buyable");
        headsPermanentBuy = headsConfig.get().getBoolean("economy.heads.permanentBuy");

        getCommandPrice = headsConfig.get().getInt("economy.get-command.price-per-head");

        // Setting up APIHandler
        api = new APIHandler();

        tagEditor = new TagEditor("chTags");

        JsonFile.setDefaultSubfolder("plugins/CustomHeads");

        // Convert old Head-Data if present
        playerDataFile = new JsonFile("playerData.json");
        if (headsConfig.get().contains("heads")) {
            Bukkit.getConsoleSender().sendMessage(chPrefix + "Found old Head Data! Trying to convert...");
            convertOldHeadData();
        }

        reloadEconomy();

        // Load Language
        if (!reloadTranslations(headsConfig.get().getString("langFile"))) {
            getServer().getConsoleSender().sendMessage(chError + "Unable to load Language from language/" + headsConfig.get().getString("langFile"));
            Bukkit.getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        PluginManager manager = getServer().getPluginManager();

        // Register Listeners
        manager.registerEvents(new InventoryListener(), instance);
        manager.registerEvents(new OtherListeners(), instance);
        manager.registerEvents(new CategoryEditorListener(), instance);

        // Reload Configs
        reloadHistoryData();
        headsConfig.save();

        // Register Commands
        getCommand("heads").setExecutor(new CustomHeadsCommand());
        getCommand("heads").setTabCompleter(new CustomHeadsTabCompleter());

        spigetFetcher = new SpigetResourceFetcher(29057);
        SpigetResourceFetcher.setUserAgent("UC-CustomHeads");

        // Don't check for updates if the Plugin is a Dev Build
        if(!DEV_BUILD) {
            // Check for updates
            spigetFetcher.fetchUpdates(new SpigetResourceFetcher.FetchResult() {
                public void updateAvailable(SpigetResourceFetcher.ResourceRelease release, SpigetResourceFetcher.ResourceUpdate update) {
                    if (headsConfig.get().getBoolean("update-notifications.console")) {
                        getServer().getConsoleSender().sendMessage(chPrefix + "§bNew Update for CustomHeads found! v" + release.getReleaseName() + " (Running on v" + getDescription().getVersion() + ") - You can Download it here https://www.spigotmc.org/resources/29057");
                    }
                }

                public void noUpdate() {
                }
            });
        }

        // No need to report Metrics for Dev Builds
        if(!DEV_BUILD) {
            initMetrics();
        }

        // -- Timers
        // Clear Cache every 30 Minutes
        Utils.runAsyncTimer(new BukkitRunnable() {
            public void run() {
                uuidCache.clear();
                HeadFontType.clearCache();
                GitHubDownloader.clearCache();
                GameProfileBuilder.cache.clear();
                ScrollableInventory.clearCache();
                PlayerWrapper.clearCache();
            }
        }, 36000, 36000);

        // Animation Timer
        Utils.runAsyncTimer(
            new BukkitRunnable() {
                public void run() {
                    getServer().getOnlinePlayers().stream().filter(player -> player.getOpenInventory() != null && player.getOpenInventory().getType() == InventoryType.CHEST && CustomHeads.getLooks().getMenuTitles().contains(player.getOpenInventory().getTitle())).collect(Collectors.toList()).forEach(player -> {
                        Inventory targetInventory = player.getOpenInventory().getTopInventory();
                        ItemStack[] inventoryContent = targetInventory.getContents();
                        for (int i = 0; i < inventoryContent.length; i++) {
                            if (inventoryContent[i] == null) continue;
                            ItemStack contentItem = inventoryContent[i];
                            if (CustomHeads.getTagEditor().getTags(contentItem).contains("openCategory") && CustomHeads.getTagEditor().getTags(contentItem).contains("icon-loop")) {
                                String[] categoryArgs = CustomHeads.getTagEditor().getTags(contentItem).get(CustomHeads.getTagEditor().indexOf(contentItem, "openCategory") + 1).split("#>");
                                if (categoryArgs[0].equals("category")) {
                                    CustomHeadsPlayer customHeadsPlayer = api.wrapPlayer(player);
                                    Category category = CustomHeads.getCategoryManager().getCategory(categoryArgs[1]);
                                    ItemStack nextIcon = category.nextIcon();
                                    boolean bought = customHeadsPlayer.getUnlockedCategories(true).contains(category);
                                    nextIcon = new ItemEditor(nextIcon)
                                            .setDisplayName(customHeadsPlayer.getUnlockedCategories(CustomHeads.hasEconomy() && !CustomHeads.keepCategoryPermissions()).contains(category) ? "§a" + nextIcon.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(nextIcon.getItemMeta().getDisplayName()) + " " + CustomHeads.getLanguageManager().LOCKED)
                                            .addLoreLine(CustomHeads.hasEconomy() && CustomHeads.categoriesBuyable() ? bought ? CustomHeads.getLanguageManager().ECONOMY_BOUGHT : Utils.getCategoryPriceFormatted(category, true) + "\n" + CustomHeads.getLanguageManager().ECONOMY_BUY_PROMPT : null)
                                            .addLoreLines(hasPermission(player, "heads.view.permissions") ? Arrays.asList(" ", "§7§oPermission: " + category.getPermission()) : null)
                                            .getItem();
                                    if (CustomHeads.hasEconomy() && !CustomHeads.keepCategoryPermissions()) {
                                        if (!bought) {
                                            nextIcon = CustomHeads.getTagEditor().addTags(nextIcon, "buyCategory", category.getId());
                                        }
                                    }
                                    contentItem = nextIcon;
                                }
                            }
                            if (tagEditor.hasMyTags(contentItem)) {
                                contentItem = CustomHeads.getTagEditor().addTags(contentItem, "menuID", CustomHeads.getLooks().getIDbyTitle(player.getOpenInventory().getTitle()));
                            }
                            if(!contentItem.equals(inventoryContent[i])) {
                                targetInventory.setItem(i, contentItem);
                            }
                        }
                    });
                }
        }, 0, 20);

        isInit = true;
    }


}
