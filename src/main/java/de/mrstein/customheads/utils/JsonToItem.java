package de.mrstein.customheads.utils;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static de.mrstein.customheads.utils.Utils.format;
import static de.mrstein.customheads.utils.Utils.toConfigString;

/**
 * <p>Supported Meta:
 * <p>Count, Damage, Display Name, Enchantments, SkullOwner/Custom Texture
 */
public class JsonToItem {

    public static ItemStack convert(String json) {
        JsonParser parser = new JsonParser();
        ItemStack r = null;
        try {
            JsonObject itemObj = parser.parse(json).getAsJsonObject();
            r = new ItemStack(Material.getMaterial(itemObj.get("item").getAsString()), itemObj.has("count") ? itemObj.get("count").getAsInt() : 1, itemObj.has("damage") ? itemObj.get("damage").getAsShort() : 0);
            ItemMeta meta = r.getItemMeta();
            if (itemObj.has("display-name"))
                meta.setDisplayName(format(itemObj.get("display-name").getAsString()));
            if (itemObj.has("lore")) {
                List<String> lore = new ArrayList<>();
                for (JsonElement loreLine : itemObj.get("lore").getAsJsonArray())
                    lore.add(format(loreLine.getAsString()));
                meta.setLore(lore);
            }
            if (itemObj.has("ench"))
                for (JsonElement enchObj : itemObj.get("ench").getAsJsonArray())
                    meta.addEnchant(new EnchantmentWrapper(enchObj.getAsJsonObject().get("id").getAsInt()), enchObj.getAsJsonObject().get("lvl").getAsInt(), true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            r.setItemMeta(meta);
            if (itemObj.get("item").getAsString().equals("skull_item")) {
                SkullMeta sM = (SkullMeta) r.getItemMeta();
                if (itemObj.has("skullOwner"))
                    sM.setOwner(itemObj.get("skullOwner").toString());
                if (itemObj.has("texture"))
                    if (!Utils.inject(sM.getClass(), sM, "profile", GameProfileBuilder.createProfileWithTexture(itemObj.get("texture").toString())))
                        return null;
                r.setItemMeta(sM);
            }
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to create Item", e);
        }
        return r;
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
                itemObject.addProperty("display-name", toConfigString(meta.getDisplayName()));
            if (meta.hasLore()) {
                JsonArray lore = new JsonArray();
                for (String l : meta.getLore()) {
                    lore.add(new JsonPrimitive(toConfigString(l)));
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
                itemObject.addProperty("texture", CustomHeads.getHeadUtil().getSkullTexture(itemStack));
            }
        }
        return Utils.GSON_PRETTY.toJson(itemObject);
    }

}
