package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class ConstructorWrapper<T> extends WrapperBase<ConstructorWrapper<T>, Constructor<?>> {

    public ConstructorWrapper(Version from, Version to, Class<?> clazz, Class<?>... paramTypes) {
        this(from, to, clazz, null, paramTypes);
    }

    public ConstructorWrapper(Version from, Version to, Class<?> clazz, ConstructorWrapper<T> replacedBy, Class<?>... paramTypes) {
        super(from, to, clazz, paramTypes, replacedBy);
    }

    protected Constructor<?> resolveValue() throws Throwable {
        return ReflectionUtils.getConstructorDynamic(super.targetClass, super.params);
    }

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to resolve Constructor for " + LoggingUtils.methodLikeString(super.targetClass.getSimpleName(), super.params));
    }

    public T construct(Object... params) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (T) resolve().newInstance(params);
    }
}
