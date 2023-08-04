package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.reflection.helpers.ReflectionUtils;
import de.likewhat.customheads.utils.reflection.helpers.Version;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class FieldWrapper extends WrapperBase<FieldWrapper, Field> {

    private final Class<?> fieldClass;
    private final String fieldName;

    public FieldWrapper(Class<?> fieldClass, String fieldName, Version from, Version to) {
        this(fieldClass, fieldName, from, to, null);
    }

    public FieldWrapper(ClassWrapper fieldClass, String fieldName, Version from, Version to) {
        this(fieldClass, fieldName, from, to, null);
    }

    public FieldWrapper(ClassWrapper fieldClass, String fieldName, Version from, Version to, WrapperBase<FieldWrapper, Field> replacedBy) {
        super(WrapperType.FIELD, from, to, replacedBy);
        if(fieldClass.supports(Version.getCurrentVersion())) {
            this.fieldClass = fieldClass.resolve();
        } else {
            this.fieldClass = null;
        }
        this.fieldName = fieldName;
    }

    public FieldWrapper(Class<?> fieldClass, String fieldName, Version from, Version to, WrapperBase<FieldWrapper, Field> replacedBy) {
        super(WrapperType.FIELD, from, to, replacedBy);

        this.fieldClass = fieldClass;
        this.fieldName = fieldName;
    }

    protected Field resolveValue() throws NoSuchFieldException {
        return ReflectionUtils.getField(fieldClass, fieldName);
    }

    protected void errorHandler(Throwable throwable) {
        CustomHeads.getPluginLogger().log(Level.WARNING, "Failed to find Field for " + fieldName + " in " + fieldClass.getCanonicalName(), throwable);
    }

    public Object getInstance(Object obj) throws IllegalAccessException {
        return ReflectionUtils.getFieldValue(resolve(), obj);
    }
}
