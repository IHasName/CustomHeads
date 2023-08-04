package de.likewhat.customheads.utils.reflection;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.collections.MethodReflectionCollection;
import de.likewhat.customheads.utils.reflection.nbt.ItemNBTUtils;
import de.likewhat.customheads.utils.reflection.nbt.NBTTagUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/*
 *  Project: CustomHeads in TagEditor
 *     by LikeWhat
 */

// It all started as an simple idea >_>
public class TagEditor {

    private final String tagName;

    public TagEditor(String tagName) {
        this.tagName = tagName;
    }

    public static ItemStack clearTags(ItemStack itemStack) {
        try {
            Object copy = ItemNBTUtils.asNMSCopy(itemStack);
            Object itemTagCompound = ItemNBTUtils.getTagFromItem(itemStack);
            Object nbtTagCompound = NBTTagUtils.createInstance(NBTTagUtils.NBTType.COMPOUND);
            MethodReflectionCollection.NBT_TAGCOMPOUND_SET.invokeOn(itemTagCompound, "tagEditor", nbtTagCompound);
            ItemNBTUtils.setTagOnItem(copy, itemTagCompound);
            return ItemNBTUtils.asBukkitCopy(copy);
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to reset Tags from Item", e);
        }
        return null;
    }

    public ItemStack setTags(ItemStack itemStack, String... tags) {
        return setTags(itemStack, Arrays.asList(tags));
    }

    public ItemStack setTags(ItemStack itemStack, List<String> tags) {
        try {
            Object copy = ItemNBTUtils.asNMSCopy(itemStack);
            Object itemTagCompound = ItemNBTUtils.getTagFromItem(itemStack);
            Object nbtTagCompound = MethodReflectionCollection.NBT_TAGCOMPOUND_GETCOMPOUND.invokeOn(itemTagCompound, "tagEditor");
            Object nbtTagList = NBTTagUtils.createInstance(NBTTagUtils.NBTType.LIST);
            for (String tag : tags) {
                NBTTagUtils.addObjectToNBTList(nbtTagList, NBTTagUtils.createInstance(NBTTagUtils.NBTType.STRING, tag));
            }
            MethodReflectionCollection.NBT_TAGCOMPOUND_SET.invokeOn(nbtTagCompound, tagName, nbtTagList);
            MethodReflectionCollection.NBT_TAGCOMPOUND_SET.invokeOn(itemTagCompound, "tagEditor", nbtTagCompound);
            ItemNBTUtils.setTagOnItem(copy, itemTagCompound);
            return ItemNBTUtils.asBukkitCopy(copy);
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to save Tags to Item", e);
        }
        return null;
    }

    public ItemStack addTags(ItemStack itemStack, String... tags) {
        return addTags(itemStack, Arrays.asList(tags));
    }

    public ItemStack addTags(ItemStack itemStack, List<String> tags) {
        List<String> prevTags = getTags(itemStack);
        for (String tag : tags) {
            if (!prevTags.contains(tag))
                prevTags.add(tag);
        }
        return setTags(itemStack, prevTags);
    }

    public ItemStack removeTags(ItemStack itemStack, String... tags) {
        return removeTags(itemStack, Arrays.asList(tags));
    }

    public ItemStack removeTags(ItemStack itemStack, List<String> tags) {
        List<String> currentTags = getTags(itemStack);
        currentTags.removeAll(tags);
        return setTags(itemStack, currentTags);
    }

    public List<String> getTags(ItemStack itemStack) {
        try {
            if (!(ItemNBTUtils.hasNBTTag(itemStack) && hasMyTags(itemStack))) {
                return new ArrayList<>();
            }
            Object itemTag = ItemNBTUtils.getTagFromItem(itemStack);
            Object itemTagCompound = MethodReflectionCollection.NBT_TAGCOMPOUND_GETCOMPOUND.invokeOn(itemTag, "tagEditor");
            Object nbtList = MethodReflectionCollection.NBT_TAGCOMPOUND_GETLIST.invokeOn(itemTagCompound, tagName, NBTTagUtils.NBTType.STRING.getId());
            List<String> result = new ArrayList<>();
            for (int i = 0; i < MethodReflectionCollection.NBT_TAGLIST_SIZE.invokeOn(nbtList); i++) {
                result.add(MethodReflectionCollection.NBT_TAGLIST_GETSTRING.invokeOn(nbtList, i));
            }
            return result;
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get Tags from Item", e);
        }
        return new ArrayList<>();
    }

    public ItemStack copyTags(ItemStack from, ItemStack to) {
        return setTags(to, getTags(from));
    }

    public int indexOf(ItemStack item, String tag) {
        return getTags(item).indexOf(tag);
    }

    public boolean hasMyTags(ItemStack item) {
        try {
            if (!ItemNBTUtils.hasNBTTag(item)) {
                return false;
            }
            Object itemTag = ItemNBTUtils.getTagFromItem(item);
            Object itemTagCompound = MethodReflectionCollection.NBT_TAGCOMPOUND_GETCOMPOUND.invokeOn(itemTag, "tagEditor");
            return MethodReflectionCollection.NBT_TAGCOMPOUND_HASKEY.invokeOn(itemTagCompound, tagName);
        } catch (Exception e) {
            return false;
        }
    }



}
