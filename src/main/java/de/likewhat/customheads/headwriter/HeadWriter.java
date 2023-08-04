package de.likewhat.customheads.headwriter;

import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.ItemEditor;
import de.likewhat.customheads.utils.reflection.helpers.Version;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Level;

/*
 *  Project: CustomHeads in HeadWriter
 *     by LikeWhat
 */

public class HeadWriter {

    private static final BlockFace[] AXIS_SHIFTED = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    private static final BlockFace[] AXIS = {BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST};
    private static final HashMap<Player, String> UNDO_LIST = new HashMap<>();
    private static final String SPLIT_STRING = "/#/";

    private final HeadFontType fontType;
    private final String text;

    private Player player;

    public HeadWriter(HeadFontType fontType, String text) {
        this.fontType = fontType;
        this.text = text;
    }

    public HeadWriter(HeadFontType fontType, String text, Player player) {
        this.fontType = fontType;
        this.text = text;
        this.player = player;
    }

    public static void undoWriting(Player player, int times, Player sendTo) {
        int timesSuccessful = HistoryManager.rollbackBlocks(player.getUniqueId(), times);
        /*int timesSuccessful = 0;
        for (int i = 0; i < times; i++) {
            if (UNDO_LIST.containsKey(player)) {
                if (UNDO_LIST.containsKey(player)) {
                    String[] history = UNDO_LIST.get(player).split(SPLIT_STRING);
                    String[] h = history[history.length - 1].split(",");
                    for (String a : h) {
                        String[] data = a.split(":");
                        Bukkit.getWorld(data[2]).getBlockAt(Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5])).setTypeIdAndData(Integer.parseInt(data[0]), Byte.parseByte(data[1]), false);
                    }
                    if (UNDO_LIST.get(player).lastIndexOf(SPLIT_STRING) < 0) {
                        UNDO_LIST.remove(player);
                    } else {
                        UNDO_LIST.put(player, UNDO_LIST.get(player).substring(0, UNDO_LIST.get(player).lastIndexOf(SPLIT_STRING)));
                    }
                    timesSuccessful++;
                }
            }
        }*/
        sendTo.sendMessage(timesSuccessful == 0 ? CustomHeads.getLanguageManager().UNDO_NOTHING_LEFT : timesSuccessful == 1 ? CustomHeads.getLanguageManager().UNDO_SUCCESSFUL.replace("{TIMES}", "") : CustomHeads.getLanguageManager().UNDO_SUCCESSFUL.replace("{TIMES}", "(" + timesSuccessful + "x)"));
    }

    public void writeAt(Location location) {
        //StringBuilder repBlock = new StringBuilder();
        boolean playerExec = player != null;
        if (text.length() > 40) {
            if (playerExec) {
                player.sendMessage(CustomHeads.getLanguageManager().WRITE_TO_LONG);
                return;
            } else {
                throw new IllegalArgumentException("Text cannot be longer than 40 Characters");
            }
        }
        if (playerExec) {
            player.sendMessage(CustomHeads.getLanguageManager().WRITE_WRITING.replace("{TEXT}", text));
        }
        Location loc = location;
        int faceIndex = Math.round((location.getYaw() * (-1)) / 90f) & 0x3;
        BlockFace face = AXIS[faceIndex].getOppositeFace();
        loc = loc.getBlock().getLocation().add(face.getModX(), face.getModY(), face.getModZ());

        BlockHistoryCollection history = null;
        if (playerExec) {
            history = new BlockHistoryCollection();
            //repBlock.append(UNDO_LIST.containsKey(player) ? UNDO_LIST.get(player) + SPLIT_STRING : "");
        }
        for (int i = 0; i < text.length(); i++) {
            if (playerExec) {
                history.addBlock(loc.getBlock());
                /*if (text.length() > 1) {
                    repBlock.append((i + 1) < text.length() ? loc.getBlock().getTypeId() + ":" + loc.getBlock().getData() + ":" + loc.getBlock().getWorld().getName() + ":" + loc.getBlock().getX() + ":" + loc.getBlock().getY() + ":" + loc.getBlock().getZ() + "," : loc.getBlock().getTypeId() + ":" + loc.getBlock().getData() + ":" + loc.getBlock().getWorld().getName() + ":" + loc.getBlock().getX() + ":" + loc.getBlock().getY() + ":" + loc.getBlock().getZ());
                } else {
                    repBlock = new StringBuilder(loc.getBlock().getTypeId() + ":" + loc.getBlock().getData() + ":" + loc.getBlock().getWorld().getName() + ":" + loc.getBlock().getX() + ":" + loc.getBlock().getY() + ":" + loc.getBlock().getZ());
                }*/
            }
            try {
                BlockFace f = AXIS[Math.round((location.getYaw() * (-1)) / 90f) & 0x3];
                CustomHeads.getApi().setSkull(loc.getBlock(), new ItemEditor(fontType.getCharacter(text.charAt(i))).getTexture(), Version.getCurrentVersion().isNewerThan(Version.V1_12_R1) ? f.getOppositeFace() : f);
            } catch (Exception e) {
                if (playerExec) {
                    player.sendMessage("§cUnsupported Character at Column " + (i + 1) + ": " + text.charAt(i));
                } else {
                    CustomHeads.getPluginLogger().log(Level.WARNING, "Could not write Text: Unsupported Character '" + text.charAt(i) + "'");
                }
            }

            BlockFace bFace = AXIS_SHIFTED[faceIndex].getOppositeFace();
            loc = loc.getBlock().getLocation().add(bFace.getModX(), bFace.getModY(), bFace.getModZ());
        }
        if (playerExec) {
            HistoryManager.addBlockHistory(player.getUniqueId(), history);
            /*UNDO_LIST.put(player, repBlock.toString());
            if (UNDO_LIST.get(player).split(SPLIT_STRING).length > (CustomHeads.getHeadsConfig().get().getInt("maxUndoHistory") == 0 ? 6 : CustomHeads.getHeadsConfig().get().getInt("maxUndoHistory"))) {
                String[] str = UNDO_LIST.get(player).split(SPLIT_STRING);
                StringBuilder undoString = new StringBuilder();
                for (int i = 1; i < str.length; i++) {
                    undoString.append(i + 1 < str.length ? str[i] + SPLIT_STRING : str[i]);
                }
                UNDO_LIST.put(player, undoString.toString());
            }*/
        }
    }

}
