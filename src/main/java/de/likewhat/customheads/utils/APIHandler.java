package de.likewhat.customheads.utils;

/*
 *  Project: CustomHeads in APIHandler
 *     by LikeWhat
 *
 *  created on 22.08.2018 at 22:12
 */

import com.mojang.authlib.GameProfile;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsAPI;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.headwriter.HeadFontType;
import de.likewhat.customheads.headwriter.HeadWriter;
import de.likewhat.customheads.utils.reflection.NBTTagUtils;
import de.likewhat.customheads.utils.reflection.TagEditor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

public class APIHandler implements CustomHeadsAPI {

    private static Class<?> tileEntitySkullClass, blockPositionClass;
    private static Constructor<?> blockPositionConstructor;

    static {
        tileEntitySkullClass = Utils.getMCServerClassByName("TileEntitySkull");
        blockPositionClass = Utils.getMCServerClassByName("BlockPosition");
        try {
            blockPositionConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Head Util Impl
    public String getSkullTexture(ItemStack itemStack) {
        if(itemStack == null) {
            throw new NullPointerException("Item cannot be null");
        }
        try {
            Object nMSCopy = TagEditor.getAsMNSCopy(itemStack);
            Object tag = nMSCopy.getClass().getMethod("getTag").invoke(nMSCopy);
            Object skullOwner = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "SkullOwner");
            Object properties = skullOwner.getClass().getMethod("getCompound", String.class).invoke(skullOwner, "Properties");
            Object textures = properties.getClass().getMethod("getList", String.class, int.class).invoke(properties, "textures", 10);
            Object list = textures.getClass().getMethod("get", int.class).invoke(textures, 0);
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
            return Utils.getTextureFromProfile((GameProfile) tileEntitySkullClass.getMethod("getGameProfile").invoke(tileEntitySkullClass.cast(nmsWorld.getClass().getMethod("getTileEntity", blockPositionClass).invoke(nmsWorld, blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ())))));
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
        try {
            block.setType(Material.AIR);
            Skull skull;
            Location location = block.getLocation();
            Object nmsWorld = block.getWorld().getClass().getMethod("getHandle").invoke(block.getWorld());
            if (NBTTagUtils.MC_VERSION > 12) {
                // Dont
                Object skullInstance = tileEntitySkullClass.getConstructor().newInstance();
                Object positionInstance = blockPositionConstructor.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                tileEntitySkullClass.getMethod("setLocation", Utils.getMCServerClassByName("World"), blockPositionClass).invoke(skullInstance, nmsWorld, positionInstance);

                Class<?> craftBlockDataClass = Utils.getCBClass("block.data.CraftBlockData");
                Object blockDataState = craftBlockDataClass.getMethod("getState").invoke(craftBlockDataClass.cast(Material.class.getMethod("createBlockData").invoke(NMSUtils.getEnumFromClass(Material.class, "player_head"))));

                nmsWorld.getClass().getMethod("setTypeAndData", blockPositionClass, Utils.getMCServerClassByName("IBlockData"), int.class).invoke(nmsWorld, positionInstance, blockDataState, 3);
                nmsWorld.getClass().getMethod("setTileEntity", blockPositionClass, Utils.getMCServerClassByName("TileEntity")).invoke(nmsWorld, positionInstance, skullInstance);
                skull = (Skull) block.getState();
            } else {
                block.setType(Material.SKULL);
                skull = (Skull) block.getState();
                skull.setSkullType(SkullType.PLAYER);
            }
            tileEntitySkullClass.getMethod("setGameProfile", GameProfile.class).invoke(tileEntitySkullClass.cast(nmsWorld.getClass().getMethod("getTileEntity", blockPositionClass).invoke(nmsWorld, blockPositionConstructor.newInstance(block.getX(), block.getY(), block.getZ()))), GameProfileBuilder.createProfileWithTexture(texture));
            skull.setRotation(blockFace);
            skull.update();
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Error placing Skull", e);
        }
    }

    public CustomHead getHead(Category category, int id) {
        return category.getHeads().stream().filter(customHead -> customHead.getId() == id).findFirst().orElse(null);
    }

    // API Impl
    public CustomHeadsPlayer wrapPlayer(OfflinePlayer player) {
        return PlayerWrapper.wrapPlayer(player);
    }

}
