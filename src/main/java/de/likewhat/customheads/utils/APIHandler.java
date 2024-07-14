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
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.ClassWrappers;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.ConstructorWrappers;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.MethodWrappers;
import de.likewhat.customheads.utils.reflection.nbt.ItemNBTUtils;
import de.likewhat.customheads.utils.reflection.nbt.NBTType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.logging.Level;

public class APIHandler implements CustomHeadsAPI {

    private static final Class<?> tileEntitySkullClass;
    private static final Class<?> blockPositionClass;
    private static final Constructor<?> blockPositionConstructor;
    private static final FireworkEffect.Type[] FX_TYPES = {FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR};

    static {
        tileEntitySkullClass = ClassWrappers.TILE_ENTITY_SKULL.resolve();
        blockPositionClass = ClassWrappers.BLOCK_POSITION.resolve();
        blockPositionConstructor = ConstructorWrappers.MINECRAFT_BLOCK_POSITION.resolve();
    }

    // Head Util Impl
    public String getSkullTexture(ItemStack itemStack) {
        if(itemStack == null) {
            throw new NullPointerException("Item cannot be null");
        }
        try {
            return ItemNBTUtils.getTagFromItem(itemStack)
                    .getCompound("SkullOwner")
                    .getCompound("Properties")
                    .getList("textures", NBTType.COMPOUND)
                    .get(0)
                    .asCompound()
                    .getString("Value");
//            Object tag = ItemNBTUtils.getTagFromItem(itemStack);
//            Object skullOwner = NBTTagCompoundWrapper.GET_COMPOUND.invokeOn(tag, "SkullOwner");
//            Object properties = NBTTagCompoundWrapper.GET_COMPOUND.invokeOn(skullOwner, "Properties");
//            Object textures = NBTTagCompoundWrapper.GET_LIST.invokeOn(properties, "textures", NBTType.COMPOUND.getId());
//            Object list = NBTTagListWrapper.GET.invokeOn(textures, 0);
//            return NBTTagCompoundWrapper.GET_STRING.invokeOn(list, "Value");
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Something went wrong while getting the Texture of an Skull", e);
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
            Object nmsWorld = ReflectionUtils.getWorldHandle(block.getWorld());
            return Utils.getTextureFromProfile((GameProfile) tileEntitySkullClass.getMethod("getGameProfile").invoke(tileEntitySkullClass.cast(ClassWrappers.WORLD.resolve().getMethod("getTileEntity", blockPositionClass).invoke(nmsWorld, blockPositionConstructor.newInstance(block.getX(), block.getY(), block.getZ())))));
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Error getting Texture from Skull", e);
        }
        return null;
    }

    public ItemStack getAlphabetHead(String character, HeadFontType font) {
        if (!font.getCharacterItems().containsKey(character.charAt(0)))
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
        Object nmsWorld = ReflectionUtils.getWorldHandle(block.getWorld());
        switch (Version.getCurrentVersion()) {
            case V1_8_R1:
            case V1_8_R2:
            case V1_8_R3:
            case V1_9_R1:
            case V1_9_R2:
            case V1_10_R1:
            case V1_11_R1:
            case V1_12_R1:
                block.setType(Material.SKULL);
                skull = (Skull) block.getState();
                skull.setSkullType(SkullType.PLAYER);
                skull.setRawData((byte) 1);
                break;
            default:
                LoggingUtils.logOnce(Level.WARNING, "Falling back to newest Method since the current Version hasn't been tested yet... (This may not work so here goes)");
            case V1_13_R1:
            case V1_13_R2:
            case V1_14_R1:
            case V1_15_R1:
            case V1_16_R1:
            case V1_16_R2:
            case V1_16_R3:
            case V1_17_R1:
            case V1_18_R1:
            case V1_18_R2:
            case V1_19_R1:
            case V1_19_R2:
            case V1_19_R3:
            case V1_20_R1:
                Class<?> craftBlockDataClass = ClassWrappers.CRAFTBUKKIT_CRAFT_BLOCK_DATA.resolve();
                Object blockDataInstance = craftBlockDataClass.cast(Material.class.getMethod("createBlockData").invoke(ReflectionUtils.getEnumConstant(Material.class, "PLAYER_HEAD")));
                Object blockDataState = craftBlockDataClass.getMethod("getState").invoke(blockDataInstance);
                Object positionInstance = blockPositionConstructor.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                MethodWrappers.WORLD_SET_TYPE_AND_DATA.invokeOn(nmsWorld, positionInstance, blockDataState, 3);
                //nmsWorld.getClass().getMethod("setTypeAndData", blockPositionClass, ReflectionUtils.getMCServerClassByName("IBlockData", "world.level.block.state"), int.class).invoke(nmsWorld, positionInstance, blockDataState, 3);

                // Actually Update the Skull now
                switch(Version.getCurrentVersion()) {
                    case V1_13_R1:
                    case V1_13_R2:
                    case V1_14_R1:
                    case V1_15_R1:
                    case V1_16_R1:
                    case V1_16_R2:
                    case V1_16_R3: {
                        Object skullInstance = tileEntitySkullClass.getConstructor().newInstance();
                        tileEntitySkullClass.getMethod("setLocation", ReflectionUtils.getMCServerClassByName("World", "world.level"), blockPositionClass).invoke(skullInstance, nmsWorld, positionInstance);
                        nmsWorld.getClass().getMethod("setTileEntity", blockPositionClass, ReflectionUtils.getMCServerClassByName("TileEntity", "world.level.block.entity")).invoke(nmsWorld, positionInstance, skullInstance);
                        break;
                    }
                    case V1_17_R1:
                    case V1_18_R1:
                    case V1_18_R2:
                    case V1_19_R1:
                    case V1_19_R2:
                    case V1_19_R3:
                    case V1_20_R1: {
                        Object skullInstance = tileEntitySkullClass.getConstructor(blockPositionClass, ReflectionUtils.getMCServerClassByName("IBlockData", "world.level.block.state")).newInstance(positionInstance, blockDataState);
                        MethodWrappers.WORLD_SET_TILE_ENTITY.invokeOn(nmsWorld, skullInstance);
                        //nmsWorld.getClass().getMethod("setTileEntity", ReflectionUtils.getMCServerClassByName("TileEntity", "world.level.block.entity")).invoke(nmsWorld, skullInstance);
                        break;
                    }
                }
                skull = (Skull) block.getState();
                break;
        }
        skull.setRotation(blockFace);
        skull.update();
        setSkullTexture(block, texture);
    } catch (Exception e) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Error placing Skull", e);
    }
}

    private void setSkullTexture(Block block, String texture) {
        try {
            Object nmsWorld = ReflectionUtils.getWorldHandle(block.getWorld());
            Object craftSkull = tileEntitySkullClass.cast(MethodWrappers.WORLD_GET_TILE_ENTITY.invokeOn(nmsWorld, blockPositionConstructor.newInstance(block.getX(), block.getY(), block.getZ())));
            MethodWrappers.TILE_ENTITY_SET_GAME_PROFILE.invokeOn(craftSkull, GameProfileBuilder.createProfileWithTexture(texture));
            //tileEntitySkullClass.getMethod("setGameProfile", GameProfile.class).invoke(craftSkull, GameProfileBuilder.createProfileWithTexture(texture));
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to set Skull Texture", e);
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
    public CustomHeadsPlayer wrapPlayer(Player player) {
        return PlayerWrapper.wrapPlayer(player);
    }

    public void createFireworkBattery(Location location, int shots, int delay) {
        createDefaultFireworkBattery(location, shots, delay);
    }

    public void createDefaultFireworkBattery(Location location, int shots, int delay) {
        Random random = new Random();
        createFireworkBattery(location, shots, delay, new FireworksBatteryHandler() {
            public void onStart() {}
            public void onNext() {
                Firework f = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                FireworkMeta fm = f.getFireworkMeta();
                FireworkEffect.Builder fx = FireworkEffect.builder();
                fx.flicker(random.nextBoolean()).trail(random.nextBoolean()).with(FX_TYPES[random.nextInt(FX_TYPES.length)]);
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
            }
            public void onEnd() {}
        });
    }

    public void createFireworkBattery(Location location, int shots, int delay, FireworksBatteryHandler handler) {
        handler.onStart();
        new BukkitRunnable() {
            int counter = shots;
            public void run() {
                if (counter == 0) {
                    handler.onEnd();
                    cancel();
                    return;
                }
                handler.onNext();
                counter--;
            }
        }.runTaskTimer(CustomHeads.getInstance(), 10, delay);
    }
}
