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
import de.mrstein.customheads.utils.ItemEditor;
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
        hisInv.setItem(0, new ItemEditor(Material.STAINED_GLASS_PANE,  5).setDisplayName(CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY_ACTIVE).getItem());
        hisInv.setItem(1, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.STAINED_GLASS_PANE,  15).setDisplayName(CustomHeads.getLanguageManager().HISTORY_GETHISTORY).getItem(), "history", "open#>get"));
        hisInv.setItem(8, new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("§b" + offlinePlayer.getName()).setOwner(offlinePlayer.getName()).getItem());
        for (int i = 9; i < 18; i++) {
            hisInv.setItem(i, new ItemEditor(Material.STAINED_GLASS_PANE,  1).setDisplayName("§0").getItem());
        }
        if (hasHistory()) {
            for (int i = 0; i < entries.size(); i++) {
                hisInv.setItem(i + 18, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName("§a" + entries.get(i)).setLore(CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY_LORE).getItem(), "history", "search#>" + entries.get(i), "tempTags", "blockMoving"));
            }
        } else {
            hisInv.setItem(31, new ItemEditor(Material.BARRIER).setDisplayName("§0").setLore(CustomHeads.getLanguageManager().HISTORY_NO_HISTORY_LORE).getItem());
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
