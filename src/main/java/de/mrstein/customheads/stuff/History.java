package de.mrstein.customheads.stuff;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.langLoader.Language;
import de.mrstein.customheads.utils.Configs;
import de.mrstein.customheads.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class History {

    private static HistoryMode mode;
    private static HashMap<OfflinePlayer, String> tempSavingGet;
    private static HashMap<OfflinePlayer, String> tempSavingSearch;

    static {
        tempSavingGet = new HashMap<>();
        tempSavingSearch = new HashMap<>();
    }

    private OfflinePlayer offlinePlayer;
    private Configs his = CustomHeads.his;
    private Language lg = CustomHeads.getLanguageManager();

    /**
     * Main Method for History
     *
     * @see #getSearchHistory()
     * @see #getGetHistory()
     */
    public History(OfflinePlayer player) {
        offlinePlayer = player;
        mode = CustomHeads.hisMode;
    }

    public SearchHistory getSearchHistory() {
        return new SearchHistory();
    }

    public GetHistory getGetHistory() {
        return new GetHistory();
    }

    public enum HistoryMode {
        TEMP(), FILE()
    }

    public class SearchHistory {
        /**
         * Will write an Entry to the Players Search History
         *
         * @param entry
         */
        public void addEntry(String entry) {
            if (mode == HistoryMode.FILE) {
                if (hasHistory()) {
                    List<String> entryList = getEntrys();
                    while (CustomHeads.hisOverflow - 1 < entryList.size()) {
                        entryList.remove(entryList.size() - 1);
                    }
                    entryList.add(0, entry);
                    StringBuilder newEntrys = new StringBuilder();
                    for (int i = 0; i < entryList.size(); i++) {
                        if (i + 1 == entryList.size()) {
                            newEntrys.append(entryList.get(i));
                        } else {
                            newEntrys.append(entryList.get(i)).append(":");
                        }
                    }
                    his.get().set(offlinePlayer.getUniqueId() + ".history.search", newEntrys.toString());
                } else {
                    his.get().set(offlinePlayer.getUniqueId() + ".history.search", entry);
                }
                his.save();
                his.reload();
            } else {
                if (hasHistory()) {
                    List<String> entryList = getEntrys();
                    if (CustomHeads.hisOverflow - 1 < entryList.size()) {
                        entryList.remove(entryList.size() - 1);
                    }
                    entryList.add(0, entry);
                    StringBuilder newEntrys = new StringBuilder();
                    for (String anEntryList : entryList) {
                        newEntrys.append(anEntryList).append(":");
                    }
                    tempSavingSearch.put(offlinePlayer, newEntrys.toString());
                } else {
                    tempSavingSearch.put(offlinePlayer, entry + ":");
                }
            }
        }

        /**
         * Gets all Enties from the Player in the Search History
         *
         * @return String List
         */
        public List<String> getEntrys() {
            return new ArrayList<>(Arrays.asList((mode == HistoryMode.FILE ? his.get().getString(offlinePlayer.getUniqueId() + ".history.search") : tempSavingSearch.get(offlinePlayer)).split(":")));
        }

        public boolean hasHistory() {
            return mode == HistoryMode.FILE ? CustomHeads.his.get().isString(offlinePlayer.getUniqueId() + ".history.search") : tempSavingSearch.containsKey(offlinePlayer);
        }

        public Inventory getAsInventory() {
            Inventory hisInv = Bukkit.createInventory(null, 9 * 5, lg.HISTORY_INV_TITLE.replace("{PLAYER}", offlinePlayer.getName()));
            hisInv.setItem(0, Utils.createItem(Material.STAINED_GLASS_PANE, 1, lg.HISTORY_SEARCHHISTORY_ACTIVE, (short) 5));
            hisInv.setItem(1, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.STAINED_GLASS_PANE, 1, lg.HISTORY_GETHISTORY, (short) 15), "history", "open#>get"));
            hisInv.setItem(8, Utils.createPlayerHead("§b" + offlinePlayer.getName(), offlinePlayer.getName()));
            for (int i = 9; i < 18; i++) {
                hisInv.setItem(i, Utils.createItem(Material.STAINED_GLASS_PANE, 1, "§0", (short) 1));
            }
            if (hasHistory()) {
                for (int i = 0; i < getEntrys().size(); i++) {
                    hisInv.setItem(i + 18, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.PAPER, 1, "§a" + getEntrys().get(i), lg.HISTORY_SEARCHHISTORY_LORE), "history", "search#>" + getEntrys().get(i), "tempTags"));
                }
            } else {
                hisInv.setItem(31, Utils.createItem(Material.BARRIER, 1, "§0", lg.HISTORY_NO_HISTORY_LORE));
            }
            return hisInv;
        }
    }

    public class GetHistory {
        /**
         * Will write an Entry to the Players Get History
         *
         * @param entry
         */
        public void addEntry(String entry) {
            if (mode == HistoryMode.FILE) {
                if (hasHistory()) {
                    List<String> entryList = getEntrys();
                    while (CustomHeads.hisOverflow - 1 < entryList.size()) {
                        entryList.remove(entryList.size() - 1);
                    }
                    entryList.add(0, entry);
                    StringBuilder newEntrys = new StringBuilder();
                    for (int i = 0; i < entryList.size(); i++) {
                        if (i + 1 == entryList.size()) {
                            newEntrys.append(entryList.get(i));
                        } else {
                            newEntrys.append(entryList.get(i)).append(":");
                        }
                    }
                    his.get().set(offlinePlayer.getUniqueId() + ".history.get", newEntrys.toString());
                } else {
                    his.get().set(offlinePlayer.getUniqueId() + ".history.get", entry);
                }
                his.save();
                his.reload();
            } else {
                if (hasHistory()) {
                    List<String> entryList = getEntrys();
                    if (CustomHeads.hisOverflow - 1 < entryList.size()) {
                        entryList.remove(entryList.size() - 1);
                    }
                    entryList.add(0, entry);
                    StringBuilder newEntrys = new StringBuilder();
                    for (String entrylstr : entryList) {
                        newEntrys.append(entrylstr).append(":");
                    }
                    tempSavingGet.put(offlinePlayer, newEntrys.toString());
                } else {
                    tempSavingGet.put(offlinePlayer, entry + ":");
                }
            }
        }

        /**
         * Gets all Enties from the Player in the Search History
         *
         * @return String List
         */
        public List<String> getEntrys() {
            return new ArrayList<>(Arrays.asList((mode == HistoryMode.FILE ? his.get().getString(offlinePlayer.getUniqueId() + ".history.get") : tempSavingGet.get(offlinePlayer)).split(":")));
        }

        public boolean hasHistory() {
            return mode == HistoryMode.FILE ? CustomHeads.his.get().isString(offlinePlayer.getUniqueId() + ".history.get") : tempSavingGet.containsKey(offlinePlayer);
        }

        public Inventory getAsInventory() {
            Inventory hisInv = Bukkit.createInventory(null, 9 * 5, lg.HISTORY_INV_TITLE.replace("{PLAYER}", offlinePlayer.getName()));
            hisInv.setItem(0, CustomHeads.getTagEditor().setTags(Utils.createItem(Material.STAINED_GLASS_PANE, 1, lg.HISTORY_SEARCHHISTORY, (short) 15), "history", "open#>search"));
            hisInv.setItem(1, Utils.createItem(Material.STAINED_GLASS_PANE, 1, lg.HISTORY_GETHISTORY_ACTIVE, (short) 5));
            hisInv.setItem(8, Utils.createPlayerHead("§b" + offlinePlayer.getName(), offlinePlayer.getName()));
            for (int i = 9; i < 18; i++) {
                hisInv.setItem(i, Utils.createItem(Material.STAINED_GLASS_PANE, 1, "§0", (short) 1));
            }
            if (hasHistory()) {
                for (int i = 0; i < getEntrys().size(); i++) {
                    hisInv.setItem(i + 18, Utils.createPlayerHead("§a" + getEntrys().get(i), getEntrys().get(i), lg.HISTORY_GET_LORE));
                }
            } else {
                hisInv.setItem(31, Utils.createItem(Material.BARRIER, 1, "§0", lg.HISTORY_NO_HISTORY_LORE));
            }
            return hisInv;
        }
    }
}
