package de.likewhat.customheads.economy;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.economy.errors.InvalidEconomyHandlerException;
import de.likewhat.customheads.economy.handlers.Vault;
import de.likewhat.customheads.utils.SimpleCallback;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static de.likewhat.customheads.utils.Utils.openCategory;

/*
 *  Project: CustomHeads in EconomyManager
 *     by LikeWhat
 */

@Getter
public class EconomyManager {

    private final HashMap<String, EconomyHandler> registeredHandlers = new HashMap<>();
    private EconomyHandler activeEconomyHandler;

    public EconomyManager() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            try {
                registerAndSetActiveHandler(new Vault());
            } catch(InvalidEconomyHandlerException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean setActiveHandler(String handlerName) {
        if(registeredHandlers.containsKey(handlerName) || activeEconomyHandler != registeredHandlers.get(handlerName)) {
            EconomyHandler handler = registeredHandlers.get(handlerName);
            checkValidHandler(handler);
            activeEconomyHandler = handler;
            return true;
        }
        return false;
    }

    public boolean registerHandler(String handlerName, EconomyHandler handler) throws InvalidEconomyHandlerException {
        if(!handler.isValid()) {
            throw new InvalidEconomyHandlerException("Invalid Handler passed as Argument");
        }
        return registeredHandlers.putIfAbsent(handlerName, handler) == null;
    }

    public boolean registerAndSetActiveHandler(EconomyHandler handler) throws InvalidEconomyHandlerException {
        if (registerHandler(handler.getName(), handler)) {
            return setActiveHandler(handler.getName());
        }
        return false;
    }

    private void checkValidHandler(EconomyHandler handler) {
        if(!handler.isValid()) {
            throw new IllegalStateException("Current Economy Handler (" + handler.getName() + ") is invalid ");
        }
    }

    private void checkValidHandler() {
        checkValidHandler(activeEconomyHandler);
    }

    public void buyCategory(CustomHeadsPlayer customHeadsPlayer, Category category, String prevMenu) {
        checkValidHandler();
        //EconomyResponse economyResponse = economyPlugin.withdrawPlayer(customHeadsPlayer.unwrap(), category.getPrice());
        EconomyCallback callback = activeEconomyHandler.handleWithdraw(customHeadsPlayer.unwrap(), category.getPrice());
        if (callback.wasSuccess()) {
            customHeadsPlayer.unlockCategory(category);
            openCategory(category, customHeadsPlayer.unwrap(), new String[]{"openMenu", prevMenu});
            customHeadsPlayer.unwrap().sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_SUCCESSFUL.replace("{ITEM}", category.getPlainName()));
        } else {
            customHeadsPlayer.unwrap().sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_FAILED.replace("{REASON}", callback.getResponseMessage()).replace("{ITEM}", category.getPlainName()));
        }
    }

    public void buyHead(CustomHeadsPlayer customHeadsPlayer, Category category, int id, boolean permanent) {
        checkValidHandler();
        CustomHead customHead = CustomHeads.getApi().getHead(category, id);
        if (customHead == null) {
            CustomHeads.getInstance().getLogger().warning("Received invalid Head ID while buying (" + category.getId() + ":" + id + ")");
            return;
        }
        EconomyCallback callback = activeEconomyHandler.handleWithdraw(customHeadsPlayer.unwrap(), customHead.getPrice());
        //EconomyResponse economyResponse = economyPlugin.withdrawPlayer(customHeadsPlayer.unwrap(), customHead.getPrice());
        if (callback.wasSuccess()) {
            if (permanent) {
                customHeadsPlayer.unlockHead(category, id);
            } else {
                customHeadsPlayer.unwrap().getInventory().addItem(customHead.getPlainItem());
            }
            customHeadsPlayer.unwrap().sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_SUCCESSFUL.replace("{ITEM}", customHead.getItemMeta().getDisplayName()));
        } else {
            customHeadsPlayer.unwrap().sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_FAILED.replace("{REASON}", callback.getResponseMessage()).replace("{ITEM}", customHead.getItemMeta().getDisplayName()));
        }
    }

    public void buyItem(Player player, int price, String itemName, SimpleCallback<Boolean> successCallback) {
        checkValidHandler();
//        EconomyResponse economyResponse = economyPlugin.withdrawPlayer(player, price);
        EconomyCallback callback = activeEconomyHandler.handleWithdraw(player, price);
        if (callback.wasSuccess()) {
            player.sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_SUCCESSFUL.replace("{ITEM}", itemName));
            successCallback.call(true);
        } else {
            player.sendMessage(CustomHeads.getLanguageManager().ECONOMY_BUY_FAILED.replace("{REASON}", callback.getResponseMessage()).replace("{ITEM}", itemName));
            successCallback.call(false);
        }
    }

}
