package de.likewhat.customheads.economy;

import org.bukkit.entity.Player;

public interface EconomyHandler {

    /**
     * Returns the Response of the Transaction Handler
     * @param player Target Player who is paying
     * @param amount The Amount to be deducted
     * @return Response of the Transaction Handler
     */
    EconomyCallback handleWithdraw(Player player, double amount);

    /**
     * Whether the Plugin/Handler is currently able to handle incoming Economy Requests
     * Example: {@link de.likewhat.customheads.economy.handlers.VaultHandler}
     * @return true/false
     */
    boolean isValid();

    /**
     * @return The Name of the Handler
     */
    String getName();

    String currencyNameSingular();

    String currencyNamePlural();
}
