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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerWrapper implements CustomHeadsPlayer {

    private static final HashMap<UUID, CustomHeadsPlayer> WRAPPED_PLAYERS_CACHE = new HashMap<>();

    @Getter private List<CustomHead> unlockedHeads = new ArrayList<>();
    private List<Category> unlockedCategories = new ArrayList<>();
    @Getter private final List<ItemStack> savedHeads = new ArrayList<>();

    @Getter private SearchHistory searchHistory;
    @Getter private GetHistory getHistory;

    private final Player player;

    private PlayerWrapper(Player player) {
        this.player = player;
        try {
            JsonObject dataRoot = CustomHeads.getPlayerDataFile().getJson().getAsJsonObject();
            this.searchHistory = new SearchHistory(player);
            this.getHistory = new GetHistory(player);

            if (dataRoot.has(player.getUniqueId().toString())) {
                JsonObject uuidObject = dataRoot.getAsJsonObject(player.getUniqueId().toString());
                uuidObject.getAsJsonObject("savedHeads").entrySet().forEach(entry -> {
                    ItemEditor editor = Utils.createPlayerHeadItemEditor().setDisplayName(entry.getKey());
                    String textureValue = entry.getValue().getAsString();
                    if (textureValue.length() > 16) {
                        editor.setTexture(textureValue);
                    } else {
                        editor.setOwner(textureValue);
                    }
                    this.savedHeads.add(editor.getItem());
                });

                if (uuidObject.has("unlockedCategories")) {
                    List<Category> categories = new ArrayList<>();
                    uuidObject.getAsJsonArray("unlockedCategories").forEach(categoryId -> categories.add(CustomHeads.getCategoryManager().getCategory(categoryId.getAsString())));
                    this.unlockedCategories = categories;
                }

                if (uuidObject.has("unlockedHeads")) {
                    List<CustomHead> heads = new ArrayList<>();
                    uuidObject.getAsJsonArray("unlockedHeads").forEach(headId -> {
                        String[] idParts = headId.getAsString().split(":");
                        heads.add(CustomHeads.getApi().getHead(CustomHeads.getCategoryManager().getCategory(idParts[0]), Integer.parseInt(idParts[1])));
                    });
                    this.unlockedHeads = heads;
                }
            }
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to wrap Player", e);
        }
        WRAPPED_PLAYERS_CACHE.put(player.getUniqueId(), this);
    }

    static CustomHeadsPlayer wrapPlayer(Player player) {
        if (WRAPPED_PLAYERS_CACHE.containsKey(player.getUniqueId())) {
            return WRAPPED_PLAYERS_CACHE.get(player.getUniqueId());
        } else {
            return new PlayerWrapper(player);
        }
    }

    public static void clearCache() {
        if (WRAPPED_PLAYERS_CACHE.isEmpty())
            return;

        JsonFile jsonFile = CustomHeads.getPlayerDataFile();
        JsonObject rootObjects = jsonFile.getJson().getAsJsonObject();
        for (UUID uuid : WRAPPED_PLAYERS_CACHE.keySet()) {
            CustomHeadsPlayer customHeadsPlayer = WRAPPED_PLAYERS_CACHE.get(uuid);
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
        WRAPPED_PLAYERS_CACHE.clear();
    }

    public List<Category> getUnlockedCategories(boolean ignorePermission) {
        return CustomHeads.getCategoryManager().getCategoryList().stream().filter(category -> (!ignorePermission && (Utils.hasPermission(this.player, category.getPermission()) || Utils.hasPermission(this.player, category.getPermission() + ".allheads"))) || this.unlockedCategories.contains(category)).collect(Collectors.toList());
    }

    public Player unwrap() {
        return this.player;
    }

    public boolean unlockCategory(Category category) {
        if (this.unlockedCategories.contains(category)) {
            return false;
        } else {
            this.unlockedCategories.add(category);
            return true;
        }
    }

    public boolean lockCategory(Category category) {
        return unlockedCategories.remove(category);
    }

    public boolean unlockHead(Category category, int id) {
        if (this.unlockedHeads.contains(CustomHeads.getApi().getHead(category, id))) {
            return false;
        } else {
            this.unlockedHeads.add(CustomHeads.getApi().getHead(category, id));
            return true;
        }
    }

    public boolean lockHead(Category category, int id) {
        return this.unlockedHeads.remove(CustomHeads.getApi().getHead(category, id));
    }

    public boolean saveHead(String name, String texture) {
        if (hasHead(name)) {
            return false;
        } else {
            ItemEditor editor = Utils.createPlayerHeadItemEditor().setDisplayName(name);
            if (texture.length() > 16) {
                editor.setTexture(texture);
            } else {
                editor.setOwner(texture);
            }
            this.savedHeads.add(editor.getItem());
            return true;
        }
    }

    public boolean deleteHead(String name) {
        if (!hasHead(name)) {
            return false;
        } else {
            this.savedHeads.remove(getHead(name));
            return true;
        }
    }

    public ItemStack getHead(String name) {
        return this.savedHeads.stream().filter(itemStack -> Utils.toConfigString(itemStack.getItemMeta().getDisplayName()).equals(name)).iterator().next();
    }

    public boolean hasHead(String name) {
        return this.savedHeads.stream().filter(itemStack -> Utils.toConfigString(itemStack.getItemMeta().getDisplayName()).equals(name)).iterator().hasNext();
    }

}
