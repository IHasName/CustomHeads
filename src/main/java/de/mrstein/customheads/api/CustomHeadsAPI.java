package de.mrstein.customheads.api;

import com.mojang.authlib.GameProfile;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.headwriter.HeadFontType;
import de.mrstein.customheads.headwriter.HeadWriter;
import de.mrstein.customheads.reflection.TagEditor;
import de.mrstein.customheads.utils.GameProfileBuilder;
import de.mrstein.customheads.utils.Utils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

import static de.mrstein.customheads.utils.Utils.getClassbyName;

public class CustomHeadsAPI {

    private static Class<?> tileEntityClass, blockPositionClass;

    static {
        tileEntityClass = getClassbyName("TileEntitySkull");
        blockPositionClass = getClassbyName("BlockPosition");
    }

    /**
     * Sets an Skull with Custom Texture
     * @param block Block Location
     * @param texture Base64 Texture Value
     * @param facing Skull Rotation
     */
    public static boolean setSkullWithTexture(Block block, String texture, BlockFace facing) {
        block.setType(Material.AIR);
        block.setType(Material.SKULL);
        Skull s = (Skull) block.getState();
        s.setRotation(facing);
        s.setRawData((byte) 1);
        s.update();
        try {
            Object nmsWorld = block.getWorld().getClass().getMethod("getHandle").invoke(block.getWorld());
            tileEntityClass.getMethod("setGameProfile", GameProfile.class).invoke(tileEntityClass.cast(nmsWorld.getClass().getMethod("getTileEntity", blockPositionClass).invoke(nmsWorld, blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ()))), GameProfileBuilder.createProfileWithTexture(texture));
            return true;
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Error placing Skull", e);
        }
        return false;
    }

    /**
     * Gets an Alphabet or Number Skull
     * @param character Character
     * @param font What Font to get from
     * @return <b>CustomHead</b> with given Character Texture
     * @throws UnsupportedOperationException when Character doesn't exist
     */
    public static CustomHead getAlphabetHead(String character, HeadFontType font) {
        if(!font.getCharacters().containsKey(character.charAt(0)))
            throw new UnsupportedOperationException("Unsupported Character: '" + character.charAt(0) + "'");
        return font.getCharacter(character.charAt(0));
    }

    /**
     * Will write Text to the right from the Starting Location
     * @param text Text that will appear on the Skulls
     * @param location Starting Location
     * @param fontType Font Type
     * @throws UnsupportedOperationException when FontType does not support an Character
     */
    public static void writeText(String text, Location location, HeadFontType fontType) { new HeadWriter(fontType, text).writeAt(location); }

    /**
     * Will get the Texture from a Skull
     * @param itemStack
     * @return <b><code>Base64 Value</code></b> of the Texture
     * @throws NullPointerException if Item is null
     * @throws IllegalArgumentException if Item is not an Player Head
     * @see #getSkullTexture(Block)
     */
    public static String getSkullTexture(ItemStack itemStack) {
        Validate.notNull(itemStack, "Item cannot be null");
        if (!itemStack.getType().equals(Material.SKULL_ITEM) | !itemStack.getData().toString().equals("SKULL_ITEM(3)")) {
            throw new IllegalArgumentException("An PlayerHead is required to get the Texture");
        }
        try {
            Object nmscopy = TagEditor.getAsMNSCopy(itemStack);
            Object tag = nmscopy.getClass().getMethod("getTag").invoke(nmscopy);
            Object so = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "SkullOwner");
            Object prop = so.getClass().getMethod("getCompound", String.class).invoke(so, "Properties");
            Object tex = prop.getClass().getMethod("getList", String.class, int.class).invoke(prop, "textures", 10);
            Object list = tex.getClass().getMethod("get", int.class).invoke(tex, 0);
            return list.getClass().getMethod("getString", String.class).invoke(list, "Value").toString();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Something went wrong while getting the Texture of an Skull", e);
        }
        return null;
    }

    /**
     * Will get the Texture from a Skull Block
     * @param block
     * @return <b><code>Base64 Value</code></b> of the Texture
     * @throws NullPointerException if Block is null or no a Skull
     * @throws IllegalArgumentException if Block is not an Player Head
     * @see #getSkullTexture(ItemStack)
     */
    public static String getSkullTexture(Block block) {
        if (block.getType() != Material.SKULL) {
            throw new IllegalArgumentException("Block must be a Skull");
        }
        Skull s = (Skull) block.getState();
        if (s.getSkullType() != SkullType.PLAYER) {
            throw new IllegalArgumentException("Block must be a Player Skull");
        }
        try {
            Object nmsWorld = block.getWorld().getClass().getMethod("getHandle").invoke(block.getWorld());
            return Utils.getTextureFromProfile((GameProfile) tileEntityClass.getMethod("getGameProfile").invoke(tileEntityClass.cast(nmsWorld.getClass().getMethod("getTileEntity", blockPositionClass).invoke(nmsWorld, blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ())))));
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Error getting Texture from Skull", e);
        }
        return null;
    }

}
