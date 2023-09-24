package de.likewhat.customheads.api;

/*
 *  Project: CustomHeads in CustomHeadsAPI
 *     by LikeWhat
 *
 *  created on 20.08.2018 at 22:48
 */

import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.headwriter.HeadFontType;
import de.likewhat.customheads.utils.FireworksBatteryHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * API Interface for CustomHeads
 *
 * @since 2.9.2
 * @version 1.1
 */
public interface CustomHeadsAPI {

    /**
     * Wraps the Player for easier access to some Methods
     *
     * @param player Player to wrap (whether online or offline)
     * @return Wrapped Player Instance
     */
    CustomHeadsPlayer wrapPlayer(Player player);

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
     * @throws UnsupportedOperationException when given Character doesn't exist
     */
    ItemStack getAlphabetHead(String character, HeadFontType font);

    /**
     * Will write Text to the right from the Starting Location
     *
     * @param text     Text that will appear on the Skulls
     * @param location Starting Location
     * @param fontType Font Type
     * @throws UnsupportedOperationException when the Font is mising an Character from the text
     */
    void writeText(String text, Location location, HeadFontType fontType);

    /**
     * Sets a Skull with Custom Texture
     *
     * @param block     Block Location
     * @param texture   Base64 Texture Value
     * @param blockFace Skull Rotation
     */
    void setSkull(Block block, String texture, BlockFace blockFace);

    /**
     * Will return a Head from the given ID.
     * Might be null if the Head doesn't exist
     *
     * @param categoryId from Which Category to pull from
     * @param headId     Head ID
     * @return Head from Database
     */
    CustomHead getHead(Category categoryId, int headId);

    /**
     * Will return a Head from the given ID.
     * Might be null if the Head doesn't exist
     *
     * @param categoryId from Which Category to pull from
     * @param headId     Head ID
     * @return Head from Database
     */
    CustomHead getHead(String categoryId, int headId);

    /**
     * Starts a Firework in the world
     * @deprecated Will be replaced with {@link CustomHeadsAPI#createDefaultFireworkBattery}
     * @param location Location where to start the Firework
     * @param shots Sets how many shots the Battery has
     * @param delay Delay between the shots (Ticks)
     */
    @Deprecated
    void createFireworkBattery(Location location, int shots, int delay);

    /**
     * Starts a Firework in the world with Default Settings
     *
     * @param location Location where to start the Firework
     * @param shots Sets how many shots the Battery has
     * @param delay Delay between the shots (Ticks)
     */
    void createDefaultFireworkBattery(Location location, int shots, int delay);

    /**
     * Starts a Firework in the world
     * Has a Callback
     * @see FireworksBatteryHandler
     *
     * @param location Location where to start the Firework
     * @param shots Sets how many shots the Battery has
     * @param delay Delay between the shots (Ticks)
     * @param handler Contains callables for when the Firework starts, ends and when the next Firework is launched
     */
    void createFireworkBattery(Location location, int shots, int delay, FireworksBatteryHandler handler);

}
