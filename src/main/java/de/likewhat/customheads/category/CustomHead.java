package de.likewhat.customheads.category;

/*
 *  Project: CustomHeads in CustomHead
 *     by LikeWhat
 *
 *  created on 21.11.2018 at 22:43
 */

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.api.CustomHeadsPlayer;
import de.likewhat.customheads.utils.ItemEditor;
import de.likewhat.customheads.utils.reflection.TagEditor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class CustomHead extends ItemStack {

    private Category originCategory;
    private int price;
    private int id;

    public CustomHead(ItemStack itemStack, Category originCategory, int id, int price) {
        super(CustomHeads.getTagEditor().addTags(itemStack, "headID", originCategory.getId() + ":" + id, "price", String.valueOf(price)));
        this.originCategory = originCategory;
        this.price = price;
        this.id = id;
    }

    public String toString() {
        return originCategory.getId() + ":" + id + " - " + price;
    }

    public boolean isFree() {
        return price == 0;
    }

    public ItemStack getPlainItem() {
        return TagEditor.clearTags(new ItemEditor(this).setLore("").getItem());
    }

    public void unlockFor(CustomHeadsPlayer customHeadsPlayer) {
        customHeadsPlayer.unlockHead(originCategory, id);
    }

    public void lockFor(CustomHeadsPlayer customHeadsPlayer) {
        customHeadsPlayer.lockHead(originCategory, id);
    }

}
