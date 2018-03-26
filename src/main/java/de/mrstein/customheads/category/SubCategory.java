package de.mrstein.customheads.category;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHead;
import de.mrstein.customheads.utils.JsonToItem;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static de.mrstein.customheads.utils.Utils.toConfigString;

public class SubCategory extends BaseCategory {

    private ItemStack icon;
    private Category originCategory;
    private List<CustomHead> heads;

    SubCategory(String id, String name, ItemStack icon, Category originCategory, List<CustomHead> heads) {
        super(id, name, "", CategoryType.SUBCATEGORY);
        this.icon = icon;
        this.originCategory = originCategory;
        this.heads = heads;
    }

    // Public Getter
    public List<CustomHead> getHeads() { return heads; }

    public Category getOriginCategory() {
        return originCategory;
    }

    public ItemStack getCategoryIcon() {
        return icon;
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
        for (CustomHead head : getHeads()) {
            JsonObject headObject = new JsonObject();
            headObject.addProperty("texture", head.getTextureValue());
            headObject.addProperty("name", toConfigString(head.getDisplayName()));
            if (head.hasLore())
                headObject.addProperty("description", toConfigString(head.getLoreAsString()));
            heads.add(headObject);
        }
        subCategory.add("heads", heads);

        Gson prettyJson = new GsonBuilder().setPrettyPrinting().create();
        return prettyJson.toJson(subCategory);
    }

}
