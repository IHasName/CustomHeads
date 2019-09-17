package de.mrstein.customheads.stuff;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHeadsPlayer;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.CustomHead;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.ScrollableInventory;
import de.mrstein.customheads.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
 *  Project: CustomHeads in CHSearchQuery
 *     by LikeWhat
 */

public class CHSearchQuery {

    private List<String> badVersions = Arrays.asList("v1_8_R1", "v1_8_R2", "v1_8_R3");
    private List<CustomHead> results;

    private boolean recordHistory = true;

    private String search;

    public CHSearchQuery(String search) {
        this.search = search;

        if (search.contains("#>")) {
            results = new ArrayList<>();
            return;
        }
        results = CustomHeads.getCategoryManager().getAllHeads().stream().filter(head -> ChatColor.stripColor(head.getItemMeta().getDisplayName().toLowerCase()).contains(search.toLowerCase())).collect(Collectors.toList());
        Collections.reverse(results);
    }

    public CHSearchQuery excludeCategories(Category... categories) {
        return excludeCategories(Arrays.asList(categories));
    }

    public CHSearchQuery excludeCategories(List<Category> categories) {
        categories.forEach(category -> results.removeAll(results.stream().filter(item -> CustomHeads.getTagEditor().getTags(item).get(CustomHeads.getTagEditor().indexOf(item, "category") + 1).equalsIgnoreCase(category.getId())).collect(Collectors.toList())));
        return this;
    }

    public ScrollableInventory viewTo(Player player, String backAction) {
        CustomHeadsPlayer customHeadsPlayer = CustomHeads.getApi().wrapPlayer(player);
        if (recordHistory)
            customHeadsPlayer.getSearchHistory().addEntry(search);

        String title = CustomHeads.getLanguageManager().SEARCH_TITLE.replace("{RESULTS}", "" + results.size());
        title = badVersions.contains(CustomHeads.version) ? title.contains("{short}") ? title.substring(0, title.lastIndexOf("{short}") >= 32 ? 29 : title.lastIndexOf("{short}")) + "..." : title.substring(0, title.length() >= 32 ? 29 : title.length()) + "..." : title.replace("{short}", "");
        List<ItemStack> heads = new ArrayList<>();
        results.forEach(customHead -> {
            ItemEditor itemEditor = new ItemEditor(customHead);
            if(CustomHeads.hasEconomy()) {
                heads.add(customHeadsPlayer.getUnlockedHeads().contains(customHead) ? CustomHeads.getTagEditor().addTags(itemEditor.addLoreLine(CustomHeads.getLanguageManager().ECONOMY_BOUGHT).getItem(), "wearable", "clonable") : CustomHeads.getTagEditor().addTags(itemEditor.addLoreLine(Utils.formatPrice(customHead.getPrice(), true)).getItem(), "buyHead", "buyHead#>" + customHead.getOriginCategory().getId() + ":" + customHead.getId()));
            } else {
                heads.add(CustomHeads.getTagEditor().addTags(itemEditor.getItem(), "wearable", "clonable"));
            }
        });
        ScrollableInventory searchInventory = new ScrollableInventory(title, heads);
        if (!backAction.equals("willClose"))
            searchInventory.setBarItem(1, Utils.getBackButton("invAction", backAction));
        searchInventory.setBarItem(2, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFO_LORE).getItem(), "dec", "info-item", "blockMoving"));
        searchInventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJiYzYzYzBlNjEzY2UzZmE3ZGVkNDEyM2U2OTkwNzE3OGU2MjI0MGNjYWZlZmNhMjBlZGM5NTM4MTRhNzIifX19").setDisplayName(CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_PREFIX + CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_DEFAULT).getItem(), "scrollInv", "reArrange#>next", "originalName", CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_PREFIX));
        player.openInventory(searchInventory.getAsInventory());
        return searchInventory;
    }

    public int resultsReturned() {
        return results.size();
    }

    public CHSearchQuery setRecordHistory(boolean recordHistory) {
        this.recordHistory = recordHistory;
        return this;
    }
}
