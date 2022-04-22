package de.likewhat.customheads.headwriter;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.Configs;
import de.likewhat.customheads.utils.ItemEditor;
import de.likewhat.customheads.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
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

    private static final char[] AVAILABLE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789!?#$%&@_'\"()[]{}<>+-/=:,;\\ ".toCharArray();

    private static final HashMap<String, HeadFontType> FONT_CACHE = new HashMap<>();
    private final HashMap<Character, ItemStack> characterItems = new HashMap<>();

    @Getter(AccessLevel.NONE)
    private final Configs fontFile;

    private boolean cached = false;

    private final String fontName;
    private String cacheID;

    private long cacheTime;

    public HeadFontType(String fontName) {
        this.fontName = fontName;
        fontFile = new Configs(CustomHeads.getInstance(), fontName + ".yml", false, "fonts");
        if (exists()) {
            if (fontFile.get().isConfigurationSection("characters")) {
                for (String key : fontFile.get().getConfigurationSection("characters").getKeys(false)) {
                    if (!Utils.charArrayContains(AVAILABLE_CHARACTERS, key.charAt(0))) {
                        throw new UnsupportedOperationException("Unsupported Character: '" + key.charAt(0) + "'");
                    }
                    characterItems.put(key.charAt(0), new ItemEditor(Material.SKULL_ITEM,  3).setTexture(fontFile.get().getString("characters." + key)).getItem());
                }
            }
        }
    }

    public static HeadFontType getCachedFont(String cacheID) {
        return FONT_CACHE.get(cacheID);
    }

    public static void clearCache() {
        FONT_CACHE.values().removeIf(font -> System.currentTimeMillis() - font.cacheTime > 600000);
    }

    public boolean addCharacter(char character, String texture, boolean forceReplace) {
        if (Utils.charArrayContains(AVAILABLE_CHARACTERS, character) && (!characterItems.containsKey(character) || forceReplace)) {
            characterItems.put(character, new ItemEditor(Material.SKULL_ITEM,  3).setTexture(texture).getItem());
            return true;
        }
        return false;
    }

    public boolean removeCharacter(char character) {
        if (characterItems.containsKey(character)) {
            characterItems.remove(character);
            return true;
        }
        return false;
    }

    public HeadFontType enableCache() {
        cached = true;
        cacheTime = System.currentTimeMillis();
        cacheID = Utils.randomAlphabetic(6);
        FONT_CACHE.put(cacheID, this);
        return this;
    }

    public HashMap<Character, ItemStack> getCharacterItems() {
        return characterItems;
    }

    public ItemStack getCharacter(char character) {
        return characterItems.get(character);
    }

    public void save() {
        fontFile.get().set("characters", null);
        if (!this.characterItems.isEmpty()) {
            List<Character> characters = new ArrayList<>(this.characterItems.keySet());
            Collections.sort(characters);
            for (char key : characters) {
                fontFile.get().set("characters." + key, new ItemEditor(this.characterItems.get(key)).getTexture());
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
