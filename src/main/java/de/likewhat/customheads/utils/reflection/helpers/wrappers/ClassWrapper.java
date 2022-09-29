package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.utils.reflection.helpers.Version;

/**
 * Wrapper for Java Classes
 */
public class ClassWrapper extends WrapperBase<ClassWrapper, Class<?>> {

    protected String className;

    public ClassWrapper(Version from, Version to, WrapperBase<ClassWrapper, Class<?>> replacedBy, String className) {
        super(from, to, replacedBy);
        this.className = className;
    }

    @Override
    protected Class<?> resolveValue() throws Throwable {
        return Class.forName(className);
    }
}
