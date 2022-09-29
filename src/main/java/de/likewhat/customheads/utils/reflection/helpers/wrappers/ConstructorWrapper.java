package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Wrapper for Constructors
 *
 * @param <T> The Type that the given Constructor returns
 */
public class ConstructorWrapper<T> extends WrapperBase<ConstructorWrapper<T>, Constructor<?>> {

    private final Class<?> targetClass;
    private final Class<?>[] paramTypes;

    public ConstructorWrapper(Version from, Version to, ClassWrapper targetClass, ClassWrapper... paramTypes) {
        this(from, to, targetClass.resolve(), null, (Class<?>[]) Arrays.stream(paramTypes).map(WrapperBase::resolve).toArray());
    }

    public ConstructorWrapper(Version from, Version to, ClassWrapper targetClass, Class<?>... paramTypes) {
        this(from, to, targetClass.resolve(), null, paramTypes);
    }

    public ConstructorWrapper(Version from, Version to, Class<?> targetClass, ClassWrapper... paramTypes) {
        this(from, to, targetClass, null, (Class<?>[]) Arrays.stream(paramTypes).map(WrapperBase::resolve).toArray());
    }

    public ConstructorWrapper(Version from, Version to, Class<?> targetClass, Class<?>... paramTypes) {
        this(from, to, targetClass, null, paramTypes);
    }

    public ConstructorWrapper(Version from, Version to, ClassWrapper targetClass, ConstructorWrapper<T> replacedBy, ClassWrapper... paramTypes) {
        this(from, to, targetClass.resolve(), replacedBy, (Class<?>[]) Arrays.stream(paramTypes).map(WrapperBase::resolve).toArray());
    }

    public ConstructorWrapper(Version from, Version to, Class<?> targetClass, ConstructorWrapper<T> replacedBy, Class<?>... paramTypes) {
        super(from, to, replacedBy);
        this.targetClass = targetClass;
        this.paramTypes = paramTypes;
    }

    protected Constructor<?> resolveValue() throws Throwable {
        return ReflectionUtils.getConstructorDynamic(this.targetClass, this.paramTypes);
    }

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to resolve Constructor for " + LoggingUtils.methodLikeString(this.targetClass.getSimpleName(), this.paramTypes));
    }

    public T construct(Object... params) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (T) resolve().newInstance(params);
    }
}
