package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class MethodWrapper<T> {

    @Getter private Version from;
    @Getter private Version to;
    private String methodName;
    private Class<?>[] params;

    private Class<?> targetClass;
    private Method targetMethod;
    private MethodWrapper replacedBy;

    public MethodWrapper(Version from, Version to, String methodName, MinecraftServerClassWrapper clazz, Class<?>... params) {
        this(from, to, methodName, clazz, null, params);
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> clazz, Class<?>... params) {
        this(from, to, methodName, clazz, null, params);
    }

    public MethodWrapper(Version from, Version to, String methodName, MinecraftServerClassWrapper classWrapper, MethodWrapper replacedBy, Class<?>... params) {
        this(from, to, methodName, classWrapper.getClazz(), replacedBy, params);
    }

    public MethodWrapper(Version from, Version to, String methodName, Class<?> clazz, MethodWrapper replacedBy, Class<?>... params) {
        this.from = from == null ? Version.OLDEST : from;
        this.to = to == null ? Version.LATEST : to;
        this.methodName = methodName;
        this.replacedBy = replacedBy;
        this.params = params;

        this.targetClass = clazz;
    }

    public Method getMethod() {
        if(targetMethod != null) {
            return targetMethod;
        }
        Version current = Version.getCurrentVersion();
        if(current.isOlderThan(from)) {
            throw new UnsupportedOperationException("Version " + current.name() + " doesn't Support this Method yet (from " + from.name() + ")");
        } else if(current.isNewerThan(to)) {
            if(replacedBy == null) {
                throw new UnsupportedOperationException("Version " + current.name() + " doesn't Support this Method yet (from " + from.name() + ")");
            } else {
                return replacedBy.getMethod();
            }
        } else {
            try {
                this.targetMethod = ReflectionUtils.getMethod(methodName, targetClass, params);
                return targetMethod;
            } catch (NoSuchMethodException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to get Method: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public T invokeOn(Object object, Object... params) throws InvocationTargetException, IllegalAccessException {
        return (T) getMethod().invoke(object, params);
    }

}
