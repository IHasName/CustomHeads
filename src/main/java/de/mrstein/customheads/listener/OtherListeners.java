package de.mrstein.customheads.listener;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.Updater;
import de.mrstein.customheads.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import static de.mrstein.customheads.utils.Utils.sendJSONMessage;


public class OtherListeners implements Listener {

    public static HashMap<Player, Location> saveLoc = new HashMap<>();

    @EventHandler
    public void fireworkbreak(BlockBreakEvent e) {
        if (saveLoc.containsValue(e.getBlock().getLocation().add(.5, 0, .5))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (saveLoc.containsKey(e.getPlayer())) {
            saveLoc.get(e.getPlayer()).getBlock().setType(Material.AIR);
            saveLoc.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void notifyUpdate(PlayerJoinEvent e) {
        new BukkitRunnable() {
            final Player player = e.getPlayer();

            public void run() {
                Object[] update = Updater.getLastUpdate();
                if (Utils.hasPermission(player, "heads.admin") && update.length == 5 && CustomHeads.getHeadsConfig().get().getBoolean("updateNotify")) {
                    sendJSONMessage("[\"\",{\"text\":\"§6-- CustomHeads Updater --\n§eFound new Update!\n§7Version: §e" + update[0] + "\n§7Whats new: §e" + update[1] + "\n\"},{\"text\":\"§6§nClick here to download the Update\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/29057\"}}]", player);
                }
            }
        }.runTaskLaterAsynchronously(CustomHeads.getInstance(), 10);
    }

}
