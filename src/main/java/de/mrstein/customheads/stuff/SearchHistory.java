package de.mrstein.customheads.stuff;

/*
 *  Project: CustomHeads in SearchHistory
 *     by LikeWhat
 *
 *  created on 08.09.2018 at 02:05
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchHistory {

    private List<String> entries = new ArrayList<>();

    private OfflinePlayer offlinePlayer;

    public SearchHistory(OfflinePlayer player) {
        offlinePlayer = player;
        JsonObject uuidObject = CustomHeads.getPlayerDataFile().getJson().getAsJsonObject().getAsJsonObject(player.getUniqueId().toString());
        JsonObject historyObject;
        if (uuidObject.has("history") && (historyObject = uuidObject.getAsJsonObject("history")).has("searchHistory")) {
            for (JsonElement element : historyObject.getAsJsonArray("searchHistory")) {
                entries.add(element.getAsString());
            }
        }
        handleOverflow();
    }

    public void addEntry(String entry) {
        entries.add(entry);
        handleOverflow();
    }

    public Inventory getInventory() {
        Inventory hisInv = Bukkit.createInventory(null, 9 * 5, CustomHeads.getLanguageManager().HISTORY_INV_TITLE.replace("{PLAYER}", offlinePlayer.getName()));
        hisInv.setItem(0, Utils.createItem(Material.STAINED_GLASS_PANE, 1, CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY_ACTIVE, (short) 5));
        hisInv.setItem(1, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.STAINED_GLASS_PANE, 1, CustomHeads.getLanguageManager().HISTORY_GETHISTORY, (short) 15), "history", "open#>get"));
        hisInv.setItem(8, Utils.createPlayerHead("§b" + offlinePlayer.getName(), offlinePlayer.getName()));
        for (int i = 9; i < 18; i++) {
            hisInv.setItem(i, Utils.createItem(Material.STAINED_GLASS_PANE, 1, "§0", (short) 1));
        }
        if (hasHistory()) {
            for (int i = 0; i < entries.size(); i++) {
                hisInv.setItem(i + 18, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.PAPER, 1, "§a" + entries.get(i), CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY_LORE), "history", "search#>" + entries.get(i), "tempTags"));
            }
        } else {
            hisInv.setItem(31, Utils.createItem(Material.BARRIER, 1, "§0", CustomHeads.getLanguageManager().HISTORY_NO_HISTORY_LORE));
        }
        return hisInv;
    }

    public boolean hasHistory() {
        return !entries.isEmpty();
    }

    private void handleOverflow() {
        while (entries.size() > CustomHeads.hisOverflow) {
            entries.remove(0);
        }
    }

}
