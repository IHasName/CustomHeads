package de.mrstein.customheads.category;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.JsonToItem;
import de.mrstein.customheads.utils.Utils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static de.mrstein.customheads.utils.Utils.toConfigString;

/*
 *  Project: CustomHeads in SubCategory
 *     by LikeWhat
 */

@Getter
public class SubCategory extends BaseCategory {

    private ItemStack categoryIcon;
    private Category originCategory;
    private List<ItemStack> heads;

    SubCategory(String id, String name, ItemStack categoryIcon, Category originCategory, List<ItemStack> heads) {
        super(id, name, "");
        this.categoryIcon = categoryIcon;
        this.originCategory = originCategory;
        this.heads = heads;
    }

    public boolean isUsed() {
        return CustomHeads.getLooks().getUsedSubCategories().contains(this);
    }

    public String getAsJson() {

        JsonObject subCategory = new JsonObject();

        //Sub Category Info Header
        subCategory.addProperty("id", Integer.parseInt(getId().contains(":") ? getId().split(":")[1] : getId()));
        subCategory.addProperty("name", getName());
        subCategory.add("icon", new JsonParser().parse(toConfigString(JsonToItem.convertToJson(getCategoryIcon()))));

        // Gets Heads and puts them into an JsonArray
        JsonArray heads = new JsonArray();
        for (ItemEditor head : getHeads().stream().map(ItemEditor::new).collect(Collectors.toList())) {
            JsonObject headObject = new JsonObject();
            headObject.addProperty("texture", head.getTexture());
            headObject.addProperty("name", toConfigString(head.getDisplayName()));
            if (head.hasLore())
                headObject.addProperty("description", toConfigString(head.getLoreAsString()));
            heads.add(headObject);
        }
        subCategory.add("heads", heads);

        return Utils.GSON_PRETTY.toJson(subCategory);
    }

    public static class Serializer implements JsonSerializer<SubCategory> {

        public JsonElement serialize(SubCategory subCategory, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject subCategoryObject = new JsonObject();

            //Sub Category Info Header
            subCategoryObject.addProperty("id", Integer.parseInt(subCategory.getId().contains(":") ? subCategory.getId().split(":")[1] : subCategory.getId()));
            subCategoryObject.addProperty("name", subCategory.getName());
            subCategoryObject.add("icon", new JsonParser().parse(toConfigString(JsonToItem.convertToJson(subCategory.getCategoryIcon()))));

            // Gets Heads and puts them into an JsonArray
            JsonArray heads = new JsonArray();
            for (ItemEditor head : subCategory.getHeads().stream().map(ItemEditor::new).collect(Collectors.toList())) {
                JsonObject headObject = new JsonObject();
                headObject.addProperty("texture", head.getTexture());
                headObject.addProperty("name", toConfigString(head.getDisplayName()));
                if (head.hasLore())
                    headObject.addProperty("description", toConfigString(head.getLoreAsString()));
                heads.add(headObject);
            }
            subCategoryObject.add("heads", heads);
            return subCategoryObject;
        }
    }

}
