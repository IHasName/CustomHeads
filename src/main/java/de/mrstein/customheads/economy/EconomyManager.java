package de.mrstein.customheads.economy;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHeadsPlayer;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.ScrollableInventory;
import de.mrstein.customheads.utils.Utils;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

import static de.mrstein.customheads.utils.Utils.openPreloader;
import static org.bukkit.Bukkit.getServer;

@Getter
public class EconomyManager {

    private Economy economyPlugin;

    public EconomyManager() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economyPlugin = economyProvider.getProvider();
        }
    }

    public void buyCategory(CustomHeadsPlayer customHeadsPlayer, Category category, String prevMenu) {
        EconomyResponse economyResponse = economyPlugin.withdrawPlayer(customHeadsPlayer.unwrap(), category.getPrice());
        if (economyResponse.transactionSuccess()) {
            customHeadsPlayer.unlockCategory(category);
            openPreloader(customHeadsPlayer.unwrap());
            List<ItemStack> heads = new ArrayList<>();
            category.getHeads().forEach(wearable -> heads.add(CustomHeads.getTagEditor().setTags(wearable, "wearable")));
            ScrollableInventory inventory = new ScrollableInventory(category.getName(), heads).setContentsClonable(true);
            inventory.setBarItem(1, Utils.getBackButton("openMenu", prevMenu));
            inventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFO_LORE).getItem(), "dec", "info-item", "blockMoving"));
            customHeadsPlayer.unwrap().openInventory(inventory.getAsInventory());
            customHeadsPlayer.unwrap().sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_SUCCESSFUL.replace("{CATEGORY}", category.getPlainName()));
        } else {
            customHeadsPlayer.unwrap().sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_FAILED.replace("{REASON}", economyResponse.errorMessage).replace("{CATEGORY}", category.getPlainName()));
        }
    }

}
