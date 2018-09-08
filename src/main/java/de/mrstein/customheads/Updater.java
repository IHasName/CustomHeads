package de.mrstein.customheads;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Updater using Spiget by <b>inventivetalent</b>
 * and Class by <b>iAmGio</b>
 */
public class Updater {

    private static final String VERSION_URL = "https://api.spiget.org/v2/resources/29057/versions?size=2147483647&spiget__ua=SpigetDocs";
    private static final String DESCRIPTION_URL = "https://api.spiget.org/v2/resources/29057/updates?size=2147483647&spiget__ua=SpigetDocs";
    private static final String USER_AGENT = "UC-CustomHeads";

    private static Object[] lastUpdate = null;
    private static Object[] lastChangelog = null;

    private static JsonParser parser = new JsonParser();

    public static Object[] getChangeLog() {
        if (lastChangelog != null) {
            return lastChangelog;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(DESCRIPTION_URL).openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            connection.setReadTimeout(5000);

            JsonArray updatesArray = parser.parse(new InputStreamReader(connection.getInputStream())).getAsJsonArray();
            String updateTitle = updatesArray.get(updatesArray.size() - 1).getAsJsonObject().get("title").toString();
            int updateID = Integer.parseInt(updatesArray.get(updatesArray.size() - 1).getAsJsonObject().get("id").toString());
            lastChangelog = new Object[]{updateTitle, updateID};
            return lastChangelog;
        } catch (Exception e) {
            return new String[0];
        }
    }

    public static Object[] getLastUpdate(boolean... force) {
        if ((System.currentTimeMillis() - CustomHeads.getUpdateFile().get().getLong("lastUpdateCheck")) >= 86400000) {
            lastUpdate = null;
            lastChangelog = null;
            CustomHeads.getUpdateFile().get().set("lastUpdateCheck", System.currentTimeMillis());
            CustomHeads.getUpdateFile().save();
        }
        if (lastUpdate != null) {
            return lastUpdate;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            connection.setReadTimeout(5000);

            JsonArray versionsArray = parser.parse(new InputStreamReader(connection.getInputStream())).getAsJsonArray();
            int latestVersion = Integer.parseInt(versionsArray.get(versionsArray.size() - 1).getAsJsonObject().get("name").toString().replace(".", ""));
            int current = Integer.parseInt(CustomHeads.getInstance().getDescription().getVersion().replace(".", ""));
            if (latestVersion > current || (force.length > 0 && force[0])) {
                Object[] changeLog = getChangeLog();
                int verbe = (latestVersion - current);
                lastUpdate = new Object[]{versionsArray.get(versionsArray.size() - 1).getAsJsonObject().get("name").toString(), changeLog[0], changeLog[1], verbe, versionsArray.size()};
                return lastUpdate;
            }
        } catch (Exception e) {
            return new String[0];
        }
        return new String[0];
    }

}
