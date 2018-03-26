package de.mrstein.customheads.utils;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.listener.InventoryListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.mrstein.customheads.utils.Utils.getDeleteDialog;
import static de.mrstein.customheads.utils.Utils.toConfigString;


public class HeadSaver {

    private static HashMap<Player, String> deleteConfirm = new HashMap<>();
    private HashMap<String, ItemStack> heads = new HashMap<>();

    private Player player;

    public HeadSaver(Player player) {
        this.player = player;
        CustomHeads.heads.reload();
        if (CustomHeads.heads.get().getString("heads." + player.getUniqueId()) != null) {
            for (String key : CustomHeads.heads.get().getConfigurationSection("heads." + player.getUniqueId()).getKeys(false)) {
                heads.put(key, (CustomHeads.heads.get().getString("heads." + player.getUniqueId() + "." + key + ".texture").length() < 16 ? Utils.createPlayerHead("§e" + key, CustomHeads.heads.get().getString("heads." + player.getUniqueId() + "." + key + ".texture")) : Utils.createHead("§e" + key, CustomHeads.heads.get().getString("heads." + player.getUniqueId() + "." + key + ".texture"))));
            }
        }
    }

    public List<ItemStack> getHeads() {
        return new ArrayList<>(heads.values());
    }

    public boolean hasHead(String name) {
        return heads.containsKey(name);
    }

    public void saveHead(String texture, String name) {
        heads.put(toConfigString(name), (texture.length() < 16 ? Utils.createPlayerHead("§e" + name, texture) : Utils.createHead("§e" + name, texture)));
        CustomHeads.heads.get().set("heads." + player.getUniqueId() + "." + toConfigString(name) + ".texture", texture);
        CustomHeads.heads.save();
    }

    public boolean removeHead(String name) {
        if (!deleteConfirm.get(player).equals(name))
            return false;
        heads.remove(name);
        CustomHeads.heads.get().set("heads." + player.getUniqueId() + "." + toConfigString(name.replace("&r", "")), null);
        CustomHeads.heads.save();
        deleteConfirm.remove(player);
        return true;
    }

    public void openDeleteDialog(String name) {
        deleteConfirm.put(player, name);
        player.openInventory(getDeleteDialog(CustomHeads.getLanguageManager().REMOVE_CONFIRMATION.replace("{HEAD}", name), new String[] {"confirmDelete", "head#>" + name, "chItem", "saved_heads", "menuID", InventoryListener.getLastMenu(player, false)}, new String[] {"chItem", "saved_heads", "menuID", InventoryListener.getLastMenu(player, true)}, heads.get(name)));
    }

}
