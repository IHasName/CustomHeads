package de.mrstein.customheads.api;

/*
 *  Project: CustomHeads in CustomHeadsAPI
 *     by LikeWhat
 *
 *  created on 20.08.2018 at 22:48
 */

import de.mrstein.customheads.headwriter.HeadFontType;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

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

    /**
     * Will get the Texture from a Skull
     *
     * @param itemStack
     * @return <b><code>Base64 Value</code></b> of the Texture
     * @throws NullPointerException     if Item is null
     * @throws IllegalArgumentException if Item is not an Player Head
     * @see #getSkullTexture(Block)
     */
    String getSkullTexture(ItemStack itemStack);

    /**
     * Will get the Texture from a Skull Block
     *
     * @param block
     * @return <b><code>Base64 Value</code></b> of the Texture
     * @throws NullPointerException     if Block is null or no a Skull
     * @throws IllegalArgumentException if Block is not an Player Head
     * @see #getSkullTexture(ItemStack)
     */
    String getSkullTexture(Block block);

    /**
     * Gets an Character from an Font
     *
     * @param character Character
     * @param font      What Font to get from
     * @return <b>CustomHead</b> with given Character Texture
     * @throws UnsupportedOperationException when Character doesn't exist
     */
    ItemStack getAlphabetHead(String character, HeadFontType font);

    /**
     * Will write Text to the right from the Starting Location
     *
     * @param text     Text that will appear on the Skulls
     * @param location Starting Location
     * @param fontType Font Type
     * @throws UnsupportedOperationException when fontType is missing an Character
     */
    void writeText(String text, Location location, HeadFontType fontType);

    /**
     * Sets an Skull with Custom Texture
     *
     * @param block     Block Location
     * @param texture   Base64 Texture Value
     * @param blockFace Skull Rotation
     */
    void setSkull(Block block, String texture, BlockFace blockFace);

}
