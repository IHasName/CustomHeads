package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

public class MinecraftServerClassWrapper {

    private Version from;
    private Version to;
    private String className;
    private boolean newMapping; // Whether or not to use the Versioning Tag or not (net.minecraft.server.v1_2_3.this.is.a.package or net.minecraft.server.this.is.a.package)
    private MinecraftServerClassWrapper replacedBy;

    private Class<?> targetClass;

    public MinecraftServerClassWrapper(Version from, Version to, String className) {
        this(from, to, className, null);
    }

    public MinecraftServerClassWrapper(Version from, Version to, String className, MinecraftServerClassWrapper replacedBy) {
        this.from = from == null ? Version.OLDEST : from;
        this.to = to == null ? Version.LATEST : to;
        this.className = className;
        this.newMapping = Version.getCurrentVersion().isNewerThan(Version.V1_16_R1);
        this.replacedBy = replacedBy;
    }

    public Class<?> getClazz() {
        if(targetClass != null) {
            return targetClass;
        }
        Version current = Version.getCurrentVersion();
        if(current.versionValue < from.versionValue) {
            throw new UnsupportedOperationException("Version " + current.name() + " doesn't Support this Method yet (from " + from.name() + ")");
        } else if(current.versionValue > to.versionValue) {
            if(targetClass == null && to != Version.LATEST) {
                throw new UnsupportedOperationException("This Method hasn't been implemented yet for " + current.name());
            } else {
                return replacedBy.getClazz();
            }
        } else {
            targetClass = ReflectionUtils.getMCServerClassByName(className, !newMapping);
            return targetClass;
        }
    }

}
