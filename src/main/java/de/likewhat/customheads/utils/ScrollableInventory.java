package de.likewhat.customheads.utils;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.loader.Language;
import de.likewhat.customheads.utils.reflection.TagEditor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    @Getter
    private static final HashMap<UUID, ScrollableInventory> CACHED_INVENTORIES = new HashMap<>();
    private final HashMap<Integer, ItemStack> buttons = new HashMap<>();

    private final Language language = CustomHeads.getLanguageManager();
    private List<ItemStack> content;
    private LinkedList<ItemStack> defContent;
    private boolean contentCloneable = false;
    private boolean contentMovable = true;
    private int currentArrangement;
    @Getter
    private int currentPage = 1;

    private final Inventory inventory;
    @Getter
    private String[] extraTags;
    @Getter
    private final UUID id;

    public ScrollableInventory(String title) {
        this(title, new ArrayList<>());
    }

    public ScrollableInventory(String title, List<ItemStack> content) {
        this.inventory = Bukkit.createInventory(new InvHolder(this), 54, title);
        this.id = UUID.randomUUID();
        this.setContent(content);
        CACHED_INVENTORIES.put(this.id, this);
        this.cacheTime = System.currentTimeMillis();
    }

    public static ScrollableInventory getInventoryById(UUID id) {
        return CACHED_INVENTORIES.get(id);
    }

    public static ScrollableInventory getInventoryById(String id) {
        return getInventoryById(UUID.fromString(id));
    }

    public static void clearCache() {
        if (CACHED_INVENTORIES.isEmpty()) {
            return;
        }
        CACHED_INVENTORIES.keySet().removeIf(id -> System.currentTimeMillis() - CACHED_INVENTORIES.get(id).cacheTime > 6000);
    }

    public ScrollableInventory setBarItem(int index, ItemStack itemStack) {
        this.buttons.put(index, itemStack);
        return this;
    }

    public ScrollableInventory setContentsClonable(boolean clonable) {
        this.contentCloneable = clonable;
        return this;
    }

    public ScrollableInventory setContentMovable(boolean movable) {
        this.contentMovable = movable;
        return this;
    }

    public Inventory getAsInventory() {
        this.setPage(1);
        return this.inventory;
    }

    public void refreshContent() {
        for (int i = 0; i < this.content.size(); i++) {
            ItemStack item = this.content.get(i);
            if (this.contentCloneable)
                item = CustomHeads.getTagEditor().addTags(item, "clonable");
            if (!this.contentMovable)
                item = CustomHeads.getTagEditor().addTags(item, "blockMoving");
            this.content.set(i, CustomHeads.getTagEditor().addTags(item, "scInvID", this.id.toString()));
        }
    }

    public void refreshCurrentPage() {
        setPage(this.currentPage);
    }

    public void setPage(int page) {
        this.currentPage = page;

        for (int i = 0; i <= 45; i++) {
            this.inventory.setItem(i, null);
        }
        this.inventory.setItem(53, null);

        this.refreshContent();

        Utils.runAsync(new BukkitRunnable() {
            public void run() {
                List<ItemStack> sublist = getContentFromPage(currentPage, true);
                for (int i = 0; i < sublist.size(); i++) {
                    inventory.setItem(i, sublist.get(i));
                }

                for (int index : buttons.keySet())
                    inventory.setItem(index + 47, CustomHeads.getTagEditor().addTags(buttons.get(index), "scInvID", id.toString()));

                if (page < getPages()) {
                    inventory.setItem(53, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(language.NEXT_PAGE).setLore(language.PAGE_GENERAL_PREFIX + " " + (page + 1)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==").getItem(), "scInvID", id.toString(), "scrollInv", "slidePage#>next"));
                }
                if (page > 1) {
                    inventory.setItem(45, CustomHeads.getTagEditor().setTags(new ItemEditor(Material.SKULL_ITEM,  3).setDisplayName(language.PREVIOUS_PAGE).setLore(language.PAGE_GENERAL_PREFIX + " " + (page - 1)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==").getItem(), "scInvID", id.toString(), "scrollInv", "slidePage#>previous"));
                }
            }
        });
    }

    public int reArrangeContents(int method) {
        if ((currentArrangement = method < 1 ? 1 : Math.min(method, (SORTING.size() + 1))) == 1) {
            this.content = new ArrayList<>(defContent);
        } else {
            this.content.sort(SORTING.get(currentArrangement - 1));
        }
        this.setPage(this.currentPage);
        return currentArrangement;
    }

    public void setContent(List<ItemStack> content) {
        this.content = content;
        this.defContent = new LinkedList<>(content);
        if (!content.isEmpty()) {
            this.reArrangeContents(1);
        }
    }

    public int nextArrangement() {
        return reArrangeContents(++this.currentArrangement > SORTING.size() ? this.currentArrangement = 1 : this.currentArrangement);
    }

    public void nextPage() {
        if (hasPage(this.currentPage + 1)) {
            setPage(++this.currentPage);
        }
    }

    public void previousPage() {
        if (hasPage(this.currentPage - 1)) {
            setPage(--this.currentPage);
        }
    }

    public ScrollableInventory setExtraTags(String... tags) {
        this.extraTags = tags;
        return this;
    }

    private boolean hasPage(int page) {
        return page >= 1 && page <= (int) Math.ceil(this.defContent.size() / 45.0);
    }

    public List<ItemStack> getContent(boolean tags) {
        List<ItemStack> contentCopy = new ArrayList<>(this.content);
        if (!tags) {
            contentCopy.replaceAll(TagEditor::clearTags);
        }
        return contentCopy;
    }

    public int getPages() {
        return Math.max((int) Math.ceil(this.content.size() / 45.0), 1);
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

    @Getter
    public static final class InvHolder extends CustomHeadsInventoryHolder.BaseHolder {
        @NonNull
        private final ScrollableInventory instance;

        public InvHolder(@NonNull String id, Player owner, ScrollableInventory instance) {
            super(id, owner);
            this.instance = instance;
        }

        public InvHolder(ScrollableInventory instance) {
            super("scrollableInventory:" + instance.getId(), null);
            this.instance = instance;
        }
    }
}
