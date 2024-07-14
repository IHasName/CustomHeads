package de.likewhat.customheads.utils.reflection;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTGenericWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTTagCompoundWrapper;
import de.likewhat.customheads.utils.reflection.helpers.wrappers.instances.nbt.NBTTagListWrapper;
import de.likewhat.customheads.utils.reflection.nbt.ItemNBTUtils;
import de.likewhat.customheads.utils.reflection.nbt.NBTType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        try {
            Object copy = ItemNBTUtils.asNMSCopy(itemStack);
            NBTTagCompoundWrapper itemTagCompound = ItemNBTUtils.getTagFromItem(itemStack);
            NBTTagCompoundWrapper nbtTagCompound = new NBTTagCompoundWrapper();
            nbtTagCompound.set("tagEditor", nbtTagCompound);
            ItemNBTUtils.setTagOnItem(copy, itemTagCompound.getNBTObject());
//            Object itemTagCompound = ItemNBTUtils.getTagFromItem(itemStack);
//            Object nbtTagCompound = NBTType.COMPOUND.createInstance();
//            NBTCompoundWrapper.SET.invokeOn(itemTagCompound, "tagEditor", nbtTagCompound);
//            ItemNBTUtils.setTagOnItem(copy, itemTagCompound);
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
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        try {
            NBTTagCompoundWrapper itemTagCompound = ItemNBTUtils.getTagFromItem(itemStack);
            NBTTagCompoundWrapper nbtTagCompound = itemTagCompound.getCompound("tagEditor");
            NBTTagListWrapper nbtTagList = new NBTTagListWrapper();
            for (String tag : tags) {
                nbtTagList.addObject(NBTType.STRING.createInstance(tag));
            }
            nbtTagCompound.set(this.tagName, nbtTagList);
            itemTagCompound.set("tagEditor", nbtTagCompound);
            return ItemNBTUtils.setTagOnItem(itemStack, itemTagCompound.getNBTObject());
        } catch (Exception e) {
            CustomHeads.getPluginLogger().log(Level.SEVERE, "Failed to save Tags to Item", e);
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
            NBTTagCompoundWrapper itemTag = ItemNBTUtils.getTagFromItem(itemStack);
            NBTTagCompoundWrapper tagEditorCompound = itemTag.getCompound("tagEditor");
            NBTTagListWrapper nbtList = tagEditorCompound.getList(tagName, NBTType.STRING);
            return nbtList.stream().map(NBTGenericWrapper::asString).collect(Collectors.toList());

//            Object itemTag = ItemNBTUtils.getTagFromItem(itemStack);
//            Object itemTagCompound = NBTTagCompoundWrapper.GET_COMPOUND.invokeOn(itemTag, "tagEditor");
//            Object nbtList = NBTTagCompoundWrapper.GET_LIST.invokeOn(itemTagCompound, tagName, NBTType.STRING.getId());
//            List<String> result = new ArrayList<>();
//            for (int i = 0; i < NBTTagListWrapper.SIZE.invokeOn(nbtList); i++) {
//                result.add(NBTTagListWrapper.GET_STRING.invokeOn(nbtList, i));
//            }
//            return result;
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
            NBTTagCompoundWrapper itemTag = ItemNBTUtils.getTagFromItem(item);
            return itemTag.getCompound("tagEditor").hasKey(tagName);
//            Object itemTagCompound = NBTTagCompoundWrapper.GET_COMPOUND.invokeOn(itemTag, "tagEditor");
//            return NBTTagCompoundWrapper.HAS_KEY.invokeOn(itemTagCompound, tagName);
        } catch (Exception e) {
            return false;
        }
    }



}
