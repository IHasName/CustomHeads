package de.mrstein.customheads.updaters;

import com.google.gson.*;
import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.utils.Configs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

/*
 * Project: CustomHeads in SpigetFetcher
 *   by LikeWhat
 */

/**
 * Fetches Updates for Spigot Updates Spigot Resources
 * <p>Uses Spiget Resource API by inventivetalent
 *
 * @author LikeWhat
 * @version 1.1
 */
public class SpigetFetcher {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(ResourceRelease.class, new ResourceRelease.Serializer()).registerTypeAdapter(ResourceUpdate.class, new ResourceUpdate.Serializer()).create();

    private static final String VERSION_URL = "https://api.spiget.org/v2/resources/%s/versions?size=2147483647&sort=-releaseDate";
    private static final String DESCRIPTION_URL = "https://api.spiget.org/v2/resources/%s/updates?size=2147483647&sort=-date";
    @Setter
    private static String userAgent = "UpdateChecker-1.1";
    private String decriptionUrlFormatted;
    private String versionUrlFormatted;

    private static JsonParser jsonParser = new JsonParser();

    public SpigetFetcher(int resourceId) {
        decriptionUrlFormatted = String.format(DESCRIPTION_URL, resourceId);
        versionUrlFormatted = String.format(VERSION_URL, resourceId);
    }

    public void fetchUpdates(FetchResult resultFetcher) {
        new BukkitRunnable() {
            public void run() {
                try {
                    Configs updateFile = CustomHeads.getUpdateFile();
                    long lastFetch = updateFile.get().getLong("lastUpdateCheck");
                    JsonArray versionArray;
                    boolean fromCache = false;
                    if (updateFile.get().isSet("lastVersionFetch") && TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastFetch) <= 0) {
                        versionArray = jsonParser.parse(new String(Base64.decodeBase64(updateFile.get().getString("lastVersionFetch").getBytes()))).getAsJsonArray();
                        fromCache = true;
                    } else {
                        HttpURLConnection connection = (HttpURLConnection) new URL(versionUrlFormatted).openConnection();
                        connection.addRequestProperty("User-Agent", userAgent);
                        connection.setReadTimeout(5000);
                        versionArray = jsonParser.parse(new InputStreamReader(connection.getInputStream())).getAsJsonArray();
                    }

                    List<ResourceRelease> releaseList = new ArrayList<>();
                    for (JsonElement resourceRaw : versionArray) {
                        releaseList.add(GSON.fromJson(resourceRaw, ResourceRelease.class));
                    }
                    if (!fromCache) {
                        updateFile.get().set("lastUpdateCheck", System.currentTimeMillis());
                        updateFile.get().set("lastVersionFetch", new String(Base64.encodeBase64(GSON.toJson(releaseList).getBytes())));
                        updateFile.save();
                    }
                    ResourceRelease latestRelease = releaseList.get(0);
                    int latestVersion = Integer.parseInt(latestRelease.getReleaseName().replace(".", ""));
                    int currentVersion = Integer.parseInt(CustomHeads.getInstance().getDescription().getVersion().replace(".", ""));

                    if (latestVersion > currentVersion) {
                        getLastUpdates(updateList -> resultFetcher.updateAvailable(latestRelease, updateList.get(0)));
                    } else {
                        resultFetcher.noUpdate();
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldnt fetch Version List", e);
                }
            }
        }.runTaskAsynchronously(CustomHeads.getInstance());
    }

    public void getLatestUpdate(Consumer<ResourceUpdate> consumer) {
        getLastUpdates(resourceUpdates -> consumer.accept(resourceUpdates.get(0)));
    }

    public void getLastUpdates(Consumer<List<ResourceUpdate>> consumer) {
        new BukkitRunnable() {
            public void run() {
                try {
                    Configs updateFile = CustomHeads.getUpdateFile();
                    long lastFetch = updateFile.get().getLong("lastUpdateCheck");
                    JsonArray descriptionArray;
                    boolean fromCache = false;
                    if (updateFile.get().isSet("lastDescriptionFetch") && TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastFetch) == 0) {
                        descriptionArray = jsonParser.parse(new String(Base64.decodeBase64(updateFile.get().getString("lastDescriptionFetch")))).getAsJsonArray();
                        fromCache = true;
                    } else {
                        HttpURLConnection descriptionConnection = (HttpURLConnection) new URL(decriptionUrlFormatted).openConnection();
                        descriptionConnection.addRequestProperty("User-Agent", userAgent);
                        descriptionConnection.setReadTimeout(5000);
                        descriptionArray = jsonParser.parse(new InputStreamReader(descriptionConnection.getInputStream())).getAsJsonArray();
                    }

                    List<ResourceUpdate> updateList = new ArrayList<>();
                    for (JsonElement updateRaw : descriptionArray) {
                        updateList.add(GSON.fromJson(updateRaw, ResourceUpdate.class));
                    }
                    if (!fromCache) {
                        updateFile.get().set("lastDescriptionFetch", new String(Base64.encodeBase64(GSON.toJson(updateList).getBytes())));
                        updateFile.save();
                    }
                    consumer.accept(updateList);
                    return;
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldnt fetch Update List", e);
                }
                consumer.accept(null);
            }
        }.runTaskAsynchronously(CustomHeads.getInstance());
    }

    public interface FetchResult {
        void updateAvailable(ResourceRelease release, ResourceUpdate update);

        void noUpdate();
    }

    @AllArgsConstructor
    @Getter
    public static class ResourceRelease {
        private String releaseName;
        private long releaseDate;
        private int releaseId;

        private static class Serializer implements JsonSerializer<ResourceRelease>, JsonDeserializer<ResourceRelease> {
            public ResourceRelease deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return new ResourceRelease(
                        jsonElement.getAsJsonObject().get("name").getAsString(),
                        jsonElement.getAsJsonObject().get("releaseDate").getAsLong(),
                        jsonElement.getAsJsonObject().get("id").getAsInt()
                );
            }

            public JsonElement serialize(ResourceRelease resource, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject resourceObject = new JsonObject();
                resourceObject.addProperty("name", resource.getReleaseName());
                resourceObject.addProperty("releaseDate", resource.getReleaseDate());
                resourceObject.addProperty("id", resource.getReleaseId());
                return resourceObject;
            }
        }
    }

    @AllArgsConstructor
    @Getter
    public static class ResourceUpdate {
        private String title;
        private String desciption;
        private long releaseDate;
        private int likes;
        private int releaseId;

        private static class Serializer implements JsonSerializer<ResourceUpdate>, JsonDeserializer<ResourceUpdate> {
            public ResourceUpdate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return new ResourceUpdate(
                        jsonElement.getAsJsonObject().get("title").getAsString(),
                        jsonElement.getAsJsonObject().get("description").getAsString(),
                        jsonElement.getAsJsonObject().get("date").getAsLong(),
                        jsonElement.getAsJsonObject().get("likes").getAsInt(),
                        jsonElement.getAsJsonObject().get("id").getAsInt()
                );
            }

            public JsonElement serialize(ResourceUpdate resource, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject resourceObject = new JsonObject();
                resourceObject.addProperty("title", resource.getTitle());
                resourceObject.addProperty("description", resource.getDesciption());
                resourceObject.addProperty("date", resource.getReleaseDate());
                resourceObject.addProperty("likes", resource.getLikes());
                resourceObject.addProperty("id", resource.getReleaseId());
                return resourceObject;
            }
        }
    }

}
