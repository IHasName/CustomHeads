package de.likewhat.customheads.utils;

import de.likewhat.customheads.CustomHeads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LoggingUtils {

    private static final List<String> alreadyLogged = new ArrayList<>();

    public static void logOnce(Level level, String string) {
        if(!alreadyLogged.contains(string)) {
            alreadyLogged.add(string);
            CustomHeads.getPluginLogger().log(level, string);
        }
    }

    /**
     * Returns a String that is formatted like a Method: name(bodyParams)
     * @param name The "Method" Name
     * @param bodyParams The Classes in the Body
     * @return Formatted String
     */
    public static String methodLikeString(String name, Class<?>... bodyParams) {
        return methodLikeString(name, Class::getCanonicalName, bodyParams);
    }

    /**
     * Returns a String that is formatted like a Method: name(bodyParams)
     * @param name The "Method" Name
     * @param mapper Function that defines how the Body Parameters should get converted
     * @param bodyParams The Classes in the Body
     * @return Formatted String
     */
    public static String methodLikeString(String name, Function<Class<?>, String> mapper, Class<?>... bodyParams) {
        return name + "(" + Arrays.stream(bodyParams).map(mapper).collect(Collectors.joining(", ")) + ")";
    }

}
