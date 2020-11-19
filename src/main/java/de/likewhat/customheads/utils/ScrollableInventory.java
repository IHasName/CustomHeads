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

    private static List<Comparator<ItemStack>> sorting = new ArrayList<>(Arrays.asList((o1, o2) -> 0, Comparator.comparing(item -> ChatColor.stripColor(Utils.format(item.getItemMeta().getDisplayName()))), Comparator.comparing(item -> item.getItemMeta().getDisplayName().substring(1, 2))));
    public static List<String> sortName = new ArrayList<>(Arrays.asList("invalid", CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_DEFAULT, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_ALPHABETICAL, CustomHeads.getLanguageManager().CYCLE_ARRANGEMENT_COLOR));
    private final long cacheTime;

    private static HashMap<String, ScrollableInventory> cachedInventories = new HashMap<>();
    private HashMap<Integer, ItemStack> buttons = new HashMap<>();

    private Language language = CustomHeads.getLanguageManager();
    private List<ItemStack> content;
    private LinkedList<ItemStack> defContent;
    private boolean clonable = false;
    private boolean contentMovable = true;
    private int currentArrangement;
    private int currentPage = 1;

    private Inventory inventory;
    private String[] extraTags;
    private String uid;

    public ScrollableInventory(String title) {
        this(title, new ArrayList<>());
    }

    public ScrollableInventory(String title, List<ItemStack> content) {
        inventory = Bukkit.createInventory(null, 54, title);
        uid = Utils.randomAlphabetic(6);
        setContent(content);
        cachedInventories.put(uid, this);
        cacheTime = System.currentTimeMillis();
    }

    public static HashMap<String, ScrollableInventory> getInventories() {
        return cachedInventories;
    }

    public static ScrollableInventory getInventoryByID(String uid) {
        return cachedInventories.get(uid);
    }

    public static void clearCache() {
        if (cachedInventories.isEmpty()) return;
        cachedInventories.keySet().removeIf(uid -> System.currentTimeMillis() - cachedInventories.get(uid).cacheTime > 6000);
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

    public Inventory getAsInventory() {
        setPage(1);
        return inventory;
    }

    public void refreshContent() {
        for (int i = 0; i < content.size(); i++) {
            ItemStack item = content.get(i);
            if (clonable)
                item = CustomHeads.getTagEditor().addTags(item, "clonable");
            if (!contentMovable)
                item = CustomHeads.getTagEditor().addTags(item, "blockMoving");
            content.set(i, CustomHeads.getTagEditor().addTags(item, "scInvID", uid));
        }
    }

    public void refreshCurrentPage() {
        setPage(currentPage);
    }

    public void setPage(int page) {
        currentPage = page;

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
        if ((currentArrangement = method < 1 ? 1 : Math.min(method, (sorting.size() + 1))) == 1) {
            content = new ArrayList<>(defContent);
        } else {
            content.sort(sorting.get(currentArrangement - 1));
        }
        setPage(currentPage);
        return currentArrangement;
    }

    public void setContent(List<ItemStack> content) {
        this.content = content;
        defContent = new LinkedList<>(content);
        if (!content.isEmpty())
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

    public String[] getExtraTags() {
        return extraTags;
    }

    public ScrollableInventory setExtraTags(String... tags) {
        extraTags = tags;
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
        return (p = (int) Math.ceil(content.size() / 45.0)) < 1 ? 1 : p;
    }

    public List<ItemStack> getContentFromPage(int page, boolean tags) {
        List<ItemStack> contentCopy = getContent(tags);
        int pages = getPages();
        if (page < 1 || page > pages)
            throw new IllegalArgumentException("Unknown Page " + page);
        int start = (page - 1) * 45;
        int end = page * 45;
        return contentCopy.subList(start, content.size() > end ? end : content.size());
    }

    public void setItemOnCurrentPage(int index, ItemStack to) {
        setItem(currentPage, index, to);
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
        return currentPage;
    }
}
