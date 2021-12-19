package de.likewhat.customheads.utils;

/*
 *  Project: CustomHeads in PlayerWrapper
 *     by LikeWhat
 *
 *  created on 20.08.2018 at 17:17
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.utils.history.GetHistory;
import de.likewhat.customheads.utils.history.SearchHistory;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerWrapper implements CustomHeadsPlayer {

    private static HashMap<UUID, CustomHeadsPlayer> wrappedPlayersCache = new HashMap<>();

    @Getter private List<CustomHead> unlockedHeads = new ArrayList<>();
    private List<Category> unlockedCategories = new ArrayList<>();
    @Getter private List<ItemStack> savedHeads = new ArrayList<>();

    @Getter private SearchHistory searchHistory;
    @Getter private GetHistory getHistory;

    private OfflinePlayer player;

    private PlayerWrapper(OfflinePlayer player) {
        this.player = player;
        try {
            JsonObject dataRoot = CustomHeads.getPlayerDataFile().getJson().getAsJsonObject();
            searchHistory = new SearchHistory(player);
            getHistory = new GetHistory(player);

            if (dataRoot.has(player.getUniqueId().toString())) {
                JsonObject uuidObject = dataRoot.getAsJsonObject(player.getUniqueId().toString());
                uuidObject.getAsJsonObject("savedHeads").entrySet().forEach(entry -> {
                    ItemEditor editor = new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(entry.getKey());
                    String textureValue = entry.getValue().getAsString();
                    if (textureValue.length() > 16) {
                        editor.setTexture(textureValue);
                    } else {
                        editor.setOwner(textureValue);
                    }
                    savedHeads.add(editor.getItem());
                });

                if (uuidObject.has("unlockedCategories")) {
                    List<Category> categories = new ArrayList<>();
                    uuidObject.getAsJsonArray("unlockedCategories").forEach(categoryId -> categories.add(CustomHeads.getCategoryManager().getCategory(categoryId.getAsString())));
                    unlockedCategories = categories;
                }

                if (uuidObject.has("unlockedHeads")) {
                    List<CustomHead> heads = new ArrayList<>();
                    uuidObject.getAsJsonArray("unlockedHeads").forEach(headId -> {
                        String[] idParts = headId.getAsString().split(":");
                        heads.add(CustomHeads.getApi().getHead(CustomHeads.getCategoryManager().getCategory(idParts[0]), Integer.parseInt(idParts[1])));
                    });
                    unlockedHeads = heads;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        wrappedPlayersCache.put(player.getUniqueId(), this);
    }

    static CustomHeadsPlayer wrapPlayer(OfflinePlayer player) {
        return wrappedPlayersCache.containsKey(player.getUniqueId()) ? wrappedPlayersCache.get(player.getUniqueId()) : new PlayerWrapper(player);
    }

    public static void clearCache() {
        if (wrappedPlayersCache.isEmpty())
            return;

        JsonFile jsonFile = CustomHeads.getPlayerDataFile();
        JsonObject rootObjects = jsonFile.getJson().getAsJsonObject();
        for (UUID uuid : wrappedPlayersCache.keySet()) {
            CustomHeadsPlayer customHeadsPlayer = wrappedPlayersCache.get(uuid);
            JsonObject uuidObject = rootObjects.has(uuid.toString()) ? rootObjects.getAsJsonObject(uuid.toString()) : new JsonObject();
            JsonObject savedHeads = new JsonObject();
            List<ItemStack> saved = customHeadsPlayer.getSavedHeads();
            for (ItemStack head : saved) {
                ItemEditor customHead = new ItemEditor(head);
                savedHeads.addProperty(Utils.toConfigString(customHead.getDisplayName()), customHead.getOwner() == null ? customHead.getTexture() : customHead.getOwner());
            }
            uuidObject.add("savedHeads", savedHeads);

            JsonArray unlockedCategories = new JsonArray();
            for (Category category : customHeadsPlayer.getUnlockedCategories(true)) {
                unlockedCategories.add(new JsonPrimitive(category.getId()));
            }
            uuidObject.add("unlockedCategories", unlockedCategories);

            JsonArray unlockedHeads = new JsonArray();
            for (CustomHead customHead : customHeadsPlayer.getUnlockedHeads()) {
                unlockedHeads.add(new JsonPrimitive(customHead.getOriginCategory().getId() + ":" + customHead.getId()));
            }
            uuidObject.add("unlockedHeads", unlockedHeads);

            JsonObject historyObject = new JsonObject();
            JsonArray searchHistory = new JsonArray();
            if (customHeadsPlayer.getSearchHistory() != null && customHeadsPlayer.getSearchHistory().hasHistory()) {
                for (String entry : customHeadsPlayer.getSearchHistory().getEntries()) {
                    searchHistory.add(new JsonPrimitive(entry));
                }
                historyObject.add("searchHistory", searchHistory);
            }
            if (customHeadsPlayer.getGetHistory() != null && customHeadsPlayer.getGetHistory().hasHistory()) {
                JsonArray getHistory = new JsonArray();
                for (String entry : customHeadsPlayer.getGetHistory().getEntries()) {
                    getHistory.add(new JsonPrimitive(entry));
                }
                historyObject.add("getHistory", getHistory);
            }
            uuidObject.add("history", historyObject);

            rootObjects.add(uuid.toString(), uuidObject);
        }
        jsonFile.setJson(rootObjects);
        jsonFile.saveJson();
        wrappedPlayersCache.clear();
    }

    public List<Category> getUnlockedCategories(boolean ignorePermission) {
        return CustomHeads.getCategoryManager().getCategoryList().stream().filter(category -> (!ignorePermission && (Utils.hasPermission(player.getPlayer(), category.getPermission()) || Utils.hasPermission(player.getPlayer(), category.getPermission() + ".allheads"))) || unlockedCategories.contains(category)).collect(Collectors.toList());
    }

    public Player unwrap() {
        return player.getPlayer();
    }

    public boolean unlockCategory(Category category) {
        if (unlockedCategories.contains(category)) {
            return false;
        } else {
            unlockedCategories.add(category);
            return true;
        }
    }

    public boolean lockCategory(Category category) {
        if (unlockedCategories.contains(category)) {
            unlockedCategories.remove(category);
            return true;
        } else {
            return false;
        }
    }

    public boolean unlockHead(Category category, int id) {
        if (unlockedHeads.contains(CustomHeads.getApi().getHead(category, id))) {
            return false;
        } else {
            unlockedHeads.add(CustomHeads.getApi().getHead(category, id));
            return true;
        }
    }

    public boolean lockHead(Category category, int id) {
        if (unlockedHeads.contains(CustomHeads.getApi().getHead(category, id))) {
            unlockedHeads.remove(CustomHeads.getApi().getHead(category, id));
            return true;
        } else {
            return false;
        }
    }

    public boolean saveHead(String name, String texture) {
        if (hasHead(name)) {
            return false;
        } else {
            ItemEditor editor = new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(name);
            if (texture.length() > 16) {
                editor.setTexture(texture);
            } else {
                editor.setOwner(texture);
            }
            savedHeads.add(editor.getItem());
            return true;
        }
    }

    public boolean deleteHead(String name) {
        if (!hasHead(name)) {
            return false;
        } else {
            savedHeads.remove(getHead(name));
            return true;
        }
    }

    public ItemStack getHead(String name) {
        return savedHeads.stream().filter(itemStack -> Utils.toConfigString(itemStack.getItemMeta().getDisplayName()).equals(name)).iterator().next();
    }

    public boolean hasHead(String name) {
        return savedHeads.stream().filter(itemStack -> Utils.toConfigString(itemStack.getItemMeta().getDisplayName()).equals(name)).iterator().hasNext();
    }

}
