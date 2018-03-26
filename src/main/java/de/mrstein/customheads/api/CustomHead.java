package de.mrstein.customheads.api;

import de.mrstein.customheads.utils.GameProfileBuilder;
import de.mrstein.customheads.utils.Utils;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.mrstein.customheads.utils.Utils.format;

/**
 * Will apply the URL from textures.minecraft.net to a Skull
 * @author MrStein
 * @version 1.1
 */
public class CustomHead {

    private String url;
    private String displayName;
    private static final String base64 = "{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";

    private List<String> lore;

    /**
     * @param url URL from textures.minecraft.net
     * @author StoneGamer
     */
    public CustomHead(String url) {
        if(url == null) {
            throw new NullPointerException("URL cannot be null");
        }
        if(!url.startsWith("http://textures.minecraft.net/")) {
            throw new IllegalArgumentException("URL must be from textures.minecraft.net!");
        }
        this.url = url;
    }

    public CustomHead setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public CustomHead addLore(String lore) {
        if(this.lore == null) this.lore = new ArrayList<>();
        this.lore.add(lore);
        return this;
    }

    public CustomHead setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Uses \n to Split Lines
     */
    public CustomHead setLore(String lore) {
        if(lore.equals("")) {
            this.lore = null;
            return this;
        }
        setLore(Arrays.asList(lore.split("\n")));
        return this;
    }

    public ItemStack toItem() {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta meta = head.getItemMeta();
        Utils.RefSet(meta.getClass(), meta, "profile", GameProfileBuilder.createProfileWithTexture(getTextureValue()));
        if(displayName != null) {
            meta.setDisplayName((displayName.startsWith("&") || displayName.startsWith("§") ? "" : "§f") + format(displayName));
        }
        if(lore != null && !lore.isEmpty()) {
            List<String> defpref = new ArrayList<>();
            for(String d : lore) {
                defpref.add((d.startsWith("&") || d.startsWith("§") ? "" : "&7") + d);
            }
            meta.setLore(format(defpref));
        }
        head.setItemMeta(meta);
        return head;
    }

    public String getTextureValue() { return new String(Base64.encodeBase64(String.format(base64, url).getBytes())); }

    public String getDisplayName() { return displayName; }

    public boolean hasDisplayName() { return displayName != null; }

    public List<String> getLore() { return lore; }

    public boolean hasLore() { return lore != null; }

    public String getLoreAsString() {
        if(lore.isEmpty()) return "";
        StringBuilder b = new StringBuilder();
        for(String l : lore) {
            b.append(l).append("\n");
        }
        b.setLength(b.length()-1);
        return b.toString();
    }

    public void setAt(Location location, BlockFace facing) { CustomHeadsAPI.setSkullWithTexture(location.getBlock(), getTextureValue(), facing); }

    public String toString() {
        StringBuilder b = new StringBuilder("CustomHead:{");
        if(hasDisplayName())
            b.append("display-name:").append(displayName).append(",");
        if(hasLore())
            b.append("lore:").append(getLore()).append(",");
        b.append("url:").append(url);
        b.append("}");
        return b.toString();
    }
}
