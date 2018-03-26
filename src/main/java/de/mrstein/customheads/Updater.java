package de.mrstein.customheads;

import de.mrstein.customheads.utils.Configs;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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

    public static Object[] getChangeLog() {
        if(lastChangelog != null) {
            return lastChangelog;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(DESCRIPTION_URL).openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            connection.setReadTimeout(5000);

            JSONArray updatesArray = (JSONArray) JSONValue.parseWithException(new InputStreamReader(connection.getInputStream()));
            String updateTitle = ((JSONObject) updatesArray.get(updatesArray.size() - 1)).get("title").toString();
            int updateID = Integer.parseInt(((JSONObject) updatesArray.get(updatesArray.size() - 1)).get("id").toString());
            lastChangelog = new Object[] { updateTitle, updateID };
            return lastChangelog;
        } catch(Exception e) {
            return new String[0];
        }
    }

    public static Object[] getLastUpdate(boolean... force) {
        Configs update = new Configs(CustomHeads.getInstance(), "update.yml", true);
        if((System.currentTimeMillis() - update.get().getLong("lastUpdateCheck")) >= 86400000) {
            lastUpdate = null;
            lastChangelog = null;
            update.get().set("lastUpdateCheck", System.currentTimeMillis());
            update.save();
        }
        if(lastUpdate != null) {
            return lastUpdate;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            connection.setReadTimeout(5000);

            JSONArray versionsArray = (JSONArray) JSONValue.parseWithException(new InputStreamReader(connection.getInputStream()));
            Double latestVersion = Double.parseDouble(((JSONObject) versionsArray.get(versionsArray.size() - 1)).get("name").toString());
            double current = Double.parseDouble(CustomHeads.getInstance().getDescription().getVersion());
            if (latestVersion > current || (force.length > 0 && force[0])) {
                Object[] changeLog = getChangeLog();
                int verbe = (int) (latestVersion * 10 - current * 10);
                lastUpdate = new Object[] { latestVersion, changeLog[0], changeLog[1], verbe, versionsArray.size()};
                return lastUpdate;
            }
        } catch (Exception e) {
            return new String[0];
        }
        return new String[0];
    }

}
