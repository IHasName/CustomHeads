package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.LoggingUtils;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

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

    private final String methodName;
    private Class<?> targetClass;
    private Class<?>[] parameters;

    private final ClassWrapper targetClassWrapper;
    private final ClassWrapper[] parametersWrapper;

    private final boolean useWrappers;

    public MethodWrapper(Version from, Version to, String methodName, ClassWrapper clazz, ClassWrapper... parameters) {
        this(from, to, methodName, clazz, null, parameters);
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> clazz, Class<?>... parameters) {
        this(from, to, methodName, clazz, null, parameters);
    }

    public MethodWrapper(Version from, Version to, String methodName, ClassWrapper targetClass, MethodWrapper<?> replacedBy, ClassWrapper... parameters) {
        super(WrapperType.METHOD, from, to, replacedBy);
        this.methodName = methodName;

        this.targetClassWrapper = targetClass;
        this.parametersWrapper = parameters;

        this.useWrappers = true;
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> targetClass, MethodWrapper<?> replacedBy, Class<?>... parameters) {
        super(WrapperType.METHOD, from, to, replacedBy);
        this.methodName = methodName;

        this.targetClass = targetClass;
        this.parameters = parameters;

        this.targetClassWrapper = null;
        this.parametersWrapper = null;

        this.useWrappers = false;
    }

    protected Method resolveValue() throws Throwable {
        if(useWrappers) {
            this.targetClass = targetClassWrapper.resolve();
            this.parameters = Arrays.stream(parametersWrapper).map(WrapperBase::resolve).toArray(Class[]::new);
        }

        return ReflectionUtils.getMethod(methodName, targetClass, parameters);
    }

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get Method for " + LoggingUtils.methodLikeString(this.methodName, this.parameters));
    }

    public T invokeOn(Object object, Object... params) throws InvocationTargetException, IllegalAccessException {
        return (T) this.getResolver().resolve().invoke(object, params);
    }

}
