package de.mrstein.customheads.utils;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.langLoader.Language;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class ScrollableInventory {

    private static HashMap<String, ScrollableInventory> cachedInventories = new HashMap<>();
    private HashMap<Integer, ItemStack> buttons = new HashMap<>();

    private static List<Comparator<ItemStack>> sorting = new ArrayList<>(Arrays.asList((o1, o2) -> 0, Comparator.comparing(item -> ChatColor.stripColor(Utils.format(item.getItemMeta().getDisplayName()))), Comparator.comparing(item -> item.getItemMeta().getDisplayName().substring(1, 2))));
    public static List<String> sortName = new ArrayList<>(Arrays.asList("invalid", CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_DEFAULT, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_ALPHABETICAL, "Color"));

    private String title;
    private String uid;

    private String[] extraTags;

    private List<ItemStack> content;
    private LinkedList<ItemStack> defContent;

    private Language language = CustomHeads.getLanguageManager();

    private int currentPage;
    private int currentArrangement;

    private boolean clonable = false;
    private boolean contentMovable = true;

    private Inventory inventory;

    private final long cacheTime;

    public ScrollableInventory(String title, List<ItemStack> content) {
        this.title = title;
        inventory = Bukkit.createInventory(null, 54, title);
        uid = RandomStringUtils.randomAlphabetic(6);
        this.content = content;
        this.defContent = new LinkedList<>(content);
        reArrangeContents(1);
        cachedInventories.put(uid, this);
        cacheTime = System.currentTimeMillis();
    }

    public static HashMap<String, ScrollableInventory> getInventories() {
        return cachedInventories;
    }

    public static ScrollableInventory getInventoryByID(String uid) {
        return cachedInventories.get(uid);
    }

    public ScrollableInventory setBarItem(int index, ItemStack itemStack) {
        buttons.put(index, itemStack);
        return this;
    }

    public ScrollableInventory setContentsClonable(boolean clonable) {
        this.clonable = clonable;
        return this;
    }

    public ScrollableInventory setContentMovable(boolean movable) {
        contentMovable = movable;
        return this;
    }

    public ScrollableInventory setExtraTags(String... tags) {
        extraTags = tags;
        return this;
    }

    public Inventory getAsInventory() {
        setPage(1);
        return inventory;
    }

    public void refreshContent() {
        for(int i = 0; i < content.size(); i++) {
            ItemStack item = content.get(i);
            if(clonable)
                item = CustomHeads.getTagEditor().addTags(item, "clonable");
            if(!contentMovable)
                item = CustomHeads.getTagEditor().addTags(item, "blockMoving");
            content.set(i, CustomHeads.getTagEditor().addTags(item, "scInvID", uid));
        }
    }

    public void refreshCurrentPage() {
        setPage(currentPage);
    }

    public void setPage(int page) {
        currentPage = page;
        if (page <= 1)
            page = 1;
        int pages = (int) Math.ceil(content.size() / 45.0);
        int start = (page - 1) * 45;
        int end = page * 45;

        for (int i = 0; i <= 45; i++)
            inventory.setItem(i, null);
        inventory.setItem(53, null);

        refreshContent();

        List<ItemStack> sublist = content.subList(start, content.size() > end ? end : content.size());
        for (int i = 0; i < sublist.size(); i++) {
            inventory.setItem(i, sublist.get(i));
        }

        for (int index : buttons.keySet())
            inventory.setItem(index + 47, CustomHeads.getTagEditor().addTags(buttons.get(index), "scInvID", uid));

        if (page < pages)
            inventory.setItem(53, CustomHeads.getTagEditor().setTags(Utils.createHead(language.NEXT_PAGE, language.PAGE_GENERAL_PREFIX + " " + (page + 1), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), "scInvID", uid, "scrollInv", "slidePage#>next"));
        if (page > 1)
            inventory.setItem(45, CustomHeads.getTagEditor().setTags(Utils.createHead(language.PREVIOUS_PAGE, language.PAGE_GENERAL_PREFIX + " " + (page - 1), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ=="), "scInvID", uid, "scrollInv", "slidePage#>previous"));
    }

    public int reArrangeContents(int method) {
        currentArrangement = method < 1 ? 1 : method > (sorting.size() + 1) ? (sorting.size() + 1) : method;
        switch (currentArrangement) {
            case 1:
                content = new ArrayList<>(defContent);
                break;
            case 2:
            case 3:
                content.sort(sorting.get(currentArrangement - 1));
                break;
        }
        setPage(currentPage);
        return currentArrangement;
    }

    public void setContent(List<ItemStack> content) {
        defContent = new LinkedList<>(content);
        reArrangeContents(1);
    }

    public int nextArrangement() {
        return reArrangeContents(++currentArrangement > sorting.size() ? currentArrangement = 1 : currentArrangement);
    }

    public void nextPage() {
        if (hasPage(currentPage + 1))
            setPage(++currentPage);
    }

    public void previousPage() {
        if (hasPage(currentPage - 1))
            setPage(--currentPage);
    }

    public boolean old() {
        return cacheTime + 600000 < System.currentTimeMillis();
    }

    public static void clearCache() {
        for(ScrollableInventory inventory : cachedInventories.values()) {
            if(inventory.old()) {
                cachedInventories.remove(inventory.getUid());
            }
        }
    }

    public String[] getExtraTags() { return extraTags; }

    private boolean hasPage(int page) {
        return page >= 1 && page <= (int) Math.ceil(defContent.size() / 45.0);
    }

    public List<ItemStack> getContent(boolean tags) {
        List<ItemStack> contentCopy = new ArrayList<>(content);
        if(!tags)
            contentCopy.replaceAll(itemStack -> CustomHeads.getTagEditor().removeAll(itemStack));
        return contentCopy;
    }

    public String getTitle() {
        return title;
    }

    public String getUid() {
        return uid;
    }

}
