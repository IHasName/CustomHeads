package de.likewhat.customheads.utils.reflection.nbt;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.MethodWrappers;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTTagCompoundWrapper;
import de.likewhat.customheads.utils.reflection.nbt.errors.NBTException;
import de.likewhat.customheads.utils.reflection.nbt.errors.NBTVerifyException;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class ItemNBTUtils {

    public static Object asNMSCopy(ItemStack item) {
        try {
            return MethodWrappers.CRAFTBUKKIT_ITEMSTACK_AS_NMS_COPY.invokeOn(null, item);
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to create NMS Copy of Item", e);
        }
        return null;
    }

    public static ItemStack asBukkitCopy(Object nmsItem) {
        try {
            return MethodWrappers.CRAFTBUKKIT_ITEMSTACK_AS_BUKKIT_COPY.invokeOn(null, nmsItem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to create Bukkit Copy of NMS Item", e);
        }
        return null;
    }

    public static boolean hasNBTTag(ItemStack item) {
        try {
            Object nmsCopy = ItemNBTUtils.asNMSCopy(item);
            if(nmsCopy != null) {
                return MethodWrappers.ITEMSTACK_HASTAG.invokeOn(nmsCopy);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to check NBT on Item", e);
        }
        return false;
    }

    public static NBTTagCompoundWrapper getTagFromItem(ItemStack itemStack) throws NBTException {
        return getTagFromItem(itemStack, false);
    }

    public static NBTTagCompoundWrapper getTagFromItem(ItemStack itemStack, boolean force) throws NBTException {
        if(!force && !hasNBTTag(itemStack)) {
            return new NBTTagCompoundWrapper();
        }
        try {
            Object nmsCopy = ItemNBTUtils.asNMSCopy(itemStack);
            return NBTTagCompoundWrapper.of(MethodWrappers.ITEMSTACK_GETTAG.invokeOn(nmsCopy));
        } catch(InvocationTargetException | IllegalAccessException e) {
            throw new NBTException(e);
        }
    }

    public static ItemStack setTagOnItem(ItemStack itemStack, Object nbt) throws NBTException, NBTVerifyException {
        Object nmsCopy = asNMSCopy(itemStack);
        setTagOnItem(nmsCopy, nbt);
        return asBukkitCopy(nmsCopy);
    }

    public static void setTagOnItem(Object nmsItem, Object nbt) throws NBTException, NBTVerifyException  {
        try {
            MethodWrappers.ITEMSTACK_SETTAG.invokeOn(nmsItem, nbt);

            // Verify NBT Set
            Object tagToVerify = MethodWrappers.ITEMSTACK_GETTAG.invokeOn(nmsItem);
            if (!tagToVerify.equals(nbt)) {
                throw new NBTVerifyException("Failed to verify NBT");
            }
        } catch(InvocationTargetException | IllegalAccessException e) {
            throw new NBTException(e);
        }
    }
}
