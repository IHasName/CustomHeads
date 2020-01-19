package de.likewhat.customheads.utils;

/*
 *  Project: CustomHeads in CategoryEditor
 *     by LikeWhat
 *
 *  created on 27.10.2019 at 15:22
 */

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.category.BaseCategory;
import de.likewhat.customheads.category.Category;
import de.likewhat.customheads.category.CustomHead;
import de.likewhat.customheads.category.SubCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CategoryEditor {

    private final Category category;
    private Player player;
    
    public CategoryEditor(Category category, Player editor) {
        this.category = category;
        player = editor;
    }

    public static void openCategoryOverview(Player player) {
        List<ItemStack> categories = new ArrayList<>();
        for (Category category : CustomHeads.getCategoryManager().getCategoryList()) {
            categories.add(CustomHeads.getTagEditor().setTags(new ItemEditor(category.hasCategoryIcon() ? category.getCategoryIcon() : category.getIcons().get(0)).setDisplayName("§e" + category.getName())
                    .setLore(Arrays.asList(
                            "§7ID: §e" + category.getId(),
                            "§7Permission: §e" + category.getPermission(),
                            "§7Has Subcategories: §e" + (category.hasSubCategories() ? "Yes" : "No")
                    )).getItem(), "cEditor", "selectCategory#>" + category.getId()));
        }
        ScrollableInventory scrollableInventory = new ScrollableInventory("§eAll Categories", categories);
        scrollableInventory.setBarItem(1, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM, (byte) 3).setDisplayName("§aCreate").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=").getItem(), "cEditor", "create#>"));
        scrollableInventory.setBarItem(2, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName("§bEdit").getItem(), "cEditor", "edit#>"));
        scrollableInventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM, (byte) 3).setDisplayName("§cDelete").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTFmMjExYzZjMzJlNzdmYWY5MTEyNzhiOGM3MjljYTUwYzVkMjc5ZDU5OGZkYjg3MTk4NzVmNzg0ZGVmNmVhIn19fQ==").getItem(), "cEditor", "delete#>"));
        player.openInventory(scrollableInventory.getAsInventory());
    }

    /*
     * ----------------------------
     * |0 |1 |2 |3 |4 |5 |6 |7 |8 |
     * |9 |10|11|12|13|14|15|16|17|
     * |18|19|20|21|22|23|24|25|26|
     * ----------------------------
     *
     * 09 :
     * 10 :
     * 11 : Category Icon
     * 12 : SKULL_ITEM -> Edit Heads
     * 13 : PAPER -> Rename
     * 14 : EMERALD -> Convert (only when not Subcategory)
     * 15 :
     * 16 : BARRIER -> Delete
     * 17 :
     * */

    /*
     * -------------------
     * | Categories      |
     * |                 |
     * |                 |
     * |      2 3 1      |
     * -------------------
     *
     * 1: Delete
     * 2: Create
     * 3: Edit
     *
     * Create:
     *   Default: Normal
     *   -> Can later be changed to Sub Category
     *
     * */

    public void showCategoryInfos() {
        if (player.hasPermission("cc.editor.showinfos")) {
            Inventory categoryEdit;

            CustomHead editHeads = getRandomHeadFromCategory(category);

            if(category.isCategory()) {
                categoryEdit = Bukkit.createInventory(player, 27, "§eCategory Info §8: §e" + category.getName());
                categoryEdit.setItem(11, CustomHeads.getTagEditor().setTags(new ItemEditor(category.getAsCategory().getCategoryIcon()).setDisplayName("§7" + category.getAsCategory().getCategoryIcon().getItemMeta().getDisplayName()).getItem(), "cEditor", "blockMoving"));
                categoryEdit.setItem(12, CustomHeads.getTagEditor().setTags((editHeads == null ? new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19") : new ItemEditor(editHeads)).setDisplayName("§aEdit Heads").setLore("").getItem(), "cEditor", "blockMoving"));
                categoryEdit.setItem(13, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName("§aRename").getItem(), "cEditor", "rename#>category#>" + category.getId()));
                categoryEdit.setItem(14, CustomHeads.getTagEditor().setTags(new ItemEditor(category.getAsCategory().hasSubCategories() ? Material.REDSTONE : Material.EMERALD).setDisplayName(category.getAsCategory().hasSubCategories() ? "§cAlready converted" : "§aCovert to Subcategory").getItem(), "cEditor", category.getAsCategory().hasSubCategories() ? "convert" : "blockMoving"));
            } else {
                categoryEdit = Bukkit.createInventory(player, 27, "§eSubCategory Info §8: §e" + category.getName());
                categoryEdit.setItem(11, CustomHeads.getTagEditor().setTags(new ItemEditor(category.getAsSubCategory().getCategoryIcon()).setDisplayName("§7" + category.getAsSubCategory().getCategoryIcon().getItemMeta().getDisplayName()).getItem(), "cEditor", "blockMoving"));
                categoryEdit.setItem(12, CustomHeads.getTagEditor().setTags((editHeads == null ? new ItemEditor(Material.SKULL_ITEM, (short) 3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19") : new ItemEditor(editHeads)).setDisplayName("§aEdit Heads").setLore("").getItem(), "cEditor", "blockMoving"));
                categoryEdit.setItem(13, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName("§aRename").getItem(), "cEditor", "rename#>category#>" + category.getId()));
            }
            categoryEdit.setItem(16, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.BARRIER).setDisplayName("§cDelete").getItem(), "cEditor", "delete#>category#>" + category.getId()));
            categoryEdit.setItem(18, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM, (short) 3).setDisplayName("§cBack").setLore("§7Back to previous Menu").setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==").getItem(), "cEditor", (category.isCategory() ? "open#>cOverview" : "openInfo#>" + category.getAsSubCategory().getOriginCategory().getId())));

            player.openInventory(categoryEdit);
        }
    }

    private CustomHead getRandomHeadFromCategory(BaseCategory category) {
        List<CustomHead> heads = new ArrayList<>();
        if(category.isCategory()) {
            if (category.getAsCategory().hasSubCategories()) {
                for (SubCategory sub : category.getAsCategory().getSubCategories()) {
                    heads.addAll(sub.getHeads());
                }
            } else {
                heads.addAll(category.getAsCategory().getHeads());
            }
        } else {
            heads.addAll(category.getAsSubCategory().getHeads());
        }
        return heads.isEmpty() ? null : heads.get(new Random().nextInt(heads.size() - 1));
    }
}
