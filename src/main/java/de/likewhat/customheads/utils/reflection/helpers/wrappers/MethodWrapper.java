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
    private Class<?> shouldReturn;
    private Class<?> targetClass;
    private Class<?>[] parameters;

    private final ClassWrapper shouldReturnWrapper;
    private final ClassWrapper targetClassWrapper;
    private final ClassWrapper[] parametersWrapper;

    private final boolean useWrappers;

    private Method tmpReplacementMethod;

    public MethodWrapper(Version from, Version to, String methodName, ClassWrapper shouldReturn, ClassWrapper clazz, ClassWrapper... parameters) {
        this(from, to, methodName, shouldReturn, clazz, null, parameters);
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> shouldReturn, Class<?> clazz, Class<?>... parameters) {
        this(from, to, methodName, shouldReturn, clazz, null, parameters);
    }

    public MethodWrapper(Version from, Version to, String methodName, ClassWrapper shouldReturn, ClassWrapper targetClass, MethodWrapper<?> replacedBy, ClassWrapper... parameters) {
        super(from, to, replacedBy);
        this.methodName = methodName;

        this.shouldReturnWrapper = shouldReturn;
        this.targetClassWrapper = targetClass;
        this.parametersWrapper = parameters;

        this.useWrappers = true;
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> shouldReturn, Class<?> targetClass, MethodWrapper<?> replacedBy, Class<?>... parameters) {
        super(from, to, replacedBy);
        this.methodName = methodName;

        this.shouldReturn = shouldReturn;
        this.targetClass = targetClass;
        this.parameters = parameters;

        this.shouldReturnWrapper = null;
        this.targetClassWrapper = null;
        this.parametersWrapper = null;

        this.useWrappers = false;
    }


    protected Method resolveValue() throws Throwable {
        if(useWrappers) {
            this.shouldReturn = shouldReturnWrapper.resolve();
            this.targetClass = targetClassWrapper.resolve();
            this.parameters = (Class<?>[]) Arrays.stream(parametersWrapper).map(WrapperBase::resolve).toArray();
        }

        return ReflectionUtils.getMethod(methodName, targetClass, parameters);
    }

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to get Method for " + LoggingUtils.methodLikeString(this.methodName, this.parameters));
    }

    public T invokeOn(Object object, Object... params) throws InvocationTargetException, IllegalAccessException {
        Method resolved;
        if(tmpReplacementMethod == null) {
            MethodWrapper<?> actualResolver = this.getResolver();
            resolved = actualResolver.resolve();
            if(actualResolver.shouldReturn != null && !resolved.getReturnType().equals(actualResolver.shouldReturn)) {
                if (to == Version.LATEST && from.isOlderThan(Version.getCurrentVersion())) {
                    LoggingUtils.logOnce(Level.WARNING, "USING EXPERIMENTAL FEATURE TO FIND REPLACEMENT METHOD! THIS MAY BREAK SO DON'T REPORT THIS!!!");
                    // Try to find a replacement Method
                    try {
                        tmpReplacementMethod = ReflectionUtils.findMethodByBody(this.targetClass, actualResolver.shouldReturn, -1, this.parameters);
                        return invokeOn(object, params);
                    } catch(Throwable e) {
                        throw new IllegalStateException(e);
                    }
                } else {
                    throw new IllegalStateException(LoggingUtils.methodLikeString(actualResolver.methodName, actualResolver.parameters) + " doesn't return what it should. Expected " + actualResolver.shouldReturn.getCanonicalName() + " but got " + resolved.getReturnType().getCanonicalName());
                }
            }
        } else {
            resolved = tmpReplacementMethod;
        }
        return (T) resolved.invoke(object, params);
    }

}
