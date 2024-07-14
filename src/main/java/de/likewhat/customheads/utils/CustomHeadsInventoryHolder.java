package de.likewhat.customheads.utils;

import de.likewhat.customheads.category.BaseCategory;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomHeadsInventoryHolder {

    @Getter
    public static class BaseHolder implements InventoryHolder {
        @NonNull
        private final String id;
        private final Player owner;

        public BaseHolder(@NonNull String id) {
            this(id, null);
        }

        public BaseHolder(@NonNull String id, Player owner) {
            this.id = id;
            this.owner = owner;
        }

        public Inventory getInventory() {
            return null;
        }
    }

    @Getter
    public static final class MenuHolder extends BaseHolder {
        @NonNull
        private final String title;
        @NonNull
        private final String permission;

        public MenuHolder(@NonNull String id, Player owner, @NonNull String title, @NonNull String permission) {
            super(id, owner);
            this.title = title;
            this.permission = permission;
        }

        public MenuHolder(@NonNull String id, @NonNull String title, @NonNull String permission) {
            super(id, null);
            this.title = title;
            this.permission = permission;
        }

        /**
         * Creates a Copy of this Holder with a Player as the Owner
         * @param newOwner The Player
         * @return New Instance with the new Owner
         */
        public MenuHolder copyWithNewOwner(Player newOwner) {
            return new MenuHolder(this.getId(), newOwner, this.getTitle(), this.getPermission());
        }
    }

    @Getter
    public static final class CategoryHolder extends BaseHolder {
        private final BaseCategory category;

        public CategoryHolder(BaseCategory category) {
            super("heads:category:" + category.getId(), null);
            this.category = category;
        }
    }
}
