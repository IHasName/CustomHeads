package de.likewhat.customheads.utils.reflection;

/*
 *  Project: CustomHeads in NBTTagCreator
 *     by LikeWhat
 *
 *  created on 17.12.2019 at 23:29
 */

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.Utils;

public class NBTTagUtils {

    public static final int MC_VERSION;

    static {
        MC_VERSION = Integer.parseInt(CustomHeads.version.split("_")[1]);
    }

    public static Object createNBTTagString(String string) {
        try {
            switch (MC_VERSION) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                    return Utils.getMCServerClassByName("NBTTagString").getConstructor(String.class).newInstance(string);
                case 15:
                case 16:
                    return Utils.getMCServerClassByName("NBTTagString").getMethod("a", String.class).invoke(null, string);
                default:
                    throw new UnsupportedOperationException("Unsupported MC_VERSION: " + MC_VERSION + " Please report this to me on my Discord (Link's on the Spigot Page)");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addObjectToNBTList(Object list, Object objectToAdd) {
        try {
            switch (MC_VERSION) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    list.getClass().getMethod("add", Utils.getMCServerClassByName("NBTBase")).invoke(list, objectToAdd);
                    break;
                case 14:
                case 15:
                case 16:
                    list.getClass().getMethod("add", int.class, Utils.getMCServerClassByName("NBTBase")).invoke(list, list.getClass().getMethod("size").invoke(list), objectToAdd);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported MC_VERSION: " + MC_VERSION + " Please report this to me on my Discord (Link's on the Spigot Page)");
            }
        } catch(Exception e) {

        }
    }

}
