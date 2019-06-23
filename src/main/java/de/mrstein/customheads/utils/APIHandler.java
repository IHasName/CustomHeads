package de.mrstein.customheads.utils;

/*
 *  Project: CustomHeads in APIHandler
 *     by LikeWhat
 *
 *  created on 22.08.2018 at 22:12
 */

import com.mojang.authlib.GameProfile;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHeadsAPI;
import de.mrstein.customheads.api.CustomHeadsPlayer;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.CustomHead;
import de.mrstein.customheads.headwriter.HeadFontType;
import de.mrstein.customheads.headwriter.HeadWriter;
import de.mrstein.customheads.reflection.TagEditor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.logging.Level;

public class APIHandler implements CustomHeadsAPI {

    private static Class<?> tileEntityClass, blockPositionClass;

    static {
        tileEntityClass = Utils.getClassbyName("TileEntitySkull");
        blockPositionClass = Utils.getClassbyName("BlockPosition");
    }

    // Head Util Impl
    public String getSkullTexture(ItemStack itemStack) {
        if(itemStack == null) {
            throw new NullPointerException("Item cannot be null");
        }
        if (!itemStack.getData().toString().contains("SKULL_ITEM(3)")) {
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

    public String getSkullTexture(Block block) {
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

    public ItemStack getAlphabetHead(String character, HeadFontType font) {
        if (!font.getCharacters().containsKey(character.charAt(0)))
            throw new UnsupportedOperationException("Unsupported Character: '" + character.charAt(0) + "'");
        return font.getCharacter(character.charAt(0));
    }

    public void writeText(String text, Location location, HeadFontType fontType) {
        new HeadWriter(fontType, text).writeAt(location);
    }

    public void setSkull(Block block, String texture, BlockFace blockFace) {
        block.setType(Material.AIR);
        block.setType(Material.SKULL);
        Skull s = (Skull) block.getState();
        s.setRotation(blockFace);
        s.setRawData((byte) 1);
        s.update();
        try {
            Object nmsWorld = block.getWorld().getClass().getMethod("getHandle").invoke(block.getWorld());
            tileEntityClass.getMethod("setGameProfile", GameProfile.class).invoke(tileEntityClass.cast(nmsWorld.getClass().getMethod("getTileEntity", blockPositionClass).invoke(nmsWorld, blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ()))), GameProfileBuilder.createProfileWithTexture(texture));
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Error placing Skull", e);
        }
    }

    public CustomHead getHead(Category category, int id) {
        Iterator<CustomHead> headIterator = category.getHeads().stream().filter(customHead -> customHead.getId() == id).iterator();
        return headIterator.hasNext() ? headIterator.next() : null;
    }

    // API Impl
    public CustomHeadsPlayer wrapPlayer(OfflinePlayer player) {
        return PlayerWrapper.wrapPlayer(player);
    }

}
