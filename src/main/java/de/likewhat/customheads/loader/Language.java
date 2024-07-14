package de.likewhat.customheads.loader;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.Configs;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.likewhat.customheads.utils.Utils.format;

/*
 *  Project: CustomHeads in Language
 *     by LikeWhat
 */

public class Language {

    @Getter
    private static boolean loaded;
    // Other or multiple use
    public final String LOCKED;
    public final String INVALID_INPUT;
    public final String NO_PERMISSION;
    public final String LANGUAGE_CHANGE_CHANGING;
    public final String LANGUAGE_CHANGE_SUCCESSFUL;
    public final String LANGUAGE_CHANGE_ALREADY_USED;
    public final String LANGUAGE_CHANGE_FAILED;
    public final String LANGUAGE_REDOWNLOAD_FAILED_WAIT;
    public final String LANGUAGE_REDOWNLOAD_BACKING_UP;
    public final String LANGUAGE_REDOWNLOAD_BACKUP_FAILED;
    public final String LANGUAGE_REDOWNLOAD_DOWNLOADING;
    public final String LANGUAGE_REDOWNLOAD_DONE;
    public final String LANGUAGE_DOWNLOAD_FETCHING;
    public final String LANGUAGE_DOWNLOAD_FETCHFAILED;
    public final String LANGUAGE_DOWNLOAD_SUCCESSFUL;
    public final String LANGUAGE_DOWNLOAD_TITLE;
    public final String RELOAD_CONFIG;
    public final String RELOAD_HISTORY;
    public final String RELOAD_LANGUAGE;
    public final String RELOAD_FAILED;
    public final String RELOAD_SUCCESSFUL;
    public final String YES;
    public final String NO;
    public final String CONFIRM_NOTHINGFOUND;
    public final String COMMAND_USAGE;
    public final String LINK;
    public final String HELP_GET_COMMAND;
    public final String SAVED_HEADS_TITLE;
    public final String PUT_ON_HEAD;
    public final List<String> NO_PERMISSION_TO_VIEW;
    public final String ECONOMY_PRICE;
    public final String ECONOMY_PRICE_FORMAT;
    public final String ECONOMY_FREE;
    public final String ECONOMY_BOUGHT;
    public final String ECONOMY_BUY_PROMPT;
    public final String ECONOMY_BUY_CONFIRM;
    public final String ECONOMY_BUY_SUCCESSFUL;
    public final String ECONOMY_BUY_FAILED;
    public final String PAGE_GENERAL_PREFIX;
    public final String BACK_GENERAL;
    public final String BACK_TO_PREVIOUS;
    public final String NEXT_PAGE;
    public final String PREVIOUS_PAGE;
    // History
    public final String HISTORY_DISABLED;
    public final String HISTORY_INVALID_PLAYER;
    public final String HISTORY_NO_VIEW_PERMISSION;
    // Firework
    public final String CANNOT_PLACE_IN_BLOCK;
    public final String CANNOT_PLACE_IN_AIR;
    public final String ALREADY_IN_USE;
    public final String STARTING;
    // Search
    public final String CHANGE_SEARCH_STRING;
    public final List<String> SEARCH_LORE;
    public final String TO_LONG_INPUT;
    public final String SEARCHING;
    public final String CYCLE_ARRANGEMENT_PREFIX;
    public final String CYCLE_ARRANGEMENT_DEFAULT;
    public final String CYCLE_ARRANGEMENT_ALPHABETICAL;
    public final String CYCLE_ARRANGEMENT_COLOR;
    public final String SEARCH_TITLE;
    public final String LOADING;
    public final String NO_RESULTS;
    public final String NO_RESULTS_TRY_AGAIN;
    // -- Categories
    // Bess Command
    public final String CATEGORIES_BASECOMMAND_HEADER;
    public final List<String> CATEGORIES_BASECOMMAND_HOVERINFO_CATEGORY;
    public final List<String> CATEGORIES_BASECOMMAND_HOVERINFO_SUBCATEGORY;
    // Remove
    public final String CATEGORIES_REMOVE_NOTFOUND;
    public final String CATEGORIES_REMOVE_INUSE;
    public final String CATEGORIES_REMOVE_FAILED;
    public final String CATEGORIES_REMOVE_SUCCESSFUL;
    // Delete Category
    public final String CATEGORIES_DELETE_CATEGORY_NOTFOUND;
    public final String CATEGORIES_DELETE_CATEGORY_INUSE;
    public final String CATEGORIES_DELETE_CATEGORY_CONFIRM;
    public final String CATEGORIES_DELETE_CATEGORY_FAILED;
    public final String CATEGORIES_DELETE_CATEGORY_SUCCESSFUL;
    // Delete Subcategory
    public final String CATEGORIES_DELETE_SUBCATEGORY_NOTFOUND;
    public final String CATEGORIES_DELETE_SUBCATEGORY_INUSE;
    public final String CATEGORIES_DELETE_SUBCATEGORY_CONFIRM;
    public final String CATEGORIES_DELETE_SUBCATEGORY_FAILED;
    public final String CATEGORIES_DELETE_SUBCATEGORY_SUCCESSFUL;
    // Import
    public final String CATEGORIES_IMPORT_NOCATEGORYFOLDER;
    public final String CATEGORIES_IMPORT_BASECOMMAND_NOFILES;
    public final String CATEGORIES_IMPORT_BASECOMMAND_LIST;
    public final String CATEGORIES_IMPORT_BASECOMMAND_LISTFORMAT;
    public final String CATEGORIES_IMPORT_BASECOMMAND_CLICKTOIMPORT;
    public final String CATEGORIES_IMPORT_SUCCESSFUL;
    public final String CATEGORIES_IMPORT_DUBLICATE_CATEGORY;
    public final String CATEGORIES_IMPORT_DUBLICATE_SUBCATEGORY;
    public final String CATEGORIES_IMPORT_ERROR;
    public final String CATEGORIES_IMPORT_INVALIDFILE;
    public final String CATEGORIES_IMPORT_FILENOTFOUND;
    // Undo
    public final String UNDO_INVALID_INPUT;
    public final String UNDO_SUCCESSFUL;
    public final String UNDO_NOTHING_LEFT;
    // Get
    public final String GET_HEAD_NAME;
    public final String GET_INVALID;
    // Save
    public final String SAVE_GET_TEXTURE;
    public final String SAVE_OWN_SUCCESSFUL;
    public final String SAVE_OWN_FAILED;
    public final String SAVE_UNAVAILABLE;
    public final String SAVE_NOT_CUSTOM_TEXTURE;
    public final String SAVE_SUCCESSFUL;
    public final String SAVE_ALREADY_EXIST;
    public final String SAVE_NOT_SKULL;
    // Remove
    public final String REMOVE_SUCCESSFUL;
    public final String REMOVE_FAILED;
    public final String REMOVE_CONFIRMATION;
    // Write
    public final String WRITE_NOFONT;
    public final String WRITE_TO_LONG;
    public final String WRITE_WRITING;
    // -- Fonts
    public final String FONTS_GENERAL_NOTFOUND;
    public final String FONTS_CREATE_ALREADYEXISTS;
    public final String FONTS_CREATE_SUCCESSFUL;
    public final String FONTS_REMOVE_SUCCESSFUL;
    public final String FONTS_REMOVE_FAILED;
    public final String FONTS_NOFONTS;
    public final String FONTS_LIST_HEADER;
    public final String FONTS_LIST;
    // Character Stuff
    public final String FONTS_EDIT_TITLE;
    public final String FONTS_EDIT_ADDCHARACTER;
    public final String FONTS_EDIT_REMOVECHARACTER;
    public final List<String> FONTS_EDIT_REMOVECHARACTER_INFO;
    public final String FONTS_EDIT_ADDCHARACTER_SUCCESSFUL;
    public final String FONTS_EDIT_ADDCHARACTER_FAILED;
    public final String FONTS_EDIT_SELECTED;
    public final String FONTS_EDIT_REMOVECHARCATER_CONFIRM;
    public final String FONTS_EDIT_REMOVECHARCATER_FAILED;
    public final String FONTS_EDIT_REMOVECHARCATER_SUCCESSFUL;
    public final String FONTS_EDIT_REMOVECHARCATER_PREINFO;
    public final String FONTS_EDIT_ADDCHARACTER_TITLE;
    public final String FONTS_EDIT_ADD_TOGGLE;
    public final String FONTS_EDIT_ADD_DROPPER_NAME;
    public final List<String> FONTS_EDIT_ADD_DROPPER_LORE;
    public final String FONTS_EDIT_SAVENEXIT;
    public final String FONTS_EDIT_SAVE_SUCCESSFUL;
    // History
    public final String HISTORY_INV_TITLE;
    public final String HISTORY_SEARCHHISTORY_ACTIVE;
    public final String HISTORY_SEARCHHISTORY;
    public final String HISTORY_GETHISTORY_ACTIVE;
    public final String HISTORY_GETHISTORY;
    public final List<String> HISTORY_NO_HISTORY_LORE;
    public final List<String> HISTORY_SEARCHHISTORY_LORE;
    public final List<String> HISTORY_GET_LORE;
    // Items
    public final String ITEMS_YOUR_HEAD;
    public final String ITEMS_COLLECTION;
    public final String ITEMS_HELP;
    public final List<String> ITEMS_HELP_LORE;
    public final String ITEMS_SEARCH;
    public final List<String> ITEMS_SEARCH_LORE;
    public final String ITEMS_FIND_MORE;
    public final List<String> ITEMS_FIND_MORE_LORE;
    public final String ITEMS_INFO;
    public final List<String> ITEMS_INFO_LORE;
    public final String DEFINITION;
    private final Configs languageConfig;
    @Getter
    private final String currentLanguage;

    public Language(String language) {
        loaded = false;
        languageConfig = new Configs(CustomHeads.getInstance(), "language.yml", false, "language/" + language);
        currentLanguage = language;

        languageConfig.reload();
        if (!CustomHeads.hasReducedDebug())
            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.PREFIX_GENERAL + "Loading Language from language/" + language + "/language.yml...");
        long timestamp = System.currentTimeMillis();
        DEFINITION = ChatColor.stripColor(getStringFromConfig("DEFINITION"));

        NO_PERMISSION = getStringFromConfig("NO_PERMISSION");

        YES = ChatColor.stripColor(getStringFromConfig("LANG_YES"));
        NO = ChatColor.stripColor(getStringFromConfig("LANG_NO"));
        CONFIRM_NOTHINGFOUND = getStringFromConfig("CONFIRM_NOTHINGFOUND");
        COMMAND_USAGE = getStringFromConfig("COMMAND_USAGE");

        LINK = getStringFromConfig("LINK");

        LANGUAGE_CHANGE_CHANGING = getStringFromConfig("LANGUAGE.CHANGE_CHANGING");
        LANGUAGE_CHANGE_SUCCESSFUL = getStringFromConfig("LANGUAGE.CHANGE_SUCCESSFUL");
        LANGUAGE_CHANGE_ALREADY_USED = getStringFromConfig("LANGUAGE.CHANGE_ALREADY_USED");
        LANGUAGE_CHANGE_FAILED = getStringFromConfig("LANGUAGE.CHANGE_FAILED");
        LANGUAGE_REDOWNLOAD_FAILED_WAIT = getStringFromConfig("LANGUAGE.REDOWNLOAD_FAILED_WAIT");
        LANGUAGE_REDOWNLOAD_BACKING_UP = getStringFromConfig("LANGUAGE.REDOWNLOAD_BACKING_UP");
        LANGUAGE_REDOWNLOAD_BACKUP_FAILED = getStringFromConfig("LANGUAGE.REDOWNLOAD_BACKUP_FAILED");
        LANGUAGE_REDOWNLOAD_DOWNLOADING = getStringFromConfig("LANGUAGE.REDOWNLOAD_DOWNLOADING");
        LANGUAGE_REDOWNLOAD_DONE = getStringFromConfig("LANGUAGE.REDOWNLOAD_DONE");

        LANGUAGE_DOWNLOAD_FETCHING = getStringFromConfig("LANGUAGE.DOWNLOAD_FETCHING");
        LANGUAGE_DOWNLOAD_FETCHFAILED = getStringFromConfig("LANGUAGE.DOWNLOAD_FETCHFAILED");
        LANGUAGE_DOWNLOAD_SUCCESSFUL = getStringFromConfig("LANGUAGE.DOWNLOAD_SUCCESSFUL");
        LANGUAGE_DOWNLOAD_TITLE = getStringFromConfig("LANGUAGE.DOWNLOAD_TITLE");

        RELOAD_CONFIG = getStringFromConfig("RELOAD.CONFIG");
        RELOAD_HISTORY = getStringFromConfig("RELOAD.HISTORY");
        RELOAD_LANGUAGE = getStringFromConfig("RELOAD.LANGUAGE");
        RELOAD_FAILED = getStringFromConfig("RELOAD.FAILED");
        RELOAD_SUCCESSFUL = getStringFromConfig("RELOAD.SUCCESSFUL");

        HELP_GET_COMMAND = getStringFromConfig("HELP_GET_COMMAND");
        SAVED_HEADS_TITLE = getStringFromConfig("SAVED_HEADS_TITLE");
        PUT_ON_HEAD = getStringFromConfig("PUT_ON_HEAD");
        NO_PERMISSION_TO_VIEW = getListFromConfig("NO_PERMISSION_TO_VIEW");

        ECONOMY_PRICE = getStringFromConfig("ECONOMY.PRICE");
        ECONOMY_PRICE_FORMAT = getStringFromConfig("ECONOMY.PRICE_FORMAT");
        ECONOMY_FREE = getStringFromConfig("ECONOMY.FREE");
        ECONOMY_BOUGHT = getStringFromConfig("ECONOMY.BOUGHT");
        ECONOMY_BUY_PROMPT = getStringFromConfig("ECONOMY.BUY_PROMPT");
        ECONOMY_BUY_CONFIRM = getStringFromConfig("ECONOMY.BUY_CONFIRM");
        ECONOMY_BUY_SUCCESSFUL = getStringFromConfig("ECONOMY.BUY_SUCCESSFUL");
        ECONOMY_BUY_FAILED = getStringFromConfig("ECONOMY.BUY_FAILED");

        HISTORY_DISABLED = getStringFromConfig("HISTORY_DISABLED");
        HISTORY_INVALID_PLAYER = getStringFromConfig("HISTORY_INVALID_PLAYER");
        HISTORY_NO_VIEW_PERMISSION = getStringFromConfig("HISTORY_NO_VIEW_PERMISSION");
        CANNOT_PLACE_IN_BLOCK = getStringFromConfig("CANNOT_PLACE_IN_BLOCK");
        CANNOT_PLACE_IN_AIR = getStringFromConfig("CANNOT_PLACE_IN_AIR");
        ALREADY_IN_USE = getStringFromConfig("ALREADY_IN_USE");
        STARTING = getStringFromConfig("STARTING");
        INVALID_INPUT = getStringFromConfig("INVALID_INPUT");
        CHANGE_SEARCH_STRING = getStringFromConfig("CHANGE_SEARCH_STRING");
        SEARCH_LORE = getListFromConfig("SEARCH_LORE");
        TO_LONG_INPUT = getStringFromConfig("TO_LONG_INPUT");
        SEARCHING = getStringFromConfig("SEARCHING");
        CYCLE_ARRANGEMENT_PREFIX = getStringFromConfig("CYCLE_ARRANGEMENT.PREFIX");
        CYCLE_ARRANGEMENT_DEFAULT = getStringFromConfig("CYCLE_ARRANGEMENT.DEFAULT");
        CYCLE_ARRANGEMENT_ALPHABETICAL = getStringFromConfig("CYCLE_ARRANGEMENT.ALPHABETICAL");
        CYCLE_ARRANGEMENT_COLOR = getStringFromConfig("CYCLE_ARRANGEMENT.COLOR");
        SEARCH_TITLE = getStringFromConfig("SEARCH_TITLE");
        LOADING = getStringFromConfig("LOADING");
        NO_RESULTS = getStringFromConfig("NO_RESULTS");
        NO_RESULTS_TRY_AGAIN = getStringFromConfig("NO_RESULTS_TRY_AGAIN");
        PAGE_GENERAL_PREFIX = getStringFromConfig("PAGE_GENERAL_PREFIX");

        CATEGORIES_BASECOMMAND_HEADER = getStringFromConfig("CATEGORIES.BASE_COMMAND.HEADER");
        CATEGORIES_BASECOMMAND_HOVERINFO_CATEGORY = getListFromConfig("CATEGORIES.BASE_COMMAND.HOVERINFO_CATEGORY");
        CATEGORIES_BASECOMMAND_HOVERINFO_SUBCATEGORY = getListFromConfig("CATEGORIES.BASE_COMMAND.HOVERINFO_SUBCATEGORY");

        CATEGORIES_REMOVE_NOTFOUND = getStringFromConfig("CATEGORIES.REMOVE.NOTFOUND");
        CATEGORIES_REMOVE_INUSE = getStringFromConfig("CATEGORIES.REMOVE.INUSE");
        CATEGORIES_REMOVE_FAILED = getStringFromConfig("CATEGORIES.REMOVE.FAILED");
        CATEGORIES_REMOVE_SUCCESSFUL = getStringFromConfig("CATEGORIES.REMOVE.SUCCESSFUL");

        CATEGORIES_DELETE_CATEGORY_NOTFOUND = getStringFromConfig("CATEGORIES.DELETE.CATEGORY.NOTFOUND");
        CATEGORIES_DELETE_CATEGORY_INUSE = getStringFromConfig("CATEGORIES.DELETE.CATEGORY.INUSE");
        CATEGORIES_DELETE_CATEGORY_CONFIRM = getStringFromConfig("CATEGORIES.DELETE.CATEGORY.CONFIRM");
        CATEGORIES_DELETE_CATEGORY_FAILED = getStringFromConfig("CATEGORIES.DELETE.CATEGORY.FAILED");
        CATEGORIES_DELETE_CATEGORY_SUCCESSFUL = getStringFromConfig("CATEGORIES.DELETE.CATEGORY.SUCCESSFUL");

        CATEGORIES_DELETE_SUBCATEGORY_NOTFOUND = getStringFromConfig("CATEGORIES.DELETE.SUBCATEGORY.NOTFOUND");
        CATEGORIES_DELETE_SUBCATEGORY_INUSE = getStringFromConfig("CATEGORIES.DELETE.SUBCATEGORY.INUSE");
        CATEGORIES_DELETE_SUBCATEGORY_CONFIRM = getStringFromConfig("CATEGORIES.DELETE.SUBCATEGORY.CONFIRM");
        CATEGORIES_DELETE_SUBCATEGORY_FAILED = getStringFromConfig("CATEGORIES.DELETE.SUBCATEGORY.FAILED");
        CATEGORIES_DELETE_SUBCATEGORY_SUCCESSFUL = getStringFromConfig("CATEGORIES.DELETE.SUBCATEGORY.SUCCESSFUL");

        CATEGORIES_IMPORT_NOCATEGORYFOLDER = getStringFromConfig("CATEGORIES.IMPORT.NOCATEGORYFOLDER");
        CATEGORIES_IMPORT_BASECOMMAND_NOFILES = getStringFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.NOFILES");
        CATEGORIES_IMPORT_BASECOMMAND_LIST = getStringFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.LIST");
        CATEGORIES_IMPORT_BASECOMMAND_LISTFORMAT = getStringFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.LIST_FORMAT");
        CATEGORIES_IMPORT_BASECOMMAND_CLICKTOIMPORT = getStringFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.CLICKTOIMPORT");
        CATEGORIES_IMPORT_SUCCESSFUL = getStringFromConfig("CATEGORIES.IMPORT.SUCCESSFUL");
        CATEGORIES_IMPORT_DUBLICATE_CATEGORY = getStringFromConfig("CATEGORIES.IMPORT.DUBLICATE_CATEGORY");
        CATEGORIES_IMPORT_DUBLICATE_SUBCATEGORY = getStringFromConfig("CATEGORIES.IMPORT.DUBLICATE_SUBCATEGORY");
        CATEGORIES_IMPORT_ERROR = getStringFromConfig("CATEGORIES.IMPORT.ERROR");
        CATEGORIES_IMPORT_INVALIDFILE = getStringFromConfig("CATEGORIES.IMPORT.INVALID_FILE");
        CATEGORIES_IMPORT_FILENOTFOUND = getStringFromConfig("CATEGORIES.IMPORT.FILENOTFOUND");

        UNDO_INVALID_INPUT = getStringFromConfig("UNDO_INVALID_INPUT");
        UNDO_SUCCESSFUL = getStringFromConfig("UNDO_SUCCESSFUL");
        UNDO_NOTHING_LEFT = getStringFromConfig("UNDO_NOTHING_LEFT");
        GET_HEAD_NAME = getStringFromConfig("GET_HEAD_NAME");
        GET_INVALID = getStringFromConfig("GET_INVALID");
        SAVE_GET_TEXTURE = getStringFromConfig("SAVE_GET_TEXTURE");
        SAVE_OWN_SUCCESSFUL = getStringFromConfig("SAVE_OWN_SUCCESSFUL");
        SAVE_OWN_FAILED = getStringFromConfig("SAVE_OWN_FAILED");
        SAVE_UNAVAILABLE = getStringFromConfig("SAVE_UNAVIABLE");
        SAVE_NOT_CUSTOM_TEXTURE = getStringFromConfig("SAVE_NOT_CUSTOM_TEXTURE");
        SAVE_SUCCESSFUL = getStringFromConfig("SAVE_SUCCESSFUL");
        SAVE_ALREADY_EXIST = getStringFromConfig("SAVE_ALREADY_EXIST");
        SAVE_NOT_SKULL = getStringFromConfig("SAVE_NOT_SKULL");
        REMOVE_SUCCESSFUL = getStringFromConfig("REMOVE_SUCCESSFUL");
        REMOVE_FAILED = getStringFromConfig("REMOVE_FAILED");
        REMOVE_CONFIRMATION = getStringFromConfig("REMOVE_CONFIRMATION");

        WRITE_NOFONT = getStringFromConfig("WRITE_NOFONT");
        WRITE_TO_LONG = getStringFromConfig("WRITE_TO_LONG");
        WRITE_WRITING = getStringFromConfig("WRITE_WRITING");

        FONTS_GENERAL_NOTFOUND = getStringFromConfig("FONTS.GENERAL_NOTFOUND");
        FONTS_CREATE_ALREADYEXISTS = getStringFromConfig("FONTS.CREATE_ALREADYEXISTS");
        FONTS_CREATE_SUCCESSFUL = getStringFromConfig("FONTS.CREATE_SUCCESSFUL");
        FONTS_REMOVE_SUCCESSFUL = getStringFromConfig("FONTS.REMOVE_SUCCESSFUL");
        FONTS_REMOVE_FAILED = getStringFromConfig("FONTS.REMOVE_FAILED");
        FONTS_NOFONTS = getStringFromConfig("FONTS.NOFONTS");
        FONTS_LIST_HEADER = getStringFromConfig("FONTS.LIST_HEADER");
        FONTS_LIST = getStringFromConfig("FONTS.LIST");

        FONTS_EDIT_TITLE = getStringFromConfig("FONTS.EDIT_TITLE");
        FONTS_EDIT_ADDCHARACTER = getStringFromConfig("FONTS.EDIT_ADDCHARACTER");
        FONTS_EDIT_REMOVECHARACTER = getStringFromConfig("FONTS.EDIT_REMOVECHARACTER");
        FONTS_EDIT_REMOVECHARACTER_INFO = getListFromConfig("FONTS.EDIT_REMOVECHARACTER_INFO");
        FONTS_EDIT_ADDCHARACTER_SUCCESSFUL = getStringFromConfig("FONTS.EDIT_ADDCHARACTER_SUCCESSFUL");
        FONTS_EDIT_ADDCHARACTER_FAILED = getStringFromConfig("FONTS.EDIT_ADDCHARACTER_FAILED");
        FONTS_EDIT_SELECTED = getStringFromConfig("FONTS.EDIT_SELECTED");
        FONTS_EDIT_REMOVECHARCATER_CONFIRM = getStringFromConfig("FONTS.EDIT_REMOVECHARCATER_CONFIRM");
        FONTS_EDIT_REMOVECHARCATER_PREINFO = getStringFromConfig("FONTS.EDIT_REMOVECHARCATER_PREINFO");
        FONTS_EDIT_REMOVECHARCATER_FAILED = getStringFromConfig("FONTS.EDIT_REMOVECHARCATER_FAILED");
        FONTS_EDIT_REMOVECHARCATER_SUCCESSFUL = getStringFromConfig("FONTS.EDIT_REMOVECHARCATER_SUCCESSFUL");
        FONTS_EDIT_ADDCHARACTER_TITLE = getStringFromConfig("FONTS.EDIT_ADDCHARACTER_TITLE");
        FONTS_EDIT_ADD_TOGGLE = getStringFromConfig("FONTS.EDIT_ADD_DROPPER_TOGGLE");
        FONTS_EDIT_ADD_DROPPER_NAME = getStringFromConfig("FONTS.EDIT_ADD_DROPPER_NAME");
        FONTS_EDIT_ADD_DROPPER_LORE = getListFromConfig("FONTS.EDIT_ADD_DROPPER_LORE");
        FONTS_EDIT_SAVENEXIT = getStringFromConfig("FONTS.EDIT_SAVENEXIT");
        FONTS_EDIT_SAVE_SUCCESSFUL = getStringFromConfig("FONTS.EDIT_SAVE_SUCCESSFUL");

        BACK_GENERAL = getStringFromConfig("BACK_GENERAL");
        BACK_TO_PREVIOUS = getStringFromConfig("BACK_TO_PREVIOUS");
        NEXT_PAGE = getStringFromConfig("NEXT_PAGE");
        PREVIOUS_PAGE = getStringFromConfig("PREVIOUS_PAGE");
        HISTORY_INV_TITLE = getStringFromConfig("HISTORY_INV_TITLE");
        HISTORY_SEARCHHISTORY_ACTIVE = getStringFromConfig("HISTORY_SEARCHHISTORY_ACTIVE");
        HISTORY_SEARCHHISTORY = getStringFromConfig("HISTORY_SEARCHHISTORY");
        HISTORY_GETHISTORY_ACTIVE = getStringFromConfig("HISTORY_GETHISTORY_ACTIVE");
        HISTORY_GETHISTORY = getStringFromConfig("HISTORY_GETHISTORY");
        HISTORY_NO_HISTORY_LORE = getListFromConfig("HISTORY_NO_HISTORY_LORE");
        HISTORY_SEARCHHISTORY_LORE = getListFromConfig("HISTORY_SEARCHHISTORY_LORE");
        HISTORY_GET_LORE = getListFromConfig("HISTORY_GET_LORE");
        ITEMS_YOUR_HEAD = getStringFromConfig("ITEM_NAMES.YOUR_HEAD");
        ITEMS_COLLECTION = getStringFromConfig("ITEM_NAMES.COLLECTION");
        ITEMS_HELP = getStringFromConfig("ITEM_NAMES.HELP");
        ITEMS_HELP_LORE = getListFromConfig("ITEM_NAMES.HELP_LORE");
        ITEMS_SEARCH = getStringFromConfig("ITEM_NAMES.SEARCH");
        ITEMS_SEARCH_LORE = getListFromConfig("ITEM_NAMES.SEARCH_LORE");
        ITEMS_FIND_MORE = getStringFromConfig("ITEM_NAMES.FIND_MORE");
        ITEMS_FIND_MORE_LORE = getListFromConfig("ITEM_NAMES.FIND_MORE_LORE");
        ITEMS_INFO = getStringFromConfig("ITEM_NAMES.INFO");
        ITEMS_INFO_LORE = getListFromConfig("ITEM_NAMES.INFO_LORE");
        LOCKED = ChatColor.stripColor(getStringFromConfig("LOCKED"));

        if (!CustomHeads.hasReducedDebug())
            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.PREFIX_GENERAL + "Successfully loaded Language from language/" + language + "/language.yml in " + (System.currentTimeMillis() - timestamp) + "ms");
        loaded = true;
    }

    private String getStringFromConfig(String path) {
        if (!languageConfig.get().contains(path))
            throw new NullPointerException("Error getting " + path + " from language/" + currentLanguage + "/language.yml. If this Error keeps occurring please try to delete the Language Folder");
        return format(languageConfig.get().getString(path));
    }

    private List<String> getListFromConfig(String path) {
        if (!languageConfig.get().contains(path))
            throw new NullPointerException("Error getting " + path + " from language/" + currentLanguage + "/language.yml. If this Error keeps occurring please try to delete the Language Folder");
        return format(languageConfig.get().getStringList(path));
    }

    public static List<String> getInstalledLanguages() {
        File languageDirectory = new File("plugins/" + CustomHeads.getInstance().getName() + "/language");
        File[] languageDirectories = languageDirectory.listFiles(pathname -> {
            File[] files = pathname.listFiles();
            if(files == null) {
                return false;
            }
            return pathname.isDirectory() && Arrays.stream(files).allMatch(file -> {
                String fileName = file.getName();
                return (file.isDirectory() && fileName.equals("categories")) || (file.isFile() && (fileName.equals("language.yml") || fileName.equals("settings.json")));
            });
        });
        if(languageDirectories == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(languageDirectories).map(File::getName).collect(Collectors.toList());
    }

}
