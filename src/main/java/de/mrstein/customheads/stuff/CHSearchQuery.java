package de.mrstein.customheads.stuff;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.api.CustomHead;
import de.mrstein.customheads.category.Category;
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


public class CHSearchQuery {

    private String search;

    private boolean recordHistory = true;

    private List<ItemStack> results;
    private List<String> badversions = Arrays.asList("v1_8_R1", "v1_8_R2", "v1_8_R3");

    public CHSearchQuery(String search) {
        this.search = search;

        if (search.contains("#>")) {
            results = new ArrayList<>();
            return;
        }
        results = CustomHeads.getCategoryImporter().getAllHeads().stream().filter(head -> ChatColor.stripColor(head.getItemMeta().getDisplayName().toLowerCase()).contains(search.toLowerCase())).collect(Collectors.toList());
        Collections.reverse(results);
    }

    public CHSearchQuery excludeCategories(Category... categories) {
        return excludeCategories(Arrays.asList(categories));
    }

    public CHSearchQuery excludeCategories(List<Category> categories) {
        categories.forEach(category -> results.removeAll(results.stream().filter(item -> CustomHeads.getTagEditor().getTags(item).get(CustomHeads.getTagEditor().indexOf(item, "aHeads") + 1).equalsIgnoreCase(category.getId())).collect(Collectors.toList())));
        return this;
    }

    public ScrollableInventory viewTo(Player player, String backAction) {
        if (recordHistory)
            new History(player).getSearchHistory().addEntry(search);

        String title = CustomHeads.getLanguageManager().SEARCH_TITLE.replace("{RESULTS}", "" + results.size());
        title = badversions.contains(CustomHeads.version) ? title.contains("{short}") ? title.substring(0, title.lastIndexOf("{short}") >= 32 ? 32 : title.lastIndexOf("{short}")) : title.substring(0, 32) : title.replace("{short}", "");
        ScrollableInventory searchInventory = new ScrollableInventory(title, results).setContentsClonable(true);
        if (!backAction.equals("close"))
            searchInventory.setBarItem(1, Utils.getBackButton("invAction", backAction));
        searchInventory.setBarItem(2, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.PAPER).setDisplayName(CustomHeads.getLanguageManager().ITEMS_INFO).setLore(CustomHeads.getLanguageManager().ITEMS_INFOLORE).getItem(), "dec", "info-item", "blockMoving"));
        searchInventory.setBarItem(3, CustomHeads.getTagEditor().setTags(new CustomHead(Utils.getURLFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJiYzYzYzBlNjEzY2UzZmE3ZGVkNDEyM2U2OTkwNzE3OGU2MjI0MGNjYWZlZmNhMjBlZGM5NTM4MTRhNzIifX19")).setDisplayName(CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_PREFIX + CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_DEFAULT).toItem(), "scrollInv", "reArrange#>next", "originalName", CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_PREFIX));
        player.openInventory(searchInventory.getAsInventory());
        return searchInventory;
    }

    public List<ItemStack> getResults() {
        return results;
    }

    public CHSearchQuery setRecordHistory(boolean recordHistory) {
        this.recordHistory = recordHistory;
        return this;
    }
}
