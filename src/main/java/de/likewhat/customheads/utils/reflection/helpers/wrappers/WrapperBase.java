package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import lombok.Getter;

import java.util.logging.Level;

/**
 * Base Class to use for different Wrappers
 * @param <M> The Class itself (used for Constructors replacedBy)
 * @param <T> The Value that {@link de.likewhat.customheads.utils.reflection.helpers.wrappers.WrapperBase#resolve()} should return
 */
public abstract class WrapperBase<M extends WrapperBase<M, T>, T> {

    @Getter
    protected Version from;
    @Getter
    protected Version to;
    protected WrapperBase<M, T> replacedBy;

    @Getter
    private final WrapperType wrapperType;

    protected T resolvedValue;

    public WrapperBase(WrapperType wrapperType, Version from, Version to, WrapperBase<M, T> replacedBy) {
        this.from = from == null ? Version.OLDEST : from;
        this.to = to == null ? Version.LATEST : to;
        if(this.from.isNewerThan(this.to)) {
            throw new IllegalArgumentException("From-Version should be older than To-Version");
        }
        if(this.to.isOlderThan(this.from)) {
            throw new IllegalArgumentException("To-Version should be newer than From-Version");
        }
        this.replacedBy = replacedBy;
        this.wrapperType = wrapperType;
    }

    /**
     * Resolve for Value T
     *
     * @return Value of T
     */
    public final T resolve() {
        if(resolvedValue != null) {
            return resolvedValue;
        }
        Version current = Version.getCurrentVersion();
        if(current.isOlderThan(from)) {
            throw new UnsupportedOperationException("Version " + current.name() + " isn't supported yet (from " + from.name() + ")");
        } else if(current.isNewerThan(to)) {
            if(replacedBy == null) {
                throw new UnsupportedOperationException("This " + wrapperType.getTypeClass().getName() + " hasn't been implemented yet for " + current.name() + " (Up to " + to + ")" + (current == Version.LATEST ? (" ( " + Version.getCurrentVersionRaw() + ")") : ""));
            } else {
                resolvedValue = replacedBy.getResolver().resolve();
                return resolvedValue;
            }
        } else {
            try {
                resolvedValue = resolveValue();
                if(resolvedValue == null) {
                    errorHandler(new NullPointerException("Resolved Value is null"));
                }
            } catch(Throwable e) {
                errorHandler(e);
            }
            return resolvedValue;
        }
    }

    /**
     * Gets the actual Resolver for the given Type
     *
     * @return Resolver for T
     */
    protected <M extends WrapperBase<M, T>> M getResolver() {
        Version current = Version.getCurrentVersion();
        if(current.isOlderThan(from)) {
            throw new UnsupportedOperationException("Version " + current.name() + " isn't supported yet (from " + from.name() + ")");
        } else if(current.isNewerThan(to)) {
            if(replacedBy == null) {
                throw new UnsupportedOperationException("This " + wrapperType.getTypeClass().getName() + " hasn't been implemented yet for " + current.name() + (current == Version.LATEST ? ("( " + Version.getCurrentVersionRaw() + ")") : ""));
            } else {
                return replacedBy.getResolver();
            }
        } else {
            return (M) this;
        }
    }

    public boolean supports(Version version) {
        return from.isOlderThan(version) && to.isNewerThan(version);
    }

    protected abstract T resolveValue() throws Throwable;

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to resolve Value", throwable);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{type=" + this.wrapperType + " fromVersion=" + this.from + " toVersion=" + this.to + "}";
    }

    public void test() {
        resolve();
    }

}
