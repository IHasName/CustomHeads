package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class MethodWrapper<T> extends WrapperBase<MethodWrapper<T>, Method> {

    private final String methodName;

    public MethodWrapper(Version from, Version to, String methodName, Class<?> clazz, Class<?>... params) {
        this(from, to, methodName, clazz, null, params);
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> clazz, MethodWrapper<T> replacedBy, Class<?>... params) {
        super(from, to, clazz, params, replacedBy);
        this.methodName = methodName;
    }

    protected Method resolveValue() throws Throwable {
        return ReflectionUtils.getMethod(methodName, targetClass, params);
    }

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get Method for " + LoggingUtils.methodLikeString(this.methodName, super.params));
    }

    public T invokeOn(Object object, Object... params) throws InvocationTargetException, IllegalAccessException {
        return (T) resolve().invoke(object, params);
    }
}
