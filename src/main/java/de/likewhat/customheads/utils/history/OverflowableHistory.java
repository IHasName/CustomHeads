package de.likewhat.customheads.utils.history;

import de.likewhat.customheads.CustomHeads;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class OverflowableHistory {

    protected List<String> entries;
    protected OfflinePlayer offlinePlayer;

    public OverflowableHistory(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
        this.entries = new ArrayList<>();

        loadEntries();
        handleOverflow();
    }

    public abstract void loadEntries();

    public abstract Inventory getInventory();

    public void addEntry(String entry) {
        entries.add(entry);
        handleOverflow();
    }

    public boolean hasHistory() {
        return !entries.isEmpty();
    }

    protected void handleOverflow() {
        while (entries.size() > CustomHeads.historyOverflow) {
            entries.remove(0);
        }
    }

}
