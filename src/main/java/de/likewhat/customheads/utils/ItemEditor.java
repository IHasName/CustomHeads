package de.likewhat.customheads.utils;

import com.mojang.authlib.GameProfile;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.collections.MethodReflectionCollection;
import de.likewhat.customheads.utils.reflection.nbt.NBTTagUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
 *  Project: CustomHeads in ItemEditor
 *     by LikeWhat
 *
 *  Just like every ItemBuilder out there
 */

public class ItemEditor {

    private final ItemStack itemStack;

    private final ItemMeta meta;

    public ItemEditor(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        meta = itemStack.getItemMeta();
    }

    public ItemEditor(Material material) {
        this(material, 0);
    }

    public ItemEditor(Material material, int damage) {
        this(new ItemStack(material, 1,  (short) damage));
    }

    public short getDamage() {
        return itemStack.getDurability();
    }

    public ItemEditor setDamage(short damage) {
        itemStack.setDurability(damage);
        return this;
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    public ItemEditor setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public boolean hasDisplayName() {
        return meta.hasDisplayName();
    }

    public String getDisplayName() {
        return meta.getDisplayName();
    }

    public ItemEditor setDisplayName(String displayName) {
        meta.setDisplayName(Utils.format(displayName));
        return this;
    }

    public ItemEditor setLore(String[] lore) {
        return setLore(lore != null && lore.length > 0 ? Arrays.asList(lore) : null);
    }

    public ItemEditor setLore(String lore) {
        return setLore(lore.isEmpty() ? null : Arrays.asList(lore.split("\n")));
    }

    public ItemEditor addLoreLine(String lore) {
        if (lore == null)
            return this;
        List<String> itemLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        itemLore.addAll(Arrays.asList(lore.split("\n")));
        meta.setLore(itemLore);
        return this;
    }

    public ItemEditor addLoreLines(List<String> lore) {
        if (lore == null)
            return this;
        List<String> itemLore = hasLore() ? getLore() : new ArrayList<>();
        itemLore.addAll(lore);
        setLore(itemLore);
        return this;
    }

    public ItemEditor removeLoreLine(int line) {
        if (!hasLore()) return this;
        List<String> itemLore = meta.getLore();
        itemLore.remove(line);
        setLore(itemLore);
        return this;
    }

    public boolean hasLore() {
        return meta.hasLore();
    }

    public ItemEditor insertLoreLine(String lore, int line) {
        List<String> itemLore = hasLore() ? getLore() : new ArrayList<>();
        itemLore.add(line, lore);
        setLore(itemLore);
        return this;
    }

    public List<String> getLore() {
        return meta.getLore();
    }

    public ItemEditor setLore(List<String> itemLore) {
        meta.setLore(itemLore);
        return this;
    }

    public String getLoreAsString() {
        if (getLore().isEmpty()) return "";
        StringBuilder b = new StringBuilder();
        for (String l : getLore()) {
            b.append(l).append("\n");
        }
        b.setLength(b.length() - 1);
        return b.toString();
    }

    public String getTexture() {
        return CustomHeads.getApi().getSkullTexture(itemStack);
    }

    public ItemEditor setTexture(String texture) {
        if (itemStack.getType() != Material.SKULL_ITEM)
            throw new IllegalArgumentException("ItemStack is not an Player Head");
        GameProfile profile = GameProfileBuilder.createProfileWithTexture(texture);
        try {
            if (!ReflectionUtils.setField(meta, "profile", profile)) {
                throw new IllegalStateException("Unable to inject GameProfile");
            }
            if (Version.getCurrentVersion().isNewerThan(Version.V1_17_R1)) {
                Object serializedProfile = MethodReflectionCollection.GAMEPROFILE_SERIALIZE.invokeOn(null, NBTTagUtils.createInstance(NBTTagUtils.NBTType.COMPOUND), profile);
                ReflectionUtils.setField(meta, "serializedProfile", serializedProfile);
            }
        } catch(NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to update Texture for Item", e);
        }
        return this;
    }

    public String getOwner() {
        return ((SkullMeta) meta).getOwner();
    }

    public ItemEditor setOwner(String owner) {
        if (itemStack.getType() != Material.SKULL_ITEM)
            throw new IllegalArgumentException("ItemStack is not an Player Head");
        SkullMeta skullMeta = ((SkullMeta) meta);
        skullMeta.setOwner(owner);
        itemStack.setItemMeta(skullMeta);
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

    public Map<Enchantment, Integer> getEnchantments() {
        return meta.getEnchants();
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
