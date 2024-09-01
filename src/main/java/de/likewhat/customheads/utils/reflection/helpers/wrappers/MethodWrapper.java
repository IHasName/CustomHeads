package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Wrapper for Methods
 *
 * @param <T> The Type that the Method returns
 */
public class MethodWrapper<T> extends WrapperBase<MethodWrapper<?>, Method> {

    @Getter
    private final String methodName;
    private Class<?> targetClass;
    private Class<?>[] parameters;

    private final ClassWrapper targetClassWrapper;
    private final ClassWrapper[] parametersWrapper;

    public MethodWrapper(Version from, Version to, String methodName, ClassWrapper targetClass, ClassWrapper... parameters) {
        this(from, to, methodName, targetClass, null, parameters);
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> targetClass, Class<?>... parameters) {
        this(from, to, methodName, targetClass, null, parameters);
    }

    public MethodWrapper(Version from, Version to, String methodName, ClassWrapper targetClass, MethodWrapper<?> replacedBy, ClassWrapper... parameters) {
        super(WrapperType.METHOD, from, to, replacedBy);
        this.methodName = methodName;

        this.targetClassWrapper = targetClass;
        this.parametersWrapper = parameters;
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> targetClass, MethodWrapper<?> replacedBy, Class<?>... parameters) {
        super(WrapperType.METHOD, from, to, replacedBy);
        this.methodName = methodName;

        this.targetClass = targetClass;
        this.parameters = parameters;

        this.targetClassWrapper = null;
        this.parametersWrapper = null;
    }

    protected Method resolveValue() throws Throwable {
        if(this.targetClassWrapper != null && this.parametersWrapper != null) {
            this.targetClass = targetClassWrapper.resolve();
            this.parameters = Arrays.stream(parametersWrapper).map(WrapperBase::resolve).toArray(Class[]::new);
        }

        if(ReflectionUtils.methodExists(targetClass, methodName, parameters)) {
            return ReflectionUtils.getMethod(methodName, targetClass, parameters);
        }

        return ReflectionUtils.getDeclaredMethod(methodName, targetClass, parameters);
    }

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get Method for " + LoggingUtils.methodLikeString(this.methodName, this.parameters), throwable);
    }

    public T invokeOn(Object object, Object... params) throws InvocationTargetException, IllegalAccessException {
        return (T) ReflectionUtils.invokeMethod(this.getResolver().resolve(), object, params);
    }

    @Override
    public String toString() {
        MethodWrapper<T> actualResolver = (MethodWrapper<T>) this.getResolver();
        return "MethodWrapper{method=" + LoggingUtils.methodLikeString(actualResolver.methodName, actualResolver.parameters) + " fromVersion=" + actualResolver.from + " toVersion=" + actualResolver.to + "}";
    }

    public Class<?> getTargetClass() {
        return targetClassWrapper == null ? targetClass : targetClassWrapper.resolve();
    }
}
