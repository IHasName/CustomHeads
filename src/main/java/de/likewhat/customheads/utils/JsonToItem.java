package de.likewhat.customheads.utils;

import com.google.gson.*;
import de.likewhat.customheads.CustomHeads;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static de.likewhat.customheads.utils.Utils.format;

/*
 *  Project: CustomHeads in JsonToItem
 *     by LikeWhat
 *
 * <p>Supported Meta:
 * <p>Count, Damage, Display Name, Enchantments, SkullOwner/Custom Texture
 */
public class JsonToItem {

    public static ItemStack convertFromJson(String json) {
        JsonParser parser = new JsonParser();
        ItemEditor itemEditor = null;
        try {
            JsonObject itemObj = parser.parse(json).getAsJsonObject();
            Material material = Material.getMaterial(itemObj.get("item").getAsString());
            if(material == null) {
                throw new IllegalArgumentException("Invalid Material Name: " + itemObj.get("item").getAsString());
            }
            itemEditor = new ItemEditor(material, itemObj.has("damage") ? itemObj.get("damage").getAsShort() : 0).setAmount(itemObj.has("count") ? itemObj.get("count").getAsInt() : 1);
            if (itemObj.has("display-name"))
                itemEditor.setDisplayName(format(itemObj.get("display-name").getAsString()));
            if (itemObj.has("lore")) {
                List<String> lore = new ArrayList<>();
                for (JsonElement loreLine : itemObj.get("lore").getAsJsonArray())
                    lore.add(format(loreLine.getAsString()));
                itemEditor.setLore(lore);
            }
            if (itemObj.has("ench")) {
                for (JsonElement enchObj : itemObj.get("ench").getAsJsonArray()) {
                    itemEditor.addEnchantment(new EnchantmentWrapper(enchObj.getAsJsonObject().get("id").getAsInt()), enchObj.getAsJsonObject().get("lvl").getAsInt());
                }
            }
            itemEditor.hideAllFlags();
            if (itemObj.get("item").getAsString().equalsIgnoreCase("skull_item")) {
                if (itemObj.has("skullOwner"))
                    itemEditor.setOwner(itemObj.get("skullOwner").getAsString());
                if (itemObj.has("texture"))
                    itemEditor.setTexture(itemObj.get("texture").getAsString());
            }
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to create Item", e);
        }
        return itemEditor == null ? null : itemEditor.getItem();
    }

    public static String convertToJson(ItemStack itemStack) {
        JsonObject itemObject = new JsonObject();
        itemObject.addProperty("item", itemStack.getType().toString());
        if (itemStack.getAmount() > 1)
            itemObject.addProperty("count", itemStack.getAmount());
        itemObject.addProperty("damage", itemStack.getDurability());
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName())
                itemObject.addProperty("display-name", meta.getDisplayName());
            if (meta.hasLore()) {
                JsonArray lore = new JsonArray();
                for (String l : meta.getLore()) {
                    lore.add(new JsonPrimitive(l));
                }
                itemObject.add("lore", lore);
            }
            if (meta.hasEnchants()) {
                JsonArray enchList = new JsonArray();
                for (Enchantment ench : meta.getEnchants().keySet()) {
                    JsonObject enchantment = new JsonObject();
                    enchantment.addProperty("id", ench.toString());
                    enchantment.addProperty("lvl", meta.getEnchantLevel(ench));
                    enchList.add(enchantment);
                }
                itemObject.add("ench", enchList);
            }
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.hasOwner()) {
                    itemObject.addProperty("skullOwner", skullMeta.getOwner());
                }
            }
            if (Utils.hasCustomTexture(itemStack)) {
                itemObject.addProperty("texture", CustomHeads.getApi().getSkullTexture(itemStack));
            }
        }
        return Utils.GSON_PRETTY.toJson(itemObject);
    }

}
