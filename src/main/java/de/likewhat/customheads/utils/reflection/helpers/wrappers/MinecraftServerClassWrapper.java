package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

public class MinecraftServerClassWrapper extends ClassWrapper {

    private final String altPackage; // Future Versions use Packages instead of the Version Prefix

    public MinecraftServerClassWrapper(String className, String altPackage) {
        this(null, null, className, altPackage, null);
    }

    public MinecraftServerClassWrapper(Version from, Version to, String className, String altPackage) {
        this(from, to, className, altPackage, null);
    }

    public MinecraftServerClassWrapper(Version from, Version to, String className, MinecraftServerClassWrapper replacedBy) {
        this(from, to, className, null, replacedBy);
    }

    public MinecraftServerClassWrapper(Version from, Version to, String className, String altPackage, MinecraftServerClassWrapper replacedBy) {
        super(from, to, replacedBy, className);
        this.altPackage = altPackage;
    }

    @Override
    protected Class<?> resolveValue() {
        return ReflectionUtils.getMCServerClassByName(className, altPackage);
    }
}
