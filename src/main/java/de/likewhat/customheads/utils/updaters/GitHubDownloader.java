package de.likewhat.customheads.utils.updaters;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.likewhat.customheads.CustomHeads;
import de.likewhat.customheads.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

/*
 *  Project: CustomHeads in GitHubDownloader
 *     by LikeWhat
 */

public class GitHubDownloader {

    private static final File downloadDir = new File(CustomHeads.getInstance().getDataFolder(), "downloads");

    private static final String GITHUB_REPO_URL = "https://api.github.com/repos/%s/%s";
    private static HashMap<String, CachedResponse<JsonElement>> responseCache = new HashMap<>();
    private String apiURLFormatted;
    private boolean unzip = false;

    private String author;
    private String projectName;

    public GitHubDownloader(String author, String projectName) {
        this.author = author;
        this.projectName = projectName;
        apiURLFormatted = String.format(GITHUB_REPO_URL, author, projectName);
    }

    public static void clearCache() {
        responseCache.values().removeIf(cachedResponse -> cachedResponse.getCacheTime() - System.currentTimeMillis() > 600000);
    }

    public GitHubDownloader enableAutoUnzipping() {
        unzip = true;
        return this;
    }

    private static void getResponseAsJson(String url, FetchResult<JsonElement> fetchResult, boolean force) {
        if (responseCache.containsKey(url) && !force) {
            fetchResult.success(responseCache.get(url).getData());
            return;
        }

        try {
            JsonElement response;
            HttpURLConnection apiConnection = (HttpURLConnection) new URL(url).openConnection();
            apiConnection.setReadTimeout(10000);
            if (apiConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                fetchResult.error(new Exception("Server responded with " + apiConnection.getResponseCode()));
                return;
            }
            response = new JsonParser().parse(new InputStreamReader(apiConnection.getInputStream()));
            if (response.isJsonObject() && response.getAsJsonObject().has("message")) {
                fetchResult.error(new NullPointerException("Release API resopnded with: " + response.getAsJsonObject().get("message").getAsString()));
                return;
            }
            responseCache.put(url, new CachedResponse<>(System.currentTimeMillis(), response));
            fetchResult.success(response);
        } catch (Exception e) {
            fetchResult.error(e);
        }
    }

    public static void getRelease(String tag, String author, String project, FetchResult<JsonObject> fetchResult) {
        getRateLimit(new FetchResult<JsonObject>() {
            public void success(JsonObject rateLimit) {
                if(rateLimit.get("remaining").getAsInt() == 0) {
                    Bukkit.getLogger().info(Utils.GSON_PRETTY.toJson(rateLimit));
                    fetchResult.error(new RateLimitExceededException(rateLimit.get("reset").getAsLong()));
                    return;
                }
                getResponseAsJson(String.format(GITHUB_REPO_URL, author, project) + "/releases", new FetchResult<JsonElement>() {
                    public void success(JsonElement js) {
                        JsonArray releaseList = js.getAsJsonArray();
                        JsonObject release = null;
                        for (JsonElement jsonElement : releaseList) {
                            if (jsonElement.getAsJsonObject().get("tag_name").getAsString().equals(tag)) {
                                release = jsonElement.getAsJsonObject();
                                break;
                            }
                        }

                        if (release == null) {
                            fetchResult.error(new NullPointerException("Unkown Tag: " + tag));
                            return;
                        }
                        fetchResult.success(release);
                    }

                    public void error(Exception exception) {
                        Bukkit.getLogger().log(Level.WARNING, "Failed to get Release", exception);
                    }
                }, false);
            }

            public void error(Exception exception) {
                fetchResult.error(exception);
            }
        });
    }

    public void download(String tagName, String assetName, File downloadTo, AsyncFileDownloader.AfterTask... afterTask) {
        getRelease(tagName, author, projectName, new FetchResult<JsonObject>() {
            public void success(JsonObject release) {
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
                                    Files.copy(new File(downloadDir, assetName), downloadTo);

                                    //FileUtils.copyFile(new File(downloadDir, assetName), downloadTo);
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
                                    Bukkit.getServer().getConsoleSender().sendMessage(CustomHeads.chError + "Failed to download " + assetName + ". " + status);
                                }
                            }
                        });
                        break;
                    }
                }
            }

            public void error(Exception exception) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to download Files", exception);
            }
        });
    }

    public void downloadLatest(String assetName, File downloadTo, AsyncFileDownloader.AfterTask... afterTask) {
        getResponseAsJson(apiURLFormatted + "/releases/latest", new FetchResult<JsonElement>() {
            public void success(JsonElement jsonElement) {
                download(jsonElement.getAsJsonObject().get("tag_name").getAsString(), assetName, downloadTo, afterTask);
            }

            public void error(Exception exception) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to fetch latest Data", exception);
            }
        }, false);
    }

    private static void getRateLimit(FetchResult<JsonObject> result) {
        getResponseAsJson("https://api.github.com/rate_limit", new FetchResult<JsonElement>() {
            public void success(JsonElement element) {
                try {
                    result.success(element.getAsJsonObject().getAsJsonObject("rate"));
                } catch(Exception e) {
                    result.error(e);
                }
            }

            @Override
            public void error(Exception exception) {
                exception.printStackTrace();
            }
        }, true);
    }

    @Getter
    @AllArgsConstructor
    public static class RateLimitExceededException extends Exception {
        private long reset;

        public String getTimeUntilReset() {
            long now = System.currentTimeMillis();
            long timeLeft = reset - now;
            if(timeLeft < 0) {
                return "0s";
            }
            timeLeft /= 1000;
            long time = timeLeft % 86400;
            return (int) Math.floor((time % 86400f) / 3600f) + "h " + (int) Math.floor((time % 3600f) / 60f) + "m " + (int) Math.floor((time % 60)) + "s";
        }

        public String getMessage() {
            return "Rate Limit exceeded. Try again later (Time until Reset: " + getTimeUntilReset() + ")";
        }
    }

}
