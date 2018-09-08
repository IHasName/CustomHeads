package de.mrstein.customheads.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.SubCategory;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.JsonFile;
import de.mrstein.customheads.utils.JsonToItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static de.mrstein.customheads.utils.Utils.format;
import static de.mrstein.customheads.utils.Utils.getBackButton;

@Getter
public class Looks {

    @Getter
    private static boolean loaded = false;
    public HashMap<Integer, Inventory> subCategoryLooks = new HashMap<>();
    private HashMap<String, Inventory> createdMenus = new HashMap<>();
    private HashMap<String, String[]> menuInfo = new HashMap<>();
    private HashMap<String, ItemStack> items = new HashMap<>();

    private List<Category> usedCategories = new ArrayList<>();
    private List<SubCategory> usedSubCategories = new ArrayList<>();

    public Looks(String language) {
        loaded = false;

        CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Loading Looks from language/" + language + "/settings.json");
        long timestamp = System.currentTimeMillis();

        JsonFile jsf = new JsonFile(new File("plugins/CustomHeads/language/" + language + "/settings.json"));

        try {
            // Base
            JsonObject jsonLooks = jsf.getJson().getAsJsonObject().get("looks").getAsJsonObject();
            JsonObject objs;

            // Items
            objs = jsonLooks.get("items").getAsJsonObject();

            if (!objs.entrySet().isEmpty()) {
                for (Map.Entry<String, JsonElement> idEntry : objs.entrySet()) {
                    if (idEntry == null || idEntry.getKey().startsWith("__")) continue;
                    ItemStack itemStack;
                    List<String> tags = new ArrayList<>();
                    tags.add("blockMoving");
                    tags.add("chItem");
                    tags.add(idEntry.getKey());
                    if (idEntry.getValue().getAsJsonObject().has("backButton")) {
                        JsonObject backButton = idEntry.getValue().getAsJsonObject().get("backButton").getAsJsonObject();
                        switch (backButton.get("action").getAsString()) {
                            case "openCategory":
                                tags.add("openCategory");
                                tags.add("category#>" + backButton.get("value").getAsString());
                                break;
                            case "openMenu":
                                tags.add("openMenu");
                                tags.add(backButton.get("value").getAsString());
                                break;
                            default:
                                break;
                        }
                        itemStack = getBackButton();

                    } else {
                        itemStack = JsonToItem.convert(idEntry.getValue().toString());
                        if (idEntry.getValue().getAsJsonObject().has("clickAction")) {
                            JsonObject clickAction = idEntry.getValue().getAsJsonObject().getAsJsonObject("clickAction");
                            switch (clickAction.get("action").getAsString()) {
                                case "openCategory":
                                    tags.add("openCategory");
                                    tags.add("category#>" + clickAction.get("value").getAsString());
                                    break;
                                case "openMenu":
                                    tags.add("openMenu");
                                    tags.add(clickAction.get("value").getAsString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    items.put(idEntry.getKey(), CustomHeads.getTagEditor().addTags(itemStack, tags));
                }
            }

            items.put("your_head", CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM, (short) 3).setDisplayName(CustomHeads.getLanguageManager().ITEMS_YOUR_HEAD).getItem(), "chItem", "your_head", "clonable", "tempTags"));
            items.put("help", CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM, (short) 3).setDisplayName(CustomHeads.getLanguageManager().ITEMS_HELP).setLore(CustomHeads.getLanguageManager().ITEMS_HELP_LORE).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE1M2JkZDE1NDU1MzFjOWViYjljNmY4OTViYzU3NjAxMmY2MTgyMGU2ZjQ4OTg4NTk4OGE3ZTg3MDlhM2Y0OCJ9fX0=").getItem(), "chItem", "help", "needsPermission", "heads.view.help"));
            items.put("search", CustomHeads.getTagEditor().setTags(new ItemEditor(Material.NAME_TAG).setDisplayName(CustomHeads.getLanguageManager().ITEMS_SEARCH).setLore(CustomHeads.getLanguageManager().ITEMS_SEARCH_LORE).getItem(), "chItem", "search", "needsPermission", "heads.use.more.search"));
            items.put("find_more", CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_FIND_MORE).setLore(CustomHeads.getLanguageManager().ITEMS_FIND_MORE_LORE).getItem(), "chItem", "find_more"));
            items.put("saved_heads", CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM, (short) 3).setDisplayName(CustomHeads.getLanguageManager().ITEMS_COLLECTION).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==").getItem(), "chItem", "saved_heads"));

            // Menus
            JsonArray menus = jsonLooks.getAsJsonArray("menus");
            if (menus == null)
                throw new IllegalArgumentException("No Menus found... At least one is required!");
            for (JsonElement menuElement : menus) {
                JsonObject menuJson = menuElement.getAsJsonObject();
                if (createdMenus.containsKey(menuJson.get("id").getAsString())) {
                    System.out.println(CustomHeads.chWarning + "Dublicate Menu ID: " + menuJson.get("id").getAsString() + ". Skipping...");
                    continue;
                }
                Inventory menu = Bukkit.createInventory(null, menuJson.get("size").getAsInt(), format(menuJson.get("title").getAsString()));
                for (JsonElement contentElement : menuJson.getAsJsonArray("content")) {
                    JsonObject contentObject = contentElement.getAsJsonObject();
                    int slot = contentObject.get("slot").getAsInt();
                    if (slot > menuJson.get("size").getAsInt())
                        throw new IllegalArgumentException("Slot ID cannot be higher than Inventory Size");
                    switch (contentObject.get("type").getAsString()) {
                        case "category":
                            Category category = CustomHeads.getCategoryLoader().getCategories().get(contentObject.get("id").getAsInt() + "");
                            if (category == null)
                                throw new NullPointerException("Cannot find Category for ID " + contentObject.get("id") + " for " + menuJson.get("id").getAsString());
                            usedCategories.add(category);
                            menu.setItem(slot, CustomHeads.getTagEditor().addTags(category.nextIcon(), "menuID", menuJson.get("id").getAsString()));
                            break;
                        case "item":
                            ItemStack item = items.get(contentObject.get("id").getAsString());
                            if (item == null)
                                throw new NullPointerException("Cannot find Item for ID " + contentObject.get("id") + " for " + menuJson.get("id").getAsString());
                            menu.setItem(slot, CustomHeads.getTagEditor().addTags(item, "menuID", menuJson.get("id").getAsString()));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown Content Type: " + contentObject.get("type").getAsString() + " for " + menuJson.get("id").getAsString());
                    }
                }

                menuInfo.put(menuJson.get("id").getAsString(), new String[]{menuJson.get("title").getAsString(), menuJson.has("permission") ? menuJson.get("permission").getAsString() : "heads.view.menu." + menuJson.get("id").getAsString()});
                createdMenus.put(menuJson.get("id").getAsString(), menu);
            }

            // Sub Category Slots
            objs = jsonLooks.get("sub_category-looks").getAsJsonObject();
            JsonObject slots = objs.get("slots").getAsJsonObject();

            for (Map.Entry<String, JsonElement> slotEntry : slots.entrySet()) {
                Inventory inv = Bukkit.createInventory(null, slots.get(slotEntry.getKey()).getAsJsonObject().get("size").getAsInt(), format(slots.get(slotEntry.getKey()).getAsJsonObject().get("title").getAsString()));
                Category category = CustomHeads.getCategoryLoader().getCategories().get(slotEntry.getKey());
                if (category != null && category.hasSubCategories()) {
                    for (JsonElement contents : slotEntry.getValue().getAsJsonObject().get("contents").getAsJsonArray()) {
                        JsonObject subcategoryObject = contents.getAsJsonObject();
                        if (subcategoryObject.get("type").getAsString().equals("category")) {
                            SubCategory subCategory = CustomHeads.getCategoryLoader().getSubCategory(category.getId() + ":" + subcategoryObject.get("id"));
                            if (subCategory == null)
                                throw new NullPointerException("Cannot find Subcategory for ID " + category.getId() + ":" + subcategoryObject.get("id"));
                            inv.setItem(subcategoryObject.get("slot").getAsInt(), subCategory.getCategoryIcon());
                            usedSubCategories.add(subCategory);
                        } else if (subcategoryObject.get("type").getAsString().equals("item")) {
                            ItemStack subCategoryItem = items.get(subcategoryObject.get("id").getAsString());
                            if (subCategoryItem == null)
                                throw new NullPointerException("Cannot find Item with ID " + subcategoryObject.get("id"));
                            inv.setItem(subcategoryObject.get("slot").getAsInt(), subCategoryItem);
                        }
                    }
                    subCategoryLooks.put(Integer.parseInt(category.getId()), inv);
                }
            }

            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Successfully loaded Looks from language/" + language + "/settings.json in " + (System.currentTimeMillis() - timestamp) + "ms");
            loaded = true;
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Something went wrong while loading Looks from Settings File " + language + "/settings.json", e);
        }
    }

    public Inventory getMenu(String menuId) {
        return createdMenus.get(menuId);
    }

    public String[] getMenuInfo(String menuId) {
        return menuInfo.get(menuId);
    }

    public String getIDbyTitle(String title) {
        for (String id : createdMenus.keySet()) {
            if (createdMenus.get(id).getTitle().equals(title)) return id;
        }
        return null;
    }

    public List<String> getMenuTitles() {
        List<String> titles = new ArrayList<>();
        createdMenus.values().forEach(inv -> titles.add(inv.getTitle()));
        return titles;
    }

}
