package de.mrstein.customheads.category;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.JsonToItem;
import de.mrstein.customheads.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/*
 *  Project: CustomHeads in Category
 *     by LikeWhat
 */

@Getter
public class Category extends BaseCategory {

    private static final Gson CATEGORY_TO_JSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().registerTypeAdapter(Category.class, new Serializer()).registerTypeAdapter(SubCategory.class, new SubCategory.Serializer()).create();
    public static int counter = 0;
    private int lastID = 1;

    private List<SubCategory> subCategories;
    private Integer[] ids = new Integer[0];
    private List<ItemStack> icons;

    private Iterator<ItemStack> iterator;

    private ItemStack categoryIcon;
    private List<CustomHead> heads = new ArrayList<>();
    private boolean fixedIcon;
    @Setter private int price;

    protected Category(int id, String name, String permission, int price, ItemStack icon) {
        super(String.valueOf(id), name, permission);
        this.price = price;
        categoryIcon = icon;
        this.icons = Arrays.asList(icon);
        iterator = icons.iterator();
    }

    public static Gson getConverter() {
        return CATEGORY_TO_JSON;
    }

    //Public Methods
    public boolean hasCategoryIcon() {
        return categoryIcon != null;
    }

    private Category setCategoryIcon(ItemStack icon) {
        categoryIcon = icon;
        return this;
    }

    public List<CustomHead> getHeads() {
        List<CustomHead> heads = new ArrayList<>();
        if (hasHeads()) {
            heads.addAll(this.heads);
        } else if (hasSubCategories()) {
            subCategories.forEach(subCategory -> heads.addAll(subCategory.getHeads()));
        }
        return heads;
    }

    public Category setHeads(List<CustomHead> heads) {
        this.heads = heads;
        return this;
    }

    public boolean hasHeads() {
        return !heads.isEmpty();
    }

    public Category setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
        return this;
    }

    public boolean isUsed() {
        return CustomHeads.getLooks().getUsedCategories().contains(this);
    }

    public boolean hasSubCategories() {
        return subCategories != null && !subCategories.isEmpty();
    }

    public Category setIcons(List<ItemStack> icons) {
        this.icons = icons;
        iterator = icons.iterator();
        return this;
    }

    public String toString() {
        return CATEGORY_TO_JSON.toJson(this);
    }

    public ItemStack nextIcon() {
        if (icons.size() == 1) return icons.get(0);
        if (!iterator.hasNext()) iterator = icons.iterator();
        return iterator.next();
    }

    public boolean isFree() {
        return price == 0;
    }

    private boolean setFixedIcon(boolean fixedIcon) {
        return this.fixedIcon = fixedIcon;
    }

    private void addID(int id) {
        ids = Utils.appendArray(ids, id);
    }

    private int checkID(int id) {
        if (heads.stream().map(CustomHead::getId).collect(Collectors.toList()).contains(id)) {
            int newID = nextID();
            Bukkit.getLogger().warning("Duplicate ID " + id + "... Replacing with " + newID);
            return newID;
        }
        return id;
    }

    private int nextID() {
        List<Integer> idsAsList = Arrays.asList(ids);
        while (idsAsList.contains(lastID)) {
            lastID++;
        }
        return lastID;
    }

    public static class Serializer implements JsonSerializer<Category>, JsonDeserializer<Category> {

        public Category deserialize(JsonElement jsonRoot, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Category category;
            JsonObject categoryJson = jsonRoot.getAsJsonObject();
            category = new Category(
                    categoryJson.get("id").getAsInt(),
                    Utils.format(categoryJson.get("name").getAsString()),
                    "heads.viewCategory." + categoryJson.get("permission").getAsString(),
                    CustomHeads.hasEconomy() && categoryJson.has("price") ? categoryJson.get("price").getAsInt() : 0,
                    categoryJson.has("sub_categories") ? null :
                            CustomHeads.getTagEditor().setTags(JsonToItem.convertFromJson(categoryJson.get("icon").toString()), "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon"));
            List<CustomHead> heads = new ArrayList<>();
            List<SubCategory> subCategories = new ArrayList<>();

            if (categoryJson.has("sub_categories")) {
                List<ItemStack> subCategoryIcons = new ArrayList<>();
                for (JsonElement jsonElement : categoryJson.get("sub_categories").getAsJsonArray()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    List<CustomHead> subHeads = new ArrayList<>();
                    ItemStack icon = JsonToItem.convertFromJson(jsonObject.get("icon").toString());

                    String subCategoryID = categoryJson.get("id").getAsInt() + ":" + jsonObject.get("id").getAsInt();
                    for (JsonElement rawHeadObject : jsonObject.get("heads").getAsJsonArray()) {
                        JsonObject headObject = rawHeadObject.getAsJsonObject();
                        ItemEditor head = new ItemEditor(Material.SKULL_ITEM,  3).setTexture(headObject.get("texture").getAsString()).setDisplayName(headObject.get("name").getAsString());
                        if (headObject.has("description"))
                            head.setLore(headObject.get("description").getAsString());
                        int price = headObject.has("price") ? headObject.get("price").getAsInt() : 0;
                        int id = headObject.has("id") ? category.checkID(headObject.get("id").getAsInt()) : category.nextID();
                        CustomHead customHead = new CustomHead(head.getItem(), category, id, price);
                        category.addID(id);
                        subHeads.add(customHead);
                    }
                    subCategories.add(new SubCategory(subCategoryID, Utils.format(jsonObject.get("name").getAsString()), CustomHeads.getTagEditor().setTags(icon, "openCategory", "subCategory#>" + categoryJson.get("id") + ":" + jsonObject.get("id"), "icon-fixed"), category, subHeads));
                    subCategoryIcons.add(CustomHeads.getTagEditor().setTags(icon, "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon-loop"));
                }
                if (!subCategories.isEmpty()) {
                    List<ItemStack> editedSubCategoryIcons = new ArrayList<>();
                    for (int i = 0; i < subCategories.size(); i++) {
                        List<String> lore = new ArrayList<>();
                        for (ItemStack icon : subCategoryIcons) {
                            lore.add(icon.getItemMeta().getDisplayName().equals(subCategoryIcons.get(i).getItemMeta().getDisplayName()) ? icon.getItemMeta().getDisplayName() : "§7" + ChatColor.stripColor(icon.getItemMeta().getDisplayName()));
                        }
                        editedSubCategoryIcons.add(new ItemEditor(subCategoryIcons.get(i)).setDisplayName(subCategories.get(i).getOriginCategory().getName()).setLore(lore).getItem());
                    }
                    subCategoryIcons = editedSubCategoryIcons;
                }
                category.setCategoryIcon(subCategoryIcons.isEmpty() || (categoryJson.has("fixed-icon") && categoryJson.get("fixed-icon").getAsBoolean()) ? CustomHeads.getTagEditor().setTags(JsonToItem.convertFromJson(categoryJson.get("icon").toString()), "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon-fixed") : subCategoryIcons.get(0));
                category.setIcons(subCategoryIcons.isEmpty() || (categoryJson.has("fixed-icon") && category.setFixedIcon(categoryJson.get("fixed-icon").getAsBoolean())) ? Arrays.asList(CustomHeads.getTagEditor().setTags(JsonToItem.convertFromJson(categoryJson.get("icon").toString()), "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon-fixed")) : subCategoryIcons);
            } else {
                for (JsonElement rawHeadObject : categoryJson.get("heads").getAsJsonArray()) {
                    JsonObject headObject = rawHeadObject.getAsJsonObject();
                    ItemEditor head = new ItemEditor(Material.SKULL_ITEM,  3).setTexture(headObject.get("texture").getAsString()).setDisplayName(headObject.get("name").getAsString());
                    if (headObject.has("description"))
                        head.setLore(headObject.get("description").getAsString());
                    int price = headObject.has("price") ? headObject.get("price").getAsInt() : 0;
                    int id = headObject.has("id") ? category.checkID(headObject.get("id").getAsInt()) : category.nextID();
                    CustomHead customHead = new CustomHead(head.getItem(), category, id, price);
                    category.addID(id);
                    heads.add(customHead);
                }
            }
            category.setHeads(heads);
            category.setSubCategories(subCategories);
            return category;
        }

        public JsonElement serialize(Category category, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject categoryObject = new JsonObject();

            //Sub Category Info Header
            categoryObject.addProperty("id", Integer.parseInt(category.getId().contains(":") ? category.getId().split(":")[0] : category.getId()));
            categoryObject.addProperty("name", category.getName());
            categoryObject.addProperty("permission", category.getPermission().replace("heads.viewCategory.", ""));
            categoryObject.addProperty("fixed-icon", category.isFixedIcon());
            if (category.hasCategoryIcon())
                categoryObject.add("icon", new JsonParser().parse(JsonToItem.convertToJson(category.getCategoryIcon())));

            // Gets Heads and puts them into an JsonArray
            if (category.hasHeads()) {
                JsonArray heads = new JsonArray();
                for (CustomHead head : category.getHeads()) {
                    ItemEditor itemEditor = new ItemEditor(head);
                    JsonObject headObject = new JsonObject();
                    headObject.addProperty("texture", itemEditor.getTexture());
                    headObject.addProperty("name", itemEditor.getDisplayName());
                    headObject.addProperty("price", head.getPrice());
                    headObject.addProperty("id", head.getId());
                    if (itemEditor.hasLore()) {
                        headObject.addProperty("description", itemEditor.getLoreAsString());
                    }
                    heads.add(headObject);
                }
                categoryObject.add("heads", heads);
            }
            if (category.hasSubCategories()) {
                JsonArray subCategories = new JsonArray();
                for (SubCategory sub : category.getSubCategories()) {
                    subCategories.add(CATEGORY_TO_JSON.toJsonTree(sub, SubCategory.class));
                }
                categoryObject.add("sub_categories", subCategories);
            }
            return categoryObject;
        }
    }

}
