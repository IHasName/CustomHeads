package de.likewhat.customheads.utils.reflection;

import de.likewhat.customheads.CustomHeads;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
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

    private String tagname;

    public TagEditor(String tagname) {
        this.tagname = tagname;
    }

    public static ItemStack clearTags(ItemStack itemStack) {
        try {
            Object copy = getAsMNSCopy(itemStack);
            Object itemTagCompound = hasNBTTag(itemStack) ? copy.getClass().getMethod("getTag").invoke(copy) : NBTTagUtils.createInstance("NBTTagCompound");
            Object nbtTagCompound = NBTTagUtils.createInstance("NBTTagCompound");
            itemTagCompound.getClass().getMethod("set", String.class, NBTTagUtils.getNBTClass("NBTBase")).invoke(itemTagCompound, "tagEditor", nbtTagCompound);
            copy.getClass().getMethod("setTag", NBTTagUtils.getNBTClass("NBTTagCompound")).invoke(copy, itemTagCompound);
            return asBukkitCopy(copy);
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to reset Tags from Item", e);
            return null;
        }
    }

    public static Object getAsMNSCopy(ItemStack item) {
        try {
            return ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(ReflectionUtils.getCBClass("inventory.CraftItemStack"), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasNBTTag(ItemStack item) {
        try {
            Object copy = getAsMNSCopy(item);
            return copy != null && (boolean) copy.getClass().getMethod("hasTag").invoke(copy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ItemStack setTags(ItemStack itemStack, List<String> tags) {
        try {
            Object copy = getAsMNSCopy(itemStack);
            Object itemTagCompound = hasNBTTag(itemStack) ? copy.getClass().getMethod("getTag").invoke(copy) : NBTTagUtils.createInstance("NBTTagCompound");
            Object nbtTagCompound = ((boolean) itemTagCompound.getClass().getMethod("hasKey", String.class).invoke(itemTagCompound, "tagEditor")) ? itemTagCompound.getClass().getMethod("get", String.class).invoke(itemTagCompound, "tagEditor") : NBTTagUtils.createInstance("NBTTagCompound");
            Object nbtTagList = NBTTagUtils.createInstance("NBTTagList");
            for (String tag : tags) {
                NBTTagUtils.addObjectToNBTList(nbtTagList, NBTTagUtils.createInstance("NBTTagString", tag));
            }
            nbtTagCompound.getClass().getMethod("set", String.class, NBTTagUtils.getNBTClass("NBTBase")).invoke(nbtTagCompound, tagname, nbtTagList);
            itemTagCompound.getClass().getMethod("set", String.class, NBTTagUtils.getNBTClass("NBTBase")).invoke(itemTagCompound, "tagEditor", nbtTagCompound);
            copy.getClass().getMethod("setTag", NBTTagUtils.getNBTClass("NBTTagCompound")).invoke(copy, itemTagCompound);
            return asBukkitCopy(copy);
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to save Tags to Item", e);
            return null;
        }
    }

    public ItemStack setTags(ItemStack itemStack, String... tags) {
        return setTags(itemStack, Arrays.asList(tags));
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
            if (!(hasNBTTag(itemStack) && hasMyTags(itemStack))) {
                return new ArrayList<>();
            }
            Object copy = getAsMNSCopy(itemStack);
            Object tag = copy.getClass().getMethod("getTag").invoke(copy);
            Object compound = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "tagEditor");
            Object list = compound.getClass().getMethod("getList", String.class, int.class).invoke(compound, tagname, 8);
            List<String> res = new ArrayList<>();
            for (int u = 0; u < (int) list.getClass().getMethod("size").invoke(list); u++) {
                res.add(list.getClass().getMethod("getString", int.class).invoke(list, u).toString());
            }
            return res;
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to get Tags from Item", e);
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
            if (!hasNBTTag(item)) {
                return false;
            }
            Object copy = getAsMNSCopy(item);
            Object itemCompound = copy.getClass().getMethod("getTag").invoke(copy);
            Object tagCompound = itemCompound.getClass().getMethod("getCompound", String.class).invoke(itemCompound, "tagEditor");
            return tagCompound != null && (boolean) tagCompound.getClass().getMethod("hasKey", String.class).invoke(tagCompound, tagname);
        } catch (Exception e) {
            return false;
        }
    }

    private static ItemStack asBukkitCopy(Object object) {
        try {
            return (ItemStack) ReflectionUtils.getCBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", ReflectionUtils.getMCServerClassByName("ItemStack", "world.item")).invoke(ReflectionUtils.getCBClass("inventory.CraftItemStack"), object);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
