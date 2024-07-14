package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;

public class CraftBukkitClassWrapper extends ClassWrapper {

    public CraftBukkitClassWrapper(String classPath) {
        super(null, null, null, classPath);
    }

    @Override
    protected Class<?> resolveValue() {
        return ReflectionUtils.getCraftBukkitClass(this.className);
    }
}
