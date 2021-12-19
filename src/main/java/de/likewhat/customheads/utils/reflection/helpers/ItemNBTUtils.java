package de.likewhat.customheads.utils.reflection.helpers;

import de.likewhat.customheads.utils.reflection.helpers.collections.ReflectionMethodCollection;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class ItemNBTUtils {

    public static final Class<?> MINECRAFT_ITEMSTACK_CLASS;

    static {
        MINECRAFT_ITEMSTACK_CLASS = ReflectionUtils.getMCServerClassByName("ItemStack", "world.item");
    }

    public static Object asNMSCopy(ItemStack item) {
        try {
            return ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack asBukkitCopy(Object itemInstance) {
        try {
            return (ItemStack) ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", MINECRAFT_ITEMSTACK_CLASS).invoke(null, itemInstance);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasNBTTag(ItemStack item) {
        try {
            Object nmsCopy = ItemNBTUtils.asNMSCopy(item);
            if(nmsCopy != null) {
                return ReflectionMethodCollection.ITEMSTACK_HASTAG.invokeOn(nmsCopy);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to check NBT from Item", e);
        }
        return false;
    }

    public static Object getTagFromItem(ItemStack itemStack) {
        if(!hasNBTTag(itemStack)) {
            return NBTTagUtils.createInstance(NBTTagUtils.NBTType.COMPOUND);
        }
        try {
            Object nmsCopy = ItemNBTUtils.asNMSCopy(itemStack);
            return ReflectionMethodCollection.ITEMSTACK_GETTAG.invokeOn(nmsCopy);
        } catch(Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to get NBT from Item", e);
        }
        return null;
    }

    public static void setTagOnItem(Object item, Object nbt) {
        try {
            ReflectionMethodCollection.ITEMSTACK_SETTAG.invokeOn(item, nbt);
        } catch(Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to update NBT of Item", e);
        }
    }
}
