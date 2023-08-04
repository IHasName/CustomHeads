package de.likewhat.customheads.utils.reflection.helpers.wrappers;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Getter
public enum WrapperType {

    CLASS(Class.class),
    CONSTRUCTOR(Constructor.class),
    METHOD(Method.class),
    FIELD(Field.class);

    private final Class<?> typeClass;

    WrapperType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

}
