package de.likewhat.customheads.utils.history;

/*
 *  Project: CustomHeads in SearchHistory
 *     by LikeWhat
 *
 *  created on 08.09.2018 at 02:05
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.CustomHeadsInventoryHolder;
import de.likewhat.customheads.utils.ItemEditor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

@Getter
public class SearchHistory extends OverflowableHistory {

    public SearchHistory(OfflinePlayer player) {
        super(player);
    }

    public void loadEntries() {
        JsonObject jsonPlayerData = CustomHeads.getPlayerDataFile().getJson().getAsJsonObject();
        if(!jsonPlayerData.has(super.offlinePlayer.getUniqueId().toString())) {
            return;
        }
        JsonObject uuidObject = jsonPlayerData.getAsJsonObject(super.offlinePlayer.getUniqueId().toString());
        JsonObject historyObject;
        if (uuidObject.has("history") && (historyObject = uuidObject.getAsJsonObject("history")).has("searchHistory")) {
            for (JsonElement element : historyObject.getAsJsonArray("searchHistory")) {
                super.entries.add(element.getAsString());
            }
        }
    }

    public Inventory getInventory() {
        Inventory hisInv = Bukkit.createInventory(new CustomHeadsInventoryHolder.BaseHolder("custom_heads:search_history"), 9 * 5, CustomHeads.getLanguageManager().HISTORY_INV_TITLE.replace("{PLAYER}", super.offlinePlayer.getName()));
        hisInv.setItem(0, new ItemEditor(Material.STAINED_GLASS_PANE,  5).setDisplayName(CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY_ACTIVE).getItem());
        hisInv.setItem(1, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.STAINED_GLASS_PANE,  15).setDisplayName(CustomHeads.getLanguageManager().HISTORY_GETHISTORY).getItem(), "history", "open#>get"));
        hisInv.setItem(8, new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName("§b" + offlinePlayer.getName()).setOwner(super.offlinePlayer.getName()).getItem());
        for (int i = 9; i < 18; i++) {
            hisInv.setItem(i, new ItemEditor(Material.STAINED_GLASS_PANE,  1).setDisplayName("§0").getItem());
        }
        if (hasHistory()) {
            for (int i = 0; i < super.entries.size(); i++) {
                hisInv.setItem(i + 18, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName("§a" + super.entries.get(i)).setLore(CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY_LORE).getItem(), "history", "search#>" + super.entries.get(i), "tempTags", "blockMoving"));
            }
        } else {
            hisInv.setItem(31, new ItemEditor(Material.BARRIER).setDisplayName("§0").setLore(CustomHeads.getLanguageManager().HISTORY_NO_HISTORY_LORE).getItem());
        }
        return hisInv;
    }

}
