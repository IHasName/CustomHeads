package de.likewhat.customheads.economy.handlers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.economy.EconomyCallback;
import de.likewhat.customheads.economy.EconomyHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHandler implements EconomyHandler {

    private Economy economyPlugin;

    public VaultHandler() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
           CustomHeads.getPluginLogger().warning("Vault Economy Service not found. Do you have a supported Economy Plugin installed?");
        } else {
            economyPlugin = economyProvider.getProvider();
        }
    }

    public EconomyCallback handleWithdraw(Player player, double amount) {
        EconomyResponse response = economyPlugin.withdrawPlayer(player, amount);
        return new EconomyCallback(response.amount, response.transactionSuccess(), response.errorMessage);
    }

    public boolean isValid() {
        return economyPlugin != null && economyPlugin.isEnabled();
    }

    public String getName() {
        return "Vault" + (economyPlugin == null ? "" : (" using " + economyPlugin.getName()));
    }

    public String currencyNameSingular() {
        return economyPlugin.currencyNameSingular();
    }

    public String currencyNamePlural() {
        return economyPlugin.currencyNamePlural();
    }

}
