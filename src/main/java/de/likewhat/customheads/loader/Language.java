package de.likewhat.customheads.loader;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.Configs;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.List;

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
    private Configs languageConfig;
    @Getter
    private String currentLanguage;

    public Language(String language) {
        loaded = false;
        languageConfig = new Configs(CustomHeads.getInstance(), "language.yml", false, "language/" + language);
        currentLanguage = language;

        languageConfig.reload();
        if (!CustomHeads.hasReducedDebug())
            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Loading Language from language/" + language + "/language.yml...");
        long timestamp = System.currentTimeMillis();
        DEFINITION = ChatColor.stripColor(getFromConfig("DEFINITION"));

        NO_PERMISSION = getFromConfig("NO_PERMISSION");

        YES = ChatColor.stripColor(getFromConfig("LANG_YES"));
        NO = ChatColor.stripColor(getFromConfig("LANG_NO"));
        CONFIRM_NOTHINGFOUND = getFromConfig("CONFIRM_NOTHINGFOUND");
        COMMAND_USAGE = getFromConfig("COMMAND_USAGE");

        LINK = getFromConfig("LINK");

        LANGUAGE_CHANGE_CHANGING = getFromConfig("LANGUAGE.CHANGE_CHANGING");
        LANGUAGE_CHANGE_SUCCESSFUL = getFromConfig("LANGUAGE.CHANGE_SUCCESSFUL");
        LANGUAGE_CHANGE_ALREADY_USED = getFromConfig("LANGUAGE.CHANGE_ALREADY_USED");
        LANGUAGE_CHANGE_FAILED = getFromConfig("LANGUAGE.CHANGE_FAILED");
        LANGUAGE_REDOWNLOAD_FAILED_WAIT = getFromConfig("LANGUAGE.REDOWNLOAD_FAILED_WAIT");
        LANGUAGE_REDOWNLOAD_BACKING_UP = getFromConfig("LANGUAGE.REDOWNLOAD_BACKING_UP");
        LANGUAGE_REDOWNLOAD_BACKUP_FAILED = getFromConfig("LANGUAGE.REDOWNLOAD_BACKUP_FAILED");
        LANGUAGE_REDOWNLOAD_DOWNLOADING = getFromConfig("LANGUAGE.REDOWNLOAD_DOWNLOADING");
        LANGUAGE_REDOWNLOAD_DONE = getFromConfig("LANGUAGE.REDOWNLOAD_DONE");

        LANGUAGE_DOWNLOAD_FETCHING = getFromConfig("LANGUAGE.DOWNLOAD_FETCHING");
        LANGUAGE_DOWNLOAD_FETCHFAILED = getFromConfig("LANGUAGE.DOWNLOAD_FETCHFAILED");
        LANGUAGE_DOWNLOAD_SUCCESSFUL = getFromConfig("LANGUAGE.DOWNLOAD_SUCCESSFUL");
        LANGUAGE_DOWNLOAD_TITLE = getFromConfig("LANGUAGE.DOWNLOAD_TITLE");

        RELOAD_CONFIG = getFromConfig("RELOAD.CONFIG");
        RELOAD_HISTORY = getFromConfig("RELOAD.HISTORY");
        RELOAD_LANGUAGE = getFromConfig("RELOAD.LANGUAGE");
        RELOAD_FAILED = getFromConfig("RELOAD.FAILED");
        RELOAD_SUCCESSFUL = getFromConfig("RELOAD.SUCCESSFUL");

        HELP_GET_COMMAND = getFromConfig("HELP_GET_COMMAND");
        SAVED_HEADS_TITLE = getFromConfig("SAVED_HEADS_TITLE");
        PUT_ON_HEAD = getFromConfig("PUT_ON_HEAD");
        NO_PERMISSION_TO_VIEW = getFromConfig("NO_PERMISSION_TO_VIEW");

        ECONOMY_PRICE = getFromConfig("ECONOMY.PRICE");
        ECONOMY_PRICE_FORMAT = getFromConfig("ECONOMY.PRICE_FORMAT");
        ECONOMY_FREE = getFromConfig("ECONOMY.FREE");
        ECONOMY_BOUGHT = getFromConfig("ECONOMY.BOUGHT");
        ECONOMY_BUY_PROMPT = getFromConfig("ECONOMY.BUY_PROMPT");
        ECONOMY_BUY_CONFIRM = getFromConfig("ECONOMY.BUY_CONFIRM");
        ECONOMY_BUY_SUCCESSFUL = getFromConfig("ECONOMY.BUY_SUCCESSFUL");
        ECONOMY_BUY_FAILED = getFromConfig("ECONOMY.BUY_FAILED");

        HISTORY_DISABLED = getFromConfig("HISTORY_DISABLED");
        HISTORY_INVALID_PLAYER = getFromConfig("HISTORY_INVALID_PLAYER");
        HISTORY_NO_VIEW_PERMISSION = getFromConfig("HISTORY_NO_VIEW_PERMISSION");
        CANNOT_PLACE_IN_BLOCK = getFromConfig("CANNOT_PLACE_IN_BLOCK");
        CANNOT_PLACE_IN_AIR = getFromConfig("CANNOT_PLACE_IN_AIR");
        ALREADY_IN_USE = getFromConfig("ALREADY_IN_USE");
        STARTING = getFromConfig("STARTING");
        INVALID_INPUT = getFromConfig("INVALID_INPUT");
        CHANGE_SEARCH_STRING = getFromConfig("CHANGE_SEARCH_STRING");
        SEARCH_LORE = getFromConfig("SEARCH_LORE");
        TO_LONG_INPUT = getFromConfig("TO_LONG_INPUT");
        SEARCHING = getFromConfig("SEARCHING");
        CYCLE_ARRANGEMENT_PREFIX = getFromConfig("CYCLE_ARRANGEMENT.PREFIX");
        CYCLE_ARRANGEMENT_DEFAULT = getFromConfig("CYCLE_ARRANGEMENT.DEFAULT");
        CYCLE_ARRANGEMENT_ALPHABETICAL = getFromConfig("CYCLE_ARRANGEMENT.ALPHABETICAL");
        CYCLE_ARRANGEMENT_COLOR = getFromConfig("CYCLE_ARRANGEMENT.COLOR");
        SEARCH_TITLE = getFromConfig("SEARCH_TITLE");
        LOADING = getFromConfig("LOADING");
        NO_RESULTS = getFromConfig("NO_RESULTS");
        NO_RESULTS_TRY_AGAIN = getFromConfig("NO_RESULTS_TRY_AGAIN");
        PAGE_GENERAL_PREFIX = getFromConfig("PAGE_GENERAL_PREFIX");

        CATEGORIES_BASECOMMAND_HEADER = getFromConfig("CATEGORIES.BASE_COMMAND.HEADER");
        CATEGORIES_BASECOMMAND_HOVERINFO_CATEGORY = getFromConfig("CATEGORIES.BASE_COMMAND.HOVERINFO_CATEGORY");
        CATEGORIES_BASECOMMAND_HOVERINFO_SUBCATEGORY = getFromConfig("CATEGORIES.BASE_COMMAND.HOVERINFO_SUBCATEGORY");

        CATEGORIES_REMOVE_NOTFOUND = getFromConfig("CATEGORIES.REMOVE.NOTFOUND");
        CATEGORIES_REMOVE_INUSE = getFromConfig("CATEGORIES.REMOVE.INUSE");
        CATEGORIES_REMOVE_FAILED = getFromConfig("CATEGORIES.REMOVE.FAILED");
        CATEGORIES_REMOVE_SUCCESSFUL = getFromConfig("CATEGORIES.REMOVE.SUCCESSFUL");

        CATEGORIES_DELETE_CATEGORY_NOTFOUND = getFromConfig("CATEGORIES.DELETE.CATEGORY.NOTFOUND");
        CATEGORIES_DELETE_CATEGORY_INUSE = getFromConfig("CATEGORIES.DELETE.CATEGORY.INUSE");
        CATEGORIES_DELETE_CATEGORY_CONFIRM = getFromConfig("CATEGORIES.DELETE.CATEGORY.CONFIRM");
        CATEGORIES_DELETE_CATEGORY_FAILED = getFromConfig("CATEGORIES.DELETE.CATEGORY.FAILED");
        CATEGORIES_DELETE_CATEGORY_SUCCESSFUL = getFromConfig("CATEGORIES.DELETE.CATEGORY.SUCCESSFUL");

        CATEGORIES_DELETE_SUBCATEGORY_NOTFOUND = getFromConfig("CATEGORIES.DELETE.SUBCATEGORY.NOTFOUND");
        CATEGORIES_DELETE_SUBCATEGORY_INUSE = getFromConfig("CATEGORIES.DELETE.SUBCATEGORY.INUSE");
        CATEGORIES_DELETE_SUBCATEGORY_CONFIRM = getFromConfig("CATEGORIES.DELETE.SUBCATEGORY.CONFIRM");
        CATEGORIES_DELETE_SUBCATEGORY_FAILED = getFromConfig("CATEGORIES.DELETE.SUBCATEGORY.FAILED");
        CATEGORIES_DELETE_SUBCATEGORY_SUCCESSFUL = getFromConfig("CATEGORIES.DELETE.SUBCATEGORY.SUCCESSFUL");

        CATEGORIES_IMPORT_NOCATEGORYFOLDER = getFromConfig("CATEGORIES.IMPORT.NOCATEGORYFOLDER");
        CATEGORIES_IMPORT_BASECOMMAND_NOFILES = getFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.NOFILES");
        CATEGORIES_IMPORT_BASECOMMAND_LIST = getFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.LIST");
        CATEGORIES_IMPORT_BASECOMMAND_LISTFORMAT = getFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.LIST_FORMAT");
        CATEGORIES_IMPORT_BASECOMMAND_CLICKTOIMPORT = getFromConfig("CATEGORIES.IMPORT.BASE_COMMAND.CLICKTOIMPORT");
        CATEGORIES_IMPORT_SUCCESSFUL = getFromConfig("CATEGORIES.IMPORT.SUCCESSFUL");
        CATEGORIES_IMPORT_DUBLICATE_CATEGORY = getFromConfig("CATEGORIES.IMPORT.DUBLICATE_CATEGORY");
        CATEGORIES_IMPORT_DUBLICATE_SUBCATEGORY = getFromConfig("CATEGORIES.IMPORT.DUBLICATE_SUBCATEGORY");
        CATEGORIES_IMPORT_ERROR = getFromConfig("CATEGORIES.IMPORT.ERROR");
        CATEGORIES_IMPORT_INVALIDFILE = getFromConfig("CATEGORIES.IMPORT.INVALID_FILE");
        CATEGORIES_IMPORT_FILENOTFOUND = getFromConfig("CATEGORIES.IMPORT.FILENOTFOUND");

        UNDO_INVALID_INPUT = getFromConfig("UNDO_INVALID_INPUT");
        UNDO_SUCCESSFUL = getFromConfig("UNDO_SUCCESSFUL");
        UNDO_NOTHING_LEFT = getFromConfig("UNDO_NOTHING_LEFT");
        GET_HEAD_NAME = getFromConfig("GET_HEAD_NAME");
        GET_INVALID = getFromConfig("GET_INVALID");
        SAVE_GET_TEXTURE = getFromConfig("SAVE_GET_TEXTURE");
        SAVE_OWN_SUCCESSFUL = getFromConfig("SAVE_OWN_SUCCESSFUL");
        SAVE_OWN_FAILED = getFromConfig("SAVE_OWN_FAILED");
        SAVE_UNAVAILABLE = getFromConfig("SAVE_UNAVIABLE");
        SAVE_NOT_CUSTOM_TEXTURE = getFromConfig("SAVE_NOT_CUSTOM_TEXTURE");
        SAVE_SUCCESSFUL = getFromConfig("SAVE_SUCCESSFUL");
        SAVE_ALREADY_EXIST = getFromConfig("SAVE_ALREADY_EXIST");
        SAVE_NOT_SKULL = getFromConfig("SAVE_NOT_SKULL");
        REMOVE_SUCCESSFUL = getFromConfig("REMOVE_SUCCESSFUL");
        REMOVE_FAILED = getFromConfig("REMOVE_FAILED");
        REMOVE_CONFIRMATION = getFromConfig("REMOVE_CONFIRMATION");

        WRITE_NOFONT = getFromConfig("WRITE_NOFONT");
        WRITE_TO_LONG = getFromConfig("WRITE_TO_LONG");
        WRITE_WRITING = getFromConfig("WRITE_WRITING");

        FONTS_GENERAL_NOTFOUND = getFromConfig("FONTS.GENERAL_NOTFOUND");
        FONTS_CREATE_ALREADYEXISTS = getFromConfig("FONTS.CREATE_ALREADYEXISTS");
        FONTS_CREATE_SUCCESSFUL = getFromConfig("FONTS.CREATE_SUCCESSFUL");
        FONTS_REMOVE_SUCCESSFUL = getFromConfig("FONTS.REMOVE_SUCCESSFUL");
        FONTS_REMOVE_FAILED = getFromConfig("FONTS.REMOVE_FAILED");
        FONTS_NOFONTS = getFromConfig("FONTS.NOFONTS");
        FONTS_LIST_HEADER = getFromConfig("FONTS.LIST_HEADER");
        FONTS_LIST = getFromConfig("FONTS.LIST");

        FONTS_EDIT_TITLE = getFromConfig("FONTS.EDIT_TITLE");
        FONTS_EDIT_ADDCHARACTER = getFromConfig("FONTS.EDIT_ADDCHARACTER");
        FONTS_EDIT_REMOVECHARACTER = getFromConfig("FONTS.EDIT_REMOVECHARACTER");
        FONTS_EDIT_REMOVECHARACTER_INFO = getFromConfig("FONTS.EDIT_REMOVECHARACTER_INFO");
        FONTS_EDIT_ADDCHARACTER_SUCCESSFUL = getFromConfig("FONTS.EDIT_ADDCHARACTER_SUCCESSFUL");
        FONTS_EDIT_ADDCHARACTER_FAILED = getFromConfig("FONTS.EDIT_ADDCHARACTER_FAILED");
        FONTS_EDIT_SELECTED = getFromConfig("FONTS.EDIT_SELECTED");
        FONTS_EDIT_REMOVECHARCATER_CONFIRM = getFromConfig("FONTS.EDIT_REMOVECHARCATER_CONFIRM");
        FONTS_EDIT_REMOVECHARCATER_PREINFO = getFromConfig("FONTS.EDIT_REMOVECHARCATER_PREINFO");
        FONTS_EDIT_REMOVECHARCATER_FAILED = getFromConfig("FONTS.EDIT_REMOVECHARCATER_FAILED");
        FONTS_EDIT_REMOVECHARCATER_SUCCESSFUL = getFromConfig("FONTS.EDIT_REMOVECHARCATER_SUCCESSFUL");
        FONTS_EDIT_ADDCHARACTER_TITLE = getFromConfig("FONTS.EDIT_ADDCHARACTER_TITLE");
        FONTS_EDIT_ADD_TOGGLE = getFromConfig("FONTS.EDIT_ADD_DROPPER_TOGGLE");
        FONTS_EDIT_ADD_DROPPER_NAME = getFromConfig("FONTS.EDIT_ADD_DROPPER_NAME");
        FONTS_EDIT_ADD_DROPPER_LORE = getFromConfig("FONTS.EDIT_ADD_DROPPER_LORE");
        FONTS_EDIT_SAVENEXIT = getFromConfig("FONTS.EDIT_SAVENEXIT");
        FONTS_EDIT_SAVE_SUCCESSFUL = getFromConfig("FONTS.EDIT_SAVE_SUCCESSFUL");

        BACK_GENERAL = getFromConfig("BACK_GENERAL");
        BACK_TO_PREVIOUS = getFromConfig("BACK_TO_PREVIOUS");
        NEXT_PAGE = getFromConfig("NEXT_PAGE");
        PREVIOUS_PAGE = getFromConfig("PREVIOUS_PAGE");
        HISTORY_INV_TITLE = getFromConfig("HISTORY_INV_TITLE");
        HISTORY_SEARCHHISTORY_ACTIVE = getFromConfig("HISTORY_SEARCHHISTORY_ACTIVE");
        HISTORY_SEARCHHISTORY = getFromConfig("HISTORY_SEARCHHISTORY");
        HISTORY_GETHISTORY_ACTIVE = getFromConfig("HISTORY_GETHISTORY_ACTIVE");
        HISTORY_GETHISTORY = getFromConfig("HISTORY_GETHISTORY");
        HISTORY_NO_HISTORY_LORE = getFromConfig("HISTORY_NO_HISTORY_LORE");
        HISTORY_SEARCHHISTORY_LORE = getFromConfig("HISTORY_SEARCHHISTORY_LORE");
        HISTORY_GET_LORE = getFromConfig("HISTORY_GET_LORE");
        ITEMS_YOUR_HEAD = getFromConfig("ITEM_NAMES.YOUR_HEAD");
        ITEMS_COLLECTION = getFromConfig("ITEM_NAMES.COLLECTION");
        ITEMS_HELP = getFromConfig("ITEM_NAMES.HELP");
        ITEMS_HELP_LORE = getFromConfig("ITEM_NAMES.HELP_LORE");
        ITEMS_SEARCH = getFromConfig("ITEM_NAMES.SEARCH");
        ITEMS_SEARCH_LORE = getFromConfig("ITEM_NAMES.SEARCH_LORE");
        ITEMS_FIND_MORE = getFromConfig("ITEM_NAMES.FIND_MORE");
        ITEMS_FIND_MORE_LORE = getFromConfig("ITEM_NAMES.FIND_MORE_LORE");
        ITEMS_INFO = getFromConfig("ITEM_NAMES.INFO");
        ITEMS_INFO_LORE = getFromConfig("ITEM_NAMES.INFO_LORE");
        LOCKED = ChatColor.stripColor(getFromConfig("LOCKED"));

        if (!CustomHeads.hasReducedDebug())
            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Successfully loaded Language from language/" + language + "/language.yml in " + (System.currentTimeMillis() - timestamp) + "ms");
        loaded = true;
    }

    private <Type> Type getFromConfig(String path) {
        if (!languageConfig.get().contains(path))
            throw new NullPointerException("Error getting " + path + " from language/" + currentLanguage + "/language.yml. If this Error keeps occurring please try to delete the Language Folder");
        if (languageConfig.get().get(path) instanceof List) {
            return (Type) format(languageConfig.get().getStringList(path));
        } else if (languageConfig.get().get(path) instanceof String) {
            return (Type) format(languageConfig.get().getString(path));
        }
        return null;
    }

}
