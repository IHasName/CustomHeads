package de.likewhat.customheads.listener;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.command.CustomHeadsCommand;
import de.likewhat.customheads.utils.Utils;
import de.likewhat.customheads.utils.updaters.SpigetResourceFetcher;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static de.likewhat.customheads.utils.Utils.sendJSONMessage;

/*
 *  Project: CustomHeads in OtherListeners
 *     by LikeWhat
 */

public class OtherListeners implements Listener {

//    public static final HashMap<UUID, CategorySetup> CACHED_CATEGORYSETUPS = new HashMap<>();

    @EventHandler
    public void onFireworkBreak(BlockBreakEvent event) {
        if (CustomHeadsCommand.CACHED_FIREWORKS.contains(event.getBlock().getLocation().add(.5, .5, .5))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void notifyUpdate(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (Utils.hasPermission(player, "heads.admin") && CustomHeads.getHeadsConfig().get().getBoolean("update-notifications.onJoin")) {
            CustomHeads.getSpigetFetcher().fetchUpdates(new SpigetResourceFetcher.FetchResult() {
                public void updateAvailable(SpigetResourceFetcher.ResourceRelease release, SpigetResourceFetcher.ResourceUpdate update) {
                    sendJSONMessage("[\"\",{\"text\":\"§6-- CustomHeads Updater --\n§eFound new Update!\n§7Version: §e" + release.getReleaseName() + "\n§7Whats new: §e" + update.getTitle() + "\n\"},{\"text\":\"§6§nClick here to download the Update\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/29057\"}}]", player);
                }

                public void noUpdate() {
                }
            });
        }
    }

    /*
    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(CACHED_CATEGORYSETUPS.containsKey(player.getUniqueId())) {
            CategorySetup setup = CACHED_CATEGORYSETUPS.get(player.getUniqueId());
            // Not implemented yet
        }
    }
    */
}
