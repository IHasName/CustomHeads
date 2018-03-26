package de.mrstein.customheads.utils;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHeadsAPI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        JSONParser parser = new JSONParser();
        ItemStack r = null;
        try {
            JSONObject itemObj = (JSONObject) parser.parse(json);
            r = new ItemStack(Material.getMaterial((String) itemObj.get("item")), itemObj.containsKey("count") ? (int) (long)itemObj.get("count") : 1, itemObj.containsKey("damage") ? (short) (long)itemObj.get("damage") : 0);
            ItemMeta meta = r.getItemMeta();
            if(itemObj.containsKey("display-name"))
                meta.setDisplayName(format((String) itemObj.get("display-name")));
            if(itemObj.containsKey("lore")) {
                List<String> lore = new ArrayList<>();
                for(Object h : (JSONArray) itemObj.get("lore")) lore.add(format(h.toString()));
                meta.setLore(lore);
            }
            if(itemObj.containsKey("ench"))
                for(Object enchObj : ((JSONArray)itemObj.get("ench")))
                    meta.addEnchant(new EnchantmentWrapper((int) (long) ((JSONObject)enchObj).get("id")), (int) (long) ((JSONObject)enchObj).get("lvl"), true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            r.setItemMeta(meta);
            if(((String) itemObj.get("item")).equalsIgnoreCase("skull_item")) {
                SkullMeta sM = (SkullMeta) r.getItemMeta();
                if (itemObj.containsKey("skullOwner"))
                    sM.setOwner(itemObj.get("skullOwner").toString());
                if(itemObj.containsKey("texture"))
                    if(!Utils.RefSet(sM.getClass(), sM, "profile", GameProfileBuilder.createProfileWithTexture(itemObj.get("texture").toString())))
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
        if(itemStack.getAmount() > 1)
            itemObject.addProperty("count", itemStack.getAmount());
        itemObject.addProperty("damage", itemStack.getDurability());
        if(itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if(meta.hasDisplayName())
                itemObject.addProperty("display-name", toConfigString(meta.getDisplayName()));
            if(meta.hasLore()) {
                JsonArray lore = new JsonArray();
                for(String l : meta.getLore()) {
                    lore.add(new JsonPrimitive(toConfigString(l)));
                }
            }
            if(meta.hasEnchants()) {
                JsonArray enchList = new JsonArray();
                for(Enchantment ench : meta.getEnchants().keySet()) {
                    JsonObject enchantment = new JsonObject();
                    enchantment.addProperty("id", ench.toString());
                    enchantment.addProperty("lvl", meta.getEnchantLevel(ench));
                    enchList.add(enchantment);
                }
                itemObject.add("ench", enchList);
            }
            if(meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if(skullMeta.hasOwner()) {
                    itemObject.addProperty("skullOwner", skullMeta.getOwner());
                }
            }
            if(Utils.hasCustomTexture(itemStack)) {
                itemObject.addProperty("texture", CustomHeadsAPI.getSkullTexture(itemStack));
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(itemObject);
    }

}
