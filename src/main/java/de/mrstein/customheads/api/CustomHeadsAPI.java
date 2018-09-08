package de.mrstein.customheads.api;

/*
 *  Project: CustomHeads in CustomHeadsAPI
 *     by LikeWhat
 *
 *  created on 20.08.2018 at 22:48
 */

import org.bukkit.OfflinePlayer;

/**
 * API Interface for CustomHeads
 *
 * @since 2.9.2
 */
public interface CustomHeadsAPI {

    /**
     * Wraps the Player for easier access to some Methods
     *
     * @param player Player to wrap (whether online or offline)
     * @return Wrapped Player Instance
     */
    CustomHeadsPlayer wrapPlayer(OfflinePlayer player);

}
