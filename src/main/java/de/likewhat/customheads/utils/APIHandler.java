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
import de.likewhat.customheads.utils.reflection.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.TagEditor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.logging.Level;

public class APIHandler implements CustomHeadsAPI {

    private static Class<?> tileEntitySkullClass, blockPositionClass;
    private static Constructor<?> blockPositionConstructor;
    private FireworkEffect.Type[] fxTypes = {FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR};

    static {
        tileEntitySkullClass = ReflectionUtils.getMCServerClassByName("TileEntitySkull", "world.level.block.entity");
        blockPositionClass = ReflectionUtils.getMCServerClassByName("BlockPosition", "core");
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
            if (ReflectionUtils.MC_VERSION > 12) {
                // Dont
                Object skullInstance = tileEntitySkullClass.getConstructor().newInstance();
                Object positionInstance = blockPositionConstructor.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                tileEntitySkullClass.getMethod("setLocation", ReflectionUtils.getMCServerClassByName("World", "world.level"), blockPositionClass).invoke(skullInstance, nmsWorld, positionInstance);

                Class<?> craftBlockDataClass = ReflectionUtils.getCBClass("block.data.CraftBlockData");
                Object blockDataState = craftBlockDataClass.getMethod("getState").invoke(craftBlockDataClass.cast(Material.class.getMethod("createBlockData").invoke(ReflectionUtils.getEnumConstant(Material.class, "player_head"))));

                nmsWorld.getClass().getMethod("setTypeAndData", blockPositionClass, ReflectionUtils.getMCServerClassByName("IBlockData", "world.level.block.state"), int.class).invoke(nmsWorld, positionInstance, blockDataState, 3);
                nmsWorld.getClass().getMethod("setTileEntity", blockPositionClass, ReflectionUtils.getMCServerClassByName("TileEntity", "world.level.block.entity")).invoke(nmsWorld, positionInstance, skullInstance);
                skull = (Skull) block.getState();
            } else {
                block.setType(Material.SKULL);
                skull = (Skull) block.getState();
                skull.setSkullType(SkullType.PLAYER);
                skull.setRawData((byte) 1);
            }

            skull.setRotation(blockFace);
            skull.update();
            setSkullTexture(block, texture);
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Error placing Skull", e);
        }
    }

    private void setSkullTexture(Block block, String texture) {
        try {
            Object nmsWorld = block.getWorld().getClass().getMethod("getHandle").invoke(block.getWorld());
            Object craftSkull = tileEntitySkullClass.cast(nmsWorld.getClass().getMethod("getTileEntity", blockPositionClass).invoke(nmsWorld, blockPositionConstructor.newInstance(block.getX(), block.getY(), block.getZ())));
            tileEntitySkullClass.getMethod("setGameProfile", GameProfile.class).invoke(craftSkull, GameProfileBuilder.createProfileWithTexture(texture));
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to set Skull Texture", e);
        }
    }

    public CustomHead getHead(String categoryId, int headId) {
        Category category = CustomHeads.getCategoryManager().getCategory(categoryId);
        if(category == null) {
            throw new NullPointerException("Unknown Category ID: " + categoryId);
        }
        return category.getHeads().stream().filter(customHead -> customHead.getId() == headId).findFirst().orElse(null);
    }

    public CustomHead getHead(Category category, int headId) {
        return category.getHeads().stream().filter(customHead -> customHead.getId() == headId).findFirst().orElse(null);
    }

    // API Impl
    public CustomHeadsPlayer wrapPlayer(OfflinePlayer player) {
        return PlayerWrapper.wrapPlayer(player);
    }

    public void createFireworkBattery(Location location, int shots, int delay) {
        createFireworkBattery(location, shots, delay, new FireworksBatteryHandler() {
            public void onStart() {}
            public void onNext() {}
            public void onEnd() {}
        });
    }

    public void createFireworkBattery(Location location, int shots, int delay, FireworksBatteryHandler handler) {
        Random random = new Random();

        handler.onStart();
        new BukkitRunnable() {
            int counter = shots;

            public void run() {
                if (counter == 0) {
                    handler.onEnd();
                    cancel();
                    return;
                }
                Firework f = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                FireworkMeta fm = f.getFireworkMeta();
                FireworkEffect.Builder fx = FireworkEffect.builder();
                fx.flicker(random.nextBoolean()).trail(random.nextBoolean()).with(fxTypes[random.nextInt(fxTypes.length)]);
                int c = random.nextInt(2) + 2;
                for (int i = 0; i < c; i++) {
                    fx.withColor(Color.fromRGB(random.nextInt(200) + 50, random.nextInt(200) + 50, random.nextInt(200) + 50));
                    if (random.nextBoolean()) {
                        fx.withFade(Color.fromRGB(random.nextInt(200) + 50, random.nextInt(200) + 50, random.nextInt(200) + 50));
                    }
                }
                fm.addEffect(fx.build());
                fm.setPower(random.nextInt(2) + 1);
                f.setFireworkMeta(fm);
                f.setVelocity(new Vector(random.nextDouble() * (random.nextBoolean() ? .01 : -.01), .2, random.nextDouble() * (random.nextBoolean() ? .01 : -.01)));
                handler.onNext();
                counter--;
            }
        }.runTaskTimer(CustomHeads.getInstance(), 10, delay);
    }
}
