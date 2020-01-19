package de.likewhat.customheads.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

/*
 *  Project: CustomHeads in BaseCategory
 *     by LikeWhat
 */

@Getter
@Setter
public class BaseCategory {

    private String permission;
    private String name;
    @Setter(AccessLevel.NONE) private String id;

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
