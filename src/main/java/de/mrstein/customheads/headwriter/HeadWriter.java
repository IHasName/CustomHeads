package de.mrstein.customheads.headwriter;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.ItemEditor;
import org.bukkit.Bukkit;
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

    private static BlockFace[] axis = {BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST};
    private static BlockFace[] axisshifted = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    private static HashMap<Player, String> undoList = new HashMap<>();
    private Player player;
    private String text;
    private HeadFontType fontType;

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
        int timesSucessful = 0;
        for (int i = 0; i < times; i++) {
            if (undoList.containsKey(player)) {
                if (undoList.containsKey(player)) {
                    String[] history = undoList.get(player).split("/#/");
                    String[] h = history[history.length - 1].split(",");
                    for (String a : h) {
                        String[] data = a.split(":");
                        Bukkit.getWorld(data[2]).getBlockAt(Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5])).setTypeIdAndData(Integer.parseInt(data[0]), Byte.parseByte(data[1]), false);
                    }
                    if (undoList.get(player).lastIndexOf("/#/") < 0) {
                        undoList.remove(player);
                    } else {
                        undoList.put(player, undoList.get(player).substring(0, undoList.get(player).lastIndexOf("/#/")));
                    }
                    timesSucessful++;
                }
            }
        }
        sendTo.sendMessage(timesSucessful == 0 ? CustomHeads.getLanguageManager().UNDO_NOTHING_LEFT : timesSucessful == 1 ? CustomHeads.getLanguageManager().UNDO_SUCCESSFUL.replace("{TIMES}", "") : CustomHeads.getLanguageManager().UNDO_SUCCESSFUL.replace("{TIMES}", "(" + timesSucessful + "x)"));
    }

    public void writeAt(Location location) {
        StringBuilder repBlock = new StringBuilder();
        boolean recHis = player != null;
        if (text.length() > 40) {
            if (recHis) {
                player.sendMessage(CustomHeads.getLanguageManager().WRITE_TO_LONG);
                return;
            } else {
                throw new IllegalArgumentException("Text cannot be longer than 40 Characters");
            }
        }
        if (recHis) {
            player.sendMessage(CustomHeads.getLanguageManager().WRITE_WRITING.replace("{TEXT}", text));
        }
        Location loc = location;
        int faceindex = Math.round((location.getYaw() * (-1)) / 90f) & 0x3;
        BlockFace face = axis[faceindex].getOppositeFace();
        loc = loc.getBlock().getLocation().add(face.getModX(), face.getModY(), face.getModZ());

        if (recHis) {
            repBlock.append(undoList.containsKey(player) ? undoList.get(player) + "/#/" : "");
        }
        for (int i = 0; i < text.length(); i++) {
            if (recHis) {
                if (text.length() > 1) {
                    repBlock.append((i + 1) < text.length() ? loc.getBlock().getTypeId() + ":" + loc.getBlock().getData() + ":" + loc.getBlock().getWorld().getName() + ":" + loc.getBlock().getX() + ":" + loc.getBlock().getY() + ":" + loc.getBlock().getZ() + "," : loc.getBlock().getTypeId() + ":" + loc.getBlock().getData() + ":" + loc.getBlock().getWorld().getName() + ":" + loc.getBlock().getX() + ":" + loc.getBlock().getY() + ":" + loc.getBlock().getZ());
                } else {
                    repBlock = new StringBuilder(loc.getBlock().getTypeId() + ":" + loc.getBlock().getData() + ":" + loc.getBlock().getWorld().getName() + ":" + loc.getBlock().getX() + ":" + loc.getBlock().getY() + ":" + loc.getBlock().getZ());
                }
            }
            try {
                CustomHeads.getApi().setSkull(loc.getBlock(), new ItemEditor(fontType.getCharacter(text.charAt(i))).getTexture(), axis[Math.round((location.getYaw() * (-1)) / 90f) & 0x3]);
            } catch (Exception e) {
                if (recHis) {
                    player.sendMessage("§cUnsupported Character at collum " + (i + 1) + ": " + text.charAt(i));
                } else {
                    CustomHeads.getInstance().getLogger().log(Level.WARNING, "Could not write Text: Unsupported Character '" + text.charAt(i) + "'");
                }
            }

            BlockFace bface = axisshifted[faceindex].getOppositeFace();
            loc = loc.getBlock().getLocation().add(bface.getModX(), bface.getModY(), bface.getModZ());
        }
        if (recHis) {
            undoList.put(player, repBlock.toString());
            if (undoList.get(player).split("/#/").length > (CustomHeads.getHeadsConfig().get().getInt("maxUndoHistory") == 0 ? 6 : CustomHeads.getHeadsConfig().get().getInt("maxUndoHistory"))) {
                String[] str = undoList.get(player).split("/#/");
                StringBuilder undoString = new StringBuilder();
                for (int i = 1; i < str.length; i++) {
                    undoString.append(i + 1 < str.length ? str[i] + "/#/" : str[i]);
                }
                undoList.put(player, undoString.toString());
            }
        }
    }

}
