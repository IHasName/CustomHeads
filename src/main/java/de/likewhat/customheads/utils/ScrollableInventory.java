package de.likewhat.customheads.utils;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.loader.Language;
import de.likewhat.customheads.utils.reflection.TagEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/*
 *  Project: CustomHeads in ScrollableInventory
 *     by LikeWhat
 */

public class ScrollableInventory {

    private static final List<Comparator<ItemStack>> SORTING = new ArrayList<>(Arrays.asList((o1, o2) -> 0, Comparator.comparing(item -> ChatColor.stripColor(Utils.format(item.getItemMeta().getDisplayName()))), Comparator.comparing(item -> item.getItemMeta().getDisplayName().substring(1, 2))));
    public static List<String> sortName = new ArrayList<>(Arrays.asList("invalid", CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_DEFAULT, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_ALPHABETICAL, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_COLOR));
    private final long cacheTime;

    private static final HashMap<String, ScrollableInventory> CACHED_INVENTORIES = new HashMap<>();
    private final HashMap<Integer, ItemStack> buttons = new HashMap<>();

    private final Language language = CustomHeads.getLanguageManager();
    private List<ItemStack> content;
    private LinkedList<ItemStack> defContent;
    private boolean contentClonable = false;
    private boolean contentMovable = true;
    private int currentArrangement;
    private int currentPage = 1;

    private final Inventory inventory;
    private String[] extraTags;
    private final String uid;

    public ScrollableInventory(String title) {
        this(title, new ArrayList<>());
    }

    public ScrollableInventory(String title, List<ItemStack> content) {
        this.inventory = Bukkit.createInventory(null, 54, title);
        this.uid = Utils.randomAlphabetic(6);
        setContent(content);
        CACHED_INVENTORIES.put(this.uid, this);
        this.cacheTime = System.currentTimeMillis();
    }

    public static HashMap<String, ScrollableInventory> getInventories() {
        return CACHED_INVENTORIES;
    }

    public static ScrollableInventory getInventoryByID(String uid) {
        return CACHED_INVENTORIES.get(uid);
    }

    public static void clearCache() {
        if (CACHED_INVENTORIES.isEmpty()) return;
        CACHED_INVENTORIES.keySet().removeIf(uid -> System.currentTimeMillis() - CACHED_INVENTORIES.get(uid).cacheTime > 6000);
    }

    public ScrollableInventory setBarItem(int index, ItemStack itemStack) {
        this.buttons.put(index, itemStack);
        return this;
    }

    public ScrollableInventory setContentsClonable(boolean clonable) {
        this.contentClonable = clonable;
        return this;
    }

    public ScrollableInventory setContentMovable(boolean movable) {
        contentMovable = movable;
        return this;
    }

    public Inventory getAsInventory() {
        setPage(1);
        return inventory;
    }

    public void refreshContent() {
        for (int i = 0; i < content.size(); i++) {
            ItemStack item = content.get(i);
            if (contentClonable)
                item = CustomHeads.getTagEditor().addTags(item, "clonable");
            if (!contentMovable)
                item = CustomHeads.getTagEditor().addTags(item, "blockMoving");
            content.set(i, CustomHeads.getTagEditor().addTags(item, "scInvID", uid));
        }
    }

    public void refreshCurrentPage() {
        setPage(this.currentPage);
    }

    public void setPage(int page) {
        this.currentPage = page;

        for (int i = 0; i <= 45; i++)
            inventory.setItem(i, null);
        inventory.setItem(53, null);

        refreshContent();

        Utils.runAsync(new BukkitRunnable() {
            public void run() {
                List<ItemStack> sublist = getContentFromPage(currentPage, true);
                for (int i = 0; i < sublist.size(); i++) {
                    inventory.setItem(i, sublist.get(i));
                }

                for (int index : buttons.keySet())
                    inventory.setItem(index + 47, CustomHeads.getTagEditor().addTags(buttons.get(index), "scInvID", uid));

                if (page < getPages())
                    inventory.setItem(53, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(language.NEXT_PAGE).setLore(language.PAGE_GENERAL_PREFIX + " " + (page + 1)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==").getItem(), "scInvID", uid, "scrollInv", "slidePage#>next"));
                if (page > 1)
                    inventory.setItem(45, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(language.PREVIOUS_PAGE).setLore(language.PAGE_GENERAL_PREFIX + " " + (page - 1)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==").getItem(), "scInvID", uid, "scrollInv", "slidePage#>previous"));
            }
        });
    }

    public int reArrangeContents(int method) {
        if ((currentArrangement = method < 1 ? 1 : Math.min(method, (SORTING.size() + 1))) == 1) {
            this.content = new ArrayList<>(defContent);
        } else {
            this.content.sort(SORTING.get(currentArrangement - 1));
        }
        setPage(this.currentPage);
        return currentArrangement;
    }

    public void setContent(List<ItemStack> content) {
        this.content = content;
        defContent = new LinkedList<>(content);
        if (!content.isEmpty())
            reArrangeContents(1);
    }

    public int nextArrangement() {
        return reArrangeContents(++currentArrangement > SORTING.size() ? currentArrangement = 1 : currentArrangement);
    }

    public void nextPage() {
        if (hasPage(this.currentPage + 1))
            setPage(++this.currentPage);
    }

    public void previousPage() {
        if (hasPage(this.currentPage - 1))
            setPage(--this.currentPage);
    }

    public String[] getExtraTags() {
        return this.extraTags;
    }

    public ScrollableInventory setExtraTags(String... tags) {
        this.extraTags = tags;
        return this;
    }

    private boolean hasPage(int page) {
        return page >= 1 && page <= (int) Math.ceil(defContent.size() / 45.0);
    }

    public List<ItemStack> getContent(boolean tags) {
        List<ItemStack> contentCopy = new ArrayList<>(content);
        if (!tags)
            contentCopy.replaceAll(TagEditor::clearTags);
        return contentCopy;
    }

    public int getPages() {
        int p;
        return (p = (int) Math.ceil(this.content.size() / 45.0)) < 1 ? 1 : p;
    }

    public List<ItemStack> getContentFromPage(int page, boolean tags) {
        List<ItemStack> contentCopy = getContent(tags);
        int pages = getPages();
        if (page < 1 || page > pages)
            throw new IllegalArgumentException("Unknown Page " + page);
        int start = (page - 1) * 45;
        int end = page * 45;
        return contentCopy.subList(start, Math.min(this.content.size(), end));
    }

    public void setItemOnCurrentPage(int index, ItemStack to) {
        setItem(this.currentPage, index, to);
    }

    public void setItem(int page, int index, ItemStack to) {
        List<ItemStack> contentCopy = getContent(true);
        contentCopy.set(index + 45 * (page - 1), to);
        setContent(contentCopy);
        refreshContent();
    }

    public String getUid() {
        return uid;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }
}
