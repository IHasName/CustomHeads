package de.mrstein.customheads.category;

import de.mrstein.customheads.CustomHeads;
import org.bukkit.ChatColor;

public class BaseCategory {

    private String id;
    private String name;
    private String permission;

    private CategoryType type;

    BaseCategory(String id, String name, String permission, CategoryType type) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.type = type;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getPlainName() { return ChatColor.stripColor(name); }

    public String getPermission() { return permission; }

    public CategoryType getType() { return type; }

    public Category getAsCategory() {
        return CustomHeads.getCategoryImporter().getCategory(id);
    }

    public SubCategory getAsSubCategory() {
        return CustomHeads.getCategoryImporter().getSubCategory(id);
    }

    public boolean isCategory() {
        return type == CategoryType.CATEGORY;
    }

    public boolean isSubCategory() {
        return type == CategoryType.SUBCATEGORY;
    }
}
