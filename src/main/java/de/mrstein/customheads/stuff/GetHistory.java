package de.mrstein.customheads.stuff;

/*
 *  Project: CustomHeads in GetHistory
 *     by LikeWhat
 *
 *  created on 08.09.2018 at 02:06
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
public class GetHistory {

    private List<String> entries = new ArrayList<>();

    private OfflinePlayer offlinePlayer;

    public GetHistory(OfflinePlayer player) {
        offlinePlayer = player;
        JsonObject uuidObject = CustomHeads.getPlayerDataFile().getJson().getAsJsonObject().getAsJsonObject(player.getUniqueId().toString());
        JsonObject historyObject;
        if (uuidObject.has("history") && (historyObject = uuidObject.getAsJsonObject("history")).has("getHistory")) {
            for (JsonElement element : historyObject.getAsJsonArray("getHistory")) {
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
        hisInv.setItem(0, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY).getItem(), "history", "open#>search"));
        hisInv.setItem(1, new ItemEditor(Material.STAINED_GLASS_PANE, (short) 5).setDisplayName(CustomHeads.getLanguageManager().HISTORY_GETHISTORY_ACTIVE).getItem());
        hisInv.setItem(8, new ItemEditor(Material.SKULL_ITEM, (short) 3).setDisplayName("§b" + offlinePlayer.getName()).setOwner(offlinePlayer.getName()).getItem());
        for (int i = 9; i < 18; i++) {
            hisInv.setItem(i, new ItemEditor(Material.STAINED_GLASS_PANE, (short) 1).setDisplayName("§0").getItem());
        }
        if (hasHistory()) {
            for (int i = 0; i < entries.size(); i++) {
                hisInv.setItem(i + 18, new ItemEditor(Material.SKULL_ITEM, (short) 3).setDisplayName("§a" + entries.get(i)).setLore(CustomHeads.getLanguageManager().HISTORY_GET_LORE).setOwner(entries.get(i)).getItem());
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
