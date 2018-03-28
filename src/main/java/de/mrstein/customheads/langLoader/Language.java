package de.mrstein.customheads.langLoader;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.Configs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.logging.Level;

import static de.mrstein.customheads.utils.Utils.format;

public class Language {

    private static Configs lang;

    private static boolean init;

    private String currentLanguage;

    // Other or multible use
    public String LOCKED;
    public String INVALID_INPUT;
    public String NO_PERMISSION;
    public String RELOAD_CONFIG;
    public String RELOAD_HISTORY;
    public String RELOAD_LANGUAGE;
    public String RELOAD_FAILED;
    public String RELOAD_SUCCESSFUL;

    public String YES;
    public String NO;
    public String CONFIRM_NOTHINGFOUND;
    public String COMMAND_USAGE;

    public String LINK;
    public String HELP_GET_COMMAND;
    public String SAVED_HEADS_TITLE;
    public String PUT_ON_HEAD;
    public List<String> NO_PERMISSION_TO_VIEW;
    public String PAGE_GENERAL_PREFIX;
    public String BACK_GENERAL;
    public String BACK_TO_PREVIOUS;
    public String NEXT_PAGE;
    public String PREVIOUS_PAGE;
    // History
    public String HISTORY_DISABLED;
    public String HISTORY_INVALID_PLAYER;
    public String HISTORY_NO_VIEW_PERMISSION;
    // Firework
    public String CANNOT_PLACE_IN_BLOCK;
    public String CANNOT_PLACE_IN_AIR;
    public String ALREADY_IN_USE;
    public String STARTING;
    // Search
    public String CHANGE_SEARCH_STRING;
    public List<String> SEARCH_LORE;
    public String TO_LONG_INPUT;
    public String SEARCHING;
    public String CYCLE_ARRANGEMENT_PREFIX;
    public String CYCLE_ARRANGEMENT_DEFAULT;
    public String CYCLE_ARRANGEMENT_ALPHABETICAL;
    public String CYCLE_ARRANGEMENT_COLOR;
    public String SEARCH_TITLE;
    public String LOADING;
    public String NO_RESULTS;
    public String NO_RESULTS_TRY_AGAIN;

    // -- Categories
    // Bess Command
    public String CATEGORIES_BASECOMMAND_HEADER;
    public List<String> CATEGORIES_BASECOMMAND_HOVERINFO_CATEGORY;
    public List<String> CATEGORIES_BASECOMMAND_HOVERINFO_SUBCATEGORY;
    // Remove
    public String CATEGORIES_REMOVE_NOTFOUND;
    public String CATEGORIES_REMOVE_INUSE;
    public String CATEGORIES_REMOVE_FAILED;
    public String CATEGORIES_REMOVE_SUCCESSFUL;
    // Delete Category
    public String CATEGORIES_DELETE_CATEGORY_NOTFOUND;
    public String CATEGORIES_DELETE_CATEGORY_INUSE;
    public String CATEGORIES_DELETE_CATEGORY_CONFIRM;
    public String CATEGORIES_DELETE_CATEGORY_FAILED;
    public String CATEGORIES_DELETE_CATEGORY_SUCCESSFUL;
    // Delete Subcategory
    public String CATEGORIES_DELETE_SUBCATEGORY_NOTFOUND;
    public String CATEGORIES_DELETE_SUBCATEGORY_INUSE;
    public String CATEGORIES_DELETE_SUBCATEGORY_CONFIRM;
    public String CATEGORIES_DELETE_SUBCATEGORY_FAILED;
    public String CATEGORIES_DELETE_SUBCATEGORY_SUCCESSFUL;
    // Import
    public String CATEGORIES_IMPORT_NOCATEGORYFOLDER;
    public String CATEGORIES_IMPORT_BASECOMMAND_NOFILES;
    public String CATEGORIES_IMPORT_BASECOMMAND_LIST;
    public String CATEGORIES_IMPORT_BASECOMMAND_LISTFORMAT;
    public String CATEGORIES_IMPORT_BASECOMMAND_CLICKTOIMPORT;
    public String CATEGORIES_IMPORT_SUCCESSFUL;
    public String CATEGORIES_IMPORT_DUBLICATE_CATEGORY;
    public String CATEGORIES_IMPORT_DUBLICATE_SUBCATEGORY;
    public String CATEGORIES_IMPORT_ERROR;
    public String CATEGORIES_IMPORT_INVALIDFILE;
    public String CATEGORIES_IMPORT_FILENOTFOUND;
    // Undo
    public String UNDO_INVALID_INPUT;
    public String UNDO_SUCCESSFUL;
    public String UNDO_NOTHING_LEFT;
    // Get
    public String GET_HEAD_NAME;
    public String GET_INVALID;
    // Save
    public String SAVE_GET_TEXTURE;
    public String SAVE_OWN_SUCCESSFUL;
    public String SAVE_OWN_FAILED;
    public String SAVE_UNAVIABLE;
    public String SAVE_NOT_CUSTOM_TEXTURE;
    public String SAVE_SUCCESSFUL;
    public String SAVE_ALREADY_EXIST;
    public String SAVE_NOT_SKULL;
    // Remove
    public String REMOVE_SUCCESSFUL;
    public String REMOVE_FAILED;
    public String REMOVE_CONFIRMATION;
    // Write
    public String WRITE_NOFONT;
    public String WRITE_TO_LONG;
    public String WRITE_WRITING;
    // -- Fonts
    public String FONTS_GENERAL_NOTFOUND;
    public String FONTS_CREATE_ALREADYEXISTS;
    public String FONTS_CREATE_SUCCESSFUL;
    public String FONTS_REMOVE_SUCCESSFUL;
    public String FONTS_REMOVE_FAILED;
    public String FONTS_NOFONTS;
    public String FONTS_LIST_HEADER;
    public String FONTS_LIST;
    // Character Stuff
    public String FONTS_EDIT_TITLE;
    public String FONTS_EDIT_ADDCHARACTER;
    public String FONTS_EDIT_REMOVECHARACTER;
    public List<String> FONTS_EDIT_REMOVECHARACTER_INFO;
    public String FONTS_EDIT_ADDCHARACTER_SUCCESSFUL;
    public String FONTS_EDIT_ADDCHARACTER_FAILED;
    public String FONTS_EDIT_SELECTED;
    public String FONTS_EDIT_REMOVECHARCATER_CONFIRM;
    public String FONTS_EDIT_REMOVECHARCATER_FAILED;
    public String FONTS_EDIT_REMOVECHARCATER_SUCCESSFUL;
    public String FONTS_EDIT_REMOVECHARCATER_PREINFO;
    public String FONTS_EDIT_ADDCHARACTER_TITLE;
    public String FONTS_EDIT_ADD_TOGGLE;
    public String FONTS_EDIT_ADD_DROPPER_NAME;
    public List<String> FONTS_EDIT_ADD_DROPPER_LORE;
    public String FONTS_EDIT_SAVENEXIT;
    public String FONTS_EDIT_SAVE_SUCCESSFUL;
    // History
    public String HISTORY_INV_TITLE;
    public String HISTORY_SEARCHHISTORY_ACTIVE;
    public String HISTORY_SEARCHHISTORY;
    public String HISTORY_GETHISTORY_ACTIVE;
    public String HISTORY_GETHISTORY;
    public List<String> HISTORY_NO_HISTORY_LORE;
    public List<String> HISTORY_SEARCHHISTORY_LORE;
    public List<String> HISTORY_GET_LORE;
    // Items
    public String ITEMS_YOUR_HEAD;
    public String ITEMS_COLLECTION;
    public String ITEMS_HELP;
    public List<String> ITEMS_HELPLORE;
    public String ITEMS_SEARCH;
    public List<String> ITEMS_SEARCHLORE;
    public String ITEMS_FIND_MORE;
    public List<String> ITEMS_FIND_MORELORE;
    public String ITEMS_INFO;
    public List<String> ITEMS_INFOLORE;

    public Language(String language) {
        init = false;
        lang = new Configs(CustomHeads.getInstance(), "language.yml", true, "language/" + language);
        currentLanguage = language;

        lang.reload();
        CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Loading Language from language/" + language + "/language.yml...");
        long timestamp = System.currentTimeMillis();
        try {
            NO_PERMISSION = format(lang.get().getString("NO_PERMISSION"));

            YES = ChatColor.stripColor(format(lang.get().getString("LANG_YES")));
            NO = ChatColor.stripColor(format(lang.get().getString("LANG_NO")));
            CONFIRM_NOTHINGFOUND = format(lang.get().getString("CONFIRM_NOTHINGFOUND"));
            COMMAND_USAGE = format(lang.get().getString("COMMAND_USAGE"));

            LINK = format(lang.get().getString("LINK"));

            RELOAD_CONFIG = format(lang.get().getString("RELOAD.CONFIG"));
            RELOAD_HISTORY = format(lang.get().getString("RELOAD.HISTORY"));
            RELOAD_LANGUAGE = format(lang.get().getString("RELOAD.LANGUAGE"));
            RELOAD_FAILED = format(lang.get().getString("RELOAD.FAILED"));
            RELOAD_SUCCESSFUL = format(lang.get().getString("RELOAD.SUCCESSFUL"));

            HELP_GET_COMMAND = format(lang.get().getString("HELP_GET_COMMAND"));
            SAVED_HEADS_TITLE = format(lang.get().getString("SAVED_HEADS_TITLE"));
            PUT_ON_HEAD = format(lang.get().getString("PUT_ON_HEAD"));
            NO_PERMISSION_TO_VIEW = format(lang.get().getStringList("NO_PERMISSION_TO_VIEW"));
            HISTORY_DISABLED = format(lang.get().getString("HISTORY_DISABLED"));
            HISTORY_INVALID_PLAYER = format(lang.get().getString("HISTORY_INVALID_PLAYER"));
            HISTORY_NO_VIEW_PERMISSION = format(lang.get().getString("HISTORY_NO_VIEW_PERMISSION"));
            CANNOT_PLACE_IN_BLOCK = format(lang.get().getString("CANNOT_PLACE_IN_BLOCK"));
            CANNOT_PLACE_IN_AIR = format(lang.get().getString("CANNOT_PLACE_IN_AIR"));
            ALREADY_IN_USE = format(lang.get().getString("ALREADY_IN_USE"));
            STARTING = format(lang.get().getString("STARTING"));
            INVALID_INPUT = format(lang.get().getString("INVALID_INPUT"));
            CHANGE_SEARCH_STRING = format(lang.get().getString("CHANGE_SEARCH_STRING"));
            SEARCH_LORE = format(lang.get().getStringList("SEARCH_LORE"));
            TO_LONG_INPUT = format(lang.get().getString("TO_LONG_INPUT"));
            SEARCHING = format(lang.get().getString("SEARCHING"));
            CYCLE_ARRANGEMENT_PREFIX = format(lang.get().getString("CYCLE_ARRANGEMENT.PREFIX"));
            CYCLE_ARRANGEMENT_DEFAULT = format(lang.get().getString("CYCLE_ARRANGEMENT.DEFAULT"));
            CYCLE_ARRANGEMENT_ALPHABETICAL = format(lang.get().getString("CYCLE_ARRANGEMENT.ALPHABETICAL"));
            CYCLE_ARRANGEMENT_COLOR = format(lang.get().getString("CYCLE_ARRANGEMENT.COLOR"));
            SEARCH_TITLE = format(lang.get().getString("SEARCH_TITLE"));
            LOADING = format(lang.get().getString("LOADING"));
            NO_RESULTS = format(lang.get().getString("NO_RESULTS"));
            NO_RESULTS_TRY_AGAIN = format(lang.get().getString("NO_RESULTS_TRY_AGAIN"));
            PAGE_GENERAL_PREFIX = format(lang.get().getString("PAGE_GENERAL_PREFIX"));

            CATEGORIES_BASECOMMAND_HEADER = format(lang.get().getString("CATEGORIES.BASE_COMMAND.HEADER"));
            CATEGORIES_BASECOMMAND_HOVERINFO_CATEGORY = format(lang.get().getStringList("CATEGORIES.BASE_COMMAND.HOVERINFO_CATEGORY"));
            CATEGORIES_BASECOMMAND_HOVERINFO_SUBCATEGORY = format(lang.get().getStringList("CATEGORIES.BASE_COMMAND.HOVERINFO_SUBCATEGORY"));

            CATEGORIES_REMOVE_NOTFOUND = format(lang.get().getString("CATEGORIES.REMOVE.NOTFOUND"));
            CATEGORIES_REMOVE_INUSE = format(lang.get().getString("CATEGORIES.REMOVE.INUSE"));
            CATEGORIES_REMOVE_FAILED = format(lang.get().getString("CATEGORIES.REMOVE.FAILED"));
            CATEGORIES_REMOVE_SUCCESSFUL = format(lang.get().getString("CATEGORIES.REMOVE.SUCCESSFUL"));

            CATEGORIES_DELETE_CATEGORY_NOTFOUND = format(lang.get().getString("CATEGORIES.DELETE.CATEGORY.NOTFOUND"));
            CATEGORIES_DELETE_CATEGORY_INUSE = format(lang.get().getString("CATEGORIES.DELETE.CATEGORY.INUSE"));
            CATEGORIES_DELETE_CATEGORY_CONFIRM = format(lang.get().getString("CATEGORIES.DELETE.CATEGORY.CONFIRM"));
            CATEGORIES_DELETE_CATEGORY_FAILED = format(lang.get().getString("CATEGORIES.DELETE.CATEGORY.FAILED"));
            CATEGORIES_DELETE_CATEGORY_SUCCESSFUL = format(lang.get().getString("CATEGORIES.DELETE.CATEGORY.SUCCESSFUL"));

            CATEGORIES_DELETE_SUBCATEGORY_NOTFOUND = format(lang.get().getString("CATEGORIES.DELETE.SUBCATEGORY.NOTFOUND"));
            CATEGORIES_DELETE_SUBCATEGORY_INUSE = format(lang.get().getString("CATEGORIES.DELETE.SUBCATEGORY.INUSE"));
            CATEGORIES_DELETE_SUBCATEGORY_CONFIRM = format(lang.get().getString("CATEGORIES.DELETE.SUBCATEGORY.CONFIRM"));
            CATEGORIES_DELETE_SUBCATEGORY_FAILED = format(lang.get().getString("CATEGORIES.DELETE.SUBCATEGORY.FAILED"));
            CATEGORIES_DELETE_SUBCATEGORY_SUCCESSFUL = format(lang.get().getString("CATEGORIES.DELETE.SUBCATEGORY.SUCCESSFUL"));

            CATEGORIES_IMPORT_NOCATEGORYFOLDER = format(lang.get().getString("CATEGORIES.IMPORT.NOCATEGORYFOLDER"));
            CATEGORIES_IMPORT_NOCATEGORYFOLDER = format(lang.get().getString("CATEGORIES.IMPORT.NOCATEGORYFOLDER"));
            CATEGORIES_IMPORT_BASECOMMAND_NOFILES = format(lang.get().getString("CATEGORIES.IMPORT.BASE_COMMAND.NOFILES"));
            CATEGORIES_IMPORT_BASECOMMAND_LIST = format(lang.get().getString("CATEGORIES.IMPORT.BASE_COMMAND.LIST"));
            CATEGORIES_IMPORT_BASECOMMAND_LISTFORMAT = format(lang.get().getString("CATEGORIES.IMPORT.BASE_COMMAND.LIST_FORMAT"));
            CATEGORIES_IMPORT_BASECOMMAND_CLICKTOIMPORT = format(lang.get().getString("CATEGORIES.IMPORT.BASE_COMMAND.CLICKTOIMPORT"));
            CATEGORIES_IMPORT_SUCCESSFUL = format(lang.get().getString("CATEGORIES.IMPORT.SUCCESSFUL"));
            CATEGORIES_IMPORT_DUBLICATE_CATEGORY = format(lang.get().getString("CATEGORIES.IMPORT.DUBLICATE_CATEGORY"));
            CATEGORIES_IMPORT_DUBLICATE_SUBCATEGORY = format(lang.get().getString("CATEGORIES.IMPORT.DUBLICATE_SUBCATEGORY"));
            CATEGORIES_IMPORT_ERROR = format(lang.get().getString("CATEGORIES.IMPORT.ERROR"));
            CATEGORIES_IMPORT_INVALIDFILE = format(lang.get().getString("CATEGORIES.IMPORT.INVALID_FILE"));
            CATEGORIES_IMPORT_FILENOTFOUND = format(lang.get().getString("CATEGORIES.IMPORT.FILENOTFOUND"));

            UNDO_INVALID_INPUT = format(lang.get().getString("UNDO_INVALID_INPUT"));
            UNDO_SUCCESSFUL = format(lang.get().getString("UNDO_SUCCESSFUL"));
            UNDO_NOTHING_LEFT = format(lang.get().getString("UNDO_NOTHING_LEFT"));
            GET_HEAD_NAME = format(lang.get().getString("GET_HEAD_NAME"));
            GET_INVALID = format(lang.get().getString("GET_INVALID"));
            SAVE_GET_TEXTURE = format(lang.get().getString("SAVE_GET_TEXTURE"));
            SAVE_OWN_SUCCESSFUL = format(lang.get().getString("SAVE_OWN_SUCCESSFUL"));
            SAVE_OWN_FAILED = format(lang.get().getString("SAVE_OWN_FAILED"));
            SAVE_UNAVIABLE = format(lang.get().getString("SAVE_UNAVIABLE"));
            SAVE_NOT_CUSTOM_TEXTURE = format(lang.get().getString("SAVE_NOT_CUSTOM_TEXTURE"));
            SAVE_SUCCESSFUL = format(lang.get().getString("SAVE_SUCCESSFUL"));
            SAVE_ALREADY_EXIST = format(lang.get().getString("SAVE_ALREADY_EXIST"));
            SAVE_NOT_SKULL = format(lang.get().getString("SAVE_NOT_SKULL"));
            REMOVE_SUCCESSFUL = format(lang.get().getString("REMOVE_SUCCESSFUL"));
            REMOVE_FAILED = format(lang.get().getString("REMOVE_FAILED"));
            REMOVE_CONFIRMATION = format(lang.get().getString("REMOVE_CONFIRMATION"));

            WRITE_NOFONT = format(lang.get().getString("WRITE_NOFONT"));
            WRITE_TO_LONG = format(lang.get().getString("WRITE_TO_LONG"));
            WRITE_WRITING = format(lang.get().getString("WRITE_WRITING"));

            FONTS_GENERAL_NOTFOUND = format(lang.get().getString("FONTS.GENERAL_NOTFOUND"));
            FONTS_CREATE_ALREADYEXISTS = format(lang.get().getString("FONTS.CREATE_ALREADYEXISTS"));
            FONTS_CREATE_SUCCESSFUL = format(lang.get().getString("FONTS.CREATE_SUCCESSFUL"));
            FONTS_REMOVE_SUCCESSFUL = format(lang.get().getString("FONTS.REMOVE_SUCCESSFUL"));
            FONTS_REMOVE_FAILED = format(lang.get().getString("FONTS.REMOVE_FAILED"));
            FONTS_NOFONTS = format(lang.get().getString("FONTS.NOFONTS"));
            FONTS_LIST_HEADER = format(lang.get().getString("FONTS.LIST_HEADER"));
            FONTS_LIST = format(lang.get().getString("FONTS.LIST"));

            FONTS_EDIT_TITLE = format(lang.get().getString("FONTS.EDIT_TITLE"));
            FONTS_EDIT_ADDCHARACTER = format(lang.get().getString("FONTS.EDIT_ADDCHARACTER"));
            FONTS_EDIT_REMOVECHARACTER = format(lang.get().getString("FONTS.EDIT_REMOVECHARACTER"));
            FONTS_EDIT_REMOVECHARACTER_INFO = format(lang.get().getStringList("FONTS.EDIT_REMOVECHARACTER_INFO"));
            FONTS_EDIT_ADDCHARACTER_SUCCESSFUL = format(lang.get().getString("FONTS.EDIT_ADDCHARACTER_SUCCESSFUL"));
            FONTS_EDIT_ADDCHARACTER_FAILED = format(lang.get().getString("FONTS.EDIT_ADDCHARACTER_FAILED"));
            FONTS_EDIT_SELECTED = format(lang.get().getString("FONTS.EDIT_SELECTED"));
            FONTS_EDIT_REMOVECHARCATER_CONFIRM = format(lang.get().getString("FONTS.EDIT_REMOVECHARCATER_CONFIRM"));
            FONTS_EDIT_REMOVECHARCATER_PREINFO = format(lang.get().getString("FONTS.EDIT_REMOVECHARCATER_PREINFO"));
            FONTS_EDIT_REMOVECHARCATER_FAILED = format(lang.get().getString("FONTS.EDIT_REMOVECHARCATER_FAILED"));
            FONTS_EDIT_REMOVECHARCATER_SUCCESSFUL = format(lang.get().getString("FONTS.EDIT_REMOVECHARCATER_SUCCESSFUL"));
            FONTS_EDIT_ADDCHARACTER_TITLE = format(lang.get().getString("FONTS.EDIT_ADDCHARACTER_TITLE"));
            FONTS_EDIT_ADD_TOGGLE = format(lang.get().getString("FONTS.EDIT_ADD_DROPPER_TOGGLE"));
            FONTS_EDIT_ADD_DROPPER_NAME = format(lang.get().getString("FONTS.EDIT_ADD_DROPPER_NAME"));
            FONTS_EDIT_ADD_DROPPER_LORE = format(lang.get().getStringList("FONTS.EDIT_ADD_DROPPER_LORE"));
            FONTS_EDIT_SAVENEXIT = format(lang.get().getString("FONTS.EDIT_SAVENEXIT"));
            FONTS_EDIT_SAVE_SUCCESSFUL = format(lang.get().getString("FONTS.EDIT_SAVE_SUCCESSFUL"));

            BACK_GENERAL = format(lang.get().getString("BACK_GENERAL"));
            BACK_TO_PREVIOUS = format(lang.get().getString("BACK_TO_PREVIOUS"));
            NEXT_PAGE = format(lang.get().getString("NEXT_PAGE"));
            PREVIOUS_PAGE = format(lang.get().getString("PREVIOUS_PAGE"));
            HISTORY_INV_TITLE = format(lang.get().getString("HISTORY_INV_TITLE"));
            HISTORY_SEARCHHISTORY_ACTIVE = format(lang.get().getString("HISTORY_SEARCHHISTORY_ACTIVE"));
            HISTORY_SEARCHHISTORY = format(lang.get().getString("HISTORY_SEARCHHISTORY"));
            HISTORY_GETHISTORY_ACTIVE = format(lang.get().getString("HISTORY_GETHISTORY_ACTIVE"));
            HISTORY_GETHISTORY = format(lang.get().getString("HISTORY_GETHISTORY"));
            HISTORY_NO_HISTORY_LORE = format(lang.get().getStringList("HISTORY_NO_HISTORY_LORE"));
            HISTORY_SEARCHHISTORY_LORE = format(lang.get().getStringList("HISTORY_SEARCHHISTORY_LORE"));
            HISTORY_GET_LORE = format(lang.get().getStringList("HISTORY_GET_LORE"));
            ITEMS_YOUR_HEAD = format(lang.get().getString("ITEM_NAMES.YOUR_HEAD"));
            ITEMS_COLLECTION = format(lang.get().getString("ITEM_NAMES.COLLECTION"));
            ITEMS_HELP = format(lang.get().getString("ITEM_NAMES.HELP"));
            ITEMS_HELPLORE = format(lang.get().getStringList("ITEM_NAMES.HELP_LORE"));
            ITEMS_SEARCH = format(lang.get().getString("ITEM_NAMES.SEARCH"));
            ITEMS_SEARCHLORE = format(lang.get().getStringList("ITEM_NAMES.SEARCH_LORE"));
            ITEMS_FIND_MORE = format(lang.get().getString("ITEM_NAMES.FIND_MORE"));
            ITEMS_FIND_MORELORE = format(lang.get().getStringList("ITEM_NAMES.FIND_MORE_LORE"));
            ITEMS_INFO = format(lang.get().getString("ITEM_NAMES.INFO"));
            ITEMS_INFOLORE = format(lang.get().getStringList("ITEM_NAMES.INFO_LORE"));
            LOCKED = ChatColor.stripColor(format(lang.get().getString("LOCKED")));

            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Successfully loaded Language from language/" + language + "/language.yml in " + (System.currentTimeMillis() - timestamp) + "ms");
            init = true;
        } catch (NullPointerException e) {
            Bukkit.getLogger().log(Level.WARNING, "[CustomHeads] Something went wrong while loading Language from " + language + "/language.yml (Maybe Outdated?)", e);
        }
        
    }

    public static boolean isLoaded() {
        return init;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

}
