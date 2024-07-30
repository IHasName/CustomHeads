package de.likewhat.customheads.utils.reflection.nbt;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.DataComponentTypes;
import de.likewhat.customheads.utils.reflection.helpers.Version;
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
        return nmsItemHasTag(ItemNBTUtils.asNMSCopy(item));
    }

    private static boolean nmsItemHasTag(Object nmsItem) {
        try {
            if(nmsItem != null) {
                if(Version.getCurrentVersion().isNewerThan(Version.V1_20_R3)) {
                    return MethodWrappers.ITEMSTACK_HASTAG.invokeOn(nmsItem);
                } else {
                    return MethodWrappers.ITEMSTACK_DATA_HAS.invokeOn(nmsItem, DataComponentTypes.CUSTOM_DATA.getInstance());
                }
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
        return getTagFromNMSItem(ItemNBTUtils.asNMSCopy(itemStack), force);
    }

    private static NBTTagCompoundWrapper getTagFromNMSItem(Object nmsItem, boolean force) throws NBTException {
        if(!force && !nmsItemHasTag(nmsItem)) {
            return new NBTTagCompoundWrapper();
        }
        try {
            Object tag;
            if(Version.getCurrentVersion().isNewerThan(Version.V1_20_R3)) {
                tag = MethodWrappers.ITEMSTACK_GETTAG.invokeOn(nmsItem);
            } else {
                Object customData = MethodWrappers.ITEMSTACK_DATA_GET.invokeOn(nmsItem, DataComponentTypes.CUSTOM_DATA.getInstance());
                tag = MethodWrappers.COMPONENT_CUSTOM_DATA_COPY_TAG.invokeOn(customData);
            }
            return NBTTagCompoundWrapper.of(tag);
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
            if(Version.getCurrentVersion().isNewerThan(Version.V1_20_R3)) {
                MethodWrappers.ITEMSTACK_SETTAG.invokeOn(nmsItem, nbt);
            } else {
                MethodWrappers.ITEMSTACK_DATA_SET.invokeOn(nmsItem, nbt);
            }

            // Verify NBT Set
            Object tagToVerify = getTagFromNMSItem(nmsItem, false).getNBTObject();
            if (!tagToVerify.equals(nbt)) {
                throw new NBTVerifyException("Failed to verify NBT");
            }
        } catch(InvocationTargetException | IllegalAccessException e) {
            throw new NBTException(e);
        }
    }
}
