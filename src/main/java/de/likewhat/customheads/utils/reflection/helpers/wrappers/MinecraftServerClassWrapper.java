package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

public class MinecraftServerClassWrapper extends ClassWrapper {

    private final String altPrefix;

    public MinecraftServerClassWrapper(String className, String altPrefix) {
        this(null, null, className, altPrefix, null);
    }

    public MinecraftServerClassWrapper(Version from, Version to, String className, String altPrefix) {
        this(from, to, className, altPrefix, null);
    }

    public MinecraftServerClassWrapper(Version from, Version to, String className, MinecraftServerClassWrapper replacedBy) {
        this(from, to, className, null, replacedBy);
    }

    public MinecraftServerClassWrapper(Version from, Version to, String className, String altPrefix, MinecraftServerClassWrapper replacedBy) {
        super(from, to, replacedBy, className);
        this.altPrefix = altPrefix;
    }

    @Override
    protected Class<?> resolveValue() throws Throwable {
        return ReflectionUtils.getMCServerClassByName(className, altPrefix);
    }
}
