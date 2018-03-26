package de.mrstein.customheads.category;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHead;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.JsonToItem;
import de.mrstein.customheads.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static de.mrstein.customheads.utils.Utils.toConfigString;

public class Category extends BaseCategory {

    private List<SubCategory> subCategories;
    private List<CustomHead> heads;
    private List<ItemStack> icons;

    private Iterator<ItemStack> iterator;

    private ItemStack categoryIcon;

    public Category(int id, String name, String permission, ItemStack icon) {
        super("" + id, name, permission, CategoryType.CATEGORY);
        categoryIcon = icon;
        this.icons = Arrays.asList(icon);
        iterator = icons.iterator();
    }

    public static Category fromJson(String json) throws JsonSyntaxException {
        JsonParser parser = new JsonParser();
        Category category;
        JsonObject categoryJson = parser.parse(json).getAsJsonObject();
        category = new Category(
                categoryJson.get("id").getAsInt(),
                Utils.format(categoryJson.get("name").getAsString()),
                "heads.viewCategory." + categoryJson.get("permission").getAsString(),
                categoryJson.has("sub_categories") ? null :
                        CustomHeads.getTagEditor().setTags(JsonToItem.convert(categoryJson.get("icon").toString()), "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon"));
        List<CustomHead> heads = new ArrayList<>();
        List<SubCategory> subCategories = new ArrayList<>();

        if (categoryJson.has("sub_categories")) {
            List<ItemStack> subCategoryIcons = new ArrayList<>();

            for (JsonElement jsonElement : categoryJson.get("sub_categories").getAsJsonArray()) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                List<CustomHead> subHeads = new ArrayList<>();
                ItemStack icon = JsonToItem.convert(jsonObject.get("icon").toString());

                for (JsonElement headObject : jsonObject.get("heads").getAsJsonArray()) {
                    CustomHead head = new CustomHead(Utils.getURLFrom(headObject.getAsJsonObject().get("texture").getAsString())).setDisplayName(headObject.getAsJsonObject().get("name").getAsString());
                    if (headObject.getAsJsonObject().has("description"))
                        head.setLore(headObject.getAsJsonObject().get("description").getAsString());
                    subHeads.add(head);
                }
                subCategories.add(new SubCategory(categoryJson.get("id").getAsInt() + ":" + jsonObject.get("id").getAsInt(), Utils.format(jsonObject.get("name").getAsString()), CustomHeads.getTagEditor().setTags(icon, "openCategory", "subCategory#>" + categoryJson.get("id") + ":" + jsonObject.get("id"), "icon-fixed"), category, subHeads));
                subCategoryIcons.add(CustomHeads.getTagEditor().setTags(icon, "openCategory", "category#>" + categoryJson.get("id").getAsString(), "icon-loop"));
            }
            if(!subCategories.isEmpty()) {
                List<ItemStack> editedSubCategoryIcons = new ArrayList<>();
                for(int i = 0; i < subCategories.size(); i++) {
                    List<String> lore = new ArrayList<>();
                    for(ItemStack icon : subCategoryIcons) {
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
                CustomHead head = new CustomHead(Utils.getURLFrom(headObject.getAsJsonObject().get("texture").getAsString())).setDisplayName(headObject.getAsJsonObject().get("name").getAsString());
                if (headObject.getAsJsonObject().has("description"))
                    head.setLore(headObject.getAsJsonObject().get("description").getAsString());
                heads.add(head);
            }
        }

        category.setHeads(heads);
        category.setSubCategories(subCategories);
        return category;
    }

    //Public Methods
    public boolean hasCategoryIcon() {
        return categoryIcon != null;
    }

    public ItemStack getCategoryIcon() {
        return categoryIcon;
    }

    private Category setCategoryIcon(ItemStack icon) {
        categoryIcon = icon;
        return this;
    }

    public List<CustomHead> getHeads() {
        List<CustomHead> heads = new ArrayList<>();
        if(hasHeads()) {
            heads.addAll(this.heads);
        } else if(hasSubCategories()) {
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

    public List<SubCategory> getSubCategories() {
        return subCategories;
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

    public List<ItemStack> getIcons() {
        return icons;
    }

    private Category setIcons(List<ItemStack> icons) {
        this.icons = icons;
        iterator = icons.iterator();
        return this;
    }

    public String getAsJson(boolean pretty) {
        JsonObject category = new JsonObject();

        //Sub Category Info Header
        category.addProperty("id", Integer.parseInt(getId().contains(":") ? getId().split(":")[0] : getId()));
        category.addProperty("name", getName());
        category.addProperty("permission", getPermission());
        if (hasCategoryIcon())
            category.add("icon", new JsonParser().parse(toConfigString(JsonToItem.convertToJson(getCategoryIcon()))));

        // Gets Heads and puts them into an JsonArray
        if (hasHeads()) {
            JsonArray heads = new JsonArray();
            for (CustomHead head : getHeads()) {
                JsonObject headObject = new JsonObject();
                headObject.addProperty("texture", head.getTextureValue());
                headObject.addProperty("name", toConfigString(head.getDisplayName()));
                if (head.hasLore()) {
                    headObject.addProperty("description", toConfigString(head.getLoreAsString()));
                }
                heads.add(headObject);
            }
            category.add("heads", heads);
        }
        if (hasSubCategories()) {
            JsonArray subCategories = new JsonArray();
            for (SubCategory sub : getSubCategories()) {
                subCategories.add(new JsonParser().parse(sub.getAsJson()));
            }
            category.add("sub_categories", subCategories);
        }
        GsonBuilder gson = new GsonBuilder().disableHtmlEscaping();
        if(pretty)
            gson.setPrettyPrinting();
        return gson.create().toJson(category);
    }

    public String toString() {
        return getAsJson(false);
    }

    public ItemStack nextIcon() {
        if (icons.size() == 1) return icons.get(0);
        if (!iterator.hasNext()) iterator = icons.iterator();
        return iterator.next();
    }

}
