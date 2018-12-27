package de.mrstein.customheads.headwriter;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.Configs;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/*
 *  Project: CustomHeads in HeadFontType
 *     by LikeWhat
 */

@Getter
public class HeadFontType {

    private char[] availibleCharacters = "abcdefghijklmnopqrstuvwxyz0123456789!?#$%&@_'\"()[]{}<>+-/=:,;\\ ".toCharArray();

    private static HashMap<String, HeadFontType> cache = new HashMap<>();
    private HashMap<Character, ItemStack> characters = new HashMap<>();

    @Getter(AccessLevel.NONE)
    private Configs fontFile;

    private boolean cached = false;

    private String fontName;
    private String cacheID;

    private long cacheTime;

    public HeadFontType(String fontName) {
        this.fontName = fontName;
        fontFile = new Configs(CustomHeads.getInstance(), fontName + ".yml", false, "fonts");
        if (exists()) {
            if (fontFile.get().isConfigurationSection("characters")) {
                for (String key : fontFile.get().getConfigurationSection("characters").getKeys(false)) {
                    if (!Utils.charArrayContains(availibleCharacters, key.charAt(0))) {
                        throw new UnsupportedOperationException("Unsupported Character: '" + key.charAt(0) + "'");
                    }
                    characters.put(key.charAt(0), new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture(fontFile.get().getString("characters." + key)).getItem());
                }
            }
        }
    }

    public static HeadFontType getCachedFont(String cacheID) {
        return cache.get(cacheID);
    }

    public static void clearCache() {
        cache.values().removeIf(font -> System.currentTimeMillis() - font.cacheTime > 600000);
    }

    public boolean addCharacter(char character, String texture, boolean forceReplace) {
        if (Utils.charArrayContains(availibleCharacters, character) && (!characters.containsKey(character) || forceReplace)) {
            characters.put(character, new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture(texture).getItem());
            return true;
        }
        return false;
    }

    public boolean removeCharacter(char character) {
        if (characters.containsKey(character)) {
            characters.remove(character);
            return true;
        }
        return false;
    }

    public HeadFontType enableCache() {
        cached = true;
        cacheTime = System.currentTimeMillis();
        cacheID = RandomStringUtils.randomAlphabetic(6);
        cache.put(cacheID, this);
        return this;
    }

    public HashMap<Character, ItemStack> getCharacters() {
        return characters;
    }

    public ItemStack getCharacter(char character) {
        return characters.get(character);
    }

    public void save() {
        fontFile.get().set("characters", null);
        if (!this.characters.isEmpty()) {
            List<Character> characters = new ArrayList<>(this.characters.keySet());
            Collections.sort(characters);
            for (char key : characters) {
                fontFile.get().set("characters." + key, new ItemEditor(this.characters.get(key)).getTexture());
            }
        }
        fontFile.save();
        fontFile.reload();
    }

    public boolean delete() {
        return exists() && new File(CustomHeads.getInstance().getDataFolder() + "/fonts", fontName + ".yml").delete();
    }

    public boolean isValid() {
        return fontFile.get().isConfigurationSection("characters");
    }

    public boolean exists() {
        return new File(CustomHeads.getInstance().getDataFolder() + "/fonts", fontName + ".yml").exists();
    }

}
