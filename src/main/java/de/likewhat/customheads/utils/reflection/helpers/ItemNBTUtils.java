package de.likewhat.customheads.utils.reflection.helpers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.collections.ReflectionClassCollection;
import de.likewhat.customheads.utils.reflection.helpers.collections.ReflectionMethodCollection;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class ItemNBTUtils {

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
            return (ItemStack) ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", ReflectionClassCollection.MINECRAFT_ITEMSTACK_CLASS.resolve()).invoke(null, itemInstance);
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
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to check NBT from Item", e);
        }
        return false;
    }

    public static Object getTagFromItem(ItemStack itemStack) {
        return getTagFromItem(itemStack, false);
    }

    public static Object getTagFromItem(ItemStack itemStack, boolean force) {
        if(!force && !hasNBTTag(itemStack)) {
            return NBTTagUtils.createInstance(NBTTagUtils.NBTType.COMPOUND);
        }
        try {
            Object nmsCopy = ItemNBTUtils.asNMSCopy(itemStack);
            return ReflectionMethodCollection.ITEMSTACK_GETTAG.invokeOn(nmsCopy);
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get NBT from Item", e);
        }
        return null;
    }

    public static void setTagOnItem(Object item, Object nbt) {
        try {
            ReflectionMethodCollection.ITEMSTACK_SETTAG.invokeOn(item, nbt);

            // Verify NBT Set
            Object tagToVerify = ReflectionMethodCollection.ITEMSTACK_GETTAG.invokeOn(item);
            if(!tagToVerify.equals(nbt)) {
                throw new InvocationTargetException(new Exception("[TagEditor] Verify: Set Tag does not match"));
            }
        } catch(Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to update NBT of Item", e);
        }
    }
}
