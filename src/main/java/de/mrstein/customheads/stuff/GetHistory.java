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
import de.mrstein.customheads.utils.Utils;
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
        hisInv.setItem(0, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.STAINED_GLASS_PANE, 1, CustomHeads.getLanguageManager().HISTORY_SEARCHHISTORY, (short) 15), "history", "open#>search"));
        hisInv.setItem(1, Utils.createItem(Material.STAINED_GLASS_PANE, 1, CustomHeads.getLanguageManager().HISTORY_GETHISTORY_ACTIVE, (short) 5));
        hisInv.setItem(8, Utils.createPlayerHead("§b" + offlinePlayer.getName(), offlinePlayer.getName()));
        for (int i = 9; i < 18; i++) {
            hisInv.setItem(i, Utils.createItem(Material.STAINED_GLASS_PANE, 1, "§0", (short) 1));
        }
        if (hasHistory()) {
            for (int i = 0; i < entries.size(); i++) {
                hisInv.setItem(i + 18, Utils.createPlayerHead("§a" + entries.get(i), entries.get(i), CustomHeads.getLanguageManager().HISTORY_GET_LORE));
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
