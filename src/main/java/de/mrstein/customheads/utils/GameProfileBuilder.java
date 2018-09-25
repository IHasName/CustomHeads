package de.mrstein.customheads.utils;

import com.google.common.base.Charsets;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import de.mrstein.customheads.CustomHeads;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public class GameProfileBuilder {

    public static final HashMap<UUID, CachedProfile> cache = new HashMap<>();
    private static final String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";
    private static final String JSON_SKIN = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
    private static final String JSON_CAPE = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";
    private static Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).registerTypeAdapter(GameProfile.class, new GameProfileSerializer()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
    private static long cacheTime = 6000;

    public static String gameProfileToString(GameProfile profile) {
        return gson.toJson(profile);
    }

    public static GameProfile gameProfileFromString(String string) {
        return gson.fromJson(string, GameProfile.class);
    }

    public static void fetch(UUID uuid, Consumer<GameProfile> consumer) {
        new BukkitRunnable() {
            public void run() {
                try {
                    if (cache.containsKey(uuid) && cache.get(uuid).isValid()) {
                        consumer.accept(cache.get(uuid).profile);
                        return;
                    } else {
                        HttpURLConnection connection = (HttpURLConnection) new URL(String.format(SERVICE_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
                        connection.setReadTimeout(5000);

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                            Bukkit.getLogger().warning("Service is unavailable at this moment");
                            consumer.accept(null);
                            return;
                        }
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            String json = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                            GameProfile result = gson.fromJson(json, GameProfile.class);
                            cache.put(uuid, new CachedProfile(result));
                            consumer.accept(result);
                            return;
                        }
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to get Profile of " + uuid.toString(), e);
                }
                consumer.accept(null);
            }
        }.runTaskAsynchronously(CustomHeads.getInstance());
    }

    public static GameProfile getProfile(UUID uuid, String name, String skinUrl, String capeUrl) {
        GameProfile profile = new GameProfile(uuid, name);
        boolean cape = capeUrl != null && !capeUrl.isEmpty();
        List<Object> args = new ArrayList<>();
        args.add(System.currentTimeMillis());
        args.add(UUIDTypeAdapter.fromUUID(uuid));
        args.add(name);
        args.add(skinUrl);
        if (cape)
            args.add(capeUrl);
        profile.getProperties().put("textures", new Property("textures", new String(Base64.getEncoder().encode(String.format(cape ? JSON_CAPE : JSON_SKIN, args.toArray(new Object[args.size()])).getBytes()), Charsets.UTF_8)));
        return profile;
    }

    public static GameProfile createProfileWithTexture(String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));
        return profile;
    }

    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

        public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = (JsonObject) json;
            UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
            String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            GameProfile profile = new GameProfile(id, name);

            if (object.has("properties")) {
                for (Map.Entry<String, Property> prop : ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class)).entries()) {
                    profile.getProperties().put(prop.getKey(), prop.getValue());
                }
            }
            return profile;
        }

        public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (profile.getId() != null)
                result.add("id", context.serialize(profile.getId()));
            if (profile.getName() != null)
                result.addProperty("name", profile.getName());
            if (!profile.getProperties().isEmpty())
                result.add("properties", context.serialize(profile.getProperties()));
            return result;
        }

    }

    public static class CachedProfile {

        public long timestamp = System.currentTimeMillis();
        public GameProfile profile;

        public CachedProfile(GameProfile profile) {
            this.profile = profile;
        }

        public boolean isValid() {
            return cacheTime < 0 || (System.currentTimeMillis() - timestamp) < cacheTime;
        }
    }

}
