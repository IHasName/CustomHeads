package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

/**
 * Wrapper for Java Classes
 */
public class ClassWrapper extends WrapperBase<ClassWrapper, Class<?>> {

    protected String className;

    private final Class<?> preResolvedClass;

    public ClassWrapper(Version from, Version to, WrapperBase<ClassWrapper, Class<?>> replacedBy, String className) {
        super(WrapperType.CLASS, from, to, replacedBy);
        this.className = className;
        this.preResolvedClass = null;
    }

    private ClassWrapper(Class<?> clazz) {
        super(WrapperType.CLASS, null, null, null);
        this.preResolvedClass = clazz;
    }

    public static ClassWrapper from(Class<?> clazz) {
        return new ClassWrapper(clazz);
    }

    /**
     * Resolves to the most recent ClassWrapper and returns its Class Name
     * @return The Class Name of the newest Wrapper
     */
    public String getFinalClassName() {
        return ((ClassWrapper) getResolver()).className;
    }

    @Override
    protected Class<?> resolveValue() throws Throwable {
        return this.preResolvedClass == null ? ReflectionUtils.getClassByName(className) : preResolvedClass;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{className=" + className + " fromVersion=" + this.from + " toVersion=" + this.to + "}";
    }
}
