package de.mrstein.customheads.category;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.JsonToItem;
import de.mrstein.customheads.utils.Utils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static de.mrstein.customheads.utils.Utils.toConfigString;

/*
 *  Project: CustomHeads in Category
 *     by LikeWhat
 */

@Getter
public class Category extends BaseCategory {

    private static final Gson CATEGORY_TO_JSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapter(Category.class, new Serializer())
            .registerTypeAdapter(SubCategory.class, new SubCategory.Serializer())
            .create();
    private List<SubCategory> subCategories;
    private List<ItemStack> heads;
    private List<ItemStack> icons;
    private int price;
    private Iterator<ItemStack> iterator;
    private ItemStack categoryIcon;

    public Category(int id, String name, String permission, int price, ItemStack icon) {
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

    public List<ItemStack> getHeads() {
        List<ItemStack> heads = new ArrayList<>();
        if (hasHeads()) {
            heads.addAll(this.heads);
        } else if (hasSubCategories()) {
            subCategories.forEach(subCategory -> heads.addAll(subCategory.getHeads()));
        }
        return heads;
    }

    public Category setHeads(List<ItemStack> heads) {
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

    private Category setIcons(List<ItemStack> icons) {
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
                            CustomHeads.getTagEditor().setTags(JsonToItem.convert(categoryJson.get("icon").toString()), "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon"));
            List<ItemStack> heads = new ArrayList<>();
            List<SubCategory> subCategories = new ArrayList<>();

            if (categoryJson.has("sub_categories")) {
                List<ItemStack> subCategoryIcons = new ArrayList<>();
                for (JsonElement jsonElement : categoryJson.get("sub_categories").getAsJsonArray()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    List<ItemStack> subHeads = new ArrayList<>();
                    ItemStack icon = JsonToItem.convert(jsonObject.get("icon").toString());

                    for (JsonElement headObject : jsonObject.get("heads").getAsJsonArray()) {
                        ItemEditor head = new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture(headObject.getAsJsonObject().get("texture").getAsString()).setDisplayName(headObject.getAsJsonObject().get("name").getAsString());
                        if (headObject.getAsJsonObject().has("description"))
                            head.setLore(headObject.getAsJsonObject().get("description").getAsString());
                        subHeads.add(head.getItem());
                    }
                    subCategories.add(new SubCategory(categoryJson.get("id").getAsInt() + ":" + jsonObject.get("id").getAsInt(), Utils.format(jsonObject.get("name").getAsString()), CustomHeads.getTagEditor().setTags(icon, "openCategory", "subCategory#>" + categoryJson.get("id") + ":" + jsonObject.get("id"), "icon-fixed"), category, subHeads));
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
                category.setCategoryIcon(subCategoryIcons.isEmpty() || (categoryJson.has("fixed-icon") && categoryJson.get("fixed-icon").getAsBoolean()) ? CustomHeads.getTagEditor().setTags(JsonToItem.convert(categoryJson.get("icon").toString()), "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon-fixed") : subCategoryIcons.get(0));
                category.setIcons(subCategoryIcons.isEmpty() || (categoryJson.has("fixed-icon") && categoryJson.get("fixed-icon").getAsBoolean()) ? Arrays.asList(CustomHeads.getTagEditor().setTags(JsonToItem.convert(categoryJson.get("icon").toString()), "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon-fixed")) : subCategoryIcons);
            } else {
                for (JsonElement headObject : categoryJson.get("heads").getAsJsonArray()) {
                    ItemEditor head = new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture(headObject.getAsJsonObject().get("texture").getAsString()).setDisplayName(headObject.getAsJsonObject().get("name").getAsString());
                    if (headObject.getAsJsonObject().has("description"))
                        head.setLore(headObject.getAsJsonObject().get("description").getAsString());
                    heads.add(head.getItem());
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
            if (category.hasCategoryIcon())
                categoryObject.add("icon", new JsonParser().parse(toConfigString(JsonToItem.convertToJson(category.getCategoryIcon()))));

            // Gets Heads and puts them into an JsonArray
            if (category.hasHeads()) {
                JsonArray heads = new JsonArray();
                for (ItemEditor head : category.getHeads().stream().map(ItemEditor::new).collect(Collectors.toList())) {
                    JsonObject headObject = new JsonObject();
                    headObject.addProperty("texture", head.getTexture());
                    headObject.addProperty("name", toConfigString(head.getDisplayName()));
                    if (head.hasLore()) {
                        headObject.addProperty("description", toConfigString(head.getLoreAsString()));
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
