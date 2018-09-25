package de.mrstein.customheads.category;

import lombok.Getter;
import org.bukkit.ChatColor;

/*
 *  Project: CustomHeads in BaseCategory
 *     by LikeWhat
 */

@Getter
public class BaseCategory {

    private String id;
    private String name;
    private String permission;

    BaseCategory(String id, String name, String permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
    }

    public String getPlainName() {
        return ChatColor.stripColor(name);
    }

    public Category getAsCategory() {
        return (Category) this;
    }

    public SubCategory getAsSubCategory() {
        return (SubCategory) this;
    }

    public boolean isCategory() {
        return this instanceof Category;
    }

    public boolean isSubCategory() {
        return this instanceof SubCategory;
    }

}
