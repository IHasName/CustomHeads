package de.likewhat.customheads.utils;

/*
 *  Project: CustomHeads in NMSUtils
 *     by LikeWhat
 *
 *  created on 29.12.2019 at 00:36
 */

public class NMSUtils {

    public static Enum<?> getEnumFromClass(Class clazz, String enumName) {
        try {
            for (Object eenum : clazz.getEnumConstants()) {
                if (((String)eenum.getClass().getMethod("name").invoke(eenum)).equalsIgnoreCase(enumName)) {
                    return (Enum<?>) eenum;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
