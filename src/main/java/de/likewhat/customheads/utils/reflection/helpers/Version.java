package de.likewhat.customheads.utils.reflection.helpers;

import org.bukkit.Bukkit;

import java.util.Arrays;

public enum Version {

    OLDEST(0),
    V1_8_R1(181), V1_8_R2(182), V1_8_R3(183),
    V1_9_R1(191), V1_9_R2(192),
    V1_10_R1(1101),
    V1_11_R1(1111),
    V1_12_R1(1121),
    V1_13_R1(1131), V1_13_R2(1132),
    V1_14_R1(1141),
    V1_15_R1(1151),
    V1_16_R1(1161), V1_16_R2(1162), V1_16_R3(1163),
    V1_17_R1(1171),
    V1_18_R1(1181), V1_18_R2(1182),
    V1_19_R1(1191), V1_19_R2(1192), V1_19_R3(1193),
    V1_20_R1(1201), V1_20_R2(1202), V1_20_R3(1203),
    V1_20_5(1205), V1_20_6(1206), V1_21_0(1210),
    LATEST(Integer.MAX_VALUE),

    LATEST_UPDATE(V1_20_R3);

    private static final String PACKET_NAME = Bukkit.getServer().getClass().getPackage().getName();
    private static final String RAW_VERSION;

    static {
        String packetVersion = PACKET_NAME.substring(PACKET_NAME.lastIndexOf('.') + 1);
        if(packetVersion.isEmpty()) { // We don't have a Craftbukkit Packet Version so we use the Server Version itself (since 1.20.5)
            String bukkitVersionString = Bukkit.getBukkitVersion();
            RAW_VERSION = bukkitVersionString.substring(0, bukkitVersionString.indexOf("-") - 1);
        } else {
            RAW_VERSION = packetVersion;
        }
    }

    public static Version getCurrentVersion() {
        if(currentVersion != null) {
            return currentVersion;
        }
        currentVersion = fromValue(Integer.parseInt(RAW_VERSION.replaceAll("\\D+", "")));
        return currentVersion;
    }

    public static String getCurrentVersionRaw() {
        return RAW_VERSION;
    }

    public final int versionValue;

    Version(int versionValue) {
        this.versionValue = versionValue;
    }

    Version(Version otherVersion) {
        this.versionValue = otherVersion.versionValue;
    }

    private static Version fromValue(int value) {
        return Arrays.stream(values()).filter(r -> r.versionValue == value).findFirst().orElse(LATEST);
    }

    private static Version currentVersion;

    public boolean isOlderThan(Version version) {
        return this.versionValue < version.versionValue;
    }

    public boolean isNewerThan(Version version) {
        return this.versionValue > version.versionValue;
    }

}
