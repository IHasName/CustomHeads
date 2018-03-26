package de.mrstein.customheads.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class ItemEditor {

    ItemStack itemStack;
    ItemMeta meta;

    public ItemEditor(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        if(itemStack.hasItemMeta())
            meta = itemStack.getItemMeta();
    }

    public ItemEditor(Material material) {
        itemStack = new ItemStack(material);
        meta = itemStack.getItemMeta();
    }

    public ItemEditor setDamage(short damage) {
        itemStack.setDurability(damage);
        return this;
    }

    public ItemEditor setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemEditor setDisplayName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemEditor setLore(List<String> itemLore) {
        meta.setLore(itemLore);
        return this;
    }

    public ItemEditor addLoreLine(String lore) {
        List<String> itemLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        itemLore.add(lore);
        meta.setLore(itemLore);
        return this;
    }

    public ItemEditor addLoreLines(List<String> lore) {
        List<String> itemLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        if(lore != null) {
            itemLore.addAll(lore);
        }
        meta.setLore(itemLore);
        return this;
    }

    public ItemEditor removeLoreLine(int line) {
        if(!meta.hasLore()) return this;
        List<String> itemLore = meta.getLore();
        itemLore.remove(line);
        meta.setLore(itemLore);
        return this;
    }

    public ItemEditor insertLoreLine(String lore, int line) {
        List<String> itemLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        itemLore.add(line, lore);
        meta.setLore(itemLore);
        return this;
    }

    public ItemEditor addEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemEditor removeEnchantment(Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        return this;
    }

    public ItemEditor hideAllFlags() {
        meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemStack getItem() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
