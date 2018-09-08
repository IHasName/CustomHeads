package de.mrstein.customheads.updaters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

public class GitHubDownloader {

    private static final String GITHUB_REPO_URL = "https://api.github.com/repos/{author}/{projectName}";
    private static final File downloadDir = new File(CustomHeads.getInstance().getDataFolder() + "/downloads");
    private static HashMap<String, Object[]> responseCache = new HashMap<>();
    private String apiURLFormatted;
    private boolean unzip = false;

    public GitHubDownloader(String author, String projectName) {
        apiURLFormatted = GITHUB_REPO_URL.replace("{author}", author).replace("{projectName}", projectName);
    }

    public static void clearCache() {
        responseCache.values().removeIf(times -> (long) times[0] - System.currentTimeMillis() > 600000);
    }

    public GitHubDownloader enableAutoUnzipping() {
        unzip = true;
        return this;
    }

    public void download(String tagName, String assetName, File downloadTo, AfterTask... afterTask) {
        JsonArray releaseList = getResponseAsJson("/releases").getAsJsonArray();

        JsonObject release = null;
        for (JsonElement jsonElement : releaseList) {
            if (jsonElement.getAsJsonObject().get("tag_name").getAsString().equals(tagName)) {
                release = jsonElement.getAsJsonObject();
                break;
            }
        }

        if (release == null)
            throw new NullPointerException("Cannot find release with Tag: " + tagName);

        JsonArray assets = release.getAsJsonArray("assets");
        for (JsonElement jsonElement : assets) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.get("name").getAsString().equals(assetName)) {
                AsyncFileDownloader downloader = new AsyncFileDownloader(jsonObject.get("browser_download_url").getAsString(), assetName, downloadDir.getPath());
                downloader.startDownload(new AsyncFileDownloader.FileDownloaderCallback() {
                    public void complete() {
                        Bukkit.getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Download of " + assetName + " complete.");
                        if (unzip && assetName.endsWith(".zip")) {
                            Utils.unzipFile(new File(downloadDir, assetName), downloadTo);
                            if (afterTask.length > 0)
                                afterTask[0].call();
                            return;
                        }
                        try {
                            FileUtils.copyFile(new File(downloadDir, assetName), downloadTo);
                            if (afterTask.length > 0)
                                afterTask[0].call();
                        } catch (Exception e) {
                            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Failed to copy downloaded File", e);
                        }
                    }

                    public void failed(AsyncFileDownloader.DownloaderStatus status) {
                        if (status == AsyncFileDownloader.DownloaderStatus.ERROR) {
                            Bukkit.getLogger().log(Level.WARNING, "Something went wrong while downloading " + assetName, status.getException());
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage(CustomHeads.chError + "Failed to download " + assetName + " : " + status);
                        }
                    }
                });
                break;
            }
        }
    }

    public void downloadLatest(String assetName, File downloadTo, AfterTask... afterTask) {
        download(getResponseAsJson("/releases/latest").getAsJsonObject().get("tag_name").getAsString(), assetName, downloadTo, afterTask);
    }

    private JsonElement getResponseAsJson(String path) {
        if (responseCache.containsKey(path)) {
            return (JsonElement) responseCache.get(path)[1];
        }
        JsonElement response = null;
        try {
            HttpURLConnection apiConnection = (HttpURLConnection) new URL(apiURLFormatted + path).openConnection();
            apiConnection.setReadTimeout(10000);
            if (apiConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new Exception("Server responded with " + apiConnection.getResponseCode());
            response = new JsonParser().parse(new InputStreamReader(apiConnection.getInputStream()));
            if (response.isJsonObject() && response.getAsJsonObject().has("message"))
                throw new NullPointerException("Release API resopnded with: " + response.getAsJsonObject().get("message").getAsString());
            responseCache.put(path, new Object[]{System.currentTimeMillis(), response});
        } catch (Exception e) {
        }
        return response;
    }

}
