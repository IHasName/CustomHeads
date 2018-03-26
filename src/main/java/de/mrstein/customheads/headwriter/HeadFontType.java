package de.mrstein.customheads.headwriter;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHead;
import de.mrstein.customheads.utils.Configs;
import de.mrstein.customheads.utils.Utils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HeadFontType {

    private Configs fontFile;

    private String fontName;
    private String cacheID;

    private char[] availibleCharacters = "abcdefghijklmnopqrstuvwxyz0123456789!?#$%&@_'\"()[]{}<>+-/=:,;\\ ".toCharArray();

    private static HashMap<String, HeadFontType> cache = new HashMap<>();
    private HashMap<Character, CustomHead> characters = new HashMap<>();

    private boolean cached = false;

    private long cacheTime;

    public HeadFontType(String fontName) {
        this.fontName = fontName;
        fontFile = new Configs(CustomHeads.getInstance(), fontName + ".yml", false, "fonts");
        if(exists()) {
            if(fontFile.get().isConfigurationSection("characters")) {
                for (String key : fontFile.get().getConfigurationSection("characters").getKeys(false)) {
                    if(!Utils.charArrayContains(availibleCharacters, key.charAt(0))) {
                        throw new UnsupportedOperationException("Unsupported Character: '" + key.charAt(0) + "'");
                    }
                    characters.put(key.charAt(0), new CustomHead(Utils.getURLFrom(fontFile.get().getString("characters." + key))));
                }
            }
        }
    }

    public static HeadFontType getCachedFont(String cacheID) {
        return cache.get(cacheID);
    }

    public boolean addCharacter(char character, String texture, boolean forceReplace) {
        if(Utils.charArrayContains(availibleCharacters, character) && (!characters.containsKey(character) || forceReplace)) {
            characters.put(character, new CustomHead(Utils.getURLFrom(texture)));
            if(cached)
                cache.put(cacheID, this);
            return true;
        }
        return false;
    }

    public boolean removeCharacter(char character) {
        if(characters.containsKey(character)) {
            characters.remove(character);
            if(cached)
                cache.put(cacheID, this);
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

    public String getCacheID() {
        return cacheID;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public HashMap<Character, CustomHead> getCharacters() {
        return characters;
    }

    public CustomHead getCharacter(char character) {
        return characters.get(character);
    }

    public String getName() {
        return fontName;
    }

    public void save() {
        fontFile.get().set("characters", null);
        if(!this.characters.isEmpty()) {
            List<Character> characters = new ArrayList<>(this.characters.keySet());
            Collections.sort(characters);
            for (char key : characters) {
                fontFile.get().set("characters." + key, this.characters.get(key).getTextureValue());
            }
        }
        fontFile.save();
        fontFile.reload();
    }

    public boolean delete() {
        return exists() && isValid() && new File(CustomHeads.getInstance().getDataFolder() + "/fonts", fontName + ".yml").delete();
    }

    public boolean isValid() {
        return fontFile.get().isConfigurationSection("characters");
    }

    public boolean exists() {
        return new File(CustomHeads.getInstance().getDataFolder() + "/fonts", fontName + ".yml").exists();
    }

    public static void clearCache() {
        for(HeadFontType font : cache.values()) {
            if(System.currentTimeMillis() - font.getCacheTime() < 6000) {
                cache.remove(font.getCacheID());
            }
        }
    }

}
