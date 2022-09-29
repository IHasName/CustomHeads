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

    protected T resolvedValue;

    public WrapperBase(Version from, Version to, WrapperBase<M, T> replacedBy) {
        this.from = from == null ? Version.OLDEST : from;
        this.to = to == null ? Version.LATEST : to;
        if(this.from.isNewerThan(this.to)) {
            throw new IllegalArgumentException("From-Version should be older than To-Version");
        }
        if(this.to.isOlderThan(this.from)) {
            throw new IllegalArgumentException("To-Version should be newer than From-Version");
        }
        this.replacedBy = replacedBy;
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
            throw new UnsupportedOperationException("Version " + current.name() + " doesn't Support this Method yet (from " + from.name() + ")");
        } else if(current.isNewerThan(to)) {
            if(replacedBy == null) {
                throw new UnsupportedOperationException("This Method hasn't been implemented yet for " + current.name());
            } else {
                return replacedBy.getResolver().resolve();
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
            throw new UnsupportedOperationException("Version " + current.name() + " doesn't Support this Method yet (from " + from.name() + ")");
        } else if(current.isNewerThan(to)) {
            if(replacedBy == null) {
                throw new UnsupportedOperationException("This Method hasn't been implemented yet for " + current.name());
            } else {
                return replacedBy.getResolver();
            }
        } else {
            return (M) this;
        }
    }

    protected abstract T resolveValue() throws Throwable;

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to resolve Value", throwable);
    }

}
