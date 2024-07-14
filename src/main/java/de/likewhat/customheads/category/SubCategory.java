package de.likewhat.customheads.category;

import com.google.gson.*;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.ItemEditor;
import de.likewhat.customheads.utils.JsonToItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

/*
 *  Project: CustomHeads in SubCategory
 *     by LikeWhat
 */

@Getter
public class SubCategory extends BaseCategory {

    private final Category originCategory;
    private final ItemStack categoryIcon;
    private final List<CustomHead> heads;

    SubCategory(String id, String name, ItemStack categoryIcon, Category originCategory, List<CustomHead> heads) {
        super(id, name, "");
        this.categoryIcon = categoryIcon;
        this.originCategory = originCategory;
        this.heads = heads;
    }

    public boolean isUsed() {
        return CustomHeads.getLooks().getUsedSubCategories().contains(this);
    }

    public static class Serializer implements JsonSerializer<SubCategory> {

        public JsonElement serialize(SubCategory subCategory, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject subCategoryObject = new JsonObject();

            //Sub Category Info Header
            subCategoryObject.addProperty("id", Integer.parseInt(subCategory.getId().contains(":") ? subCategory.getId().split(":")[1] : subCategory.getId()));
            subCategoryObject.addProperty("name", subCategory.getName());
            subCategoryObject.add("icon", JsonToItem.convertToJson(subCategory.getCategoryIcon()));

            // Gets Heads and puts them into an JsonArray
            JsonArray heads = new JsonArray();
            for (CustomHead head : subCategory.getHeads()) {
                ItemEditor itemEditor = new ItemEditor(head);
                JsonObject headObject = new JsonObject();
                headObject.addProperty("texture", itemEditor.getTexture());
                headObject.addProperty("name", itemEditor.getDisplayName());
                headObject.addProperty("id", head.getId());
                headObject.addProperty("price", head.getPrice());
                if (itemEditor.hasLore())
                    headObject.addProperty("description", itemEditor.getLoreAsString());
                heads.add(headObject);
            }
            subCategoryObject.add("heads", heads);
            return subCategoryObject;
        }
    }

}
