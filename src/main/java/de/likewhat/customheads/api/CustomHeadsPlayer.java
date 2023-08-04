package de.likewhat.customheads.api;

/*
 *  Project: CustomHeads in CustomHeadsPlayer
 *     by LikeWhat
 *
 *  created on 20.08.2018 at 17:14
 */

import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.utils.history.GetHistory;
import de.likewhat.customheads.utils.history.SearchHistory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Wrapped Player for easier access
 *
 * @since 2.9.2
 */
public interface CustomHeadsPlayer {

    /**
     * Returns a List of the Categories the Player has unlocked
     *
     * @return List of Categories
     * @throws NullPointerException when Player is Offline and <code>ignorePermission</code> is true
     */
    List<Category> getUnlockedCategories(boolean ignorePermission);

    /**
     * Unlocks a Category for the Player
     *
     * @param category Category that should be unlocked
     * @return Whether the Category could be unlocked
     */
    boolean unlockCategory(Category category);

    /**
     * Locks a Category for a Player (does not apply when the Player has an bypass Permission)
     *
     * @param category Category that should be locked
     * @return Whether the Category could be locked or not
     */
    boolean lockCategory(Category category);

    /**
     * Returns a List of Heads (Itemstacks) the Player has unlocked
     *
     * @return List of unlocked Heads
     */
    List<CustomHead> getUnlockedHeads();

    /**
     * Unlocks a Head from the Given ID
     *
     * @param category Which Category to pull from
     * @param id ID of the Head
     * @return false when the ID doesn't exist
     */
    boolean unlockHead(Category category, int id);

    /**
     * Locks a Head from the Given ID
     *
     * @param category Which Category to pull from
     * @param id ID of the Head
     * @return false when the ID doesn't exist
     */
    boolean lockHead(Category category, int id);

    /**
     * Returns a List of the Heads a Player saved
     *
     * @return List of ItemStacks
     */
    List<ItemStack> getSavedHeads();

    /**
     * Save a Head to the Players Collection
     *
     * @param name    Name as what the Head should be saved as
     * @param texture Texture of the Head to save (can be Playername or Texture)
     * @return true or false whether the Head could be saved or not
     */
    boolean saveHead(String name, String texture);

    /**
     * Returns the specified Head from the Players Collection
     *
     * @param name Name of the saved Head
     * @return ItemStack of the saved Head
     */
    ItemStack getHead(String name);

    /**
     * Remove a Head from the Players Collection
     *
     * @param name Name of the saved Head
     * @return Whether the Head was removed
     */
    boolean deleteHead(String name);

    /**
     * Check if a given Head was saved by the Player
     *
     * @param name Name of the saved Head
     * @return Whether the Player has saved the given Head
     */
    boolean hasHead(String name);

    /**
     * Unwraps the Player
     *
     * @return Player Instance that got wrapped or <code>null</code> when the Player is offline
     */
    Player unwrap();

    SearchHistory getSearchHistory();

    GetHistory getGetHistory();
}
